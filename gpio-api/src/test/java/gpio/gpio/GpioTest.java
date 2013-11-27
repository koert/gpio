package gpio.gpio;

import gpio.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

/**
 * Unit test for Gpio.
 * @author Koert Zeilstra
 */
@RunWith(MockitoJUnitRunner.class)
public class GpioTest {

    private Gpio gpio;
    @Mock private GpioDevice gpioDevice;
    @Mock private GpioFactory factory;
    @Mock private BinaryInputPin inputPin;
    @Mock private BinaryOutputPin outputPin;

    private PinDefinition pinDefinition = BeagleboneGPio.P8_10;

    @Before
    public void setup() {
        gpio = new Gpio(factory);
    }

    @Test
    public void pinInput() throws Exception {
        Mockito.when(factory.createBinaryInputPin(pinDefinition)).thenReturn(inputPin);

        BinaryInputPin pin = gpio.binaryInputPin(BeagleboneGPio.P8_10);
        assertThat(pin, sameInstance(inputPin));
    }


    @Test
    public void pinOutput() throws Exception {
        Mockito.when(factory.createBinaryOutputPin(pinDefinition)).thenReturn(outputPin);

        BinaryOutputPin pin = gpio.binaryOutputPin(BeagleboneGPio.P8_10);
        assertThat(pin, sameInstance(outputPin));
    }
}
