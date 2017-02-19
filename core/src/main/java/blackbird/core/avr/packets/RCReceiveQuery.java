package blackbird.core.avr.packets;

import blackbird.core.avr.CommandBytes;

import java.io.ByteArrayOutputStream;

public class RCReceiveQuery extends TransmittableAVRPacket {

    private boolean activate;

    public RCReceiveQuery(boolean activate) {
        super(CommandBytes.RC_SWITCH);
        this.activate = activate;
    }

    @Override
    public void composePacket(ByteArrayOutputStream outputStream) {
        outputStream.write(activate ? 20 : 30);
    }

}
