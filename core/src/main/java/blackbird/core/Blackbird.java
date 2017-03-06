package blackbird.core;

import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;
import java.util.*;
import java.util.stream.Collectors;

public class Blackbird {

    private Map<Device, DIFrame> frames;

    private List<Device> devices;
    private List<Compound> compounds;

     public Map<Device, DIFrame> getDIFrames() {
        return frames;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public List<Compound> getCompounds() {
        return compounds;
    }

    public Blackbird() {

        devices = new ArrayList<>();
        compounds = new ArrayList<>();

    }

}
