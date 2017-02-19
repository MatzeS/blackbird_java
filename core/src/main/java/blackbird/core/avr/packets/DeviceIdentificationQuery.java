package blackbird.core.avr.packets;

import blackbird.core.avr.CommandBytes;

import java.io.ByteArrayOutputStream;

public class DeviceIdentificationQuery extends TransmittableAVRPacket {

    public DeviceIdentificationQuery() {
        super(CommandBytes.DEVICE_IDENTIFICATION);
    }

    @Override
    public void composePacket(ByteArrayOutputStream outputStream) {
    }

}
