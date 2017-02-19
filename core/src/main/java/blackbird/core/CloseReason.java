package blackbird.core;

/**
 * Enum indicating the reasons a connection has been closed for.
 */
public enum CloseReason {

    /**
     * Indicates the connection was closed by the other side.
     */
    BY_OTHER_SIDE,

    /**
     * Indicates the connection was closed intentionally from this side.
     */
    INTENTIONALLY,

    /**
     * Indicates the connection was closed due to a connection or communication error or corruption.
     */
    FAILURE
}
