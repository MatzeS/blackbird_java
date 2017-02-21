package blackbird.core.impl.avr;

import blackbird.core.ComponentDIBuilder;
import blackbird.core.DInterface;
import blackbird.core.DPort;
import blackbird.core.PacketConnection;
import blackbird.core.avr.packets.CommonInterruptPacket;
import blackbird.core.events.PacketReceivedEvent;
import blackbird.core.impl.I2CMaster.Implementation;

public class AVRI2CMasterImplementation extends Implementation {

    public AVRI2CMasterImplementation(DInterface component, AVRDevice.Implementation avrImplementation) {
        super(component);

        avrImplementation.getAVRConnection().addListener(
                new PacketConnection.PacketTypeListener<CommonInterruptPacket>() {

                    @Override
                    public void packetReceived(CommonInterruptPacket packet, PacketReceivedEvent event) {
                        fireCommonInterruptOccurred();
                    }
                });

    }

    public static class Builder extends ComponentDIBuilder<AVRDevice, AVRI2CMasterImplementation,
            DPort, DInterface> {

        @Override
        public AVRI2CMasterImplementation build(AVRDevice device, DPort port, DInterface componentInterface) {

            AVRDevice.Implementation avrImplementation =
                    device.getImplementation(AVRDevice.Implementation.class);

            return new AVRI2CMasterImplementation(componentInterface, avrImplementation);
        }

    }


}
