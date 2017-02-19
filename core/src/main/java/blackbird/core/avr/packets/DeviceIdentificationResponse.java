package blackbird.core.avr.packets;

public class DeviceIdentificationResponse extends AVRPacket {

    private String avrID;

    public DeviceIdentificationResponse(String avrID) {
        this.avrID = avrID;
    }

    public String getAvrID() {
        return avrID;
    }

}
