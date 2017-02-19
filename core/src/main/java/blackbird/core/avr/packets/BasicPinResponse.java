package blackbird.core.avr.packets;

public class BasicPinResponse extends AVRPacket {

    private BasicPinQuery.Operation operation;
    private int pin;
    private int value;

    public BasicPinResponse(BasicPinQuery.Operation operation, int pin, int value) {
        this.operation = operation;
        this.pin = pin;
        this.value = value;
    }

    public BasicPinQuery.Operation getOperation() {
        return operation;
    }

    public int getPin() {
        return pin;
    }

    public int getValue() {
        return value;
    }

}
