package blackbird.core.impl;

import java.util.Objects;

/**
 * The RCSocket class is used to identify popular low cost
 * remote radio controlled power outlets.
 * <p>
 * They are controlled by a 433MHz transmitter sending a signal decoded
 * by the PT2262 chip.
 * <p>
 * The sockets are addressed by 10 DIP switches, therefore a 10bit address.
 */
public class RCSocket extends RemoteSocket {

    private static final long serialVersionUID = 2014135975043037056L;

    private int address;

    public RCSocket(String name, String address) {
        this(name, 0);

        if (!address.matches("[01]*"))
            throw new IllegalArgumentException("Address must consist of 0 and 1s");

        if (address.length() != 10)
            throw new IllegalArgumentException("Address must be 10bit long");

        for (int i = 0; i < 10; i++)
            this.address |= address.charAt(9 - i) == '1' ? (1 << i) : 0;
    }

    public RCSocket(String name, int address) {
        super(name);
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        RCSocket rcSocket = (RCSocket) o;
        return Objects.equals(address, rcSocket.address);
    }

    public int getAddress() {
        return address;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), address);
    }

}
