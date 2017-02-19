package blackbird.core.impl;

import blackbird.core.DInterface;
import blackbird.core.exception.ImplementationFailedException;
import blackbird.core.ports.ParentDevicePort;

import java.util.Objects;

public class AukeySocket extends RemoteSocket {

    private static final long serialVersionUID = -3612748815785045148L;

    private int socketNum;

    public AukeySocket(String name, int socketNum) {
        super(name);
        this.socketNum = socketNum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AukeySocket that = (AukeySocket) o;
        return socketNum == that.socketNum;
    }

    public int getSocketNum() {
        return socketNum;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), socketNum);
    }

    public static class Implementation extends RemoteSocket.Implementation {

        private AukeyRemote.Interface remote;

        public Implementation(DInterface component, AukeyRemote.Interface remote) {
            super(component);
            this.remote = remote;
        }

        @Override
        public AukeySocket getDevice() {
            return (AukeySocket) super.getDevice();
        }

        @Override
        public void setToggleState(int toggleState) {
            remote.switchSocket(getDevice().getSocketNum(), toggleState);
            super.toggleState = toggleState;
            fireStateChanged();
        }

        public static class Builder
                extends ParentDevicePort.Builder<AukeySocket, Interface, AukeyRemote, AukeyRemote.Interface> {

            @Override
            public Interface assemble(DInterface component, AukeyRemote.Interface parentInterface)
                    throws ImplementationFailedException {
                return new Implementation(component, parentInterface);
            }
        }

    }

}
