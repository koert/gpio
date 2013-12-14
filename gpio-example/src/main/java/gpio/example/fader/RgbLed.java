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

    private Color currentColor = new Color(0, 0, 0);

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

    public void fade(Color from, Color to, long delay) throws IOException {
        for (int i = 0; i <= 1000; i++) {
            currentColor.red = from.red  + i * (to.red - from.red) / 1000;
            currentColor.green = from.green + i * (to.green - from.green) / 1000;
            currentColor.blue = from.blue + i * (to.blue - from.blue) / 1000;
            System.out.printf("rgb %1.3f  %1.3f  %1.3f\r", currentColor.red, currentColor.green, currentColor.blue);
            red.dutyCycle(currentColor.red);
            green.dutyCycle(currentColor.green);
            blue.dutyCycle(currentColor.blue);
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
            }
        }
    }

    public void fadeTo(Color color, long delay) throws IOException {
        float fromRed = currentColor.red;
        float fromGreen = currentColor.green;
        float fromBlue = currentColor.blue;

        for (int i = 0; i <= 1000; i++) {
            currentColor.red = fromRed  + i * (color.red - fromRed) / 1000;
            currentColor.green = fromGreen + i * (color.green - fromGreen) / 1000;
            currentColor.blue = fromBlue + i * (color.blue - fromBlue) / 1000;
            System.out.printf("rgb %1.3f  %1.3f  %1.3f\r", currentColor.red, currentColor.green, currentColor.blue);
            red.dutyCycle(currentColor.red);
            green.dutyCycle(currentColor.green);
            blue.dutyCycle(currentColor.blue);
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
            }
        }
    }
}
