package blackbird.core;

import blackbird.core.connection.CloseReason;
import blackbird.core.connection.Connection;
import blackbird.core.connection.Packet;
import blackbird.core.connection.PacketConnection;
import com.thoughtworks.xstream.XStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

/**
 * The class provides the general connection implementation for {@link HostDevice}s
 * using ObjectInput/OutputStreams to encode XML Strings produced from {@link Packet}s via XStream.
 * <p>
 * Note: android had issues with the ObjectInput/OutputStreams so XStream is used.
 * For reliable communication OI/OS are still used for the Strings.
 */
public class HostConnection extends PacketConnection implements Runnable {

    private static XStream xstream = new XStream();
    private Logger logger = LogManager.getLogger(HostConnection.class);
    private Thread receiveThread;
    private ObjectOutputStream objectOutputStream;
    private DecompressibleInputStream objectInputStream;

    private HostDevice host;

    public HostConnection(Connection componentConnection) throws IOException {
        super(componentConnection); // delegate


        //WELL, we are fucked!
        objectOutputStream = new ObjectOutputStream(getOutputStream());
        objectInputStream = new DecompressibleInputStream(getInputStream());

        receiveThread = new Thread(this);
        receiveThread.start();
    }

    public HostDevice getHost() {

        return host;
    }

    public void setHost(HostDevice host) {
        this.host = host;
    }

    @Override
    public void close() throws IOException {
        try {
            receiveThread.join();
        } catch (InterruptedException e) {
            logger.error("error joining receive thread", e);
        }
        super.close();
    }

    @Override
    public void run() {
        try {
            String text;
            Object obj;
            //noinspection InfiniteLoopStatement
            while (true) {

                try {
                    text = (String) objectInputStream.readObject();
                } catch (ClassNotFoundException e) {
                    logger.error("String could not be received by objectstream...");
                    continue;
                }
                obj = xstream.fromXML(text);

                if (obj instanceof Packet) {
                    Packet packet = (Packet) obj;
                    firePacketReceived(packet);
                } else
                    logger.warn("received non packet object, " + obj.getClass());

            }

        } catch (EOFException e) {
            // end of object input stream
            logger.trace("closed by other side, EOF");
            fireClosed(CloseReason.BY_OTHER_SIDE);
        } catch (IOException e) {
            logger.error("I/O Exception on network connection receive thread");
            e.printStackTrace();
            fireClosed(CloseReason.FAILURE);
        }
    }

    @Override
    public synchronized void send(Packet packet) throws IOException {
        if (this.isClosed())
            throw new IOException("this connection is already closed");

        String xml = xstream.toXML(packet);
        objectOutputStream.writeObject(xml);
        objectOutputStream.flush();
    }


    /**
     * this is only provisional
     * by Pascal Thivent from stackoverflow
     * http://stackoverflow.com/questions/1816559/make-java-runtime-ignore-serialversionuids
     */
    public static class DecompressibleInputStream extends ObjectInputStream {

        private static Logger logger = LogManager.getLogger(DecompressibleInputStream.class);

        public DecompressibleInputStream(InputStream in) throws IOException {
            super(in);
        }

        protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
            ObjectStreamClass resultClassDescriptor = super.readClassDescriptor(); // initially streams descriptor
            Class localClass; // the class in the local JVM that this descriptor represents.
            try {
                localClass = Class.forName(resultClassDescriptor.getName());
            } catch (ClassNotFoundException e) {
                logger.error("No local class for " + resultClassDescriptor.getName(), e);
                return resultClassDescriptor;
            }
            ObjectStreamClass localClassDescriptor = ObjectStreamClass.lookup(localClass);
            if (localClassDescriptor != null) { // only if class implements serializable
                final long localSUID = localClassDescriptor.getSerialVersionUID();
                final long streamSUID = resultClassDescriptor.getSerialVersionUID();
                if (streamSUID != localSUID) { // check for serialVersionUID mismatch.
                    String s = "Overriding serialized class version mismatch: " + "local serialVersionUID = " + localSUID +
                            " stream serialVersionUID = " + streamSUID;
                    Exception e = new InvalidClassException(s);
                    logger.error("Potentially Fatal Deserialization Operation.", e);
                    resultClassDescriptor = localClassDescriptor; // Use local class descriptor for deserialization
                }
            }
            return resultClassDescriptor;
        }
    }

}
