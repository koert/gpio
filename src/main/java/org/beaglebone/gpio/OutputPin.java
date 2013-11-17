package org.beaglebone.gpio;

import java.io.IOException;

/**
 * @author koert
 */
public class OutputPin {
    private PinDefinition pinDefinition;
    private GpioDevice device;


    public OutputPin(PinDefinition pinDefinition, GpioDevice device) throws IOException {
        //To change body of created methods use File | Settings | File Templates.
        this.pinDefinition = pinDefinition;
        this.device = device;
        device.setup(pinDefinition, GpioDevice.PinUse.OUTPUT_DIGITAL);
    }

    public void high() throws IOException {
        device.setValue(pinDefinition, true);
        //To change body of created methods use File | Settings | File Templates.
    }

    public void low() throws IOException {
        device.setValue(pinDefinition, false);
        //To change body of created methods use File | Settings | File Templates.
    }
}
