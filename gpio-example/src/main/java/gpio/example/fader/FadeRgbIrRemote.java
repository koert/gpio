package gpio.example.fader;

import gpio.BeagleboneGPio;
import gpio.BeagleboneGpioFactory;
import gpio.Gpio;
import gpio.PwmOutputPin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Test program that fades RGB leds with IR remote receiver input.
 * @author Koert Zeilstra
 */
public class FadeRgbIrRemote {

    private Gpio gpio = new Gpio(new BeagleboneGpioFactory());
    private BlockingQueue<IrInput> inputQueue = new LinkedBlockingQueue<IrInput>();
    private BlockingQueue<Command> commandQueue = new LinkedBlockingQueue<Command>();
    private IrInputThread irInputThread;
    private Map<IrInput, Command> inputCommands = new HashMap<IrInput, Command>();
    private PirSensorInputThread pirSensorInputThread;
    private Thread currentCommandThread;

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
    private Color whiteLow = new Color(0.3F, 0.1F, 0.1F);

    private RgbLed rgbLed;
    private FaderThread faderThread;
    private Timer timer = new Timer(true);
    private PwmOutputPin pwmOutputPin;

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
        inputCommands.put(IrInput.KEY_1, new IrKey1());
        inputCommands.put(IrInput.KEY_2, new IrKey2());
        irInputThread = new IrInputThread(commandQueue, gpio.binaryInputPin(BeagleboneGPio.P9_11), inputCommands);
        pirSensorInputThread = new PirSensorInputThread(commandQueue, gpio.binaryInputPin(BeagleboneGPio.P9_13),
                new MotionDetected());
        pwmOutputPin = gpio.pwmOutputPin(BeagleboneGPio.P9_21);
        pwmOutputPin.dutyCycle(0.0F);
        rgbLed = new RgbLed(gpio.pwmOutputPin(BeagleboneGPio.P9_14),
                gpio.pwmOutputPin(BeagleboneGPio.P9_16),
                gpio.pwmOutputPin(BeagleboneGPio.P9_22));
    }

    public void start() throws IOException {
        faderThread = new FaderThread(rgbLed, colors);
        // faderThread.start();
        irInputThread.setDaemon(true);
        irInputThread.start();
        pirSensorInputThread.setDaemon(true);
        pirSensorInputThread.start();
        boolean running = true;
        while (running) {
            running = takeFromCommandQueue();
//            running = takeFromIrInput();
        }
    }

    private boolean takeFromCommandQueue() throws IOException {
        boolean running = true;
        Command command = null;
        try {
            command = commandQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        if (command != null) {
            if (currentCommandThread != null) {
                System.out.println("interrupt");
                currentCommandThread.interrupt();
                try {
                    currentCommandThread.join(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            currentCommandThread = new Thread(command);
            currentCommandThread.run();
        }
        return running;
    }

    private boolean takeFromIrInput() throws IOException {
        boolean running = true;
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
                rgbLed.fadeTo(Color.black, 1000, 10);
                running = false;
                break;
        }
        return running;
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

    private class MotionDetected extends Command {
        @Override
        public void run() {
            System.out.println("MotionDetectedStart on");
            Running running = new Running() {
                private boolean interrupted = false;
                @Override public boolean isRunning() {
                    interrupted = Thread.currentThread().isInterrupted();
                    if (interrupted) {
                        System.out.println("isRunning: " + interrupted);
                    }
                    return !interrupted;
                }
            };
            try {
                rgbLed.fadeTo(whiteLow, 1, running);
//                for(int i=0; isRunning() && i<=100; i++) {
//                    pwmOutputPin.dutyCycle((float) i / 100);
//                    Thread.sleep(10);
//                }
                if (running.isRunning()) {
                    Thread.sleep(10000);
                }
//                for(int i=100; isRunning() && i>=0; i--) {
//                    pwmOutputPin.dutyCycle((float) i / 100);
//                    Thread.sleep(10);
//                }
                System.out.println("MotionDetectedStart off");
//                pwmOutputPin.dutyCycle(0.0F);
                rgbLed.fadeTo(black, 1, running);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class IrKey1 extends Command {
        @Override
        public void run() {
            System.out.println("IrKey1");
        }
    }

    private class IrKey2 extends Command {
        @Override
        public void run() {
            System.out.println("IrKey2");
        }
    }

}
