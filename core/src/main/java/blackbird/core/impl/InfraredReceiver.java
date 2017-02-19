package blackbird.core.impl;

import java.util.function.Consumer;
import java.util.function.Predicate;

import blackbird.core.Blackbird;
import blackbird.core.ComponentImplementation;
import blackbird.core.DInterface;
import blackbird.core.Device;
import blackbird.core.rmi.Remote;
import blackbird.core.util.ListenerList;


public class InfraredReceiver extends Device {

    private static final long serialVersionUID = 6655024766419323859L;

    public InfraredReceiver(String name) {
        super(name);
        getUIData().put("iconName", "ic_satellite");
    }

    public static Runnable map(InfraredReceiver infraredReceiver,
                               Predicate<Integer> codeFilter,
                               Consumer<Integer> action) {
        Interface impl = Blackbird.getInstance().interfaceDevice(infraredReceiver, Interface.class);
        Listener listener = code -> {
            if (codeFilter.test(code))
                action.accept(code);
        };
        impl.addListener(listener);
        return () -> impl.removeListener(listener);
    }

    public interface Interface<D extends InfraredReceiver> extends DInterface {

        void addListener(Listener listener);

        void removeListener(Listener listener);

    }

    public interface Listener extends Remote {

        void irReceive(int code);

    }

    public static abstract class Implementation
            extends ComponentImplementation<InfraredReceiver, DInterface> implements Interface {

        private ListenerList<Listener> listeners;

        public Implementation(DInterface component) {
            super(component);
            listeners = new ListenerList<>();
        }

        public void addListener(Listener listener) {
            listeners.add(listener);
        }

        protected ListenerList<Listener> getListeners() {
            return listeners;
        }

        public Listener map(Predicate<Integer> code, Runnable runnable) {
            Listener listener = codeInt -> {
                if (code.test(codeInt))
                    runnable.run();
            };
            addListener(listener);
            return listener;
        }

        public void removeListener(Listener listener) {
            listeners.remove(listener);
        }

    }

}
