package gpio;

import java.io.IOException;

/**
 * @author koert
 */
public interface GpioFactory {
    BinaryOutputPin createBinaryOutputPin(PinDefinition pinDefinition) throws IOException;

    BinaryInputPin createBinaryInputPin(PinDefinition pinDefinition) throws IOException;

    PwmOutputPin createPwmOutputPin(PinDefinition pinDefinition) throws IOException;
}

