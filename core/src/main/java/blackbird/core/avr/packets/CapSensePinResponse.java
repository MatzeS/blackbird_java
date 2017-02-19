package blackbird.core.avr.packets;

public class CapSensePinResponse extends AVRPacket {

    private int pin;
    private boolean pressed;

    public CapSensePinResponse(int pin, boolean pressed) {

        this.pin = pin;
        this.pressed = pressed;
    }

    public int getPin() {
        return pin;
    }

    public boolean isPressed() {
        return pressed;
    }

}
