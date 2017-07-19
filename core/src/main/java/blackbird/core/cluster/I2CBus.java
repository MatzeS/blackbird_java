package blackbird.core.cluster;

import java.util.ArrayList;
import java.util.List;

import blackbird.core.Domain;
import blackbird.core.device.I2CMaster;
import blackbird.core.device.I2CSlave;

public class I2CBus implements Domain {

    private I2CMaster master;
    private List<I2CSlave> slaves;

    public I2CBus() {
        slaves = new ArrayList<>();
    }

    public void addSlave(I2CSlave slave) {
        this.slaves.add(slave);
    }

    public I2CMaster getMaster() {
        return master;
    }

    public void setMaster(I2CMaster master) {
        this.master = master;
    }

    public List<I2CSlave> getSlaves() {
        return slaves;
    }

    public void removeSlave(I2CSlave slave) {
        this.slaves.remove(slave);
    }

}
