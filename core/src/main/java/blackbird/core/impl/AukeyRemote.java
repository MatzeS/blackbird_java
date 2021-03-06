package blackbird.core.impl;

import java.io.IOException;

import blackbird.core.ComponentImplementation;
import blackbird.core.DInterface;
import blackbird.core.Device;
import blackbird.core.exception.ImplementationFailedException;
import blackbird.core.ports.ParentDevicePort;

import static blackbird.core.avr.DigitalPinValue.HIGH;
import static blackbird.core.avr.DigitalPinValue.LOW;

public class AukeyRemote extends Device {

    private static final long serialVersionUID = -7567884786987932459L;

    public AukeyRemote(String name) {
        super(name);
        getUIData().put("iconName", "ic_remote");
    }

    public interface Interface extends DInterface {

        void switchSocket(int num, int state);
    }

    public static class Implementation extends ComponentImplementation<AukeyRemote, DInterface> implements Interface {

        public static final MCP23017.Pin OFF_PIN = MCP23017.Pin.B7;
        public static final MCP23017.Pin ON_PIN = MCP23017.Pin.B5;
        public static final MCP23017.Pin SOCKET_1_PIN = MCP23017.Pin.B1;
        public static final MCP23017.Pin SOCKET_2_PIN = MCP23017.Pin.B3;

        public static final MCP23017.Pin STATE_LED_PIN = MCP23017.Pin.A7;

        MCP23017.Interface mcp;

        public Implementation(DInterface component, MCP23017.Interface mcp) throws ImplementationFailedException {
            super(component);
            this.mcp = mcp;

            try {
                mcp.pinMode(OFF_PIN, false);
                mcp.pinMode(ON_PIN, false);
                mcp.pinMode(SOCKET_1_PIN, false);
                mcp.pinMode(SOCKET_2_PIN, false);
                mcp.pinMode(STATE_LED_PIN, false);

                mcp.digitalWrite(OFF_PIN, LOW);
                mcp.digitalWrite(ON_PIN, LOW);
                mcp.digitalWrite(SOCKET_1_PIN, LOW);
                mcp.digitalWrite(SOCKET_2_PIN, LOW);
                mcp.digitalWrite(STATE_LED_PIN, HIGH);
            } catch (IOException e) {
                throw new ImplementationFailedException("IO Exception during initializing aukey remote, " +
                        "system may be in inconsistent", e);
            }
        }

        @Override
        public synchronized void switchSocket(int num, int state) {
            try {

                MCP23017.Pin socketPin = null;
                MCP23017.Pin statePin;

                switch (num) {
                    case 1:
                        socketPin = SOCKET_1_PIN;
                        break;
                    case 2:
                        socketPin = SOCKET_2_PIN;
                        break;
                }

                if (state == Socket.ON)
                    statePin = ON_PIN;
                else
                    statePin = OFF_PIN;

                mcp.digitalWrite(statePin, HIGH);
                mcp.digitalWrite(socketPin, HIGH);

                Thread.sleep(200);

                mcp.digitalWrite(statePin, LOW);
                mcp.digitalWrite(socketPin, LOW);

                Thread.sleep(1000);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        public static class Builder extends ParentDevicePort.Builder<AukeyRemote, Interface, MCP23017, MCP23017.Interface> {

            @Override
            public Interface assemble(DInterface component, MCP23017.Interface parentInterface)
                    throws ImplementationFailedException {
                return new Implementation(component, parentInterface);
            }
        }

    }

}
