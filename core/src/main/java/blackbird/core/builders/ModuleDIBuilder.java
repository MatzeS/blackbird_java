package blackbird.core.builders;

import blackbird.core.Device;
import blackbird.core.exception.BNFException;
import blackbird.core.util.BuildRequirement;
import blackbird.core.util.Generics;

public abstract class ModuleDIBuilder<D extends Device,
        I,
        M extends Device,
        MI>
        extends GenericBuilder<D, I> {

    protected Class<M> moduleType;
    protected Class<MI> moduleInterfaceType;

    public ModuleDIBuilder() {
        moduleType = (Class<M>)
                Generics.getGenericArgument(this, 2);
        moduleInterfaceType = (Class<MI>)
                Generics.getGenericArgument(this, 3);
    }

    public abstract I buildFromModule(D device, M module, MI moduleImpl);

    @Override
    public I buildGeneric(D device) {
        Device module = getModule(device);
        if (module == null)
            throw new BNFException("device has not expected submodule");
        if (!moduleType.isAssignableFrom(module.getClass()))
            throw new BNFException("module of unexpected type");

        MI moduleImpl = blackbird.implementDevice(module, moduleInterfaceType);

        return buildFromModule(device, (M) module, moduleImpl);
    }

    public abstract Device getModule(D device);

    @Override
    public BuildRequirement getPossiblyUniqueSingleRequirement(Device device) {
        return new BuildRequirement(getModule((D) device), moduleInterfaceType);
    }

}