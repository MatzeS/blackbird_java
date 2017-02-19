package blackbird.core.avr.i2c;

import blackbird.core.avr.packets.AVRPacket;

public class I2CReadResponse extends AVRPacket {

    private int slaveAddress;
    private byte registerAddress;
    private byte[] data;

    public I2CReadResponse(int slaveAddress, byte registerAddress, byte[] data) {

        this.slaveAddress = slaveAddress;
        this.registerAddress = registerAddress;
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    public byte getRegisterAddr() {
        return registerAddress;
    }

    public int getSlaveAddr() {
        return slaveAddress;
    }

}
