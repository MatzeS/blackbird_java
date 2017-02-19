package blackbird.core.impl.avr;

import java.io.IOException;

import blackbird.core.ComponentImplementation;
import blackbird.core.DInterface;
import blackbird.core.GenericDIBuilder;
import blackbird.core.avr.AVRConnection;
import blackbird.core.avr.SerialAVRConnection;
import blackbird.core.avr.packets.BasicPinQuery;
import blackbird.core.avr.packets.BasicPinResponse;
import blackbird.core.avr.packets.DeviceIdentificationQuery;
import blackbird.core.avr.packets.DeviceIdentificationResponse;
import blackbird.core.exception.ImplementationFailedException;
import blackbird.core.impl.SerialDevice;

public class AVRDevice extends SerialDevice {

    private String avrID;

    public AVRDevice(String name, String avrID) {
        super(name);

        this.avrID = avrID;
    }

    public String getAvrID() {
        return avrID;
    }

    public enum PinMode {
        INPUT(0), OUTPUT(1), INPUT_PULL_UP(2);
        private int value;

        PinMode(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public interface Interface extends DInterface {

        int analogRead(int pin);

        void analogWrite(int pin, int value);

        boolean digitalRead(int pin);

        void digitalReadContinuously(int pin);

        void digitalWrite(int pin, boolean high);

        @Override
        AVRDevice getDevice();

        void pinMode(int pin, PinMode pinMode);

    }

    public static class Implementation extends ComponentImplementation<AVRDevice, DInterface>
            implements Interface {

        private AVRConnection avrConnection;

        public Implementation(DInterface component, AVRConnection avrConnection) {
            super(component);
            this.avrConnection = avrConnection;
        }

        @Override
        public int analogRead(int pin) {
            try {
                return avrConnection.sendAndReceive(
                        new BasicPinQuery(BasicPinQuery.Operation.ANALOG_READ, pin),
                        BasicPinResponse.class,
                        r -> r.getPin() == pin
                                && r.getOperation() == BasicPinQuery.Operation.ANALOG_READ
                ).getValue();
            } catch (IOException e) {
                throw new RuntimeException("could not perform analog read", e);
            }
        }

        @Override
        public void analogWrite(int pin, int value) {
            throw new UnsupportedOperationException("PWM not implemented");
//            try {
//                avrConnection.send(new BasicPinQuery(
//                        BasicPinQuery.Operation.ANALOG_WRITE,
//                        pin,
//                        value));
//            } catch (IOException e) {
//                throw new RuntimeException("could not perform digital write", e);
//            }
        }

        @Override
        public boolean digitalRead(int pin) {
            try {
                return avrConnection.sendAndReceive(
                        new BasicPinQuery(BasicPinQuery.Operation.DIGITAL_READ, pin),
                        BasicPinResponse.class,
                        r -> r.getPin() == pin
                                && r.getOperation() == BasicPinQuery.Operation.DIGITAL_READ)
                        .getValue() != 0;
            } catch (IOException e) {
                throw new RuntimeException("could not perform digital read", e);
            }
        }

        @Override
        public void digitalReadContinuously(int pin) {
            try {
                avrConnection.send(new BasicPinQuery(
                        BasicPinQuery.Operation.DIGITAL_READ_CONTINUOUSLY,
                        pin));
            } catch (IOException e) {
                throw new RuntimeException("could not perform digital write", e);
            }
        }

        @Override
        public void digitalWrite(int pin, boolean high) {
            try {
                avrConnection.send(new BasicPinQuery(
                        BasicPinQuery.Operation.DIGITAL_WRITE,
                        pin,
                        high ? 1 : 0));
            } catch (IOException e) {
                throw new RuntimeException("could not perform digital write", e);
            }
        }

        public AVRConnection getAVRConnection() {
            return avrConnection;
        }

        @Override
        public void pinMode(int pin, PinMode pinMode) {
            try {
                avrConnection.send(new BasicPinQuery(
                        BasicPinQuery.Operation.PIN_MODE,
                        pin,
                        pinMode.getValue()));
            } catch (IOException e) {
                throw new RuntimeException("could not set pin mode", e);
            }
        }

        public static class Builder extends
                GenericDIBuilder<AVRDevice, Implementation, SerialDevice.Port> {

            @Override
            public Implementation build(AVRDevice device, Class<Implementation> interfaceType, Port port) {

                SerialDevice.Implementation serialImplementation = blackbird.implementDevice(
                        device, SerialDevice.Implementation.class);

                try {

                    SerialAVRConnection avrConnection = new SerialAVRConnection(serialImplementation.getSerialConnection());

                    //device identification
                    DeviceIdentificationResponse response =
                            avrConnection.sendAndReceive(new DeviceIdentificationQuery(), DeviceIdentificationResponse.class, 2000);

                    System.out.println("Received device identification response: " + response.getAvrID());

                    if (response.getAvrID().trim().equalsIgnoreCase(device.getAvrID().trim())) {
                        return new AVRDevice.Implementation(serialImplementation, avrConnection);
                    } else
                        throw new ImplementationFailedException("avr device on serial port was not the expected one, "
                                + "-" + response.getAvrID() + "/" + device.getAvrID() + "-");

                } catch (IOException e) {
                    throw new ImplementationFailedException("IOException during avr connect/handshake", e);
                }

            }

        }
    }

}
