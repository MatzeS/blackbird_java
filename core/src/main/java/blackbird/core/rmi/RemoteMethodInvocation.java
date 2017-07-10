package blackbird.core.rmi;

import org.apache.commons.lang3.ClassUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import blackbird.core.HostConnection;
import blackbird.core.connection.CloseEvent;
import blackbird.core.connection.PacketReceivedEvent;
import blackbird.core.rmi.packets.InvokeMethod;

public class RemoteMethodInvocation {

    private Logger logger = LogManager.getLogger(RemoteMethodInvocation.class);

    private String namespace;
    private Map<Integer, Object> objectMap;
    private InvokeListener invokeListener;
    private List<HostConnection> connections;

    public RemoteMethodInvocation(Class<?> namespace) {
        this(namespace.getName());
    }

    public RemoteMethodInvocation(String namespace) {
        this.namespace = namespace;
        objectMap = new HashMap<>();
        connections = new ArrayList<>();
        invokeListener = new InvokeListener();
    }


    public <T> T getRemoteObject(Class<T> objInterface, HostConnection connection, int objectID) {
        return getRemoteObject(objInterface, connection, objectID, 5000);
    }

    public <T> T getRemoteObject(Class<T> objInterface, HostConnection connection, int objectID, int timeout) {
        Class<?>[] objInterfaces = {objInterface};
        return (T) getRemoteObject(objInterfaces, connection, objectID, timeout);
    }

    public Object getRemoteObject(Class<?>[] objInterfaces, HostConnection connection, int objectID) {
        return getRemoteObject(objInterfaces, connection, objectID, 5000);
    }


    /**
     * Get remote object implementing objInterface from connection by objectID.
     *
     * @param objInterfaces the interfaces the remote object implements which shall provided
     * @param connection    the connection providing the remote object
     * @param objectID      the objectID the object is registered under
     * @param timeout       the timeout an invocation waits before throwing a TimeoutException
     * @return the remote object to work with
     */
    public Object getRemoteObject(Class<?>[] objInterfaces, HostConnection connection, int objectID, int timeout) {

        //noinspection ToArrayCallWithZeroLengthArrayArgument
        objInterfaces = Arrays.stream(objInterfaces)
                .filter(i -> !Serializable.class.isAssignableFrom(i))
                .collect(Collectors.toList()).toArray(new Class<?>[0]);

        if (!Arrays.stream(objInterfaces).allMatch(Remote.class::isAssignableFrom))
            throw new RuntimeException("Remote objects must implement the remote interface asdf ("
                    + Arrays.stream(objInterfaces).filter(c -> !Remote.class.isAssignableFrom(c))
                    .map(Class::toString)
                    .collect(Collectors.joining(", "))
                    + ")");

        return Proxy.newProxyInstance(getClass().getClassLoader(),
                objInterfaces, new InvocationHandler(connection, objectID, timeout));
    }

    /**
     * Used to register a connection to RMI. <br>
     * Doing so just adds the invokeListener to the Connection, possibly invoking incoming {@link InvokeMethod.Request}s.
     *
     * @param connection the connection registered to RMI
     */
    public void registerConnection(HostConnection connection) {
        connections.add(connection);
        connection.addListener(invokeListener);
    }

    /**
     * Register the object to RMI making it available to all added connections invoking on the objectID.
     *
     * @param object the object to register
     * @return objectID used by {@link RemoteMethodInvocation#getRemoteObject(Class, HostConnection, int)}
     */
    public int registerObject(Object object) {
        int objectID = object.hashCode();
        while (objectMap.containsKey(objectID))
            objectID += (int) (Math.random() * 31);
        objectMap.put(objectID, object);
        return objectID;
    }

    public void releaseAllConnections() {
        connections.forEach(this::releaseConnection);
    }

    public void releaseConnection(HostConnection connection) {
        connections.remove(connection);
        connection.removeListener(invokeListener);
    }

    public void releaseObject(int objectID) {
        objectMap.remove(objectID);
    }

    private static class RemoteDummy implements Serializable {

        private static final long serialVersionUID = -8125994968765123026L;
        private int objectID;
        private Class<?>[] interfaces;

        public RemoteDummy(int objectID) {
            this.objectID = objectID;
        }

        public RemoteDummy(int objectID, Class<?>[] interfaces) {
            this.objectID = objectID;
            this.interfaces = interfaces;
        }

        public Class<?>[] getInterfaces() {
            return interfaces;
        }

        public int getObjectID() {
            return objectID;
        }

    }

    public class InvocationHandler implements java.lang.reflect.InvocationHandler {

        private HostConnection connection;
        private int objectID;
        private int timeout;

        public InvocationHandler(HostConnection connection, int objectID, int timeout) {
            this.connection = connection;
            this.objectID = objectID;
            this.timeout = timeout;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            MethodIdentifier methodIdentifier = new MethodIdentifier(method);

            Object[] arguments;
            Serializable[] serializableArguments = null;
            if (args != null) {
                arguments = new Object[args.length];
                for (int i = 0; i < args.length; i++) {
                    Object arg = args[i];

                    if (arg instanceof Remote) {
                        int objectID = registerObject(arg);
                        RemoteDummy remoteDummy = new RemoteDummy(objectID);
                        arguments[i] = remoteDummy;
                    } else
                        arguments[i] = arg;

                }

                serializableArguments = new Serializable[arguments.length];
                for (int i = 0; i < arguments.length; i++)
                    serializableArguments[i] = (Serializable) arguments[i];

            }

            InvokeMethod.Request request = new InvokeMethod.Request(namespace, objectID, methodIdentifier, serializableArguments);
            InvokeMethod.Reply reply;
            try {
                reply = connection.sendAndReceive(request, InvokeMethod.Reply.class,
                        r -> r.getOriginRequest().getNamespace().equals(namespace)
                                && r.getOriginRequest().getObjectID() == objectID
                                && r.getOriginRequest().getMethodIdentifier().equals(methodIdentifier),
                        timeout);
            } catch (IOException e) {
                logger.error("IOException during RMI request on " + connection);
                logger.trace("IOException during RMI request", e);
                throw e;
            }

            if (reply instanceof InvokeMethod.Error) {
                InvokeMethod.Error error = (InvokeMethod.Error) reply;
                if (error.getException() instanceof InvocationTargetException)
                    throw error.getException().getCause();

                logger.error("Error due remote method invocation: ", error.getMessage());
                throw error.getException();
            }

            Object result = reply.getResult();
            if (result instanceof RemoteDummy) {
                RemoteDummy remoteResult = ((RemoteDummy) result);
                return getRemoteObject(remoteResult.getInterfaces(), connection, remoteResult.getObjectID());
            } else
                return result;
        }

    }

