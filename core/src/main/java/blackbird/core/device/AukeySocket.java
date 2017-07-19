package blackbird.core.device;

import java.util.Objects;

import blackbird.core.builders.EndpointBuilder;

public class AukeySocket extends RemoteSocket {

    private static final long serialVersionUID = -3612748815785045148L;

    private int socketNum;
    private AukeyRemote remote;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AukeySocket that = (AukeySocket) o;
        return socketNum == that.socketNum;
    }

    public AukeyRemote getRemote() {

        return remote;
    }

    public void setRemote(AukeyRemote remote) {
        this.remote = remote;
    }

    public int getSocketNum() {
        return socketNum;
    }

    public void setSocketNum(int socketNum) {
        this.socketNum = socketNum;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), socketNum);
    }

    public static class Implementation extends RemoteSocket.Implementation {

        private AukeyRemote.Interface remote;

        public Implementation(AukeyRemote.Interface remote) {
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
                extends EndpointBuilder<AukeySocket, Interface, AukeyRemote, AukeyRemote.Interface> {

            @Override
            public Interface buildFromEndpoint(AukeySocket device, AukeyRemote endpoint, AukeyRemote.Interface endpointImpl) {
                return new Implementation(endpointImpl);
            }

            @Override
            public AukeyRemote getSingleEndpoint(AukeySocket device) {
                return device.getRemote();
            }
        }

    }

}
