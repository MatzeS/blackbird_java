package blackbird.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Blackbird {

    private static Blackbird instance = new Blackbird();

    private Map<Device, DIFrame> frames;

    private List<Device> devices;
    private List<Link> links;


    public static Map<Device, DIFrame> getDIFrames() {
        return instance.frames;
    }

    public static List<Device> getDevices() {
        return instance.devices;
    }

    public static List<Link> getLinks() {
        return instance.links;
    }

    private Blackbird() {

        devices = new ArrayList<>();
        links = new ArrayList<>();

    }

}
