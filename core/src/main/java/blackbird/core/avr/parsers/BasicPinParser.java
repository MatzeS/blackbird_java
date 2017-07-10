package blackbird.core.avr.parsers;

import java.util.Arrays;

import blackbird.core.connection.Packet;
import blackbird.core.avr.CommandBytes;
import blackbird.core.avr.packets.BasicPinQuery;
import blackbird.core.avr.packets.BasicPinResponse;

public class BasicPinParser extends PacketParser {

    public BasicPinParser() {
        super(CommandBytes.BASIC_PIN);
    }

    @Override
    public Packet parse(byte[] data) {

        byte flag = data[0];
        int pin = data[1];
        int value = (0xFF & data[3]) | ((0xFF & data[2]) << 8);

        BasicPinQuery.Operation operation =
                Arrays.stream(BasicPinQuery.Operation.values())
                        .filter(o -> o.getValue() == flag)
                        .findAny().get();

        //System.out.println("got " + pin + "/"+operation + "/" + value);

        return new BasicPinResponse(operation, pin, value);
    }

}
