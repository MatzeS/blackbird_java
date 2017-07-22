package blackbird.core.util;

import blackbird.core.Device;
import blackbird.core.HostDevice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ConstructionPlan implements Serializable {

    private Device device;
    private Class<?> type;
    private HostDevice succeeded;
    private List<HostDevice> failed;
    private List<HostDevice> possible;


    public ConstructionPlan(Device device, Class<?> type) {

        this.device = device;
        this.type = type;

        possible = new ArrayList<>();
        failed = new ArrayList<>();
    }


    public void addFailed(HostDevice host) {

        failed.add(host);
    }


    public void addPossibleHosts(List<HostDevice> otherHosts) {

        otherHosts.stream()
                .filter(h -> !failed.contains(h) && !possible.contains(h))
                .forEach(h -> possible.add(h));
    }


    public Device getDevice() {

        return device;
    }


    public List<HostDevice> getFailed() {

        return failed;
    }


    public List<HostDevice> getPossible() {

        return possible;
    }


    public HostDevice getSucceeded() {

        return succeeded;
    }


    public void setSucceeded(HostDevice succeeded) {

        this.succeeded = succeeded;
    }


    public Class<?> getType() {

        return type;
    }


    public boolean succeeded() {

        return succeeded != null;
    }

}
