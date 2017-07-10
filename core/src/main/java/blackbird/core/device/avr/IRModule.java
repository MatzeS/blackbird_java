package blackbird.core.device.avr;

import blackbird.core.avr.packets.IRReceiveResponse;
import blackbird.core.builders.SubModuleBuilder;
import blackbird.core.connection.PacketConnection;
import blackbird.core.connection.PacketReceivedEvent;
import blackbird.core.device.InfraredReceiver;

public class IRModule extends InfraredReceiver {

    public static class Builder extends SubModuleBuilder
            <IRModule, Implementation, AVRDevice, AVRDevice.Implementation> {
        @Override
        public Implementation buildFromModule(IRModule device, AVRDevice module, AVRDevice.Implementation moduleImpl) {
            return new Implementation(moduleImpl);
        }
    }

    public static class Implementation extends InfraredReceiver.Implementation<IRModule> {

        public Implementation(AVRDevice.Implementation avr) {
            avr.getAVRConnection().addListener(new PacketConnection.PacketTypeListener<IRReceiveResponse>() {

                @Override
                public void packetReceived(IRReceiveResponse packet, PacketReceivedEvent event) {
                    getListeners().fire(l -> l.irReceive(packet.getCode()));
                }
            });

        }

    }

}
