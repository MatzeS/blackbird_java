package blackbird.core.exception;

import blackbird.core.HostDevice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class OtherHostException extends HandlingException {

    private List<HostDevice> hosts = new ArrayList<>();


    public OtherHostException(HostDevice... hosts) {

        addHosts(hosts);
    }


    public OtherHostException(String message, HostDevice... hosts) {

        super(message);
        addHosts(hosts);
    }


    public OtherHostException(String message, Throwable cause, HostDevice... hosts) {

        super(message, cause);
        addHosts(hosts);
    }


    public OtherHostException(Throwable cause, HostDevice... hosts) {

        super(cause);
        addHosts(hosts);
    }


    public OtherHostException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, HostDevice... hosts) {

        super(message, cause, enableSuppression, writableStackTrace);
        addHosts(hosts);
    }


    public void addHosts(Collection<HostDevice> hosts) {

        this.hosts.addAll(hosts);
    }


    public void addHosts(HostDevice... hosts) {

        addHosts(Arrays.asList(hosts));
    }


    public List<HostDevice> getHosts() {

        return hosts;
    }
}
