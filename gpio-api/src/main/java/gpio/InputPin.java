package gpio;

import java.io.IOException;
import java.text.MessageFormat;

/**
 * Input pin.
 * @author Koert Zeilstra
 */
public class InputPin {

    private PinDefinition pinDefinition;
    private GpioDevice device;

    /**
     * Constructor.
     * @param pinDefinition Pin.
     * @param device Device abstraction.
     * @throws IOException Failed to read/write device.
     */
    InputPin(PinDefinition pinDefinition, GpioDevice device) throws IOException {
        this.pinDefinition = pinDefinition;
        this.device = device;
        device.setup(pinDefinition, GpioDevice.PinUse.INPUT_DIGITAL);
    }

    /**
     * @return True if pin is high, false otherwise.
     * @throws IOException Failed to read/write device.
     */
    public boolean isHigh() throws IOException {
        return device.getBooleanValue(pinDefinition);
    }

    public void waitForEdge(Edge edge) throws IOException {
        String deviceName = MessageFormat.format("/sys/class/gpio/gpio{0}/edge", pinDefinition.getGpio());
        device.writeToDevice(deviceName, edge.getCode());

        String inputDeviceName = MessageFormat.format("/sys/class/gpio/gpio{0}/value", pinDefinition.getGpio());
        EpollDescriptor epollDescriptor = new EpollDescriptor(inputDeviceName);
        epollDescriptor.waitForEvent();

        epollDescriptor.close();

        // http://tutorials.jenkov.com/java-nio/selectors.html
//        Selector selector = Selector.open();
//        channel.configureBlocking(false);
//        SelectionKey key = channel.register(selector, SelectionKey.OP_READ);
//        while(true) {
//            int readyChannels = selector.select();
//            if(readyChannels == 0) continue;
//            Set<SelectionKey> selectedKeys = selector.selectedKeys();
//            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
//            while(keyIterator.hasNext()) {
//                SelectionKey key = keyIterator.next();
//                if(key.isAcceptable()) {
//                    // a connection was accepted by a ServerSocketChannel.
//                } else if (key.isConnectable()) {
//                    // a connection was established with a remote server.
//                } else if (key.isReadable()) {
//                    // a channel is ready for reading
//                } else if (key.isWritable()) {
//                    // a channel is ready for writing
//                }
//                keyIterator.remove();
//            }
//        }


//        snprintf(filename, sizeof(filename), "/sys/class/gpio/gpio%d/edge", gpio);
//
//        if ((fd = open(filename, O_WRONLY)) < 0)
//            return -1;
//
//        write(fd, stredge[edge], strlen(stredge[edge]) + 1);
//        close(fd);

    }
}
