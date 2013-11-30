package gpio;

import java.io.IOException;

/**
 * Output PWM pin.
 * @author Koert Zeilstra
 */
public interface PwmOutputPin {

    /**
     * @param frequency Frequency.
     * @throws java.io.IOException Failed to read/write device.
     */
    PwmOutputPin frequency(float frequency) throws IOException;

    /**
     * @param polarity Polarity.
     * @throws java.io.IOException Failed to read/write device.
     */
    PwmOutputPin polarity(boolean polarity) throws IOException;

    /**
     * @param dutyCycle Duty cycle, minimum value is 0, maxiumum value is 1.
     * @throws java.io.IOException Failed to read/write device.
     */
    PwmOutputPin dutyCycle(float dutyCycle) throws IOException;

    /**
     * @param dutyCycle Duty cycle, minimum value is 0, maxiumum value is Short.MAX_VALUE.
     * @throws java.io.IOException Failed to read/write device.
     */
    PwmOutputPin dutyCycle(short dutyCycle) throws IOException;

    /**
     * Stop using this pin.
     */
    void close() throws IOException;
}
