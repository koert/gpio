package org.beaglebone.gpio;

import java.io.IOException;
import java.text.MessageFormat;

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

    public void waitForEdge(Edge edge) throws IOException {
        String deviceName = MessageFormat.format("/sys/class/gpio/gpio{0}/edge", pinDefinition.getGpio());
        device.writeToDevice(deviceName, Integer.toString(edge.getValue()));

//        snprintf(filename, sizeof(filename), "/sys/class/gpio/gpio%d/edge", gpio);
//
//        if ((fd = open(filename, O_WRONLY)) < 0)
//            return -1;
//
//        write(fd, stredge[edge], strlen(stredge[edge]) + 1);
//        close(fd);

    }
}
