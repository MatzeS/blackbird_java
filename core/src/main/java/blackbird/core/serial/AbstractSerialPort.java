package blackbird.core.serial;

import blackbird.core.util.ListenerList;

public abstract class AbstractSerialPort implements SerialPort {

    protected ListenerList<SerialPort.Listener> listeners;

    public AbstractSerialPort() {
        listeners = new ListenerList<>();
    }

    @Override
    public void addListener(SerialPort.Listener listener) {
        listeners.add(listener);
    }

    protected void fireDataAvailable() {
        listeners.forEach(SerialPort.Listener::dataAvailable);
    }

    @Override
    public void removeListener(SerialPort.Listener listener) {
        listeners.remove(listener);
    }

}
