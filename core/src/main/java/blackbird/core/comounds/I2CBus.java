package blackbird.core.comounds;

import blackbird.core.Compound;
import blackbird.core.impl.I2CMaster;
import blackbird.core.impl.I2CSlave;

import java.util.ArrayList;
import java.util.List;

public class I2CBus extends Compound {

    private I2CMaster master;
    private List<I2CSlave> slaves;

    public I2CBus() {
        slaves = new ArrayList<>();
    }

    public void addSlave(I2CSlave slave) {
        this.slaves.add(slave);
    }

    public void removeSlave(I2CSlave slave) {
        this.slaves.remove(slave);
    }

    public void setMaster(I2CMaster master) {
        this.master = master;
    }

    public I2CMaster getMaster() {
        return master;
    }

    public List<I2CSlave> getSlaves() {
        return slaves;
    }

}
