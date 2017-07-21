package blackbird.core.network;

import blackbird.core.connection.Connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Connection implementation for communication via TCP/IP sockets.
 */
public class NetworkConnection extends Connection {

    private Socket socket;

    /**
     * Creates a connection using a connected socket
     *
     * @param socket the socket this connection is based on
     * @throws IOException if the socket is either closed, unbound or not connected.
     */
    public NetworkConnection(Socket socket) throws IOException {
        if (socket.isClosed() || !socket.isBound() || !socket.isConnected())
            throw new IOException("NetworkConnection expects a not closed, bound and connected socket");

        this.socket = socket;
    }

    public static String getSocketText(Socket socket) {
        return socket.getRemoteSocketAddress().toString().substring(1);
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return socket.getInputStream();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return socket.getOutputStream();
    }

    public Socket getSocket() {
        return socket;
    }

    public String getSocketRemoteAddress() {
        return socket.getRemoteSocketAddress().toString().substring(1);
    }

    @Override
    public String toString() {
        return "NETCON_" + hashCode() + "[" + getSocketRemoteAddress() + "]";
    }

}
