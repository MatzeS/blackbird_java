package blackbird.core.device.avr;

import java.io.IOException;
import java.io.Serializable;
import java.util.function.Predicate;

import blackbird.core.Blackbird;
import blackbird.core.DImplementation;
import blackbird.core.DInterface;
import blackbird.core.Device;
import blackbird.core.avr.packets.RCReceiveQuery;
import blackbird.core.avr.packets.RCReceiveResponse;
import blackbird.core.builders.SubModuleBuilder;
import blackbird.core.connection.PacketConnection;
import blackbird.core.connection.PacketReceivedEvent;
import blackbird.core.rmi.Remote;
import blackbird.core.util.ListenerList;

/**
 * abstract and move out of package
 */
public class RCReceiver extends Device {

    public static Listener map(RCReceiver.Interface rcReceiverInterface,
                               int code,
                               Runnable task) {
        return map(
                rcReceiverInterface,
                c -> c.getValue() == code,
                task
        );
    }

    public static Listener map(Blackbird blackbird,
                               RCReceiver rcReceiver,
                               Predicate<Code> test,
                               Runnable task) {
        return map(blackbird.interfaceDevice(rcReceiver, Interface.class), test, task);
    }

    public static Listener map(RCReceiver.Interface rcReceiverInterface,
                               Predicate<Code> test,
                               Runnable task) {
        Listener listener = data -> {
            if (test.test(data.getCode()))
                task.run();
        };
        rcReceiverInterface.addListener(listener);
        return listener;
    }

    public interface Interface extends DInterface {

        void addListener(Listener listener);

        @Override
        RCReceiver getDevice();

        void removeListener(Listener listener);
    }

    public interface Listener extends Remote {

        void received(RCReceiveResponse data);
    }

    public static class Code implements Serializable {

        private int value;
        private int bitLength;
        private int protocol;

        public Code(int value, int bitLength, int protocol) {
            this.value = value;
            this.bitLength = bitLength;
            this.protocol = protocol;
        }

        public int getBitLength() {
            return bitLength;
        }

        public int getProtocol() {
            return protocol;
        }

        public int getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "Code{" +
                    "value=" + value +
                    ", bitLength=" + bitLength +
                    ", protocol=" + protocol +
                    '}';
        }
    }

    public static class Implementation extends DImplementation implements Interface {

        private ListenerList<Listener> listeners;

        public Implementation(AVRDevice.Implementation avr) {

            listeners = new ListenerList<>();

            try {
                avr.getAVRConnection().send(new RCReceiveQuery(true));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            avr.getAVRConnection().addListener(
                    new PacketConnection.PacketTypeListener<RCReceiveResponse>() {

                        @Override
                        public void packetReceived(RCReceiveResponse packet, PacketReceivedEvent event) {
                            fireReceived(packet);
                        }
                    });
        }

        @Override
        public void addListener(Listener listener) {
            listeners.add(listener);
        }

        protected void fireReceived(RCReceiveResponse data) {
            listeners.fire(l -> l.received(data));
        }

        @Override
        public RCReceiver getDevice() {
            return (RCReceiver) super.getDevice();
        }

        @Override
        public void removeListener(Listener listener) {
            listeners.remove(listener);
        }

        public static class Builder extends
                SubModuleBuilder<RCReceiver, Implementation, AVRDevice, AVRDevice.Implementation> {

            @Override
            public Implementation buildFromModule(RCReceiver device, AVRDevice module, AVRDevice.Implementation moduleImpl) {
                return new Implementation(moduleImpl);
            }

        }

    }

}
