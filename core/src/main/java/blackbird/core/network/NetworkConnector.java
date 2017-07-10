package blackbird.core.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import blackbird.core.GenericConnector;
import blackbird.core.HostDevice;
import blackbird.core.exception.ImplementationFailedException;
import blackbird.core.connection.exceptions.NoConnectionException;
import blackbird.core.util.MultiException;

/**
 * A connector using the default java network communication through {@link Socket}s.
 * <p>
 * The connector creates a {@link ServerSocket} for incoming connections.
 */
public class NetworkConnector extends GenericConnector<HostDevice, NetworkPort> {

    public static final int DEFAULT_NETWORK_PORT = 1337;
    public static final int DEFAULT_TIMEOUT = 2000;

    private Logger logger = LogManager.getLogger(NetworkConnector.class);

    /**
     * The local network server instance accepting external connects.
     *
     * @see Server
     */
    private Server server;

    /**
     * Creates the connector and starts the server on port 1337;
     */
    public NetworkConnector() throws IOException {
        this(DEFAULT_NETWORK_PORT);
    }

    /**
     * Creates the connector and starts the server on the given port
     *
     * @param serverPort the server socket port
     */
    public NetworkConnector(int serverPort) throws IOException {
        startServer(serverPort);
    }

    /**
     * Presetting the timeout with the <code>DEFAULT_TIMEOUT</code>
     *
     * @param socketAddress the socket address to connect to
     * @return the connected connection
     * @throws NoConnectionException if the connect fails
     * @see NetworkConnector#connect(InetSocketAddress, int)
     */
    public NetworkConnection connect(InetSocketAddress socketAddress) throws NoConnectionException {
        return connect(socketAddress, DEFAULT_TIMEOUT);
    }

    /**
     * Connecting to the given address with a <code>Socket</code>.
     *
     * @param socketAddress the socket address to connect to
     * @param timeout       the connection timeout for the socket
     * @return the connected connection
     * @throws NoConnectionException if the socket connect fails
     */
    public NetworkConnection connect(InetSocketAddress socketAddress, int timeout) throws NoConnectionException {
        try {
            Socket socket = new Socket();
            socket.connect(socketAddress, timeout);
            return connect(socket);
        } catch (IOException e) {
            throw new NoConnectionException("could not connect", e);
        }
    }

    public NetworkConnection connect(Socket socket) throws IOException {
        return new NetworkConnection(socket);
    }

    public NetworkConnection connect(String host) throws NoConnectionException {
        return connect(new InetSocketAddress(host, DEFAULT_NETWORK_PORT));
    }

    @Override
    public NetworkConnection connect(HostDevice device, NetworkPort port) throws NoConnectionException {
        List<NoConnectionException> failures = new ArrayList<>();

        for (InetSocketAddress address : port.getSocketAddresses())
            try {
                return connect(address);
            } catch (NoConnectionException e) {
                failures.add(e);
            }

        String text = MultiException.generateMultipleExceptionText(failures);
        throw new NoConnectionException("not able to connect to any known socket address\n" + text);
    }

    private void startServer(int port) throws IOException {
        // start network server
        try {
            server = new Server(port);
        } catch (IOException e) {
            logger.error("failed to start local network server", e);
            throw e;
        }
    }

    /**
     * The class implements a local network server accepting connections to the port running on
     * allowing external devices to connect with the local blackbird instance.
     */
    public class Server extends ServerSocket implements Runnable {

        private Logger logger = LogManager.getLogger(Server.class);

        private Thread socketAcceptThread;

        protected Server(int port) throws IOException {
            super(port);

            socketAcceptThread = new Thread(this);
            socketAcceptThread.start();

            logger.info("opened network server on port " + this.getLocalPort());
        }

        @Override
        public void close() throws IOException {
            logger.trace("closing network server socket");
            super.close();
            try {
                socketAcceptThread.join();
            } catch (InterruptedException e) {
                logger.error("interrupted waiting for socket accept thread while closing server", e);
            }
            logger.info("closed network server socket successfully");
        }

        @Override
        public void run() {
            while (isBound() && !isClosed()) {
                try {
                    Socket socket = accept();
                    logger.info("accepting connection from " + NetworkConnection.getSocketText(socket));
                    NetworkConnection networkConnection = connect(socket);

                    blackbird.getHostDeviceImplementationBuilder().setupConnection(networkConnection);
                } catch (SocketException e) {
                    // caused by intentional close
                } catch (IOException e) {
                    logger.error("I/O exception on network server while accepting connection", e);
                } catch (ImplementationFailedException e) {
                    logger.error("Implementation failed on accepted network connection", e);
                }
            }
        }

    }

}