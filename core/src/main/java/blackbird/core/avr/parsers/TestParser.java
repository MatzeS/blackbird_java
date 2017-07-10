package blackbird.core.avr.parsers;

import blackbird.core.connection.Packet;

public class TestParser extends PacketParser {

    public TestParser() {
        super((byte) 0x01);
    }

    @Override
    public Packet parse(byte[] data) {

        for (int i = 0; i < data.length; i++)
            if (i % 2 == 0)
                if (data[i] != (byte) 0xFF)
                    System.out.println(i + " is different");
                else ;
            else if (data[i] != (byte) 0x00)
                System.out.println(i + " is different");

        System.out.println("done!!!!!!!");

        return null;

    }

}
