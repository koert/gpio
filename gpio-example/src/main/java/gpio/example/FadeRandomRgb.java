package gpio.example;

import gpio.BeagleboneGPio;
import gpio.BeagleboneGpioFactory;
import gpio.Gpio;
import gpio.PwmOutputPin;

import java.io.IOException;
import java.util.Random;

/**
 * Test program that fades an RGB LED (P9_14, P9_16, P9_22) with random values.
 * @author Koert Zeilstra
 */
public class FadeRandomRgb {

    private Gpio gpio = new Gpio(new BeagleboneGpioFactory());

    private Color black = new Color((short) 0, (short) 0, (short) 0);
    private Color white = new Color(Short.MAX_VALUE, Short.MAX_VALUE, Short.MAX_VALUE);

    private PwmOutputPin red = gpio.pwmOutputPin(BeagleboneGPio.P9_14);
    private PwmOutputPin green = gpio.pwmOutputPin(BeagleboneGPio.P9_16);
    private PwmOutputPin blue = gpio.pwmOutputPin(BeagleboneGPio.P9_22);

    public static void main(String[] args) {
        FadeRandomRgb fade = null;
        try {
            fade = new FadeRandomRgb();
            fade.fade();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fade != null) {
                try {
                    fade.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public FadeRandomRgb() throws IOException {
    }

    public void fade() throws IOException {

        Random random = new Random();
        Color currentColor = black;
        while (true) {
            Color color = new Color(getRandomShort(random), getRandomShort(random), getRandomShort(random));
            System.out.printf("-> %9d %9d %9d        \n", color.red, color.green, color.blue);
            fade(currentColor, color);
            currentColor = color;
        }
    }

    public void close() throws IOException {
        red.close();
        green.close();
        blue.close();
    }

    private short getRandomShort(Random random) {
        return (short) (Math.abs(random.nextInt()) % Short.MAX_VALUE);
    }

    private void fade(Color from, Color to) throws IOException {
        for (int i = 0; i <= 1000; i++) {
            short r = (short) (from.red  + i * (to.red - from.red) / 1000);
            short g = (short) (from.green + i * (to.green - from.green) / 1000);
            short b = (short) (from.blue + i * (to.blue - from.blue) / 1000);
            System.out.printf("rgb %9d  %9d  %9d  \r", r, g, b);
            red.dutyCycle(r);
            green.dutyCycle(g);
            blue.dutyCycle(b);
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
            }
        }
    }

    public class Color {
        public short red;
        public short green;
        public short blue;

        public Color(short red, short green, short blue) {
            this.red = red;
            this.green = green;
            this.blue = blue;
        }
    }
}
