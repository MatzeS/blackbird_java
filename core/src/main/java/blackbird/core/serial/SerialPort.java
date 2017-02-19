package blackbird.core.serial;

import blackbird.core.CloseReason;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class abstracts SerialPorts defined in concrete librarys (RXTX/PureJavaComm).
 * <p>
 * A common SerialPort interface implemented by every serial port implementation
 * abstracting different serial port libraries and implementations to a common interface.
 * <p>
 * Note: Primarily defined to be available on all -
 * even not directly serial interfacing - platforms (e.g. android).
 */
public interface SerialPort {

    void addListener(Listener listener);

    void close();

    InputStream getInputStream() throws IOException;

    String getName();

    OutputStream getOutputStream() throws IOException;

    void removeListener(Listener listener);

    interface Listener {

        void closed(CloseReason closeReason);

        void dataAvailable();

    }

}
