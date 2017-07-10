package blackbird.core.device.avr;

import java.io.IOException;

import blackbird.core.avr.AVRConnection;
import blackbird.core.avr.i2c.I2CReadQuery;
import blackbird.core.avr.i2c.I2CReadResponse;
import blackbird.core.avr.i2c.I2CWriteQuery;
import blackbird.core.avr.packets.CommonInterruptPacket;
import blackbird.core.builders.SubModuleBuilder;
import blackbird.core.connection.PacketConnection;
import blackbird.core.connection.PacketReceivedEvent;
import blackbird.core.device.I2CMaster;
import blackbird.core.device.avr.AVRDevice.Implementation;

public class AVRI2CMasterModuleImplementation extends I2CMaster.Implementation {

    private AVRDevice.Implementation avrImplementation;
    private AVRConnection connection;

    public AVRI2CMasterModuleImplementation(AVRDevice.Implementation avrImplementation) {
        this.avrImplementation = avrImplementation;
        connection = avrImplementation.getAVRConnection();
        connection.addListener(
                new PacketConnection.PacketTypeListener<CommonInterruptPacket>() {

                    @Override
                    public void packetReceived(CommonInterruptPacket packet, PacketReceivedEvent event) {
                        fireCommonInterruptOccurred();
                    }
                });
    }


    @Override
    public byte readRegister(int slaveAddress, byte registerAddress) throws IOException {
        I2CReadQuery request = new I2CReadQuery(slaveAddress, registerAddress, 1);
        I2CReadResponse read = connection.sendAndReceive(request, I2CReadResponse.class);
        return read.getData()[0];
    }

    @Override
    public void writeRegister(int slaveAddress, byte registerAddress, byte value) throws IOException {
        byte[] data = {value};
        connection.send(new I2CWriteQuery(slaveAddress, registerAddress, data));
    }

    public static class Builder extends SubModuleBuilder
            <I2CMaster, AVRI2CMasterModuleImplementation, AVRDevice, AVRDevice.Implementation> {

        @Override
        public AVRI2CMasterModuleImplementation buildFromModule(I2CMaster device, AVRDevice module, Implementation moduleImpl) {
            return new AVRI2CMasterModuleImplementation(moduleImpl);
        }

    }

}
