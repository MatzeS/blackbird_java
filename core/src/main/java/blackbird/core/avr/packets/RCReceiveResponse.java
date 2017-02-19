package blackbird.core.avr.packets;

import blackbird.core.impl.avr.RCReceiver;

public class RCReceiveResponse extends AVRPacket {

    private RCReceiver.Code code;

    public RCReceiveResponse(RCReceiver.Code code) {
        this.code = code;
    }

    public RCReceiver.Code getCode() {
        return code;
    }

}
