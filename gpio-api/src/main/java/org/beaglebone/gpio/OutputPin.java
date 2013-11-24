package org.beaglebone.gpio;

import java.io.IOException;

/**
 * Output pin.
 * @author Koert Zeilstra
 */
public class OutputPin {
    private PinDefinition pinDefinition;
    private GpioDevice device;

    /**
     * Constructor.
     * @param pinDefinition Pin.
     * @param device Device abstraction.
     * @throws IOException Failed to read/write device.
     */
    OutputPin(PinDefinition pinDefinition, GpioDevice device) throws IOException {
        //To change body of created methods use File | Settings | File Templates.
        this.pinDefinition = pinDefinition;
        this.device = device;
        device.setup(pinDefinition, GpioDevice.PinUse.OUTPUT_DIGITAL);
    }

    /**
     * Set pin high.
     * @throws IOException Failed to read/write device.
     */
    public void high() throws IOException {
        device.setValue(pinDefinition, true);
        //To change body of created methods use File | Settings | File Templates.
    }

    /**
     * Set pin low.
     * @throws IOException Failed to read/write device.
     */
    public void low() throws IOException {
        device.setValue(pinDefinition, false);
        //To change body of created methods use File | Settings | File Templates.
    }
}
