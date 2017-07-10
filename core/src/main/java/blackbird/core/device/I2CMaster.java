package blackbird.core.device;

import java.io.IOException;

import blackbird.core.DImplementation;
import blackbird.core.DInterface;
import blackbird.core.Device;
import blackbird.core.util.ListenerList;

public class I2CMaster extends Device {

    public interface Interface extends DInterface {

        void addListener(Listener listener);

        byte readRegister(int slaveAddress, byte registerAddress) throws IOException;

        //TODO
        //emitStartCondition() STOP// more basic functionalities and thread safety

        byte[] readRegisters(int slaveAddress, byte registerAddress, int num) throws IOException;

        void removeListener(Listener listener);

        void writeRegister(int slaveAddress, byte registerAddress, byte value) throws IOException;

        void writeRegisters(int slaveAddress, byte registerAddress, byte[] data) throws IOException;


    }

    public interface Listener {

        void commonInterruptOccurred();
    }

    public static abstract class Implementation extends DImplementation<I2CMaster>
            implements Interface {

        private ListenerList<Listener> listeners;

        public Implementation() {
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

        @Override
        public byte[] readRegisters(int slaveAddress, byte registerAddress, int num) throws IOException {
            if (num <= 0)
                throw new IndexOutOfBoundsException("Can not read zero or a negative number of registers.");

            byte[] data = new byte[num];
            for (int i = 0; i < num; i++)
                data[i] = readRegister(slaveAddress, (byte) (registerAddress + num));
            return data;
        }

        @Override
        public void writeRegisters(int slaveAddress, byte registerAddress, byte[] data) throws IOException {
            if (data == null)
                throw new IllegalArgumentException("data can not be null");

            for (int i = 0; i < data.length; i++)
                writeRegister(slaveAddress, (byte) (registerAddress + i), data[i]);
        }

    }

}
