package blackbird.core.avr.packets;

import blackbird.core.avr.CommandBytes;

import java.io.ByteArrayOutputStream;

public class CapSensePinActivateQuery extends TransmittableAVRPacket {

    public static final int DEFAULT_THRESHOLD = 1000;
    public static final int DEFAULT_SAMPLES = 50;

    private int pin;
    private int threshold;
    private int samples;

    public CapSensePinActivateQuery(int pin) {
        this(pin, DEFAULT_THRESHOLD, DEFAULT_SAMPLES);
    }

    public CapSensePinActivateQuery(int pin, int threshold, int samples) {
        super(CommandBytes.CAP_SENSE);
        this.pin = pin;
        this.threshold = threshold;
        this.samples = samples;
    }

    @Override
    public void composePacket(ByteArrayOutputStream outputStream) {

        byte flag = 0x00;

        outputStream.write(flag);
        outputStream.write(pin);
        outputStream.write(threshold >> 8);
        outputStream.write(threshold);
        outputStream.write(samples >> 8);
        outputStream.write(samples);


    }

}
