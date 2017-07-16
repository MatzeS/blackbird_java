package blackbird.core.device;

import java.util.function.Consumer;
import java.util.function.Predicate;

import blackbird.core.Blackbird;
import blackbird.core.DImplementation;
import blackbird.core.DInterface;
import blackbird.core.Device;
import blackbird.core.rmi.Remote;
import blackbird.core.util.ListenerList;


public abstract class InfraredReceiver extends Device {

    private static final long serialVersionUID = 6655024766419323859L;

    public InfraredReceiver() {
        getUIProperties().put("iconName", "ic_satellite");
    }

    public static Runnable map(Blackbird blackbird,
                               InfraredReceiver infraredReceiver,
                               Predicate<Integer> codeFilter,
                               Consumer<Integer> action) {
        Interface impl = blackbird.interfaceDevice(infraredReceiver, Interface.class);
        return map(impl, codeFilter, action);
    }

    public static Runnable map(InfraredReceiver.Interface handle,
                               Predicate<Integer> codeFilter,
                               Consumer<Integer> action) {
        Listener listener = code -> {
            if (codeFilter.test(code))
                action.accept(code);
        };
        handle.addListener(listener);
        return () -> handle.removeListener(listener);
    }

    public interface Interface<D extends InfraredReceiver> extends DInterface {

        void addListener(Listener listener);

        void removeListener(Listener listener);

    }

    public interface Listener extends Remote {

        void irReceive(int code);

    }

    public static abstract class Implementation
            extends DImplementation implements Interface {

        private ListenerList<Listener> listeners;

        public Implementation() {
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
