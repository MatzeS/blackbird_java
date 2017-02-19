package blackbird.core.events;

import blackbird.core.Packet;
import blackbird.core.PacketConnection;

import java.io.IOException;
import java.util.EventObject;

/**
 * An event which indicates a packet was received by a connection.
 */
public class PacketReceivedEvent extends EventObject {

    private static final long serialVersionUID = 1L;

    private Packet packet;

    public PacketReceivedEvent(PacketConnection source, Packet packet) {
        super(source);
        this.packet = packet;
    }

    public Packet getPacket() {
        return packet;
    }

    @Override
    public PacketConnection getSource() {
        return (PacketConnection) super.getSource();
    }

    /**
     * Send an answer to the received packet.
     *
     * @param answer the answer to be send
     * @throws IOException if an IO error occurs during send
     */
    public void sendAnswer(Packet answer) throws IOException {
        answer.setAnswerTo(packet.getID());
        getSource().send(answer);
    }

}
