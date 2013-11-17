package org.beaglebone.gpio;

import java.io.IOException;

/**
 * @author koert
 */
public class InputPin {
    private PinDefinition pinDefinition;
    private GpioDevice device;


    public InputPin(PinDefinition pinDefinition, GpioDevice device) throws IOException {
        this.pinDefinition = pinDefinition;
        this.device = device;
        device.setup(pinDefinition, GpioDevice.PinUse.INPUT_DIGITAL);
    }

    public boolean isHigh() {
        return device.getBooleanValue(pinDefinition);
    }
}
