package blackbird.core.avr.i2c;

import blackbird.core.avr.CommandBytes;
import blackbird.core.avr.packets.TransmittableAVRPacket;

import java.io.ByteArrayOutputStream;

public class I2CWriteQuery extends TransmittableAVRPacket {

    private int slaveAddress;
    private byte registerAddress;
    private byte[] data;

    public I2CWriteQuery(int slaveAddress, byte registerAddress, byte[] data) {
        super(CommandBytes.I2C);

        this.slaveAddress = slaveAddress;
        this.registerAddress = registerAddress;
        this.data = data;
    }

    @Override
    public void composePacket(ByteArrayOutputStream outputStream) {

        byte flagByte = 0x00;
        flagByte |= 0x0000_0000; // write flag

        outputStream.write(flagByte);
        outputStream.write(slaveAddress);
        outputStream.write(registerAddress);
        for (byte b : data)
            outputStream.write(b);

    }

}
