package gpio;

import gpio.gpio.beaglebone.InputPinChangeMonitor;

import java.io.IOException;

/**
 * Input pin.
 * @author Koert Zeilstra
 */
public interface BinaryInputPin {

    /**
     * @return True if pin is high, false otherwise.
     * @throws java.io.IOException Failed to read/write device.
     */
    boolean isHigh() throws IOException;

    /**
     * Wait for change.
     * @param edge Spcified edge.
     * @throws IOException Failed to use device.
     */
    void waitForEdge(Edge edge) throws IOException;

    /**
     * Create change monitor.
     * @param edge Edge to monitor for.
     * @return Change monitor.
     * @throws IOException Failed to use device.
     */
    public InputPinChangeMonitor monitorChange(Edge edge) throws IOException;

        /**
         * Stop using this pin.
         */
    void close() throws IOException;

}
