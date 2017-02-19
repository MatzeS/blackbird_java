package blackbird.core.avr.DS18B20;

import blackbird.core.avr.packets.AVRPacket;

public class DS18B20ReadResponse extends AVRPacket {

    private long address;
    private float temp;

    public DS18B20ReadResponse(long address, float temp) {
        this.address = address;
        this.temp = temp;
    }

    public long getAddress() {
        return address;
    }

    public float getTemp() {
        return temp;
    }

}
