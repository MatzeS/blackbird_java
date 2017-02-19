package blackbird.core.avr.packets;

import java.io.ByteArrayOutputStream;

import blackbird.core.avr.CommandBytes;

public class DeviceIdentificationQuery extends TransmittableAVRPacket {

    public DeviceIdentificationQuery() {
        super(CommandBytes.DEVICE_IDENTIFICATION);
    }

    @Override
    public void composePacket(ByteArrayOutputStream outputStream) {
    }

}
