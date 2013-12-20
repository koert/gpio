package gpio.example;

import gpio.BeagleboneGPio;
import gpio.BeagleboneGpioFactory;
import gpio.BinaryInputPin;
import gpio.Gpio;
import gpio.gpio.beaglebone.InputPinChangeMonitor;
import gpio.gpio.beaglebone.IrReceiverInput;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test program that listen vor IR remote reception.
 * @author Koert Zeilstra
 */
public class ReadIrColorRemote {

    private static final String KEY_ON = "on";
    private static final String KEY_OFF = "off";
    private static final String KEY_UP = "up";
    private static final String KEY_DOWN = "down";

    private Gpio gpio;
    private BinaryInputPin pin;
    private Map<String, String> keys = new HashMap<String, String>();

    public ReadIrColorRemote() throws IOException {
        gpio = new Gpio(new BeagleboneGpioFactory());
        pin = gpio.binaryInputPin(BeagleboneGPio.P9_11);

        keys.put("* 16.8.1.1.1.1.1.1.1.1.1.1.1.1.1.1.1.1.1.3.1.3.1.3.1.3.1.1.1.3.1.3.1.3.1.3.1.3.1.1.1.1.1.1.1.1.1.1.1.1.1.1.1.1.1.3.1.3.1.3.1.3.1.3.1.3.1.75.16.4.1.", KEY_ON);
        keys.put("* 16.8.1.1.1.1.1.1.1.1.1.1.1.1.1.1.1.1.1.3.1.3.1.3.1.3.1.1.1.3.1.3.1.3.1.1.1.3.1.1.1.1.1.1.1.1.1.1.1.1.1.3.1.1.1.3.1.3.1.3.1.3.1.3.1.3.1.74.16.4.1.", KEY_OFF);
        keys.put("* 16.8.1.1.1.1.1.1.1.1.1.1.1.1.1.1.1.1.1.3.1.3.1.3.1.3.1.1.1.3.1.3.1.3.1.1.1.1.1.1.1.1.1.1.1.1.1.1.1.1.1.3.1.3.1.3.1.3.1.3.1.3.1.3.1.3.1.74.16.4.1.", KEY_UP);
        keys.put("* 16.8.1.1.1.1.1.1.1.1.1.1.1.1.1.1.1.1.1.3.1.3.1.3.1.3.1.1.1.3.1.3.1.3.1.3.1.1.1.1.1.1.1.1.1.1.1.1.1.1.1.1.1.3.1.3.1.3.1.3.1.3.1.3.1.3.1.74.16.4.1.", KEY_DOWN);
    }

    public void readPattern() throws IOException {

        System.out.println("value: " + pin.isHigh());
        IrReceiverInput irRemoteInput = new IrReceiverInput(pin, 563190, 150);

        while(true) {
            String sequence = irRemoteInput.readSequence();
            System.out.println(sequence);
            if (keys.containsKey(sequence)) {
                System.out.println(keys.get(sequence));
            }
        }
    }

    public List<Long> readSequence(InputPinChangeMonitor changeMonitor) throws IOException {
        List<Long> times = new ArrayList<Long>();
        changeMonitor.waitForEvent(-1);
        long previousTime = System.nanoTime();

        boolean reading = true;
        while (reading) {
            if (changeMonitor.waitForEvent(200) == 0) {
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
            long x = ((t + 100000) / 100000) / 5;
            builder.append(x).append(".");
        }
        System.out.println(builder.toString());
        return times;
    }

    public void readSequence(InputPinChangeMonitor changeMonitor, List<Value> values) throws IOException {
        List<Long> times = new ArrayList<Long>();
        changeMonitor.waitForEvent();
        long previousTime = System.nanoTime();

        boolean reading = true;
        while (reading) {
            if (changeMonitor.waitForEvent(200) == 0) {
                reading = false;
            } else {
                long time = System.nanoTime();
                times.add(time - previousTime);
                previousTime = time;
            }

        }
        System.out.println("timeout");
        for (Long t : times) {
            System.out.print(t);
            System.out.print(" ");
        }
        System.out.println();
        if (values.isEmpty() || values.size() != times.size()) {
            values.clear();
            for (int i=0; i< times.size(); i++) {
                Value value = new Value();
                Long t = times.get(i);
                value.accumulated = t;
                value.count = 1;
                value.min = t;
                value.max = t;
                values.add(value);
            }
        } else {
            for (int i=0; i< times.size(); i++) {
                Value value = values.get(i);
                Long t = times.get(i);
                value.accumulated = value.accumulated + t;
                value.count = value.count + 1;
                if (value.min > t) {
                    value.min = t;
                }
                if (value.max < t) {
                    value.max = t;
                }
            }
        }
    }

    private class Value {
        long accumulated;
        long count;
        long min;
        long max;
    }

    public static void main(String[] args) {

        try {
            ReadIrColorRemote readIrRemote = new ReadIrColorRemote();
            readIrRemote.readPattern();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
