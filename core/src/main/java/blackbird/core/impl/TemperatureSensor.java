package blackbird.core.impl;

import blackbird.core.DInterface;
import blackbird.core.Device;

public class TemperatureSensor extends Device {

    public interface Interface extends DInterface {

        @Override
        TemperatureSensor getDevice();

        /**
         * @return the temperature in Celsius
         */
        float getTemperature();

    }

}
