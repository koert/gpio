package gpio.example.fader;

import gpio.PwmOutputPin;

import java.io.IOException;

/**
 * @author Koert Zeilstra
 */
public class RgbLed {
    private PwmOutputPin red;
    private PwmOutputPin green;
    private PwmOutputPin blue;

    private short currentRed = 0;
    private short currentGreen = 0;
    private short currentBlue = 0;

    public RgbLed(PwmOutputPin red, PwmOutputPin green, PwmOutputPin blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public void close() throws IOException {
        red.close();
        green.close();
        blue.close();
    }

    public void setColor(float r, float g, float b) throws IOException {
        red.dutyCycle(r);
        green.dutyCycle(g);
        blue.dutyCycle(b);
    }

    public void setColor(Color color) throws IOException {
        System.out.println("color: " + color);
        red.dutyCycle(color.getRed());
        green.dutyCycle(color.getGreen());
        blue.dutyCycle(color.getBlue());
    }

    public void fade(Color from, Color to, long delay) throws IOException {
        for (int i = 0; i <= 1000; i++) {
            currentRed = (short) (from.getRed()  + i * (to.getRed() - from.getRed()) / 1000);
            currentGreen = (short) (from.getGreen() + i * (to.getGreen() - from.getGreen()) / 1000);
            currentBlue = (short) (from.getBlue() + i * (to.getBlue() - from.getBlue()) / 1000);
            System.out.printf("rgb %d  %d  %d\r", currentRed, currentGreen, currentBlue);
            red.dutyCycle(currentRed);
            green.dutyCycle(currentGreen);
            blue.dutyCycle(currentBlue);
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
            }
        }
    }

    public void fadeTo(Color color, int loopCount, long delay) throws IOException {
        short fromRed = currentRed;
        short fromGreen = currentGreen;
        short fromBlue = currentBlue;

        System.out.println("color: " + color);

        for (int i = 0; i <= loopCount; i++) {
            System.out.printf("rgb %d %d  %d\n", i, (int) color.getRed() - fromRed, (int) i * (color.getRed() - fromRed) / loopCount);
            currentRed = (short) (fromRed  + i * (color.getRed() - fromRed) / loopCount);
            currentGreen = (short) (fromGreen + i * (color.getGreen() - fromGreen) / loopCount);
            currentBlue = (short) (fromBlue + i * (color.getBlue() - fromBlue) / loopCount);
//            System.out.printf("rgb %d  %d  %d\r", (int) currentRed, (int) currentGreen, (int) currentBlue);
            red.dutyCycle(currentRed);
            green.dutyCycle(currentGreen);
            blue.dutyCycle(currentBlue);
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
            }
        }
//        System.out.println();
    }

    public void fadeTo(Color color, long delay, Running running) throws IOException {
        int fromRed = currentRed;
        int fromGreen = currentGreen;
        int fromBlue = currentBlue;

        for (int i = 0; running.isRunning() && i <= 1000; i++) {
            currentRed = (short) (fromRed  + i * (color.getRed() - fromRed) / 1000);
            currentGreen = (short) (fromGreen + i * (color.getGreen() - fromGreen) / 1000);
            currentBlue = (short) (fromBlue + i * (color.getBlue() - fromBlue) / 1000);
//            System.out.printf("rgb %1.3f  %1.3f  %1.3f\r", currentColor.red, currentColor.green, currentColor.blue);
            red.dutyCycle(currentRed);
            green.dutyCycle(currentGreen);
            blue.dutyCycle(currentBlue);
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
            }
        }
//        System.out.println();
    }
}
