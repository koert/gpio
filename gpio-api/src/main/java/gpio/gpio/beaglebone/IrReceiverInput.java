package gpio.gpio.beaglebone;

import gpio.BinaryInputPin;
import gpio.Edge;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Input reader for IR remote receiver.
 * @author Koert Zeilstra
 */
public class IrReceiverInput {

    private InputPinChangeMonitor changeMonitor;
    private long resolutionNs;
    private long margin;
    private int sequenceTimeoutMs;

    public IrReceiverInput(BinaryInputPin pin, long resolutionNs, int sequenceTimeoutMs) throws IOException {
        this.resolutionNs = resolutionNs;
        this.sequenceTimeoutMs = sequenceTimeoutMs;
        this.margin = resolutionNs / 2;
        changeMonitor= pin.monitorChange(Edge.BOTH);
    }

    public String readSequence() throws IOException {
        List<Long> times = new ArrayList<Long>();
        changeMonitor.waitForEvent(-1);
        long previousTime = System.nanoTime();

        boolean reading = true;
        while (reading) {
            if (changeMonitor.waitForEvent(sequenceTimeoutMs) == 0) {
                reading = false;
            } else {
                long time = System.nanoTime();
                times.add(time - previousTime);
                previousTime = time;
            }

        }
//        System.out.print("timeout ");
//        for (Long t : times) {
//            System.out.print(t);
//            System.out.print(" ");
//        }
//        System.out.println();
//        System.out.print("* ");
        StringBuilder builder = new StringBuilder("* ");
        for (Long t : times) {
            long x = (t + margin) / resolutionNs;
            builder.append(x).append(".");
        }
        return builder.toString();
    }


}
