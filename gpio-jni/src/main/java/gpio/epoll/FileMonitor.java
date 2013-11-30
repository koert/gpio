package gpio.epoll;

/**
 * File monitor.
 * @author Koert Zeilstra
 */
public interface FileMonitor {

    /**
     * Add file to monitor.
     * @param fileName File name.
     */
    void addFile(String fileName);

    /**
     * Wait for event on files.
     */
    int waitForEvent();

    /**
     * Wait for event on files.
     * @param timeout Timeout in milliseconds.
     */
    int waitForEvent(int timeout);

    /**
     * Close descriptor.
     */
    void close();
}
