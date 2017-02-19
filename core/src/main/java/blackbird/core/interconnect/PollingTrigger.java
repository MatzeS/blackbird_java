package blackbird.core.interconnect;

import java.time.Duration;
import java.util.function.BooleanSupplier;

public class PollingTrigger extends Thread {

    private boolean hold = false;
    private Duration interval;

    private BooleanSupplier check;
    private Runnable action;

    public PollingTrigger(Duration interval, BooleanSupplier check, Runnable action) {
        this.interval = interval;
        this.check = check;
        this.action = action;
    }

    @Override
    public void run() {
        while (!hold) {
            if (check.getAsBoolean())
                action.run();

            try {
                Thread.sleep(interval.toMillis());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void hold() {
        hold = true;
    }

}
