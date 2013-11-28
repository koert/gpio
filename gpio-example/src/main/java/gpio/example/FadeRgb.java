package gpio.example;

import gpio.BeagleboneGPio;
import gpio.BeagleboneGpioFactory;
import gpio.Gpio;
import gpio.PwmOutputPin;

import java.io.IOException;

/**
 * Test program that fades the P9_14 output ten times.
 * @author Koert Zeilstra
 */
public class FadeRgb {

    private Gpio gpio = new Gpio(new BeagleboneGpioFactory());

    private Color[] colors = {
        new Color(1, 0, 0), new Color(0, 1, 0), new Color(0, 0, 1), new Color(0.5F, 0.5F, 0.5F)
    };
    private Color black = new Color(0, 0, 0);
    private Color white = new Color(1, 1, 1);

    private PwmOutputPin red = gpio.pwmOutputPin(BeagleboneGPio.P9_14);
    private PwmOutputPin green = gpio.pwmOutputPin(BeagleboneGPio.P9_16);
    private PwmOutputPin blue = gpio.pwmOutputPin(BeagleboneGPio.P9_22);

    public static void main(String[] args) {
        FadeRgb fade = null;
        try {
            fade = new FadeRgb();
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

    public FadeRgb() throws IOException {
    }

    public void fade() throws IOException {

        Color currentColor = black;
        for (Color color : colors) {
            fade(currentColor, color);
            currentColor = color;
        }

//        for (int i=0; i<10; i++) {
//            for (int j=15; j<=1000; j++) {
//                pin.dutyCycle((float) j/10);
//                Thread.sleep(1);
//            }
//            for (int j=1000; j>=15; j--) {
//                pin.dutyCycle((float) j/10);
//                Thread.sleep(1);
//            }
//        }
//        pin.dutyCycle(0);
//        pin.close();
    }

    public void close() throws IOException {
        red.close();
        green.close();
        blue.close();
    }

    private void fade(Color from, Color to) throws IOException {
        for (int i = 0; i <= 1000; i++) {
            float r = from.red  + i * (to.red - from.red) / 1000;
            float g = from.green + i * (to.green - from.green) / 1000;
            float b = from.blue + i * (to.blue - from.blue) / 1000;
            System.out.print("rgb " + r + " " + g + " " + b + "    \r");
            red.dutyCycle(r * 100);
            green.dutyCycle(g * 100);
            blue.dutyCycle(b * 100);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }
    }

    public class Color {
        public float red;
        public float green;
        public float blue;

        public Color(float red, float green, float blue) {
            this.red = red;
            this.green = green;
            this.blue = blue;
        }
    }
}
