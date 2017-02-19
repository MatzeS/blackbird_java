package blackbird.core.impl.avr;

import blackbird.core.DInterface;
import blackbird.core.avr.AVRConnection;
import blackbird.core.avr.i2c.I2CReadQuery;
import blackbird.core.avr.i2c.I2CReadResponse;
import blackbird.core.avr.i2c.I2CWriteQuery;
import blackbird.core.impl.I2CSlave;
import blackbird.core.ports.ParentDevicePort;

import java.io.IOException;

public class AVRI2CSlaveImplementation extends I2CSlave.Implementation {

    private AVRDevice.Implementation avrImplementation;
    private AVRConnection connection;

    public AVRI2CSlaveImplementation(DInterface component, AVRDevice.Implementation avrImplementation) {
        super(component);

        this.avrImplementation = avrImplementation;
        connection = avrImplementation.getAVRConnection();
    }

    @Override
    public synchronized byte readRegister(byte register) throws IOException {
        I2CReadQuery request = new I2CReadQuery(getDevice().getI2CAddress(), register, 1);
        I2CReadResponse read = connection.sendAndReceive(request, I2CReadResponse.class);
        return read.getData()[0];
    }

    @Override
    public synchronized void writeRegister(byte register, byte value) throws IOException {
        byte[] data = {value};
        connection.send(new I2CWriteQuery(getDevice().getI2CAddress(), register, data));
    }


    public static class Builder extends ParentDevicePort.Builder<I2CSlave, AVRI2CSlaveImplementation,
            AVRDevice, AVRDevice.Implementation> {

        @Override
        public AVRI2CSlaveImplementation assemble(DInterface component, AVRDevice.Implementation parentInterface) {
            return new AVRI2CSlaveImplementation(component, parentInterface);
        }
    }

}
