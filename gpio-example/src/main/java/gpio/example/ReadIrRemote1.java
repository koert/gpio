package gpio.example;

import gpio.*;
import gpio.beaglebone.InputPinChangeMonitor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Test program that listen vor IR remote reception.
 * @author Koert Zeilstra
 */
public class ReadIrRemote1 {

    private Gpio gpio;
    private BinaryInputPin pin;

    public ReadIrRemote1() throws IOException {
        gpio = new Gpio(new BeagleboneGpioFactory());
        pin = gpio.binaryInputPin(BeagleboneGPio.P9_11);
    }

    public void read4() throws IOException {
        List<Value> values = new ArrayList<Value>();
        System.out.println("value: " + pin.isHigh());
        long previousTime = System.nanoTime();
        InputPinChangeMonitor changeMonitor = pin.monitorChange(Edge.BOTH);
        readSequence(changeMonitor, values);
        values.clear();

        while(true) {
            readSequence(changeMonitor, values);

            for (int i=0; i< values.size(); i++) {
                Value value = values.get(i);
                System.out.printf("%d %d %d %d\n", value.accumulated, value.count, value.min, value.max);
            }
        }
    }

    private void readSequence(InputPinChangeMonitor changeMonitor, List<Value> values) throws IOException {
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
            ReadIrRemote1 readIrRemote = new ReadIrRemote1();
            readIrRemote.read4();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
