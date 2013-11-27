package gpio;

import java.io.IOException;

/**
 * Output pin.
 * @author Koert Zeilstra
 */
public interface BinaryOutputPin {
    /**
     * Set pin high.
     * @throws IOException Failed to read/write device.
     */
    public void high() throws IOException;

    /**
     * Set pin low.
     * @throws IOException Failed to read/write device.
     */
    public void low() throws IOException;

    /**
     * Stop using this pin.
     */
    void close() throws IOException;
}
