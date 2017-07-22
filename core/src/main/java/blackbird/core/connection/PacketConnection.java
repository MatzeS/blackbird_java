package blackbird.core.connection;

import blackbird.core.connection.exceptions.NoReplyException;
import blackbird.core.util.ListenerList;
import blackbird.core.util.TypeFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.ParameterizedType;
import java.util.function.Predicate;

/**
 * This class establishes the event driven {@link Packet} layer on top of the genuine {@link Connection}.
 * <p>
 * The extending class must still implement the input stream handling and decoding,
 * output stream encoding, event firing and
 * may properly ends the communication/session before closing the underlying connection.
 */
public abstract class PacketConnection extends Connection {

    public static final int DEFAULT_TIMEOUT = 2000;
    public static final int DEFAULT_RETRIES = 1;
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


    private static Predicate<Packet> getAnswerFilter(Packet request) {

        return a -> a.isAnswerTo(request);
    }


    private static Predicate<Packet> getTypeFilter(Class<?> type) {

        return new TypeFilter<>(type);
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
    public <R extends Packet> R sendAndReceive(Packet request,
                                               Class<R> expectedPacketType)
            throws IOException {

        return sendAndReceive(
                request,
                expectedPacketType,
                DEFAULT_TIMEOUT
        );
    }


    /**
     * Filters to a given reply packet type.
     *
     * @see PacketConnection#sendAndReceive(Packet, Predicate, long)
     */
    public <R extends Packet> R sendAndReceive(Packet request,
                                               Class<R> expectedPacketType,
                                               long timeout)
            throws IOException {

        return (R) sendAndReceive(
                request,
                DEFAULT_RETRIES,
                getTypeFilter(expectedPacketType),
                timeout
        );
    }


    public Packet sendAndReceive(Packet request,
                                 long timeout)
            throws IOException {

        return sendAndReceive(
                request,
                DEFAULT_RETRIES,
                p -> true, timeout);
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
    public Packet sendAndReceive(Packet request,
                                 int retries,
                                 Predicate<Packet> filter,
                                 long timeout)
            throws IOException {

        SynchronousListener listener = new SynchronousListener(filter);
        addListener(listener);
        Packet reply = null;

        for (int i = 0; i < retries && reply == null; i++) {
            send(request);
            listener.waitForReply(timeout);
            reply = listener.getReplyPacket();
        }

        removeListener(listener);

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
    public <R extends Packet> R sendAndReceive(Packet request,
                                               Class<R> expectedPacketType,
                                               Predicate<R> filter)
            throws IOException {

        return sendAndReceive(
                request,
                expectedPacketType,
                filter,
                DEFAULT_TIMEOUT
        );
    }


    /**
     * Combines expected packet type and additional filter.
     *
     * @see PacketConnection#sendAndReceive(Packet, Predicate, long)
     */
    public <R extends Packet> R sendAndReceive(Packet request,
                                               Class<R> expectedPacketType,
                                               Predicate<R> filter,
                                               long timeout)
            throws IOException {

        Predicate<Packet> typeFilter = getTypeFilter(expectedPacketType);
        Predicate<Packet> combined = p -> typeFilter.test(p) && filter.test((R) p);
        return (R) sendAndReceive(
                request,
                DEFAULT_RETRIES,
                combined,
                timeout
        );
    }


    /**
     * <code>timeout</code> preset with
     * {@link PacketConnection.SynchronousListener#DEFAULT_TIMEOUT}
     *
     * @see PacketConnection#sendAndReceive(Packet, Predicate, long)
     */
    public Packet sendAndReceive(Packet request,
                                 Predicate<Packet> filter)
            throws IOException {

        return sendAndReceive(
                request,
                DEFAULT_RETRIES,
                filter,
                DEFAULT_TIMEOUT
        );
    }


    public <R extends Packet> R sendAndReceiveAnswer(Packet request,
                                                     int retries,
                                                     Class<R> expectedPacketType,
                                                     Predicate<R> filter,
                                                     long timeout)
            throws IOException {

        Predicate<Packet> typeFilter = getTypeFilter(expectedPacketType);
        Predicate<Packet> combined = p -> typeFilter.test(p) && filter.test((R) p);

        return (R) sendAndReceiveAnswer(
                request,
                retries,
                combined,
                timeout
        );
    }


    public <R extends Packet> R sendAndReceiveAnswer(Packet request,
                                                     int retries,
                                                     Class<R> expectedPacketType,
                                                     Predicate<R> filter)
            throws IOException {

        return sendAndReceiveAnswer(
                request,
                retries,
                expectedPacketType,
                filter,
                DEFAULT_TIMEOUT
        );
    }


    public <R extends Packet> R sendAndReceiveAnswer(Packet request,
                                                     int retries,
                                                     Class<R> expectedPacketType,
                                                     long timeout)
            throws IOException {

        return (R) sendAndReceiveAnswer(
                request,
                retries,
                getTypeFilter(expectedPacketType),
                timeout
        );
    }


    public <R extends Packet> R sendAndReceiveAnswer(Packet request,
                                                     int retries,
                                                     Class<R> expectedPacketType)
            throws IOException {

        return sendAndReceiveAnswer(
                request,
                retries,
                expectedPacketType,
                DEFAULT_TIMEOUT
        );
    }


    public <R extends Packet> R sendAndReceiveAnswer(Packet request,
                                                     Class<R> expectedPacketType)
            throws IOException {

        return sendAndReceiveAnswer(
                request,
                DEFAULT_RETRIES,
                expectedPacketType
        );
    }


    public Packet sendAndReceiveAnswer(Packet request,
                                       int retries,
                                       long timeout)
            throws IOException {

        return sendAndReceiveAnswer(
                request,
                retries,
                p -> true,
                timeout);
    }


    public Packet sendAndReceiveAnswer(Packet request,
                                       int retries,
                                       Predicate<Packet> filter,
                                       long timeout)
            throws IOException {

        return sendAndReceive(
                request,
                retries,
                getAnswerFilter(request).and(filter),
                timeout
        );
    }


    public Packet sendAndReceiveAnswer(Packet request,
                                       int retries,
                                       Predicate<Packet> filter)
            throws IOException {

        return sendAndReceiveAnswer(
                request,
                retries,
                filter,
                DEFAULT_TIMEOUT
        );
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
        default void closed(CloseEvent event) {

        }

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
    public interface FilteredPacketListener extends Listener, Predicate<PacketReceivedEvent> {

        void filteredPacketReceived(PacketReceivedEvent event);

        @Override
        default void packetReceived(PacketReceivedEvent event) {

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
    public static abstract class PacketTypeListener<T> implements FilteredPacketListener {

        private Predicate<Packet> typeFilter;


        public PacketTypeListener() {

            Class<T> ownType = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass())
                    .getActualTypeArguments()[0];
            typeFilter = getTypeFilter(ownType);
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

    public static abstract class AnswerListener<T> extends PacketTypeListener<T> {

        public abstract Packet answer(T packet);


        @Override
        public void packetReceived(T packet, PacketReceivedEvent event) {

            try {
                event.sendAnswer(answer(packet));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * This class is internally used for synchronous connection interaction on the asynchronous event driven implementation.
     *
     * @see PacketConnection#sendAndReceive(Packet, Predicate, long)
     */
    public class SynchronousListener implements Listener {


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
