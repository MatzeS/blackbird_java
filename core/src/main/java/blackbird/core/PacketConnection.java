package blackbird.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.ParameterizedType;
import java.util.function.Predicate;

import blackbird.core.events.CloseEvent;
import blackbird.core.events.PacketReceivedEvent;
import blackbird.core.exception.NoReplyException;
import blackbird.core.util.ListenerList;
import blackbird.core.util.TypeFilter;

/**
 * This class establishes the event driven {@link Packet} layer on top of the genuine {@link Connection}.
 * <p>
 * The extending class must still implement the input stream handling and decoding,
 * output stream encoding, event firing and
 * may properly ends the communication/session before closing the underlying connection.
 */
public abstract class PacketConnection extends Connection {

    private Logger logger = LogManager.getLogger(PacketConnection.class);

    /**
     * The delegation object for the connection.
     */
    private Connection componentConnection;
    private ListenerList<Listener> listeners;

    public PacketConnection(Connection componentConnection) {
        this.componentConnection = componentConnection;

        listeners = new ListenerList<>();
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    @Override
    public void close() throws IOException {
        componentConnection.close();
    }

    protected void fireClosed(CloseReason reason) {
        logger.trace("connection closed " + reason);
        CloseEvent e = new CloseEvent(this, reason);
        listeners.fire(l -> l.closed(e));
    }

    protected void firePacketReceived(Packet packet) {
        logger.trace("received " + packet.getClass().getSimpleName() + "/" + packet.hashCode());
        PacketReceivedEvent e = new PacketReceivedEvent(PacketConnection.this, packet);
        listeners.fire(l -> l.packetReceived(e));
    }

    @Override
    public CloseReason getCloseReason() {
        return componentConnection.getCloseReason();
    }

    @Override
    public void setCloseReason(CloseReason closeReason) {
        componentConnection.setCloseReason(closeReason);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return componentConnection.getInputStream();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return componentConnection.getOutputStream();
    }

    @Override
    public boolean isClosed() {
        return componentConnection.isClosed();
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    /**
     * Sends the packet through the connection (using the output stream).
     *
     * @param packet the packet to send
     * @throws IOException if an IO error occurs during send
     */
    public abstract void send(Packet packet) throws IOException;

    /**
     * <code>timeout</code> preset with
     * {@link PacketConnection.SynchronousListener#DEFAULT_TIMEOUT}
     *
     * @see PacketConnection#sendAndReceive(Packet, Predicate, long)
     */
    public <R extends Packet> R sendAndReceive(Packet request, Class<R> expectedPacketType) throws IOException {
        return sendAndReceive(request, expectedPacketType, SynchronousListener.DEFAULT_TIMEOUT);
    }

    /**
     * Filters to a given reply packet type.
     *
     * @see PacketConnection#sendAndReceive(Packet, Predicate, long)
     */
    public <R extends Packet> R sendAndReceive(Packet request, Class<R> expectedPacketType, long timeout)
            throws IOException {
        return (R) sendAndReceive(request, new TypeFilter<>(expectedPacketType), timeout);
    }

    /**
     * Sends the packet and returns the expected reply if received.<br/>
     *
     * @param request the request to send
     * @param filter  identifies expected reply packet object
     * @param timeout limits the wait time for the reply, 0 for infinite wait
     * @return the reply
     * @throws NoReplyException if no matching reply was received
     * @throws IOException      if an IO error occurs.
     */
    public Packet sendAndReceive(Packet request, Predicate<Packet> filter, long timeout) throws IOException {
        SynchronousListener listener = new SynchronousListener(filter);
        addListener(listener);
        send(request);
        listener.waitForReply(timeout);
        removeListener(listener);
        Packet reply = listener.getReplyPacket();
        if (reply == null)
            throw new NoReplyException();
        return reply;
    }

    /**
     * <code>timeout</code> preset with
     * {@link PacketConnection.SynchronousListener#DEFAULT_TIMEOUT}
     *
     * @see PacketConnection#sendAndReceive(Packet, Predicate, long)
     */
    public <R extends Packet> R sendAndReceive(Packet request, Class<R> expectedPacketType, Predicate<R> filter)
            throws IOException {
        return sendAndReceive(request, expectedPacketType, filter, SynchronousListener.DEFAULT_TIMEOUT);
    }

    /**
     * Combines expected packet type and additional filter.
     *
     * @see PacketConnection#sendAndReceive(Packet, Predicate, long)
     */
    public <R extends Packet> R sendAndReceive(Packet request, Class<R> expectedPacketType, Predicate<R> filter,
                                               long timeout) throws IOException {
        Predicate<Packet> typeFilter = new TypeFilter<>(expectedPacketType);
        Predicate<Packet> composed = p -> typeFilter.test(p) && filter.test((R) p);
        return (R) sendAndReceive(request, composed, timeout);
    }

    /**
     * <code>timeout</code> preset with
     * {@link PacketConnection.SynchronousListener#DEFAULT_TIMEOUT}
     *
     * @see PacketConnection#sendAndReceive(Packet, Predicate, long)
     */
    public Packet sendAndReceive(Packet request, Predicate<Packet> filter) throws IOException {
        return sendAndReceive(request, filter, SynchronousListener.DEFAULT_TIMEOUT);
    }

    /**
     * Filters incoming packages to answers to the request.
     *
     * @see PacketConnection#sendAndReceive(Packet, Predicate, long)
     */
    public Packet sendAndReceiveAnswer(Packet request, long timeout) throws IOException {
        return sendAndReceive(request, a -> a.getAnswerTo().equals(request.getID()), timeout);
    }

    /**
     * The listener interface for receiving connection events on a {@link PacketConnection}.
     */
    public interface Listener {

        /**
         * Invoked when the connection is closed for any reason.
         *
         * @param event the event, containing close reason and connection
         */
        void closed(CloseEvent event);

        /**
         * Invoked when the connection receives a packet.
         *
         * @param event the event, containing the received packet and connection
         */
        void packetReceived(PacketReceivedEvent event);

    }

    /**
     * The abstract listener class filtering {@link PacketReceivedEvent}s.
     * <p>
     * The receive events are filtered by the <code>test</code> method and only matching events
     * are passed to the <code>filteredPacketReceived</code> method.
     * <p>
     * The <code>closed</code> method is default overwritten with no operation.
     */
    public static abstract class FilteredPacketListener implements Listener, Predicate<PacketReceivedEvent> {

        @Override
        public void closed(CloseEvent event) {
        }

        public abstract void filteredPacketReceived(PacketReceivedEvent event);

        @Override
        public void packetReceived(PacketReceivedEvent event) {
            if (test(event))
                filteredPacketReceived(event);
        }

    }

    /**
     * The commonly used abstract listener class for a {@link PacketConnection}.
     * <p>
     * The received packets are filtered by the packet type allowing the <code>packetReceived</code> method to
     * provide a generic argument of the expected packet type.<br>
     * Additional filters can be applied overriding the <code>additionalTest</code> method.
     *
     * @param <T> filter to this packet type
     */
    public static abstract class PacketTypeListener<T> extends FilteredPacketListener {

        private TypeFilter<Packet> typeFilter;

        public PacketTypeListener() {
            Class<?> ownType = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass())
                    .getActualTypeArguments()[0];
            typeFilter = new TypeFilter<>(ownType);
        }

        public boolean additionalTest(T packet, PacketReceivedEvent event) {
            return true;
        }

        @Override
        public void filteredPacketReceived(PacketReceivedEvent event) {
            packetReceived((T) event.getPacket(), event);
        }

        public abstract void packetReceived(T packet, PacketReceivedEvent event);

        @Override
        public boolean test(PacketReceivedEvent event) {
            return typeFilter.test(event.getPacket()) && additionalTest((T) event.getPacket(), event);
        }

    }

    /**
     * This class is internally used for synchronous connection interaction on the asynchronous event driven implementation.
     *
     * @see PacketConnection#sendAndReceive(Packet, Predicate, long)
     */
    public class SynchronousListener implements Listener {

        public static final int DEFAULT_TIMEOUT = 2000;

        /**
         * Used as wait lock for the synchronous reply.
         */
        private final Object lock = new Object();
        private Predicate<Packet> filter = null;
        private Packet replyPacket;

        public SynchronousListener(Predicate<Packet> filter) {
            this.filter = filter;
        }

        @Override
        public void closed(CloseEvent event) {
        }

        /**
         * Gets the reply received.
         *
         * @return replyPacket
         */
        public Packet getReplyPacket() {
            return replyPacket;
        }

        @Override
        public void packetReceived(PacketReceivedEvent event) {
            if (!filter.test(event.getPacket()))
                return;

            if (replyPacket != null) {
                logger.warn("received more than one synchronous reply" + PacketConnection.this);
                return;
            }

            replyPacket = event.getPacket();
            synchronized (lock) {
                lock.notify();
            }
        }

        /**
         * <code>timeout = DEFAULT_TIMEOUT</code><br>
         *
         * @see SynchronousListener#waitForReply(long)
         */
        public boolean waitForReply() {
            return waitForReply(DEFAULT_TIMEOUT);
        }

        /**
         * Waits for a reply packet to arrive.
         * Blocks until a reply is received or timeout is reached.
         *
         * @param timeout the maximum blocking time
         * @return true, if a reply was received
         */
        public boolean waitForReply(long timeout) {
            if (replyPacket != null)
                return true;

            synchronized (lock) {
                try {
                    lock.wait(timeout);
                } catch (InterruptedException e) {
                    logger.warn("interrupted while waiting for reply");
                }
            }

            return replyPacket != null;
        }

    }

}
