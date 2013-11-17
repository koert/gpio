package org.beaglebone.gpio;

/**
 * @author koert
 */
public class Test {

    public static void main(String[] args) {
        Gpio gpio = new Gpio();
        try {
//            Gpio.PinBuilder b1 = gpio.pin("P9_12");  // try GPIO 60 - http://derekmolloy.ie/gpios-on-the-beaglebone-black-using-device-tree-overlays/
//            System.out.println(b1);
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
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
