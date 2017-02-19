package blackbird.core.impl;

import blackbird.core.ComponentImplementation;
import blackbird.core.DInterface;
import blackbird.core.Device;

import java.io.IOException;
import java.util.Objects;

import static blackbird.core.avr.ByteHelper.setBit;

/**
 * The class defines devices attached to the inter integrated circuit bus as slaves.
 * <p>
 * TODO improve communication
 */
public class I2CSlave extends Device {

    private static final long serialVersionUID = -7586714846085036127L;

    private int i2cAddress;

    public I2CSlave(String name, int i2cAddress) {
        super(name);
        this.i2cAddress = i2cAddress;
    }

    public I2CSlave(String name, int deviceAddress, int fixedAddress) {
        super(name);
        this.i2cAddress = deviceAddress | fixedAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        I2CSlave i2cSlave = (I2CSlave) o;
        return i2cAddress == i2cSlave.i2cAddress;
    }

    public int getI2CAddress() {
        return i2cAddress;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), i2cAddress);
    }

    @Override
    public String toString() {
        if (getToken() != null)
            return getToken();
        else
            return "I2C_" + i2cAddress;
    }

    // TODO move back to interface, just moved for java 7 compatible android compile
    public static void updateRegisterBit(Interface implementation, byte register, int bit, boolean value) throws IOException {
        byte registerValue = implementation.readRegister(register);
        registerValue = setBit(registerValue, bit, value);
        implementation.writeRegister(register, registerValue);
    }

    /**
     * TODO enchant
     */
    public interface Interface extends DInterface {

        //TODO Device getMaster();

        @Override
        I2CSlave getDevice();

        /**
         * Reads a single register from the slave device at the given <code>registerAddress</code>.
         * <p>
         * TODO what happens if the address is not available?
         *
         * @param registerAddress the address of the register to be read
         * @return the value of the register
         * @throws IOException
         */
        byte readRegister(byte registerAddress) throws IOException;

        /**
         * Reads <code>num</code> registers of the slave device
         * starting at the given <code>registerAddress</code>.
         * <p>
         * The <code>readRegisters</code> method calls the <code>readRegister</code>
         * method for each register (incrementing the address) to be read.
         * Subclasses are encouraged to override this method and
         * provide a more efficient implementation generally using the common
         * I2C auto increment hardware implementation.
         *
         * @param registerAddress the first register to be read
         * @param num             number of registers to be read
         * @return the value of the read registers
         * @throws IOException
         * @see <a href="http://www.i2c-bus.org/auto-increment/">I2C bus auto increment</a>
         */
        byte[] readRegisters(byte registerAddress, int num) throws IOException;

        void writeRegister(byte registerAddress, byte value) throws IOException;

        /**
         * Writes all <code>data</code> bytes continuously to the slave device
         * starting at the given <code>registerAddress</code>.
         * <p>
         * The <code>writeRegisters</code> method calls the <code>writeRegister</code>
         * method for each data byte (incrementing the address).
         * Subclasses are encouraged to override this method and
         * provide a more efficient implementation generally using the common
         * I2C auto increment hardware implementation.
         *
         * @param registerAddress the first register address to be written to
         * @param data            data to be written
         * @throws IOException
         * @see <a href="http://www.i2c-bus.org/auto-increment/">I2C bus auto increment</a>
         */
        void writeRegisters(byte registerAddress, byte[] data) throws IOException;

    }

    public static abstract class Implementation
            extends ComponentImplementation<I2CSlave, DInterface> implements Interface {

        public Implementation(DInterface component) {
            super(component);
        }

        @Override
        public byte[] readRegisters(byte registerAddress, int num) throws IOException {
            if (num <= 0)
                throw new IndexOutOfBoundsException("Can not read zero or a negative number of registers.");

            byte[] data = new byte[num];
            for (int i = 0; i < num; i++)
                data[i] = readRegister((byte) (registerAddress + num));
            return data;
        }

        public void updateRegisterBit(byte register, int bit, boolean value) throws IOException {
            I2CSlave.updateRegisterBit(this, register, bit, value);
        }

        @Override
        public void writeRegisters(byte registerAddress, byte[] data) throws IOException {
            if (data == null)
                throw new IllegalArgumentException("data can not be null");

            for (int i = 0; i < data.length; i++)
                writeRegister((byte) (registerAddress + i), data[i]);
        }

    }

}
