package blackbird.core.impl.avr;

import java.io.IOException;

import blackbird.core.DInterface;
import blackbird.core.avr.packets.RCSwitchQuery;
import blackbird.core.impl.RCSocket;
import blackbird.core.ports.ParentDevicePort;

import static blackbird.core.impl.Socket.ON;

public class RCSocketImplementation extends RCSocket.Implementation {

    private AVRDevice.Implementation avr;

    public RCSocketImplementation(DInterface component, AVRDevice.Implementation avr) {
        super(component);
        this.avr = avr;
    }

    @Override
    public RCSocket getDevice() {
        return (RCSocket) super.getDevice();
    }

    @Override
    public synchronized void setToggleState(int toggleState) throws IllegalArgumentException {
        try {
            avr.getAVRConnection().send(
                    new RCSwitchQuery(
                            getDevice().getAddress(),
                            toggleState == ON
                    )
            );
            super.toggleState = toggleState;
            fireStateChanged();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException("Interrupted during timeout", e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class Builder extends
            ParentDevicePort.Builder<RCSocket, RCSocketImplementation,
                    AVRDevice, AVRDevice.Implementation> {

        @Override
        public RCSocketImplementation assemble(DInterface component, AVRDevice.Implementation parentInterface) {
            return new RCSocketImplementation(component, parentInterface);
        }
    }

}
