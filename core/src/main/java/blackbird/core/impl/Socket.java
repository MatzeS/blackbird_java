package blackbird.core.impl;

import blackbird.core.ComponentImplementation;
import blackbird.core.DInterface;
import blackbird.core.Device;
import blackbird.core.rmi.Remote;
import blackbird.core.util.ListenerList;

/**
 * A ordinary power outlet/socket.
 * <p>
 * Note: The socket state is defined through static <code>int</code> fields
 * only because a <code>State</code> enum results in massive name conflicts with
 * the DInterface.State.
 */
public abstract class Socket extends Device {

    public static final int ON = 1;
    public static final int UNKNOWN = 0;
    public static final int OFF = -1;

    private static final long serialVersionUID = -4184226294452997800L;

    public Socket(String name) {
        super(name);
        getUIData().put("iconName", "ic_power");
    }

    public static int getToggleState(Socket socket) {
        return socket.getInterface(Interface.class).getToggleState();
    }

    public static void setToggleState(Socket socket, int toggleState) {
        socket.getInterface(Interface.class).setToggleState(toggleState);
    }

    public static void toggle(Socket socket) {
        socket.getInterface(Interface.class).toggle();
    }

    public static void turnOff(Socket socket) {
        socket.getInterface(Interface.class).turnOff();
    }

    public static void turnOn(Socket socket) {
        socket.getInterface(Interface.class).turnOn();

    }

    /**
     * The interface to control a socket.
     * <p>
     * A implementation preferably extends the {@link Implementation} class,
     * which reduces all convenience methods to the <code>setToggleState</code>,
     * <code>getToggleState</code> method and the <code>fireStateChanged</code> invocation.
     * <p>
     * A socket is either <code>ON</code> (1),
     * <code>OFF</code> (-1) or <code>UNKNOWN</code> (0).
     * <p>
     * Use any of the provided methods to manipulate or read this state.
     */
    public interface Interface extends DInterface {

        void addListener(Listener listener);

        @Override
        Socket getDevice();

        int getToggleState();

        /**
         * Set the socket to the given argument.
         *
         * @param toggleState ON: 1, OFF: -1
         * @throws IllegalArgumentException if <code>toggleState</code> is neither <code>1</code> nor <code>-1</code>
         */
        void setToggleState(int toggleState) throws IllegalArgumentException;

        void removeListener(Listener listener);

        /**
         * Change the turn state vice-versa.
         * A turned on socket, will turn off and
         * a turned off socket, will turn on.
         */
        void toggle();

        /**
         * Turn the socket off
         */
        void turnOff();

        /**
         * Turn the socket on
         */
        void turnOn();

    }

    /**
     * A listener interface to monitor state changes on a socket.
     */
    public interface Listener extends Remote {

        /**
         * Invoked when the toggle state of a socket changes.
         *
         * @param socket      the socket changed
         * @param toggleState the new toggleState
         */
        void stateChanged(Socket socket, int toggleState);

    }

    /**
     * An abstract socket implementation.
     * <p>
     * Note: The deriving class still has to invoke the <code>fireStateChanged</code> method.
     */
    public static abstract class Implementation
            extends ComponentImplementation<Socket, DInterface>
            implements Interface {

        private ListenerList<Listener> listeners;

        public Implementation(DInterface component) {
            super(component);
            listeners = new ListenerList<>();
        }

        @Override
        public void addListener(Listener listener) {
            listeners.add(listener);
        }

        protected void fireStateChanged() {
            listeners.fire(l -> l.stateChanged(getDevice(), getToggleState()));
        }

        @Override
        public void removeListener(Listener listener) {
            listeners.remove(listener);
        }

        @Override
        public void toggle() {
            switch (getToggleState()) {
                case ON:
                    turnOff();
                    break;
                case OFF:
                    turnOn();
                    break;
                default:
                    turnOff();
                    //TODO
                    //throw new RuntimeException("state is unknown, can't toggle");
            }
        }

        @Override
        public void turnOff() {
            setToggleState(OFF);
        }

        @Override
        public void turnOn() {
            setToggleState(ON);
        }

    }

}
