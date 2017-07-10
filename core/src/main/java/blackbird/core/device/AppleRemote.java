package blackbird.core.device;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import blackbird.core.ComponentDIBuilder;
import blackbird.core.ComponentImplementation;
import blackbird.core.DInterface;
import blackbird.core.Device;
import blackbird.core.util.ListenerList;


public class AppleRemote extends Device {

    private static final long serialVersionUID = -8776848175725433375L;

    private byte remoteID;

    public AppleRemote(String name, byte remoteID) {
        super(name);

        this.remoteID = remoteID;

        getUIData().put("iconName", "ic_satellite");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AppleRemote that = (AppleRemote) o;
        return remoteID == that.remoteID;
    }

    public byte getRemoteID() {
        return remoteID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), remoteID);
    }

    public static Runnable map(AppleRemote remote, Predicate<Code> code, Runnable action) {
        Interface impl = remote.getInterface(Interface.class);
        Listener listener = new KeyMapper(code, action);
        impl.addListener(listener);
        return () -> impl.removeListener(listener);
    }

    public enum Key {

        UP(-48), DOWN(-80), RIGHT(-32), LEFT(16), MENU(64), PLAY(122), CENTER(-70);

        private int code;

        Key(int code) {
            this.code = code;
        }

        public int get() {
            return code;
        }

    }

    public interface Interface extends DInterface {

        void addListener(Listener listener);

        @Override
        AppleRemote getDevice();

        void removeListener(Listener listener);

    }

    public interface Listener extends InfraredReceiver.Listener {

        void codeReceived(Code code);
    }

    public static class Code implements Serializable {

        private static final long serialVersionUID = -4565597330632092147L;
        private int code;

        public Code(int code) {
            super();
            this.code = code;
        }

        public int getKey() {
            return (byte) (code >> 8);
        }

        public byte getRemoteID() {
            return (byte) code;
        }

        public boolean is(int remoteID) {
            return getRemoteID() == remoteID;
        }

        public boolean is(Key key) {
            return getKey() == key.get();
        }

    }

    public static class Implementation
            extends ComponentImplementation<AppleRemote, DInterface>
            implements Interface {

        private ListenerList<Listener> listeners;

        public Implementation(DInterface component) {
            super(component);
            this.listeners = new ListenerList<>();
        }

        @Override
        public void addListener(Listener listener) {
            listeners.add(listener);
        }

        public Listener map(Predicate<Code> code, Runnable run) {
            Listener listener = new KeyMapper(code, run);
            addListener(listener);
            return listener;
        }

        @Override
        public void removeListener(Listener listener) {
            listeners.remove(listener);
        }

        public static class Builder extends
                ComponentDIBuilder<AppleRemote, Interface, Port, DInterface> {

            public Builder() {
                setPortType(null);
            }

            @Override
            public Interface build(AppleRemote device, Port port,
                                   DInterface componentInterface) {
                Implementation impl = new Implementation(componentInterface);

                port.getReceivers().forEach(d ->
                        InfraredReceiver.map(d,
                                code -> new Code(code).is(device.getRemoteID()),
                                code -> impl.listeners.fire(l -> l.codeReceived(new Code(code)))
                        ));

                return impl;
            }

        }

    }

    public static class KeyMapper extends ListenerAdapter {

        private Predicate<Code> code;
        private Runnable run;

        public KeyMapper(Predicate<Code> code, Runnable run) {
            this.code = code;
            this.run = run;
        }

        @Override
        public void codeReceived(Code receivedCode) {
            if (code.test(receivedCode))
                run.run();
        }
    }

    public abstract static class ListenerAdapter implements Listener {

        @Override
        public void irReceive(int code) {
        }

    }

    public static class Port extends DPort {

        private List<InfraredReceiver> receivers;

        public Port(InfraredReceiver... receivers) {
            this.receivers = new ArrayList<>();
            for (InfraredReceiver r : receivers)
                this.receivers.add(r);
        }

        public Port(List<InfraredReceiver> receivers) {
            this.receivers = receivers;
        }

        public List<InfraredReceiver> getReceivers() {
            return receivers;
        }

    }

}
