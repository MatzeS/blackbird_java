package blackbird.core.device;

import blackbird.core.DIState;

/**
 * A remote socket, usually controlled via a radio transmitter using
 * a unidirectional not acknowledged connection.
 * <p>
 * There fore the state of a remote socket is only known due to the last
 * state setting transmission operation (normally the <code>setToggleState</code> method)
 * assuming the connection is reliable and there is no other transmitter.
 * Also note the <code>fireStateChanged</code> method is only
 * invoked by the <code>setToggleState</code> method.
 * <p>
 * The state save ability is used to reproduce the unknown state.
 * If there is no previous state available, the <code>normalState</code> value is used.
 * <p>
 * If <code>forceState</code> is true, the state will also be transmitted to the socket
 * forcing the local value to represent the socket, but might produce an unexpected socket toggle.
 * <p>
 * By default there is no normal state set and the socket will not be forced.
 */
public class RemoteSocket extends Socket {

    private static final long serialVersionUID = -8451060214205420253L;

    /**
     * The normal socket state expected for an unknown remote socket.
     */
    private int normalState = UNKNOWN;

    private boolean forceState = false;

    public boolean getForceState() {
        return forceState;
    }

    public void setForceState(boolean forceState) {
        this.forceState = forceState;
    }

    public int getNormalState() {
        return normalState;
    }

    public void setNormalState(int normalState) {
        this.normalState = normalState;
    }

    public static abstract class Implementation
            extends Socket.Implementation {

        protected int toggleState;

        public Implementation() {
            toggleState = UNKNOWN;
        }

        //TODO
        public DIState destroy() {
            return new RemoteSocket.State(getToggleState());
        }

        @Override
        public RemoteSocket getDevice() {
            return (RemoteSocket) super.getDevice();
        }

        @Override
        public int getToggleState() {
            return toggleState;
        }

        //TODO
        public void loadState(DIState state) {
            if (state != null) {
                RemoteSocket.State rsState = (RemoteSocket.State) state;
                toggleState = rsState.getToggleState();
            } else if (getDevice().getNormalState() != 0)
                toggleState = getDevice().getNormalState();

            if (getDevice().getForceState())
                setToggleState(toggleState);
        }

    }

    public static class State extends DIState {

        private static final long serialVersionUID = -8348071755599441998L;

        private int toggleState;

        public State(int toggleState) {
            this.toggleState = toggleState;
        }

        public int getToggleState() {
            return toggleState;
        }

    }

}
