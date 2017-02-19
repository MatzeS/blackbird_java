package blackbird.core.impl;

import java.io.IOException;
import java.io.Serializable;

import blackbird.core.Blackbird;
import blackbird.core.ComponentDIBuilder;
import blackbird.core.ComponentImplementation;
import blackbird.core.avr.ByteHelper;
import blackbird.core.exception.ImplementationFailedException;
import blackbird.core.ports.ParentDevicePort;
import blackbird.core.rmi.Remote;
import blackbird.core.util.ListenerList;

public class MPR121 extends I2CSlave {

    public static final int ADDRESS_GND = 0x5A;
    public static final int ADDRESS_VDD = 0x5B;
    public static final int ADDRESS_SDA = 0x5C;
    public static final int ADDRESS_SCL = 0x5D;

    public static final int PROXIMITY_ELECTRODE = 12;

    public MPR121(String name, int i2cAddress) {
        super(name, i2cAddress);
    }

    public static Runnable map(MPR121 mpr, int electrode, Transition transition, Runnable action) {
        MPR121.Interface mprInterface = Blackbird.getInstance().interfaceDevice(mpr, MPR121.Interface.class);
        Listener listener = event -> {
            if (transition.matches(event.getTransition()) && electrode == event.getElectrode())
                action.run();
        };
        mprInterface.addListener(listener);
        return () -> mprInterface.removeListener(listener);
    }

    @SuppressWarnings("SpellCheckingInspection")
    public enum Register {

        // touch and OOR statuses
        TS1(0x00),
        TS2(0x01),
        OORS1(0x02),
        OORS2(0x03),

        // filtered data
        E0FDL(0x04),
        E0FDH(0x05),
        E1FDL(0x06),
        E1FDH(0x07),
        E2FDL(0x08),
        E2FDH(0x09),
        E3FDL(0x0A),
        E3FDH(0x0B),
        E4FDL(0x0C),
        E4FDH(0x0D),
        E5FDL(0x0E),
        E5FDH(0x0F),
        E6FDL(0x10),
        E6FDH(0x11),
        E7FDL(0x12),
        E7FDH(0x13),
        E8FDL(0x14),
        E8FDH(0x15),
        E9FDL(0x16),
        E9FDH(0x17),
        E10FDL(0x18),
        E10FDH(0x19),
        E11FDL(0x1A),
        E11FDH(0x1B),
        E12FDL(0x1C),
        E12FDH(0x1D),

        // baseline values
        E0BV(0x1E),
        E1BV(0x1F),
        E2BV(0x20),
        E3BV(0x21),
        E4BV(0x22),
        E5BV(0x23),
        E6BV(0x24),
        E7BV(0x25),
        E8BV(0x26),
        E9BV(0x27),
        E10BV(0x28),
        E11BV(0x29),
        E12BV(0x2A),

        // general electrode touch sense baseline filters
        // rising filter
        MHDR(0x2B),
        NHDR(0x2C),
        NCLR(0x2D),
        FDLR(0x2E),

        // falling filter
        MHDF(0x2F),
        NHDF(0x30),
        NCLF(0x31),
        FDLF(0x32),

        // touched filter
        NHDT(0x33),
        NCLT(0x34),
        FDLT(0x35),

        // proximity electrode touch sense baseline filters
        // rising filter
        MHDPROXR(0x36),
        NHDPROXR(0x37),
        NCLPROXR(0x38),
        FDLPROXR(0x39),

        // falling filter
        MHDPROXF(0x3A),
        NHDPROXF(0x3B),
        NCLPROXF(0x3C),
        FDLPROXF(0x3D),

        // touched filter
        NHDPROXT(0x3E),
        NCLPROXT(0x3F),
        FDLPROXT(0x40),

        // electrode touch and release thresholds
        E0TTH(0x41),
        E0RTH(0x42),
        E1TTH(0x43),
        E1RTH(0x44),
        E2TTH(0x45),
        E2RTH(0x46),
        E3TTH(0x47),
        E3RTH(0x48),
        E4TTH(0x49),
        E4RTH(0x4A),
        E5TTH(0x4B),
        E5RTH(0x4C),
        E6TTH(0x4D),
        E6RTH(0x4E),
        E7TTH(0x4F),
        E7RTH(0x50),
        E8TTH(0x51),
        E8RTH(0x52),
        E9TTH(0x53),
        E9RTH(0x54),
        E10TTH(0x55),
        E10RTH(0x56),
        E11TTH(0x57),
        E11RTH(0x58),
        E12TTH(0x59),
        E12RTH(0x5A),

        // debounce settings
        DTR(0x5B),

        // configuration registers
        AFE1(0x5C),
        AFE2(0x5D),
        ECR(0x5E),

        // electrode currents
        CDC0(0x5F),
        CDC1(0x60),
        CDC2(0x61),
        CDC3(0x62),
        CDC4(0x63),
        CDC5(0x64),
        CDC6(0x65),
        CDC7(0x66),
        CDC8(0x67),
        CDC9(0x68),
        CDC10(0x69),
        CDC11(0x6A),
        CDCPROX(0x6B),

        // electrode charge times
        CDT01(0x6C),
        CDT23(0x6D),
        CDT45(0x6E),
        CDT67(0x6F),
        CDT89(0x70),
        CDT1011(0x71),
        CDTPROX(0x72),

        // GPIO
        CTL0(0x73),
        CTL1(0x74),
        DAT(0x75),
        DIR(0x76),
        EN(0x77),
        SET(0x78),
        CLR(0x79),
        TOG(0x7A),

        // auto-config
        ACCR0(0x7B),
        ACCR1(0x7C),
        USL(0x7D),
        LSL(0x7E),
        TL(0x7F),

