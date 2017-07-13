package blackbird.core.device;

import java.io.IOException;
import java.util.Objects;

import blackbird.core.DImplementation;
import blackbird.core.DInterface;
import blackbird.core.Device;
import blackbird.core.builders.EndpointBuilder;
import blackbird.core.cluster.I2CBus;

import static blackbird.core.avr.ByteHelper.setBit;

/**
 * The class defines devices attached to the inter integrated circuit bus as slaves.
 * <p>
 * TODO improve communication
 */
public class I2CSlave extends Device {

    private static final long serialVersionUID = -7586714846085036127L;

    private int i2cAddress;
    private I2CBus bus;

    public I2CSlave() {
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

    public void setI2CAddress(int i2cAddress) {
        this.i2cAddress = i2cAddress;
    }

    public I2CMaster getMaster() {
        return bus.getMaster();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), i2cAddress);
    }


    public void setI2CAddress(int slaveAddress, int fixedAddress) {
        setI2CAddress(slaveAddress | fixedAddress);
    }

    public static void updateRegisterBit(I2CSlave.Implementation impl, byte register, int bit, boolean value) throws IOException {
        byte registerValue = impl.readRegister(register);
        registerValue = setBit(registerValue, bit, value);
        impl.writeRegister(register, registerValue);
    }


    /**
     * TODO enchant
     */
    public interface Interface extends DInterface {

        @Override
        I2CSlave getDevice();

        //TODO Device getMaster();

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

    public static class Builder extends EndpointBuilder<I2CSlave, Implementation, I2CMaster, I2CMaster.Implementation> {

        @Override
        public Implementation buildFromEndpoint(I2CSlave device, I2CMaster endpoint, I2CMaster.Implementation endpointImpl) {
            return new Implementation(endpointImpl);
        }

        @Override
        public I2CMaster getSingleEndpoint(I2CSlave device) {
            return device.getMaster();
        }

    }

    public static class Implementation<D extends I2CSlave> extends DImplementation implements Interface {

        private I2CMaster.Implementation master;

        public Implementation(I2CMaster.Implementation master) {
            this.master = master;
        }

        private int getAddress() {
            return getDevice().getI2CAddress();
        }

        @Override
        public I2CSlave getDevice() {
            return (I2CSlave) super.getDevice();
        }

        @Override
        public byte readRegister(byte registerAddress) throws IOException {
            return master.readRegister(getAddress(), registerAddress);
        }

        @Override
        public byte[] readRegisters(byte registerAddress, int num) throws IOException {
            return master.readRegisters(getAddress(), registerAddress, num);
        }

        @Override
        public void writeRegister(byte registerAddress, byte value) throws IOException {
            master.writeRegister(getAddress(), registerAddress, value);
        }

        @Override
        public void writeRegisters(byte registerAddress, byte[] data) throws IOException {
            master.writeRegisters(getAddress(), registerAddress, data);
        }

    }

}
