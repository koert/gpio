package gpio.example;

import gpio.*;
import gpio.gpio.beaglebone.InputPinChangeMonitor;
import gpio.gpio.beaglebone.IrRemoteInput;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Test program that listen vor IR remote reception.
 * @author Koert Zeilstra
 */
public class ReadIrRemote {

    private Gpio gpio;
    private BinaryInputPin pin;

    public ReadIrRemote() throws IOException {
        gpio = new Gpio(new BeagleboneGpioFactory());
        pin = gpio.binaryInputPin(BeagleboneGPio.P9_11);
    }

    public void read1() throws IOException {
        System.out.println("value: " + pin.isHigh());
        boolean previousState = pin.isHigh();
        long previousTime = System.nanoTime();
        InputPinChangeMonitor changeMonitor = pin.monitorChange(Edge.BOTH);
        while(true) {
            changeMonitor.waitForEvent();
            boolean state = pin.isHigh();
            long time = System.nanoTime();
            if (state) {
                System.out.printf("high %d\n", time - previousTime);
            } else {
                System.out.printf("low  %d\n", time - previousTime);
            }
            previousState = state;
            previousTime = time;
        }
    }

    public void read2() throws IOException {
        System.out.println("value: " + pin.isHigh());
        long previousTime = System.nanoTime();
        InputPinChangeMonitor changeMonitor = pin.monitorChange(Edge.BOTH);
        while(true) {
            changeMonitor.waitForEvent();
            long time = System.nanoTime();
            previousTime = time;

            boolean reading = true;
            while (reading) {
                if (changeMonitor.waitForEvent(100) == 0) {
                    reading = false;
                } else {
                    time = System.nanoTime();
                    System.out.printf("%d\n", time - previousTime);
                    previousTime = time;
                }

            }
            System.out.println("timeout");
        }
    }

    public void read3() throws IOException {
        List<Value> values = new ArrayList<Value>();
        List<Long> times = new ArrayList<Long>();
        System.out.println("value: " + pin.isHigh());
        long previousTime = System.nanoTime();
        InputPinChangeMonitor changeMonitor = pin.monitorChange(Edge.BOTH);
        while(true) {
            times.clear();
            changeMonitor.waitForEvent();
            long time = System.nanoTime();
            previousTime = time;

            boolean reading = true;
            while (reading) {
                if (changeMonitor.waitForEvent(100) == 0) {
                    reading = false;
                } else {
                    time = System.nanoTime();
                    times.add(time - previousTime);
                    previousTime = time;
                }

            }
            System.out.println("timeout");
            for (Long t : times) {
                System.out.print(t);
                System.out.print(" ");
            }
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
            for (int i=0; i< times.size(); i++) {
                Value value = values.get(i);
                Long t = times.get(i);
                System.out.printf("%d - %d %d %d %d\n", t, value.accumulated, value.count, value.min, value.max);
            }
        }
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

    public void read5() throws IOException {
        System.out.println("value: " + pin.isHigh());
        long previousTime = System.nanoTime();
        InputPinChangeMonitor changeMonitor = pin.monitorChange(Edge.BOTH);
        readSequence(changeMonitor);

        while(true) {
            List<Long> times = readSequence(changeMonitor);

//            for (int i=0; i< values.size(); i++) {
//                Value value = values.get(i);
//                System.out.printf("%d %d %d %d\n", value.accumulated, value.count, value.min, value.max);
//            }
        }
    }

    public void read6() throws IOException {
        System.out.println("value: " + pin.isHigh());
        IrRemoteInput irRemoteInput = new IrRemoteInput(pin, 100000, 200);

        while(true) {
            String sequence = irRemoteInput.readSequence();
            System.out.println(sequence);
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
            ReadIrRemote readIrRemote = new ReadIrRemote();
            readIrRemote.read6();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