    public class InvokeListener extends HostConnection.PacketTypeListener<InvokeMethod.Request> {

        @Override
        public void closed(CloseEvent event) {
            releaseConnection((HostConnection) event.getSource());
        }

        @Override
        public void packetReceived(InvokeMethod.Request request, PacketReceivedEvent event) {

            //namespace check
            if (!request.getNamespace().equals(namespace))
                return;

            Object targetObject = objectMap.get(request.getObjectID());
            HostConnection connection = (HostConnection) event.getSource();

            if (targetObject == null) {
                String msg = "RMI Error: No object registered to ID " + request.getObjectID();
                logger.warn(msg);
                try {
                    connection.send(new InvokeMethod.Error(request, msg));
                } catch (IOException ioException) {
                    logger.error("IOException due error reply", ioException);
                }
                return;
            }

            MethodIdentifier methodIdentifier = request.getMethodIdentifier();
            Method method;
            try {
                method = methodIdentifier.getDeclaringClass().getMethod(methodIdentifier.getName(), methodIdentifier.getParameterTypes());
            } catch (NoSuchMethodException e) {
                String msg = "Invoke Error: No such method (" + methodIdentifier + ")";
                logger.error(msg, e);
                try {
                    connection.send(new InvokeMethod.Error(request, msg, e));
                } catch (IOException ioException) {
                    logger.error("IOException due error reply", ioException);
                }
                return;
            }

            logger.trace(targetObject + " invoking " + methodIdentifier + " objectID " + request.getObjectID() + "/ " + request);

            if (!method.getDeclaringClass().isAssignableFrom(targetObject.getClass())) {
                String msg = "Invoke Error: Target object is not of expected declaring class (" +
                        method.getDeclaringClass() + " expected; " + targetObject.getClass() + " found)";
                logger.error(msg);
                try {
                    connection.send(new InvokeMethod.Error(request, msg, null));
                } catch (IOException ioException) {
                    logger.error("IOException due error reply", ioException);
                }
                return;
            }

            Object[] args = request.getArgs();
            Object[] arguments = null;

            if (args != null) {
                arguments = new Object[args.length];
                for (int i = 0; i < args.length; i++) {
                    Object arg = args[i];

                    if (arg instanceof RemoteDummy) {

                        RemoteDummy dummy = ((RemoteDummy) arg);

                        Class<?> type = method.getParameterTypes()[i];

                        Object remoteArgument = getRemoteObject(type, connection, dummy.getObjectID());

                        arguments[i] = remoteArgument;

                    } else
                        arguments[i] = arg;
                }
            }


            Object result;
            try {
                result = method.invoke(targetObject, arguments);
            } catch (IllegalAccessException e) {
                String msg = "IllegalAccessException due remote method invocation";
                logger.trace(msg, e);
                try {
                    connection.send(new InvokeMethod.Error(request, msg, e));
                } catch (IOException ioException) {
                    logger.error("IOException due error reply", ioException);
                }
                return;
            } catch (InvocationTargetException e) {
                String msg = "InvocationTargetException due remote method invocation";
                logger.error(msg, e);
                try {
                    connection.send(new InvokeMethod.Error(request, msg, e));
                } catch (IOException ioException) {
                    logger.error("IOException due error reply", ioException);
                }
                return;
            } catch (Exception e) {
                String msg = "Exception during remote method invocation";
                logger.warn(msg, e);
                try {
                    connection.send(new InvokeMethod.Error(request, msg, e));
                } catch (IOException ioException) {
                    logger.error("IOException due error reply", ioException);
                }
                return;
            }

            Serializable replyResult;
            if (result instanceof Remote) {
                int objectID = registerObject(result);
                List<Class<?>> allInterfaces = ClassUtils.getAllInterfaces(result.getClass());
                replyResult = new RemoteDummy(objectID,
                        allInterfaces.toArray(new Class<?>[allInterfaces.size()]));
            } else if (result instanceof Serializable)
                replyResult = (Serializable) result;
            else if (method.getReturnType().equals(void.class))
                replyResult = null;
            else {
                String msg = "Return type of method is neither serializable nor remote object, can't send result back";
                try {
                    connection.send(new InvokeMethod.Error(request, msg));
                } catch (IOException e) {
                    logger.error("IOException due error reply", e);
                }
                logger.error(msg);
                return;
            }

            InvokeMethod.Reply reply = new InvokeMethod.Reply(request, replyResult);
            try {
                connection.send(reply);
            } catch (IOException e) {
                logger.error("Failed to send RMI invoke method reply, " + replyResult + ", " + result + ", " + methodIdentifier, e);
            }

            logger.trace("successfully invoked " + methodIdentifier + " on " + targetObject);
        }

    }

}