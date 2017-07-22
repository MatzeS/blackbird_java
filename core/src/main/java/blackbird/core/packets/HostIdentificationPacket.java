package blackbird.core.packets;

import blackbird.core.HostConnection;
import blackbird.core.HostDevice;
import blackbird.core.connection.Packet;
import blackbird.core.connection.PacketConnection;
import blackbird.core.connection.PacketReceivedEvent;
import blackbird.core.connection.exceptions.NoReplyException;

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
public class HostIdentificationPacket extends Packet {

    public static final long DEFAULT_HANDSHAKE_TIMEOUT = 2000;
    private static final long serialVersionUID = 5603371753276866796L;
    private boolean doAnswer;
    private HostDevice device;


    public HostIdentificationPacket() {

        doAnswer = true;
    }


    public HostIdentificationPacket(HostDevice device) {

        this.doAnswer = false;
        this.device = device;
    }


    public HostIdentificationPacket(HostDevice device, boolean doAnswer) {

        this(device);
        this.doAnswer = doAnswer;
    }


    public static HostDevice identify(HostConnection connection) throws IOException {

        return identify(connection, DEFAULT_HANDSHAKE_TIMEOUT);
    }


    public static HostDevice identify(HostConnection connection, long timeout) throws IOException {

        Object lock = new Object();

        final HostIdentificationPacket[] reply = new HostIdentificationPacket[1]; // TODO send and receive?
        connection.addListener(new PacketConnection.PacketTypeListener<HostIdentificationPacket>() {

            @Override
            public void packetReceived(HostIdentificationPacket packet, PacketReceivedEvent event) {

                reply[0] = packet;
                synchronized (lock) {
                    lock.notify();
                }
            }
        });

        connection.send(new HostIdentificationPacket());

        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (lock) {
            try {
                lock.wait(timeout);
            } catch (InterruptedException ignored) {
            }
        }

        if (reply[0] == null)
            throw new NoReplyException("no identifaction received");

        return reply[0].getDevice();
    }


    public HostDevice getDevice() {

        return device;
    }


    public boolean doAnswer() {

        return doAnswer;
    }

}
