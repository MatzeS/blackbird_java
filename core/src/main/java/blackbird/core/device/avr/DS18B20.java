package blackbird.core.device.avr;

import java.io.IOException;

import blackbird.core.DImplementation;
import blackbird.core.avr.ByteHelper;
import blackbird.core.avr.DS18B20.DS18B20ReadQuery;
import blackbird.core.avr.DS18B20.DS18B20ReadResponse;
import blackbird.core.builders.EndpointBuilder;
import blackbird.core.device.TemperatureSensor;

/**
 * TODO move to device packet upwards after abstracting
 */
public class DS18B20 extends TemperatureSensor {

    private long address;
    private AVRDevice master;

    public long getAddress() {
        return address;
    }

    public void setAddress(byte[] address) {
        setAddress(ByteHelper.decode8Byte(address));
    }

    public AVRDevice getMaster() {
        return master;
    }

    public void setAddress(long address) {
        this.address = address;
    }

    public void setAddress(String address) {
        setAddress(ByteHelper.hexStringToByteArray(address));
    }

    public interface Interface extends TemperatureSensor.Interface {

    }

    public static class Implementation extends DImplementation
            implements Interface {

        private AVRDevice.Implementation avr;

        public Implementation(AVRDevice.Implementation avr) {
            this.avr = avr;
        }

        @Override
        public DS18B20 getDevice() {
            return (DS18B20) super.getDevice();
        }

        @Override
        public double getTemperature() {
            try {
                return avr.getAVRConnection().sendAndReceive(
                        new DS18B20ReadQuery(getDevice().getAddress()),
                        DS18B20ReadResponse.class,
                        r -> r.getAddress() == getDevice().address
                ).getTemp();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public static class Builder extends
                EndpointBuilder<DS18B20, Implementation, AVRDevice, AVRDevice.Implementation> {

            @Override
            public Implementation buildFromEndpoint(DS18B20 device, AVRDevice endpoint, AVRDevice.Implementation endpointImpl) {
                return new Implementation(endpointImpl);
            }

            @Override
            public AVRDevice getSingleEndpoint(DS18B20 device) {
                return device.getMaster();
            }

        }

    }

}
