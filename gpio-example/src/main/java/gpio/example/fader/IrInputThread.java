package gpio.example.fader;

import gpio.BinaryInputPin;
import gpio.beaglebone.IrReceiverInput;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * @author Koert Zeilstra
 */
public class IrInputThread extends Thread {

    private static final String PREFIX = "16.8.";
    private static final int SEQUENCE_LENGTH = 129;

    private BlockingQueue<Command> commandQueue;
    private BinaryInputPin pin;
    private Map<IrInput, Command> inputCommands;

    public IrInputThread(BlockingQueue<Command> commandQueue, BinaryInputPin pin,
                         Map<IrInput, Command> inputCommands) {
        this.commandQueue = commandQueue;
        this.pin = pin;
        this.inputCommands = inputCommands;
    }

    @Override
    public void run() {
        IrReceiverInput irRemoteInput = null;
        try {
            irRemoteInput = new IrReceiverInput(pin, 576554, 200);
            while(!isInterrupted()) {
                String rawSequence = irRemoteInput.readSequence();
                System.out.println("readSequence " + rawSequence);
                if (rawSequence.startsWith(PREFIX) && rawSequence.length() > PREFIX.length() + SEQUENCE_LENGTH) {
                    String sequence = rawSequence.substring(PREFIX.length(), PREFIX.length() + SEQUENCE_LENGTH);
                    System.out.println("sequence " + sequence);
                    IrInput irInput = IrInput.valueOfSequence(sequence);
                    if (irInput != null) {
                        System.out.println("irInput " + irInput.name());
                        if (inputCommands.containsKey(irInput)) {
                            commandQueue.offer(inputCommands.get(irInput));
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        System.out.println("run done");
    }
}
