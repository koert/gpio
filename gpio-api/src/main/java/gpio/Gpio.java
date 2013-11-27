package gpio;

import java.io.IOException;

/**
 * GPIO API for Beaglebone black.
 * @author Koert Zeilstra
 */
public class Gpio {

    private GpioDevice device = new GpioDevice();
    private GpioFactory factory;

    /**
     * Constructor.
     */
    public Gpio() {
    }

    /**
     * Constructor.
     */
    public Gpio(GpioFactory factory) {
        this.factory = factory;
    }

    /**
     * Setup binary output pin.
     *
     * @param pinDefinition Defined pin.
     * @return Pin builder.
     */
    public BinaryInputPin binaryInputPin(final PinDefinition pinDefinition) throws IOException {
        return factory.createBinaryInputPin(pinDefinition);
    }

    /**
     * Setup binary output pin.
     *
     * @param pinDefinition Defined pin.
     * @return Pin builder.
     */
    public BinaryOutputPin binaryOutputPin(final PinDefinition pinDefinition) throws IOException {
        return factory.createBinaryOutputPin(pinDefinition);
    }

    /**
     * Setup PWM output pin.
     *
     * @param pinDefinition Defined pin.
     * @return Pin builder.
     */
    public PwmOutputPin pwmOutputPin(final PinDefinition pinDefinition) throws IOException {
        return factory.createPwmOutputPin(pinDefinition);
    }

}
