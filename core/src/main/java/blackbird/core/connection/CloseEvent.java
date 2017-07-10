package blackbird.core.connection;

import java.util.EventObject;

import blackbird.core.connection.CloseReason;
import blackbird.core.connection.Connection;

/**
 * An event providing information the connection closure.
 *
 * @see CloseReason
 */
public class CloseEvent extends EventObject {

    private static final long serialVersionUID = 1L;
    private CloseReason closeReason;

    public CloseEvent(Connection source, CloseReason closeReason) {
        super(source);
        this.closeReason = closeReason;
    }

    public boolean closedByFailure() {
        return closeReason == CloseReason.FAILURE;
    }

    public boolean closedByOtherSide() {
        return closeReason == CloseReason.BY_OTHER_SIDE;
    }

    public boolean closedIntentionally() {
        return closeReason == CloseReason.INTENTIONALLY;
    }

    public CloseReason getCloseReason() {
        return closeReason;
    }

    @Override
    public Connection getSource() {
        return (Connection) super.getSource();
    }

}
