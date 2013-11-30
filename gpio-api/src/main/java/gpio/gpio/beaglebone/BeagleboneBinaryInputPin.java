package gpio.gpio.beaglebone;

import gpio.*;
import gpio.epoll.FileMonitor;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.MessageFormat;

/**
 * Output pin.
 * @author Koert Zeilstra
 */
public class BeagleboneBinaryInputPin implements BinaryInputPin {
    private PinDefinition pinDefinition;
    private BeagleboneGpioDevice device;
    private FileInputStream reader;
    private FileChannel channel;

    /**
     * Constructor.
     * @param pinDefinition Pin.
     * @param device Device abstraction.
     * @throws java.io.IOException Failed to read/write device.
     */
    public BeagleboneBinaryInputPin(PinDefinition pinDefinition, BeagleboneGpioDevice device) throws IOException {
        //To change body of created methods use File | Settings | File Templates.
        this.pinDefinition = pinDefinition;
        this.device = device;
        device.setup(pinDefinition, GpioDevice.PinUse.INPUT_DIGITAL);
        reader = new FileInputStream(getInputDeviceName());
        channel = reader.getChannel();
    }

    /**
     * @return True if pin is high, false otherwise.
     * @throws IOException Failed to read/write device.
     */
    @Override
    public boolean isHigh() throws IOException {
        byte[] buffer = new byte[10];
        channel.position(0);
        int length = reader.read(buffer);
        if (length == 0) {
            throw new IOException("Failed to read value from device '" + getInputDeviceName()  + "'");
        }
        boolean value = false;
        if (buffer[0] != '0') {
            value = true;
        }
        return value;
    }

    @Override
    public void waitForEdge(Edge edge) throws IOException {
        device.setEdge(pinDefinition, edge);
        FileMonitor fileMonitor = null;
        try {
            fileMonitor = device.createFileMonitor();
            String inputDeviceName = MessageFormat.format("/sys/class/gpio/gpio{0}/value", pinDefinition.getGpio());
            fileMonitor.addFile(inputDeviceName);
            fileMonitor.waitForEvent();
        } finally {
            if (fileMonitor != null) {
                fileMonitor.close();
            }
        }
    }

    @Override
    public InputPinChangeMonitor monitorChange(Edge edge) throws IOException {
        device.setEdge(pinDefinition, edge);
        InputPinChangeMonitor inputMonitor = null;
        FileMonitor fileMonitor = device.createFileMonitor();
        String inputDeviceName = MessageFormat.format("/sys/class/gpio/gpio{0}/value", pinDefinition.getGpio());
        fileMonitor.addFile(inputDeviceName);
        inputMonitor = new InputPinChangeMonitor(fileMonitor);
        return inputMonitor;
    }

    /**
     * Stop using this pin.
     */
    @Override
    public void close() throws IOException {
        reader.close();
        device.close(pinDefinition);

    }

    private String getInputDeviceName() {
        return MessageFormat.format("/sys/class/gpio/gpio{0}/value", pinDefinition.getGpio());
    }

}
