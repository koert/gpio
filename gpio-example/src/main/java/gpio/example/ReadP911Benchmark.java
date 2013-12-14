package gpio.example;

import gpio.BeagleboneGPio;
import gpio.BeagleboneGpioFactory;
import gpio.BinaryInputPin;
import gpio.Gpio;

/**
 * Test program that reads the value of the P9_11 input.
 * @author Koert Zeilstra
 */
public class ReadP911Benchmark {

    public static void main(String[] args) {
        Gpio gpio = new Gpio(new BeagleboneGpioFactory());
        try {
            BinaryInputPin pin = gpio.binaryInputPin(BeagleboneGPio.P9_11);
            System.out.println("value: " + pin.isHigh());
            long time = System.currentTimeMillis();
            for (int i=0; i<1000; i++) {
                pin.isHigh();
            }
            System.out.println("time: " + (System.currentTimeMillis() - time));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
