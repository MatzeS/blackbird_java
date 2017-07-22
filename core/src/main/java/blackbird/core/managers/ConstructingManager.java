package blackbird.core.managers;

import blackbird.core.*;
import blackbird.core.builders.DIBuilder;
import blackbird.core.exception.BFException;
import blackbird.core.exception.OtherHostException;
import blackbird.core.util.ConstructionPlan;
import com.google.common.base.Objects;

import java.util.*;
import java.util.stream.Collectors;

public abstract class ConstructingManager extends DeviceManager {

    protected final Stack<DInterface> implementationStack;

    private List<Args> args;


    public ConstructingManager(Blackbird blackbird, Device device) {

        super(blackbird, device);
        args = new ArrayList<>();

        implementationStack = new Stack<>();
    }


    @Override
    protected Optional<DInterface> getHandle() {

        Optional superHandle = super.getHandle();
        if (superHandle.isPresent())
            return superHandle;
        else
            return getImplementation();
    }


    public boolean isLocallyImplemented() {

        return getImplementation().isPresent();
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


    public ConstructionPlan constructHandle(ConstructionPlan plan) {

        Device device = plan.getDevice();

        List<HostDevice> otherHosts = new ArrayList<>();

        //logger.info("construction " + device + " to " + plan.getType());

        try {
            build(plan.getType());

            plan.setSucceeded(blackbird.getLocalDevice());

            return plan;
        } catch (OtherHostException e) {
            otherHosts.add(e.getHost());
        }

        plan.addFailed(blackbird.getLocalDevice());
        plan.addPossibleHosts(otherHosts);

        if (plan.getPossible().isEmpty())
            return plan;

        ConstructionPlan finalPlan = plan;
        List<Map.Entry<HostDevice, HostDevice.Interface>> possibleHosts =
                blackbird.getAvailableHostDeviceInterfaces().entrySet().stream()
                        .filter(e -> finalPlan.getPossible().contains(e.getKey()))
                        .collect(Collectors.toList());
        for (Map.Entry<HostDevice, HostDevice.Interface> possibleHost : possibleHosts)
            plan = possibleHost.getValue().constructHandle(plan);

        return plan;
    }


    public DImplementation build(Class<?> type) {

//        logger.info(device + " BUILD");
        for (DIBuilder builder : blackbird.getBuilders())
            if (!args.contains(new Args(device, type, builder)))
                if (builder.canBuild(device) && builder.produces(type)) { // pre filter
//                    logger.info(device + " attempting " + builder);
                    try {
                        //TODO remove
                        builder.setImplementationReference(new DIBuilder.ImplementationReference() {
                            @Override
                            public <T> T implement(Device device, Class<T> type) {

                                System.out.println("implementing: " + device + "/" + type);
                                return blackbird.interfaceDevice(device, type);
                            }
                        });


                        Args arg = new Args(device, type, builder);

                        args.add(arg);
                        DImplementation impl = builder.build(device);
                        args.remove(arg);


                        impl.setDevice(device);
                        impl.setHost(blackbird.getLocalDevice());

                        //TODO call after construction/load state
                        //TODO call devicemanager postconstruction
                        //TODO destroy stack down

                        getImplementationStack().push(impl);

                        return impl;

                    } catch (Exception ignored) {
                        System.err.print(builder + " failed on " + device + " \n \n");
                        ignored.printStackTrace();
                        System.err.print(" \n \n");
                    }

                } //else
        //System.out.println(builder + ":" + builder.canBuild(device) + "/" + builder.produces(plan.getType()));

        throw new BFException("could not build");
    }


    public static class Args {
        Device device;
        Class<?> type;
        DIBuilder builder;


        public Args(Device device, Class<?> type, DIBuilder builder) {

            this.device = device;
            this.type = type;
            this.builder = builder;
        }


        @Override
        public boolean equals(Object o) {

            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Args args = (Args) o;
            return Objects.equal(device, args.device) &&
                    Objects.equal(type, args.type) &&
                    Objects.equal(builder, args.builder);
        }


        @Override
        public int hashCode() {

            return Objects.hashCode(device, type, builder);
        }
    }

}
