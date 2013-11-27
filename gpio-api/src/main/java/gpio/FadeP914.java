package gpio;

/**
 * Test program that fades the P9_14 output ten times.
 * @author Koert Zeilstra
 */
public class FadeP914 {

    public static void main(String[] args) {
        Gpio gpio = new Gpio();
        try {
            PwmOutputPin pin = gpio.pwmOutputPin(BeagleboneGPio.P9_14);
            for (int i=0; i<10; i++) {
                for (int j=15; j<=1000; j++) {
                    pin.dutyCycle((float) j/10);
                    Thread.sleep(1);
                }
                for (int j=1000; j>=15; j--) {
                    pin.dutyCycle((float) j/10);
                    Thread.sleep(1);
                }
            }
            pin.dutyCycle(0);
            pin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
