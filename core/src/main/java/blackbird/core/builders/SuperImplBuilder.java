package blackbird.core.builders;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import blackbird.core.Device;
import blackbird.core.exception.BFException;
import blackbird.core.util.Generics;

public abstract class SuperImplBuilder<D extends Device,
        I extends SI,
        SI> extends GenericBuilder<D, I> {

    protected Class<SI> superImplType;

    private Lock listLock;
    private List<Device> recursionBlock;

    public SuperImplBuilder() {
        superImplType = (Class<SI>)
                Generics.getGenericArgument(this, 2);

        listLock = new ReentrantLock();
        recursionBlock = new ArrayList<>();
    }

    public abstract I buildFromSuperImpl(D device, SI superImpl);

    @Override
    public I buildGeneric(D device) throws BFException {

        listLock.lock();
        boolean locked = recursionBlock.contains(device);
        listLock.unlock();

        if (locked)
            throw new BFException("recursion blocked");

        listLock.lock();
        recursionBlock.add(device);
        listLock.unlock();

        SI superImpl = blackbird.implementDevice(device, superImplType);

        listLock.lock();
        recursionBlock.remove(device);
        listLock.unlock();

        return buildFromSuperImpl(device, superImpl);
    }

}
