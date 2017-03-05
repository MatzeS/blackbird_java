package blackbird.core.interconnect;

import blackbird.core.util.ListenerList;

public class Trigger {

    ListenerList<Runnable> receivers;

    public Trigger() {
        receivers = new ListenerList<>();
    }

    public void fire(){
        receivers.fire(Runnable::run);
    }

    public void addReceiver(Runnable receiver){
        receivers.add(receiver);
    }

    public void removeReceiver(Runnable receiver){
        receivers.remove(receiver);
    }

}
