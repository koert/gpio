package gpio.example.fader;

import gpio.BeagleboneGPio;
import gpio.BeagleboneGpioFactory;
import gpio.Gpio;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Test program that fades RGB leds with IR remote receiver input.
 * @author Koert Zeilstra
 */
public class FadeRgbIrRemote {

    private Gpio gpio = new Gpio(new BeagleboneGpioFactory());
    private BlockingQueue<IrInput> inputQueue = new LinkedBlockingQueue<IrInput>();
    private IrInputThread irInputThread;

    private Color[] colors0 = {
        new Color(1, 0, 0), new Color(0, 1, 0), new Color(0, 0, 1)
    };
    private Color[] colors = {
            new Color(1, 0, 0), new Color(0, 1, 0), new Color(0, 0, 1),
            new Color(1, 1, 0), new Color(0, 1, 1), new Color(1, 0, 1), new Color(0.5F, 0.5F, 0.5F),
            new Color(0.5F, 1, 0), new Color(0, 0.5F, 1), new Color(1, 0, 0.5F),
            new Color(0.5F, 1, 1), new Color(1, 0.5F, 1), new Color(1, 1, 0.5F),
            new Color(0.5F, 1, 0.5F),
            new Color(0, 0, 0)
    };
    private Color black = new Color(0, 0, 0);
    private Color white = new Color(1, 1, 1);

    private RgbLed rgbLed;
    private FaderThread faderThread;

    public static void main(String[] args) {
        FadeRgbIrRemote app = null;
        try {
            app = new FadeRgbIrRemote();
            app.start();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (app != null) {
                try {
                    app.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public FadeRgbIrRemote() throws IOException {
        irInputThread = new IrInputThread(inputQueue, gpio.binaryInputPin(BeagleboneGPio.P9_11));
        rgbLed = new RgbLed(gpio.pwmOutputPin(BeagleboneGPio.P9_14),
                gpio.pwmOutputPin(BeagleboneGPio.P9_16),
                gpio.pwmOutputPin(BeagleboneGPio.P9_22));
    }

    public void start() throws IOException {
        faderThread = new FaderThread(rgbLed, colors);
        faderThread.start();
        irInputThread.setDaemon(true);
        irInputThread.start();
        boolean running = true;
        while (running) {
            IrInput input = null;
            try {
                input = inputQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            switch (input) {
                case KEY_1:
                    try {
                        faderThread.interrupt();
                        faderThread.join();
                    } catch (InterruptedException e) {
                    }
                    faderThread = new FaderThread(rgbLed, colors0);
                    faderThread.run();
                    break;
                case KEY_2:
                    try {
                        faderThread.interrupt();
                        faderThread.join();
                    } catch (InterruptedException e) {
                    }
                    rgbLed.fadeTo(Color.black, 10);
                    running = false;
                    break;
            }
        }
    }

    public void fade() throws IOException {

        Color currentColor = black;
        for (Color color : colors) {
            rgbLed.fade(currentColor, color, 10);
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
        rgbLed.close();
    }

//    private void fade(Color from, Color to) throws IOException {
//        for (int i = 0; i <= 1000; i++) {
//
//            float r = from.red  + i * (to.red - from.red) / 1000;
//            float g = from.green + i * (to.green - from.green) / 1000;
//            float b = from.blue + i * (to.blue - from.blue) / 1000;
//            System.out.printf("rgb %1.3f  %1.3f  %1.3f\r", r, g, b);
//            rgbLed.setColor(r, g, b);
//            try {
//                Thread.sleep(10);
//            } catch (InterruptedException e) {
//            }
//        }
//    }
}
