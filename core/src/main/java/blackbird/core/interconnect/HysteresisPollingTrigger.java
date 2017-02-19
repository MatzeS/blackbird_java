package blackbird.core.interconnect;

import java.time.Duration;
import java.util.function.BooleanSupplier;

import static blackbird.core.interconnect.HysteresisPollingTrigger.State.HIGH;
import static blackbird.core.interconnect.HysteresisPollingTrigger.State.LOW;

public class HysteresisPollingTrigger extends Thread {

    public enum State {HIGH, LOW}

    private State state;

    private BooleanSupplier risingCheck, fallingCheck;
    private Runnable risingAction, fallingAction;

    private boolean hold = false;
    private Duration interval;

    public HysteresisPollingTrigger(BooleanSupplier risingCheck, BooleanSupplier fallingCheck,
                                    Runnable risingAction, Runnable fallingAction,
                                    Duration interval) {
        this(risingCheck, fallingCheck, risingAction, fallingAction, LOW, interval);
    }

    public HysteresisPollingTrigger(BooleanSupplier risingCheck, BooleanSupplier fallingCheck,
                                    Runnable risingAction, Runnable fallingAction,
                                    State initialState,
                                    Duration interval) {
        this.risingCheck = risingCheck;
        this.fallingCheck = fallingCheck;
        this.risingAction = risingAction;
        this.fallingAction = fallingAction;
        this.interval = interval;
        this.state = initialState;
    }

    public HysteresisPollingTrigger activate() {
        this.start();
        return this;
    }

    @Override
    public void run() {
        while (!hold) {

            if (state == HIGH && fallingCheck.getAsBoolean()) {
                state = LOW;
                fallingAction.run();
            } else if (state == LOW && risingCheck.getAsBoolean()) {
                state = HIGH;
                risingAction.run();
            }

            try {
                Thread.sleep(interval.toMillis());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void hold() {
        hold = true;
        try {
            this.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}