package blackbird.core.device;

import java.io.IOException;

import blackbird.core.avr.DigitalPinValue;
import blackbird.core.builders.SuperImplBuilder;

import static blackbird.core.avr.ByteHelper.setBit;


public class MCP23017 extends I2CSlave {

    public static final byte ADDRESS_PREFIX = (byte) 0b00100000;
//    public static final String I2C_SLAVE_MODULE = "blackbird.core.device.MCP23017.I2C_SLAVE_MODULE";

    private static final long serialVersionUID = 1373486865571380227L;

//    public I2CSlave getI2CSlaveModule() {
//        return (I2CSlave) getModules().get(I2C_SLAVE_MODULE);
//    }

    public enum Pin {

        A0(Port.A, 0),
        A1(Port.A, 1),
        A2(Port.A, 2),
        A3(Port.A, 3),
        A4(Port.A, 4),
        A5(Port.A, 5),
        A6(Port.A, 6),
        A7(Port.A, 7),

        B0(Port.B, 0),
        B1(Port.B, 1),
        B2(Port.B, 2),
        B3(Port.B, 3),
        B4(Port.B, 4),
        B5(Port.B, 5),
        B6(Port.B, 6),
        B7(Port.B, 7);

        private Port port;
        private int num;

        Pin(Port port, int num) {
            this.port = port;
            this.num = num;
        }

        public int getNum() {
            return num;
        }

        public Port getPort() {
            return port;
        }

    }

    public enum Port {
        A, B
    }

    @SuppressWarnings("SpellCheckingInspection")
    public enum Registers {

        IODIR(0x00, 0x01),
        IPOL(0x02, 0x03),
        GPINTEN(0x04, 0x05),
        DEFVAL(0x06, 0x07),
        INTCON(0x08, 0x09),
        IOCON(0x0A, 0x0B),
        GPPU(0x0C, 0x0D),
        INTF(0x0E, 0x0F),
        INTCAP(0x10, 0x11),
        GPIO(0x12, 0x13),
        OLAT(0x14, 0x15);

        byte registerAddressPortA;
        byte registerAddressPortB;

        Registers(int registerAddressPortA, int registerAddressPortB) {
            this.registerAddressPortA = (byte) registerAddressPortA;
            this.registerAddressPortB = (byte) registerAddressPortB;
        }

        public byte getRegisterAddress(Port port) {
            if (port == Port.A)
                return registerAddressPortA;
            else
                return registerAddressPortB;
        }

        public byte getRegisterAddressPortA() {
            return registerAddressPortA;
        }

        public byte getRegisterAddressPortB() {
            return registerAddressPortB;
        }

    }

    public interface Interface extends I2CSlave.Interface {

        void digitalWrite(Pin pin, DigitalPinValue value) throws IOException;

        void pinMode(Pin pin, boolean mode) throws IOException;

    }

    public static class Implementation extends I2CSlave.Implementation implements Interface {

        private I2CSlave.Implementation delegate;

        public Implementation(I2CSlave.Implementation delegate) {
            super(null);
            this.delegate = delegate;
        }

        public void digitalWrite(Pin pin, DigitalPinValue value) throws IOException {
            byte gpio = readRegister(Registers.OLAT.getRegisterAddress(pin.getPort()));
            gpio = setBit(gpio, pin.getNum(), value == DigitalPinValue.HIGH);
            writeRegister(Registers.GPIO.getRegisterAddress(pin.getPort()), gpio);
        }

        /**
         * @param pin  the pin
         * @param mode false = OUTPUT, true = INPUT
         */
        public void pinMode(Pin pin, boolean mode) throws IOException {
            I2CSlave.updateRegisterBit(this, Registers.IODIR.getRegisterAddress(pin.getPort()), pin.getNum(), mode);
        }

        @Override
        public byte readRegister(byte registerAddress) throws IOException {
            return delegate.readRegister(registerAddress);
        }

        @Override
        public byte[] readRegisters(byte registerAddress, int num) throws IOException {
            return delegate.readRegisters(registerAddress, num);
        }

        @Override
        public void writeRegister(byte registerAddress, byte value) throws IOException {
            delegate.writeRegister(registerAddress, value);
        }

        @Override
        public void writeRegisters(byte registerAddress, byte[] data) throws IOException {
            delegate.writeRegisters(registerAddress, data);
        }

        public static class Builder extends SuperImplBuilder<MCP23017, Implementation, I2CSlave.Implementation> {

            @Override
            public Implementation buildFromSuperImpl(MCP23017 device, I2CSlave.Implementation superImpl) {
                return new Implementation(superImpl);
            }

        }

    }

}