        // soft reset
        SRST(0x80),

        // PWM
        PWM0(0x81),
        PWM1(0x82),
        PWM2(0x83),
        PWM3(0x84);


        byte registerAddress;

        Register(int registerAddress) {
            this((byte) registerAddress);
        }

        Register(byte registerAddress) {
            this.registerAddress = registerAddress;
        }

        public byte add(int i) {
            return (byte) (get() + i);
        }

        public byte get() {
            return getRegisterAddress();
        }

        public byte getRegisterAddress() {
            return registerAddress;
        }

    }

    public enum Transition {
        TOUCHED, RELEASED, BOTH;

        public boolean matches(Transition transition) {
            return this == BOTH || transition == BOTH || this == transition;
        }

        public static Transition get(boolean touched) {
            return touched ? TOUCHED : RELEASED;
        }

    }

    public interface Interface extends I2CSlave.Interface {

        void addListener(Listener listener);

        byte readRegister(Register register) throws IOException;

        void removeListener(Listener listener);

        void writeRegister(Register register, byte value) throws IOException;

    }

    public interface Listener extends Remote {

        void changed(TouchEvent event);

    }

    public static class Implementation extends ComponentImplementation<MPR121, I2CSlave.Interface>
            implements Interface {

        private ListenerList<Listener> listeners;

        private int touchStates = 0;


        public Implementation(I2CSlave.Interface component, I2CMaster.Interface i2cMasterInterface) throws IOException {
            super(component);
            this.listeners = new ListenerList<>();

            loadDefaultConfiguration();

            i2cMasterInterface.addListener(this::updateTouchStates);
        }


        @Override
        public void addListener(Listener listener) {
            this.listeners.add(listener);
        }

        protected void fireTouchEvent(TouchEvent event) {
            listeners.fire(l -> l.changed(event));
        }

        private void loadDefaultConfiguration() throws IOException {

            // perform a soft reset
            writeRegister(Register.SRST, 0x63);

            writeRegister(Register.ECR, 0x00);

            int touch = 12;
            int release = 6;
            for (int i = 0; i < 12; i++) {
                writeRegister(Register.E0TTH.add(2 * i), touch);
                writeRegister(Register.E0RTH.add(2 * i), release);
            }
            writeRegister(Register.MHDR, 0x01);
            writeRegister(Register.NHDR, 0x01);
            writeRegister(Register.NCLR, 0x0E);
            writeRegister(Register.FDLR, 0x00);

            writeRegister(Register.MHDF, 0x01);
            writeRegister(Register.NHDF, 0x05);
            writeRegister(Register.NCLF, 0x01);
            writeRegister(Register.FDLF, 0x00);

            writeRegister(Register.NHDT, 0x00);
            writeRegister(Register.NCLT, 0x00);
            writeRegister(Register.FDLT, 0x00);

            writeRegister(Register.DTR, 0x00);
            writeRegister(Register.AFE1, 0x10); // default, 16uA charge current
            writeRegister(Register.AFE2, 0x20); // 0.5uS encoding, 1ms period

            writeRegister(Register.ECR, 0x8F);  // start with first 5 bits of baseline tracking

        }

        public byte readRegister(byte registerAddress) throws IOException {
            return component.readRegister(registerAddress);
        }

        @Override
        public byte readRegister(Register register) throws IOException {
            return readRegister(register.get());
        }

        public byte[] readRegisters(byte registerAddress, int num) throws IOException {
            return component.readRegisters(registerAddress, num);
        }

        @Override
        public void removeListener(Listener listener) {
            this.listeners.remove(listener);
        }

        private synchronized void updateTouchStates() {
            try {
                int states = (readRegister(Register.TS2) << 8) + readRegister(Register.TS1);

                int change = touchStates ^ states;
                touchStates = states;

                for (int i = 0; i < 12; i++)
                    if (ByteHelper.testBit(change, i))
                        fireTouchEvent(new TouchEvent(i, Transition.get(ByteHelper.testBit(states, i))));

            } catch (IOException e) {
                e.printStackTrace(); //TODO
            }
        }

        private void writeRegister(Register register, int value) throws IOException {
            writeRegister(register, (byte) value);
        }

        private void writeRegister(byte register, int value) throws IOException {
            writeRegister(register, (byte) value);
        }

        @Override
        public void writeRegister(Register register, byte value) throws IOException {
            writeRegister(register.get(), value);
        }

        public void writeRegister(byte registerAddress, byte value) throws IOException {
            component.writeRegister(registerAddress, value);
        }

        public void writeRegisters(byte registerAddress, byte[] data) throws IOException {
            component.writeRegisters(registerAddress, data);
        }

        public static class Builder extends
                ComponentDIBuilder<MPR121, Implementation, ParentDevicePort, I2CSlave.Interface> {

            @Override
            public Implementation build(MPR121 device, ParentDevicePort port, I2CSlave.Interface componentInterface) {
                try {
                    I2CMaster.Interface i2cMasterInterface = blackbird.interfaceDevice(
                            port.getParentDevice(), I2CMaster.Interface.class);
                    return new Implementation(componentInterface, i2cMasterInterface);
                } catch (IOException e) {
                    throw new ImplementationFailedException("I2C slave does not answer or not behaves as expected", e);
                }
            }
        }

    }

    public static class TouchEvent implements Serializable {

        private int electrode;

        private Transition transition;

        public TouchEvent(int electrode, Transition transition) {
            this.electrode = electrode;
            this.transition = transition;
        }

        public int getElectrode() {
            return electrode;
        }

        public Transition getTransition() {
            return transition;
        }

    }

}
