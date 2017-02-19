package blackbird.core.avr;

public enum CommandBytes {

    DEVICE_IDENTIFICATION(0xE0),
    I2C(0xE1),
    CAP_SENSE(0xE2),
    BASIC_PIN(0xE3),
    RC_SWITCH(0xE4),
    DS18B20(0xE5),
    IR(0xE6),
    CAPACITIVE_SENSOR_SET_A(0x01),
    COMMON_INTERRUPT(0xE7);

    byte commandByte;

    CommandBytes(byte commandByte) {
        this.commandByte = commandByte;
    }

    CommandBytes(int commandByte) {
        this((byte) commandByte);
    }

    public byte getByte() {
        return commandByte;
    }

}
