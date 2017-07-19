package blackbird.core.device;

import java.io.IOException;

import blackbird.core.DImplementation;
import blackbird.core.DInterface;
import blackbird.core.Device;
import blackbird.core.builders.ModuleBuilder;
import blackbird.core.connection.serial.SerialConnection;
import blackbird.core.util.Color;

public class LightController extends SerialDevice {

    private static final long serialVersionUID = 7937368501061607511L;


    public interface Interface extends DInterface {

        @Override
        LightController getDevice();

        void setPixels(byte stripIdentifier, int[] color) throws IOException;

    }

    public static class Implementation
            extends DImplementation implements Interface {

        private SerialConnection serialConnection;

        public Implementation(SerialDevice.Implementation module) {
            this.serialConnection = module.getSerialConnection();
        }

        @Override
        public LightController getDevice() {
            return (LightController) super.getDevice();
        }

        @Override
        public synchronized void setPixels(byte stripIdentifier, int[] color) throws IOException {
            byte[] data = new byte[color.length * 3 + 1];
            data[0] = stripIdentifier;
            for (int i = 0; i < color.length; i++) {
                data[i * 3 + 1] = (byte) Color.red(color[i]);
                data[i * 3 + 2] = (byte) Color.green(color[i]);
                data[i * 3 + 3] = (byte) Color.blue(color[i]);
            }
            serialConnection.getOutputStream().write(data);
            serialConnection.getOutputStream().flush();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        public static class Builder extends ModuleBuilder<LightController, Implementation, SerialDevice, SerialDevice.Implementation> {

            @Override
            public Implementation buildFromModule(LightController device, SerialDevice module, SerialDevice.Implementation moduleImpl) {
                return new Implementation(moduleImpl);
            }

            @Override
            public Device getModule(LightController device) {
                return device.getModule("asdf");//TODO
            }
        }

    }

}
