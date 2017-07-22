package blackbird.core.packets;

import blackbird.core.HostConnection;
import blackbird.core.HostDevice;
import blackbird.core.connection.Packet;

import java.io.IOException;

/**
 * The handshake packet is used to identify
 * the device on the remote side of the connection.
 * <p>
 * In general a handshake should be answered with the own
 * handshake to identify each other (this packet is used as request/query and response).
 * If the {@code doNotAnswer} flag is set, the receiver might not answer.
 * Use this flag to avoid endless ping pong handshakes.
 */
public class HostIdentification {

    public static final long DEFAULT_HANDSHAKE_TIMEOUT = 2000;


    public static HostDevice identify(HostConnection connection) throws IOException {

        return identify(connection, 5, DEFAULT_HANDSHAKE_TIMEOUT / 5);
    }


    public static HostDevice identify(HostConnection connection, long retries, long timeout) throws IOException {

        return connection
                .sendAndReceiveAnswer(new Request(), 5, Reply.class, timeout)
                .getDevice();

    }


    public static class Request extends Packet {

    }

    public static class Reply extends Packet {
        private HostDevice device;


        public Reply(HostDevice device) {


            this.device = device;
        }


        public HostDevice getDevice() {

            return device;
        }
    }

}
