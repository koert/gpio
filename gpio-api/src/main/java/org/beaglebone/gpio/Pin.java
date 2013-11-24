package org.beaglebone.gpio;

/**
 * @author Koert Zeilstra
 */
public class Pin {

    private PinDefinition pinDefinition;
    private GpioDevice gpioDevice;

    public Pin(PinDefinition pinDefinition, GpioDevice gpioDevice) {
        this.pinDefinition = pinDefinition;
        this.gpioDevice = gpioDevice;
    }



}
