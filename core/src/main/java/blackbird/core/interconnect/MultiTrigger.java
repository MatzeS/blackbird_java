package blackbird.core.interconnect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiTrigger extends Trigger{

    private List<Trigger> trigger;
    private Map<Trigger, Long> lastTriggered;

    private long offset = 80;

    public MultiTrigger() {
        trigger = new ArrayList<>();
        lastTriggered = new HashMap<>();
    }

    public MultiTrigger(Runnable receiver){
        this();
        addReceiver(receiver);
    }

    public void addTrigger(Trigger trigger){
        this.trigger.add(trigger);
        lastTriggered.put(trigger, System.currentTimeMillis());
        trigger.addReceiver(() -> {
            lastTriggered.put(trigger, System.currentTimeMillis());
            fire();
        });
    }

    @Override
    public void fire() {
        if(lastTriggered.values().stream().
                mapToInt(l -> (int) (System.currentTimeMillis() - l)).
                anyMatch(i -> i > offset))
            return;

        super.fire();
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
