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
    void waitForEvent();

    /**
     * Close descriptor.
     */
    void close();
}
