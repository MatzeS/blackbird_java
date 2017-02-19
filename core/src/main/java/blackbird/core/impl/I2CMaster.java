package blackbird.core.impl;

import blackbird.core.ComponentImplementation;
import blackbird.core.DInterface;
import blackbird.core.Device;
import blackbird.core.util.ListenerList;

public class I2CMaster {

    public interface Interface extends DInterface {

        void addListener(Listener listener);

        void removeListener(Listener listener);

        //TODO
        //emitStartCondition()

    }

    public interface Listener {

        void commonInterruptOccurred();
    }

    public static abstract class Implementation extends ComponentImplementation<Device, DInterface>
            implements Interface {

        private ListenerList<Listener> listeners;

        public Implementation(DInterface component) {
            super(component);

            listeners = new ListenerList<>();
        }

        public void addListener(Listener listener) {
            listeners.add(listener);
        }

        protected void fireCommonInterruptOccurred() {
            listeners.fire(Listener::commonInterruptOccurred);
        }

        public void removeListener(Listener listener) {
            listeners.remove(listener);
        }

    }

}
