package blackbird.core.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class defines the most basic connection,
 * only providing an input/output stream and a close state.
 */
public abstract class Connection {

    /**
     * Closing the connection sets this field storing the reason and indicating the connection is closed.<br>
     * As long as this field is null, the connection is either not yet connected/opened or open.
     */
    private CloseReason closeReason;

    /**
     * Closes the connection.<br>
     * Make sure this stops all running threads and eliminates the substructure as far as possible. <br>
     * Although an IOException can be thrown there should be no cleanup necessary after calling this method.
     *
     * @throws IOException should be rarely used as indicator
     */
    public abstract void close() throws IOException;

    public CloseReason getCloseReason() {
        return closeReason;
    }

    protected void setCloseReason(CloseReason closeReason) {
        this.closeReason = closeReason;
    }

    public abstract InputStream getInputStream() throws IOException;

    public abstract OutputStream getOutputStream() throws IOException;

    public boolean isClosed() {
        return closeReason != null;
    }

}
