package blackbird.core.device;

import java.time.Duration;

import blackbird.core.Blackbird;
import blackbird.core.DInterface;
import blackbird.core.Device;
import blackbird.core.interconnect.HysteresisPollingTrigger;

public class TemperatureSensor extends Device {

    public static final Duration TEMP_SENSOR_POLLING_INTERVAL = Duration.ofMillis(5 * 1000);

    public static double getTemperature(Blackbird blackbird, TemperatureSensor sensor) {
        return blackbird.interfaceDevice(sensor, Interface.class).getTemperature();
    }

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
    public static HysteresisPollingTrigger hysteresisTrigger(Blackbird blackbird,
                                                             TemperatureSensor sensor,
                                                             Double highEdge, Double lowEdge,
                                                             Runnable risingAction, Runnable fallingAction) {
        return new HysteresisPollingTrigger(
                () -> getTemperature(blackbird, sensor) >= highEdge && highEdge != null,
                () -> getTemperature(blackbird, sensor) <= lowEdge && lowEdge != null,
                risingAction,
                fallingAction,
                TEMP_SENSOR_POLLING_INTERVAL
        ).activate();
    }

    public interface Interface<D extends TemperatureSensor> extends DInterface {

        /**
         * @return the temperature in Celsius
         */
        double getTemperature();

    }

}
