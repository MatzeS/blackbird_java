package blackbird.core.impl.avr;

import blackbird.core.DInterface;
import blackbird.core.PacketConnection;
import blackbird.core.avr.packets.IRReceiveResponse;
import blackbird.core.events.PacketReceivedEvent;
import blackbird.core.impl.InfraredReceiver;
import blackbird.core.ports.ParentDevicePort;

public class InfraredReceiverImplementation extends InfraredReceiver.Implementation {

    public InfraredReceiverImplementation(DInterface component, AVRDevice.Implementation avr) {
        super(component);

        avr.getAVRConnection().addListener(new PacketConnection.PacketTypeListener<IRReceiveResponse>() {

            @Override
            public void packetReceived(IRReceiveResponse packet, PacketReceivedEvent event) {
                getListeners().fire(l -> l.irReceive(packet.getCode()));
            }
        });

    }

    public static class Builder extends ParentDevicePort.Builder
            <InfraredReceiver, InfraredReceiverImplementation, AVRDevice, AVRDevice.Implementation> {

        @Override
        public InfraredReceiverImplementation assemble(DInterface component, AVRDevice.Implementation parentInterface) {
            return new InfraredReceiverImplementation(component, parentInterface);
        }
    }

}
