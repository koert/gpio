package org.beaglebone.gpio;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;

/**
 * Unit test for Gpio.
 * @author Koert Zeilstra
 */
@RunWith(MockitoJUnitRunner.class)
public class GpioTest {

    @InjectMocks private Gpio gpio;
    @Mock GpioDevice gpioDevice;

    private PinDefinition pinDefinition = new PinDefinition("USR1", "USR1", 54, -1, -1);

    @Test
    public void pinInput() throws Exception {
        Mockito.when(gpioDevice.getBooleanValue(eq(pinDefinition))).thenReturn(true);

        InputPin pin = gpio.pin("USR1").input();
        assertThat(pin.isHigh(), is(true));
        Mockito.verify(gpioDevice).setup(eq(pinDefinition), eq(GpioDevice.PinUse.INPUT_DIGITAL));
    }


    @Test
    public void pinOutput() throws Exception {

        OutputPin pin = gpio.pin("USR1").output();
        pin.high();
        Mockito.verify(gpioDevice).setup(eq(pinDefinition), eq(GpioDevice.PinUse.OUTPUT_DIGITAL));
        Mockito.verify(gpioDevice).setValue(eq(pinDefinition), eq(true));
    }
}
