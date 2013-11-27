package gpio.gpio.beaglebone;

import gpio.BeagleboneGpioDevice;
import gpio.GpioDevice;
import gpio.BinaryOutputPin;
import gpio.PinDefinition;

import java.io.IOException;

/**
 * Output pin.
 * @author Koert Zeilstra
 */
public class BeagleboneBinaryOutputPin implements BinaryOutputPin {
    private PinDefinition pinDefinition;
    private BeagleboneGpioDevice device;

    /**
     * Constructor.
     * @param pinDefinition Pin.
     * @param device Device abstraction.
     * @throws java.io.IOException Failed to read/write device.
     */
    public BeagleboneBinaryOutputPin(PinDefinition pinDefinition, BeagleboneGpioDevice device) throws IOException {
        //To change body of created methods use File | Settings | File Templates.
        this.pinDefinition = pinDefinition;
        this.device = device;
        device.setup(pinDefinition, GpioDevice.PinUse.OUTPUT_DIGITAL);
    }

    /**
     * Set pin high.
     * @throws java.io.IOException Failed to read/write device.
     */
    @Override
    public void high() throws IOException {
        device.setValue(pinDefinition, true);
        //To change body of created methods use File | Settings | File Templates.
    }

    /**
     * Set pin low.
     * @throws java.io.IOException Failed to read/write device.
     */
    @Override
    public void low() throws IOException {
        device.setValue(pinDefinition, false);
        //To change body of created methods use File | Settings | File Templates.
    }

    /**
     * Stop using this pin.
     */
    @Override
    public void close() throws IOException {
        device.close(pinDefinition);
    }
}
