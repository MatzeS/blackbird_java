package blackbird.core.network;

import blackbird.core.GenericConnector;
import blackbird.core.HostDevice;
import blackbird.core.connection.Connection;
import blackbird.core.connection.exceptions.NoConnectionException;
import blackbird.core.util.MultiException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * Consumes {@link NetworkPort}s to establish TCP/IP sockets.
 * Server is setup on port 1337 / DEFAULT_NETWORK_PORT.
 */
public class NetworkConnector extends GenericConnector<HostDevice, NetworkPort> {

    public static final int DEFAULT_NETWORK_PORT = 1337;
    public static final int DEFAULT_TIMEOUT = 2000;

    private Logger logger = LogManager.getLogger(NetworkConnector.class);

    private Server server;

    /**
     * uses DEFAULT_NETWORK_PORT
     *
     * @param serverPort the server socket port
     * @see NetworkConnector#NetworkConnector(int)
     * /
     * public NetworkConnector() throws IOException {
     * this(DEFAULT_NETWORK_PORT);
     * }
     * <p>
     * /**
     * Creates the connector and starts the server on the given port
     */
    public NetworkConnector(int serverPort) throws IOException {
        startServer(serverPort);
    }

    public Server getServer() {
        return server;
    }

    /**
     * Presetting the timeout with the <code>DEFAULT_TIMEOUT</code>
     *
     * @param socketAddress the socket address to connect to
     * @return the connected connection
     * @see NetworkConnector#connect(InetSocketAddress, int)
     */
    public NetworkConnection connect(InetSocketAddress socketAddress) {
        return connect(socketAddress, DEFAULT_TIMEOUT);
    }

    /**
     * Connects to the given address
     *
     * @param socketAddress the socket address to connect to
     * @param timeout       the connection timeout for the socket
     * @return the connected connection
     */
    public NetworkConnection connect(InetSocketAddress socketAddress, int timeout) {
        try {
            Socket socket = new Socket();
            socket.connect(socketAddress, timeout);
            return connect(socket);
        } catch (IOException e) {
            throw new NoConnectionException("IOException during TCP connect", e);
        }
    }

    private NetworkConnection connect(Socket socket) throws IOException {
        return new NetworkConnection(socket);
    }

    /**
     * Connects to an address using the DEFAULT_NETWORK_PORT
     *
     * @param host IP address
     * @return connection targeting the given address
     */
    public NetworkConnection connect(String host) {
        return connect(new InetSocketAddress(host, DEFAULT_NETWORK_PORT));
    }

    @Override
    public Connection connectToPort(HostDevice device, NetworkPort port) {
        List<NoConnectionException> failures = new ArrayList<>();

        for (InetSocketAddress address : port.getSocketAddresses())
            try {
                return connect(address);
            } catch (NoConnectionException e) {
                failures.add(e);
            }

        throw new NoConnectionException("could not connect to any socket address\n" +
                MultiException.generateMultipleExceptionText(failures));
    }

    private void startServer(int port) throws IOException {
        try {
            server = new Server(port);
        } catch (IOException e) {
            logger.error("failed to start local network server", e);
            throw e;
        }
    }


    /**
     * Sets up a local TCP/IP network server socket
     * allowing external devices to connect to the local host/blackbird.
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
                    logger.info("accepting connection from " +
                            NetworkConnection.getSocketText(socket));

                    NetworkConnection networkConnection = connect(socket);
                    acceptConnection(networkConnection);

                } catch (SocketException e) {
                    // caused by intentional close //TODO
                } catch (IOException e) {
                    logger.error("I/O exception on network server while accepting connection", e);
                }
            }
        }

    }

}