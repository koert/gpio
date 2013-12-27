package gpio;

import gpio.beaglebone.BeagleboneBinaryInputPin;
import gpio.beaglebone.BeagleboneBinaryOutputPin;
import gpio.beaglebone.BeaglebonePwmOutputPin;

import java.io.IOException;

/**
 * @author koert
 */
public class BeagleboneGpioFactory implements GpioFactory {

    private BeagleboneGpioDevice device = new BeagleboneGpioDevice();

    public BeagleboneGpioFactory() {
        this.device = device;
    }

    @Override
    public BinaryInputPin createBinaryInputPin(PinDefinition pinDefinition) throws IOException {
        return new BeagleboneBinaryInputPin(pinDefinition, device);
    }

    @Override
    public BinaryOutputPin createBinaryOutputPin(PinDefinition pinDefinition) throws IOException {
        return new BeagleboneBinaryOutputPin(pinDefinition, device);
    }

    @Override
    public PwmOutputPin createPwmOutputPin(PinDefinition pinDefinition) throws IOException {
        return new BeaglebonePwmOutputPin(pinDefinition, device);
    }

}
