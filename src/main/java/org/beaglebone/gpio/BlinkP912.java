package org.beaglebone.gpio;

/**
 * Test program that blinks the P9_12 output 10 ten times.
 * @author Koert Zeilstra
 */
public class BlinkP912 {

    public static void main(String[] args) {
        Gpio gpio = new Gpio();
        try {
            OutputPin usr1 = gpio.pin("P9_12").output();
            System.out.println(usr1);
            for (int i=0; i<10; i++) {
                usr1.low();
                Thread.sleep(1000);
                usr1.high();
                Thread.sleep(1000);
            }
            usr1.low();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
