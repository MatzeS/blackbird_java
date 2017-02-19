package blackbird.core.avr.packets;

import java.io.ByteArrayOutputStream;

import blackbird.core.avr.CommandBytes;

public class BasicPinQuery extends TransmittableAVRPacket {

    private Operation operation;
    private int pin;
    private int value;

    public BasicPinQuery(Operation operation, int pin) {
        super(CommandBytes.BASIC_PIN);
        this.operation = operation;
        this.pin = pin;
    }

    public BasicPinQuery(Operation operation, int pin, int value) {
        this(operation, pin);
        this.value = value;
    }

    @Override
    public void composePacket(ByteArrayOutputStream outputStream) {

        byte flag = (byte) operation.getValue();

        outputStream.write(flag);
        outputStream.write(pin);
        switch (operation) {
            case DIGITAL_WRITE:
            case PIN_MODE:
            case ANALOG_WRITE:
                outputStream.write(value);
                break;
            default:
                break;
        }

    }

    public enum Operation {
        PIN_MODE(10),
        DIGITAL_READ(20),
        DIGITAL_READ_CONTINUOUSLY(25),
        DIGITAL_WRITE(30),
        ANALOG_WRITE(40),
        ANALOG_READ(50);

        private int value;

        Operation(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

    }

}
