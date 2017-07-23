package blackbird.core.builders;

import blackbird.core.Device;

public abstract class SubModuleBuilder<D extends Device,
        I,
        M extends Device,
        MI>
        extends ModuleBuilder<D, I, M, MI> {

    @Override
    public boolean filterDevice(D device) {

        if (!super.filterDevice(device))
            return false;

        return moduleType.isAssignableFrom(device.getSuperModule().getClass());
    }


    @Override
    public M getModule(D device) {

        return (M) device.getSuperModule().get();
    }

}
