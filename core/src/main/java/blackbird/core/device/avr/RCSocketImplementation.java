package blackbird.core.device.avr;

import java.io.IOException;

import blackbird.core.avr.packets.RCSwitchQuery;
import blackbird.core.builders.EndpointBuilder;
import blackbird.core.device.RCSocket;
import blackbird.core.device.avr.AVRDevice.Implementation;

import static blackbird.core.device.Socket.ON;

public class RCSocketImplementation extends RCSocket.Implementation {

    private AVRDevice.Implementation avr;

    public RCSocketImplementation(AVRDevice.Implementation avr) {
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
            EndpointBuilder<RCSocket, RCSocketImplementation,
                    AVRDevice, AVRDevice.Implementation> {

        @Override
        public RCSocketImplementation buildFromEndpoint(RCSocket device, AVRDevice endpoint, Implementation endpointImpl) {
            return new RCSocketImplementation(endpointImpl);
        }

        @Override
        public AVRDevice getSingleEndpoint(RCSocket device) {
            return device.getTransmitter();
        }
    }

}
