package gpio.example.fader;

import gpio.BeagleboneGPio;
import gpio.BeagleboneGpioFactory;
import gpio.Gpio;
import gpio.PwmOutputPin;

import java.io.IOException;

/**
 * Test program that fades the RGB LED output.
 * @author Koert Zeilstra
 */
public class RgbLedApp {

    private Gpio gpio = new Gpio(new BeagleboneGpioFactory());

    private Color[] colors = {
        new Color(1F, 0, 0), new Color(0, 1F, 0), new Color(0, 0, 1F),
        new Color(1F, 1F, 0), new Color(0, 1F, 1F), new Color(1F, 0, 1F), new Color(0.5F, 0.5F, 0.5F),
        new Color(0.5F, 1F, 0), new Color(0, 0.5F, 1F), new Color(1F, 0, 0.5F),
        new Color(0.5F, 1F, 1F), new Color(1, 0.5F, 1F), new Color(1F, 1F, 0.5F),
        new Color(0.5F, 1F, 0.5F),
        new Color(0, 0, 0)
    };
    private Color black = new Color(0, 0, 0);
    private Color white = new Color(1, 1, 1);

    RgbLed rgbLed;

    private PwmOutputPin red = gpio.pwmOutputPin(BeagleboneGPio.P9_14);
    private PwmOutputPin green = gpio.pwmOutputPin(BeagleboneGPio.P9_16);
    private PwmOutputPin blue = gpio.pwmOutputPin(BeagleboneGPio.P9_22);

    public static void main(String[] args) {
        RgbLedApp fade = null;
        try {
            fade = new RgbLedApp();
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

    public RgbLedApp() throws IOException {
        rgbLed = new RgbLed(red, green, blue);
    }

    public void fade() throws IOException {

        rgbLed.setColor(black);
        for (int i=0; i < 10; i++) {
            for (Color color : colors) {
                rgbLed.fadeTo(color, 100, 100L);
            }
        }
//        rgbLed.fadeTo(new Color(0.5F, 0.5F, 0.5F), 10, 1000L);
        rgbLed.fadeTo(black, 100, 100L);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void close() throws IOException {
        red.close();
        green.close();
        blue.close();
    }

}
