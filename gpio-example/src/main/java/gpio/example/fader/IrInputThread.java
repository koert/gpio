package gpio.example.fader;

import gpio.BinaryInputPin;
import gpio.gpio.beaglebone.IrReceiverInput;

import java.io.IOException;
import java.util.Queue;

/**
 * @author Koert Zeilstra
 */
public class IrInputThread extends Thread {

    private Queue<IrInput> inputQueue;
    private BinaryInputPin pin;

    public IrInputThread(Queue<IrInput> inputQueue, BinaryInputPin pin) {
        this.inputQueue = inputQueue;
        this.pin = pin;
    }

    @Override
    public void run() {
        IrReceiverInput irRemoteInput = null;
        try {
            irRemoteInput = new IrReceiverInput(pin, 576554, 200);
            while(!isInterrupted()) {
                String sequence = irRemoteInput.readSequence();
                System.out.println("readSequence " + sequence);
                IrInput irInput = IrInput.valueOfSequence(sequence);
                if (irInput != null) {
                    System.out.println("irInput " + irInput.name());
                    inputQueue.offer(irInput);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        System.out.println("run done");
    }
}
