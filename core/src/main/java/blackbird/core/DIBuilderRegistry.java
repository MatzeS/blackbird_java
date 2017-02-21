package blackbird.core;

import java.util.ArrayList;
import java.util.List;

import blackbird.core.impl.AppleRemote;
import blackbird.core.impl.AukeyRemote;
import blackbird.core.impl.AukeySocket;
import blackbird.core.impl.LightController;
import blackbird.core.impl.MCP23017;
import blackbird.core.impl.avr.AVRDevice;
import blackbird.core.impl.avr.AVRI2CSlaveImplementation;
import blackbird.core.impl.avr.DS18B20;
import blackbird.core.impl.avr.InfraredReceiverImplementation;
import blackbird.core.impl.avr.RCReceiver;
import blackbird.core.impl.avr.RCSocketImplementation;

/**
 * Register all DIBuilders to this registry so they can be used by blackbird.
 * <p>
 * TODO solve this via annotations/reflection
 */
public class DIBuilderRegistry {

    private static DIBuilderRegistry ourInstance = new DIBuilderRegistry();
    private List<DIBuilder> builderList;

    private DIBuilderRegistry() {
        builderList = new ArrayList<>();

        builderList.add(new InfraredReceiverImplementation.Builder());
        builderList.add(new RCReceiver.Implementation.Builder());
        builderList.add(new AppleRemote.Implementation.Builder());
        builderList.add(new AukeyRemote.Implementation.Builder());
        builderList.add(new AukeySocket.Implementation.Builder());
        builderList.add(new MCP23017.Implementation.Builder());
        builderList.add(new RCSocketImplementation.Builder());
        builderList.add(new LightController.Implementation.Builder());
        builderList.add(new AVRDevice.Implementation.Builder());
        builderList.add(new AVRI2CSlaveImplementation.Builder());
        builderList.add(new blackbird.core.impl.avr.RCSocketImplementation.Builder());
        builderList.add(new blackbird.core.impl.avr.RCReceiver.Implementation.Builder());
        builderList.add(new DS18B20.Implementation.Builder());
        builderList.add(new blackbird.core.impl.avr.InfraredReceiverImplementation.Builder());
        builderList.add(new blackbird.core.impl.MPR121.Implementation.Builder());
        builderList.add(new blackbird.core.impl.avr.AVRI2CMasterImplementation.Builder());

        builderList.add(new LocalHostDeviceImplementation.Builder());
    }

    public static void addBuilder(DIBuilder builder) {
        getInstance().builderList.add(builder);
    }

    public static List<DIBuilder> getBuilders() {
        return getInstance().builderList;
    }

    public static DIBuilderRegistry getInstance() {
        return ourInstance;
    }

    public static void removeBuilder(DIBuilder builder) {
        getInstance().builderList.remove(builder);
    }

}
