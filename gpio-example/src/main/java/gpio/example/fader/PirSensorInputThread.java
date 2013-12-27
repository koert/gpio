package gpio.example.fader;

import gpio.BinaryInputPin;
import gpio.Edge;
import gpio.beaglebone.InputPinChangeMonitor;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 * @author Koert Zeilstra
 */
public class PirSensorInputThread extends Thread {

    private BlockingQueue<Command> commandQueue;
    private BinaryInputPin pin;
    private Command command;
    private InputPinChangeMonitor inputPinChangeMonitor;

    public PirSensorInputThread(BlockingQueue<Command> commandQueue, BinaryInputPin pin, Command command)
            throws IOException {
        this.commandQueue = commandQueue;
        this.pin = pin;
        this.command = command;
        inputPinChangeMonitor = pin.monitorChange(Edge.RISING);
    }

    @Override
    public void run() {

        try {
            while(!isInterrupted()) {
                System.out.println("PirSensorInputThread waiting");
                inputPinChangeMonitor.waitForEvent();
                System.out.println("PirSensorInputThread event");
                if (pin.isHigh()) {
                    System.out.println("isHigh");
                    commandQueue.offer(command);
                    System.out.println("offered");
                } else {
                    System.out.println("isLow");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        System.out.println("run done");
    }

    public void close() throws IOException {
        inputPinChangeMonitor.close();
        pin.close();
    }
}
