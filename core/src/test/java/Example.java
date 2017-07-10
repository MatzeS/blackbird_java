import blackbird.core.device.MPR121;
import blackbird.core.device.avr.AVRDevice;

public class Example {

    public void example(){


        AVRDevice ontario;

        MPR121 mpr;

        ontario.attach(mpr);

        //or

        ontario.getOneWireLink().attach(mpr);

        // or

        OneWireLink oneWireLink;

        oneWireLink.setMaster(ontario);
        oneWireLink.addSlave(mpr);

        mpr.getImplementation().


    }

}
