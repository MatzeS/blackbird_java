package blackbird.core;

public class Bond {

    private Device from;
    private Device to;


    public Device getFrom() {
        return from;
    }

    public Device getTo() {
        return to;
    }


    public Bond(Device from, Device to) {
        this.from = from;
        this.to = to;
    }

}
