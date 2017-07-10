package blackbird.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import blackbird.core.Device;

public class ConstructionPlan {

    private List<Device> needs;
    private List<Device> avoid;

    public ConstructionPlan() {

        needs = new ArrayList<>();
        avoid = new ArrayList<>();
    }

    public ConstructionPlan(Collection<Device> needs) {
        this();

        this.needs.addAll(needs);
    }

    public boolean isConstructable() {
        return needs.isEmpty();
    }



}
