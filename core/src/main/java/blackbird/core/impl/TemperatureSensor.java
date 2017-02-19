package blackbird.core.impl;

import java.time.Duration;
import java.util.function.Predicate;

import blackbird.core.Blackbird;
import blackbird.core.DInterface;
import blackbird.core.Device;
import blackbird.core.interconnect.HysteresisPollingTrigger;
import blackbird.core.interconnect.PollingTrigger;

public class TemperatureSensor extends Device {

    public static final Duration TEMP_SENSOR_POLLING_INTERVAL = Duration.ofMillis(5 * 1000);

    /**
     * Sets up a polling routine checking the temperature.
     *
     * @param sensor        the sensor
     * @param highEdge      inactive if null
     * @param lowEdge       inactive if null
     * @param risingAction  executed once, when temp rises above highEdge
     * @param fallingAction executed once, when temp falls below lowEdge
     * @return
     */
    public static HysteresisPollingTrigger hysteresisTrigger(TemperatureSensor sensor,
                                                             Double highEdge, Double lowEdge,
                                                             Runnable risingAction, Runnable fallingAction) {
        return new HysteresisPollingTrigger(
                () -> getTemperature(sensor) >= highEdge && highEdge != null,
                () -> getTemperature(sensor) <= lowEdge && lowEdge != null,
                risingAction,
                fallingAction,
                TEMP_SENSOR_POLLING_INTERVAL
        ).activate();
    }

    public static double getTemperature(TemperatureSensor sensor) {
        return Blackbird.getInstance().interfaceDevice(sensor, TemperatureSensor.Interface.class).getTemperature();
    }

    public interface Interface extends DInterface {

        @Override
        TemperatureSensor getDevice();

        /**
         * @return the temperature in Celsius
         */
        double getTemperature();

    }

}
