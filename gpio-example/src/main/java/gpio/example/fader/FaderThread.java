package gpio.example.fader;

import java.io.IOException;

/**
 * @author Koert Zeilstra
 */
public class FaderThread extends Thread {

    private RgbLed rgbLed;
    private Color[] colors;

    public FaderThread(RgbLed rgbLed, Color[] colors) {
        this.rgbLed = rgbLed;
        this.colors = colors;
    }

    @Override
    public void run() {
        Color currentColor = Color.black;
        try {
            while(!isInterrupted()) {
                for (int i=0; i<colors.length && !isInterrupted(); i++) {
                    Color color = colors[i];
                    rgbLed.fade(currentColor, color, 5);
                    currentColor = color;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
