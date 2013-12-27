package gpio.example;

import gpio.*;
import gpio.beaglebone.InputPinChangeMonitor;

/**
 * Test program that waits for and reads the value of the P9_12 input.
 * @author Koert Zeilstra
 */
public class WaitForP912 {

    public static void main(String[] args) {
        Gpio gpio = new Gpio(new BeagleboneGpioFactory());
        try {
            BinaryInputPin pin = gpio.binaryInputPin(BeagleboneGPio.P9_12);
            System.out.println("value: " + pin.isHigh());
            InputPinChangeMonitor changeMonitor = pin.monitorChange(Edge.BOTH);
            while(true) {
                changeMonitor.waitForEvent();
                System.out.println("value2: " + pin.isHigh());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
