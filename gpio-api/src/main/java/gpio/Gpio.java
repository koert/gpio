package gpio;

import java.io.IOException;

/**
 * GPIO API for Beaglebone black.
 * @author Koert Zeilstra
 */
public class Gpio {

    private GpioDevice device = new GpioDevice();

    /**
     * Constructor.
     */
    public Gpio() {
    }

    /**
     * Setup pin.
     * @param pinDefinition Defined pin.
     * @return Pin builder.
     */
    public PinBuilder pin(final PinDefinition pinDefinition) {
        return new PinBuilder(pinDefinition);
    }

    /**
     * Builder for configuring a pin.
     */
    public class PinBuilder {
        private PinDefinition pinDefinition;

        /**
         * Constructor.
         * @param pinDefinition Pin definition.
         */
        PinBuilder(PinDefinition pinDefinition) {
            this.pinDefinition = pinDefinition;
        }

        /**
         * Configure pin as input pin.
         * @return Configured input pin.
         * @throws IOException Failed to read/write device.
         */
        public InputPin input() throws IOException {
            return new InputPin(pinDefinition, device);
        }

        /**
         * Configure pin as output pin.
         * @return Configured output pin.
         * @throws IOException Failed to read/write device.
         */
        public OutputPin output() throws IOException {
            return new OutputPin(pinDefinition, device);
        }

        /**
         * Configure pin as output pin.
         * @return Configured output pin.
         * @throws IOException Failed to read/write device.
         */
        public OutputPwmPin outputPwm() throws IOException {
            return new OutputPwmPin(pinDefinition, device);
        }

        @Override
        public String toString() {
            return "PinBuilder{" +
                    "pinDefinition=" + pinDefinition +
                    '}';
        }
    }
}
