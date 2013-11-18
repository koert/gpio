package org.beaglebone.gpio;

import java.io.IOException;

/**
 * Output PWM pin.
 * @author Koert Zeilstra
 */
public class OutputPwmPin {
    private PinDefinition pinDefinition;
    private GpioDevice device;

    /**
     * Constructor.
     * @param pinDefinition Pin.
     * @param device Device abstraction.
     * @throws java.io.IOException Failed to read/write device.
     */
    OutputPwmPin(PinDefinition pinDefinition, GpioDevice device) throws IOException {
        //To change body of created methods use File | Settings | File Templates.
        this.pinDefinition = pinDefinition;
        this.device = device;
        device.setup(pinDefinition, GpioDevice.PinUse.OUTPUT_PWM);
    }

    /**
     * Set pin high.
     * @throws java.io.IOException Failed to read/write device.
     */
    public void setValue() throws IOException {
        device.setValue(pinDefinition, true);
        //To change body of created methods use File | Settings | File Templates.
    }

}
