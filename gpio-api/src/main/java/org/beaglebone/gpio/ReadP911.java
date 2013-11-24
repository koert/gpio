package org.beaglebone.gpio;

/**
 * Test program that reads the value of the P9_11 input.
 * @author Koert Zeilstra
 */
public class ReadP911 {

    public static void main(String[] args) {
        Gpio gpio = new Gpio();
        try {
            InputPin pin = gpio.pin(BeagleboneGPio.P9_11).input();
            System.out.println("value: " + pin.isHigh());
            while(true) {
                System.out.println("value2: " + pin.isHigh());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
