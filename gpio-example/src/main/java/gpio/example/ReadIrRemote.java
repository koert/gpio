package gpio.example;

import gpio.*;
import gpio.gpio.beaglebone.InputPinChangeMonitor;

/**
 * Test program that listen vor IR remote reception.
 * @author Koert Zeilstra
 */
public class ReadIrRemote {

    public static void main(String[] args) {
        Gpio gpio = new Gpio(new BeagleboneGpioFactory());
        try {
            BinaryInputPin pin = gpio.binaryInputPin(BeagleboneGPio.P9_11);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
