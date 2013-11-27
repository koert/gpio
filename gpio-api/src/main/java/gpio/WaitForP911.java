package gpio;

/**
 * Test program that waits for and reads the value of the P9_11 input.
 * @author Koert Zeilstra
 */
public class WaitForP911 {

    public static void main(String[] args) {
        Gpio gpio = new Gpio(new BeagleboneGpioFactory());
        try {
            BinaryInputPin pin = gpio.binaryInputPin(BeagleboneGPio.P9_11);
            System.out.println("value: " + pin.isHigh());
            while(true) {
                pin.waitForEdge(Edge.RISING);
                System.out.println("value2: " + pin.isHigh());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
