package blackbird.core.avr.parsers;

import blackbird.core.Packet;
import blackbird.core.avr.CommandBytes;
import blackbird.core.avr.packets.DeviceIdentificationResponse;

public class DeviceIdentificationParser extends blackbird.core.avr.parsers.PacketParser {

    public DeviceIdentificationParser() {
        super(CommandBytes.DEVICE_IDENTIFICATION);
    }

    @Override
    public Packet parse(byte[] data) {
        String id = new String(data);
        return new DeviceIdentificationResponse(id);
    }

}
