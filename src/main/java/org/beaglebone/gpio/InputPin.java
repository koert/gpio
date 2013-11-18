package org.beaglebone.gpio;

import java.io.IOException;

/**
 * Input pin.
 * @author Koert Zeilstra
 */
public class InputPin {
    private PinDefinition pinDefinition;
    private GpioDevice device;

    /**
     * Constructor.
     * @param pinDefinition Pin.
     * @param device Device abstraction.
     * @throws IOException Failed to read/write device.
     */
    InputPin(PinDefinition pinDefinition, GpioDevice device) throws IOException {
        this.pinDefinition = pinDefinition;
        this.device = device;
        device.setup(pinDefinition, GpioDevice.PinUse.INPUT_DIGITAL);
    }

    /**
     * @return True if pin is high, false otherwise.
     * @throws IOException Failed to read/write device.
     */
    public boolean isHigh() throws IOException {
        return device.getBooleanValue(pinDefinition);
    }
}
