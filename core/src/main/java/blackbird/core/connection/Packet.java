package blackbird.core.connection;

import java.io.Serializable;
import java.util.UUID;

/**
 * The root class for communication over the {@see blackbird.core.connection.PacketConnection}.
 * <p>
 * Besides the obviously necessary <code>Serializable</code> property a packet has a unique ID.
 * As well it might provides a <code>answerTo</code> ID indicating the packet is generated and send as
 * response to a the original packet bound to the <code>answerTo</code> ID.
 * This can be used for a reliable ping-pong communication pattern.
 * Therefor either set the answerTo ID manually by the <code>setAnswerTo</code> method or use the
 * {@link PacketReceivedEvent#sendAnswer(Packet)} on the request event.
 * <p>
 * <p>
 * Note: Default Java serialization is crucial for network communication (ObjectInput/OutputStream).
 *
 * @see PacketConnection#send(Packet)
 * @see PacketConnection#sendAndReceiveAnswer(Packet, long)
 * @see PacketReceivedEvent#sendAnswer(Packet)
 */
public class Packet implements Serializable {

    private static final long serialVersionUID = -133769312647656351L;

    private UUID ID = UUID.randomUUID();

    /**
     * The ID of the package this packet is an answer to.
     */
    private UUID answerTo;

    public Packet(UUID answerTo) {
        this.answerTo = answerTo;
    }

    public Packet() {
    }

    public UUID getAnswerTo() {
        return answerTo;
    }

    public void setAnswerTo(UUID answerTo) {
        this.answerTo = answerTo;
    }

    public UUID getID() {
        return ID;
    }

}
