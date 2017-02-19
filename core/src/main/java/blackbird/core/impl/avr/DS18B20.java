package blackbird.core.impl.avr;

import java.io.IOException;

import blackbird.core.ComponentImplementation;
import blackbird.core.DInterface;
import blackbird.core.avr.ByteHelper;
import blackbird.core.avr.DS18B20.DS18B20ReadQuery;
import blackbird.core.avr.DS18B20.DS18B20ReadResponse;
import blackbird.core.impl.OneWireDevice;
import blackbird.core.impl.TemperatureSensor;
import blackbird.core.ports.ParentDevicePort;

public class DS18B20 extends TemperatureSensor implements OneWireDevice {

    private long address;

    @Override
    public long getAddress() {
        return address;
    }

    public void setAddress(byte[] address) {
        setAddress(ByteHelper.decode8Byte(address));
    }

    public void setAddress(long address) {
        this.address = address;
    }

    public void setAddress(String address) {
        setAddress(ByteHelper.hexStringToByteArray(address));
    }

    public interface Interface extends TemperatureSensor.Interface {

        @Override
        DS18B20 getDevice();
    }

    public static class Implementation extends ComponentImplementation<DS18B20, DInterface>
            implements Interface {

        private AVRDevice.Implementation avr;
        private int sensorIndex;

        public Implementation(DInterface component, AVRDevice.Implementation avr) {
            super(component);
            this.avr = avr;
        }

        @Override
        public float getTemperature() {
            try {
                return avr.getAVRConnection().sendAndReceive(
                        new DS18B20ReadQuery(getDevice().address),
                        DS18B20ReadResponse.class,
                        r -> r.getAddress() == getDevice().address
                ).getTemp();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public static class Builder extends
                ParentDevicePort.Builder<DS18B20, Implementation, AVRDevice, AVRDevice.Implementation> {

            @Override
            public Implementation assemble(DInterface component, AVRDevice.Implementation parentInterface) {
                return new Implementation(component, parentInterface);
            }
        }

    }

}
