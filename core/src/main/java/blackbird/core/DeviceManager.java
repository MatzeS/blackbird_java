package blackbird.core;

import java.util.Optional;
import java.util.Stack;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class DeviceManager {

    /**
     * This lock ensures there cannot be two threads
     * performing some interfacing or implementation action at the same time.
     */
    protected final Lock lock = new ReentrantLock();
    protected final Device device;
    protected final Stack<DInterface> implementationStack;
    protected BlackbirdImpl blackbird; //TODO by getter or chage at all
    protected DInterface remoteHandle;

    public DeviceManager(BlackbirdImpl blackbird, Device device) {
        this.blackbird = blackbird;
        checkNotNull(device);

        this.device = device;
        implementationStack = new Stack<>();
    }

    protected abstract Object extendHandle(Class<?> type);

    protected Device getDevice() {
        return device;
    }

    protected Optional<DInterface> getHandle() {
        if (getRemoteHandle().isPresent())
            return getRemoteHandle();
        else
            return getImplementation();
    }

    protected Object getHandle(Class<?> type) {
        getLock().lock();
        try {
            if (isHandleSatisfying(type))
                return getHandle().get();
            else if (!isHandleExtensible(type))
                throw new RuntimeException("current device handle is not extensible for expected type");

            return extendHandle(type);
        } finally {
            getLock().unlock();
        }
    }

    protected Optional<DInterface> getImplementation() {
        if (getImplementationStack().isEmpty())
            return Optional.empty();
        else
            return Optional.of(getImplementationStack().peek());
    }

    protected Stack<DInterface> getImplementationStack() {
        return this.implementationStack;
    }

    protected Lock getLock() {
        return this.lock;
    }

    protected Optional<DInterface> getRemoteHandle() {
        return Optional.ofNullable(remoteHandle);
    }

    protected boolean isHandleExtensible(Class<?> type) {
        if (!getHandle().isPresent())
            return true;

        return getHandle()
                .filter(i -> i.getClass().isAssignableFrom(type))
                .isPresent();
    }

    protected boolean isHandleSatisfying(Class<?> type) {
        return getHandle()
                .filter(i -> type.isAssignableFrom(i.getClass()))
                .isPresent();
    }

    protected boolean isLocallyImplemented() {
        return getImplementation().isPresent();
    }

    protected void lock() {
        getLock().lock();
    }

    protected boolean tryLock() {
        return getLock().tryLock();
    }

    protected boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return getLock().tryLock(time, unit);
    }

    protected void unlock() {
        getLock().unlock();
    }


}
