package blackbird.core.builders;

import java.util.ArrayList;
import java.util.List;

import blackbird.core.Blackbird;
import blackbird.core.DImplementation;
import blackbird.core.DInterface;
import blackbird.core.Device;
import blackbird.core.util.BuildRequirement;

/**
 * DIBuilders are used by blackbird to create implementations for devices.
 * <p>
 * The primary <code>buildImplementation</code>Method consumes the device and the port.
 * Also the expected DInterface type is given. TODO
 * <p>
 */
public abstract class DIBuilder {

    protected Blackbird blackbird; //TODO extract to handle

    public abstract DImplementation build(Device device);

    public abstract boolean canBuild(Device device);

    public List<List<BuildRequirement>> getBuildRequirements(Device device) {
        List<List<BuildRequirement>> list = new ArrayList<>();

        if (canBuild(device)) {

            List<BuildRequirement> uniqueRequirements = getPossiblyUniqueRequirements(device);
            if (uniqueRequirements != null)
                list.add(uniqueRequirements);

        }

        return list;
    }

    public List<BuildRequirement> getPossiblyUniqueRequirements(Device device) {
        BuildRequirement requirement = getPossiblyUniqueSingleRequirement(device);

        if (requirement == null)
            return null;

        List<BuildRequirement> list = new ArrayList<>();
        list.add(requirement);
        return list;
    }

    public BuildRequirement getPossiblyUniqueSingleRequirement(Device device) {
        return null;
    }

    public abstract Class<DInterface> produces();

    public boolean produces(Class<?> type) {
        return produces().isAssignableFrom(type);
    }

    public void setBlackbird(Blackbird blackbird) {
        this.blackbird = blackbird;
    }

}