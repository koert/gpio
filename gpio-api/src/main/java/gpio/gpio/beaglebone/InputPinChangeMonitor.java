package gpio.gpio.beaglebone;

import gpio.epoll.FileMonitor;

/**
 * @author Koert Zeilstra
 */
public class InputPinChangeMonitor {
    private FileMonitor fileMonitor;

    public InputPinChangeMonitor(FileMonitor fileMonitor) {
        this.fileMonitor = fileMonitor;
    }

    public void waitForEvent() {
        fileMonitor.waitForEvent();
    }

    public void close() {
        fileMonitor.close();
    }
}
