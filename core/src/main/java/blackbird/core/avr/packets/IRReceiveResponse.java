package blackbird.core.avr.packets;

public class IRReceiveResponse extends AVRPacket {

    private int code;

    public IRReceiveResponse(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
