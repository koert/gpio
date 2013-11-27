package gpio.example;

import gpio.*;

/**
 * Test program that waits for and reads the value of the P9_11 input.
 * @author Koert Zeilstra
 */
public class ButtonFade {

    public static void main(String[] args) {
    }

    private PwmOutputPin outputPin;
    private Fader fader = new Fader();

    public ButtonFade() {
        Gpio gpio = new Gpio(new BeagleboneGpioFactory());
        try {
            outputPin = gpio.pwmOutputPin(BeagleboneGPio.P9_14);
            BinaryInputPin pin = gpio.binaryInputPin(BeagleboneGPio.P9_11);
            System.out.println("value: " + pin.isHigh());
            while(true) {
                pin.waitForEdge(Edge.RISING);
                System.out.println("value2: " + pin.isHigh());
                Thread thread = new Thread(fader);
                thread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class Fader implements Runnable {
        private boolean running = true;


        private void setRunning(boolean running) {
            this.running = running;
        }

        @Override
        public void run() {
            try {
                int i = 0;
                while (running && i < 2) {
                    fadeUp();
                    fadeDown();
                }
                outputPin.dutyCycle(0);
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        private void fadeUp() throws Exception {
            int j = 15;
            while (j <= 1000) {
                outputPin.dutyCycle((float) j/10);
                Thread.sleep(1);
                j++;
            }

        }

        private void fadeDown() throws Exception {
            int j = 1000;
            while (j >= 15) {
                outputPin.dutyCycle((float) j/10);
                Thread.sleep(1);
                j--;
            }

        }
    }

}
