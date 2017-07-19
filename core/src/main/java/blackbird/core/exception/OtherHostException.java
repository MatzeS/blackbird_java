package blackbird.core.exception;

import blackbird.core.HostDevice;

public class OtherHostException extends HandlingException {

    private HostDevice host;

    public OtherHostException(HostDevice host) {
        this.host = host;
    }

    public OtherHostException(String message, HostDevice host) {
        super(message);
        this.host = host;
    }

    public OtherHostException(String message, Throwable cause, HostDevice host) {
        super(message, cause);
        this.host = host;
    }

    public OtherHostException(Throwable cause, HostDevice host) {
        super(cause);
        this.host = host;
    }

    public OtherHostException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, HostDevice host) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.host = host;
    }

    public HostDevice getHost() {
        return host;
    }

}
