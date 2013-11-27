package gpio.example;

import gpio.BeagleboneGPio;
import gpio.BeagleboneGpioFactory;
import gpio.BinaryInputPin;
import gpio.Gpio;

/**
 * Test program that reads the value of the P9_11 input.
 * @author Koert Zeilstra
 */
public class ReadP911 {

    public static void main(String[] args) {
        Gpio gpio = new Gpio(new BeagleboneGpioFactory());
        try {
            BinaryInputPin pin = gpio.binaryInputPin(BeagleboneGPio.P9_11);
            System.out.println("value: " + pin.isHigh());
            while(true) {
                System.out.println("value2: " + pin.isHigh());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
