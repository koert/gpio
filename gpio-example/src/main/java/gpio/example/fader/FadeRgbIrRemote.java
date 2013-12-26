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
//    private BlockingQueue<IrInput> inputQueue = new LinkedBlockingQueue<IrInput>();
    private BlockingQueue<Command> commandQueue = new LinkedBlockingQueue<Command>(10);
    private IrInputThread irInputThread;
    private Map<IrInput, Command> inputCommands = new HashMap<IrInput, Command>();
    private PirSensorInputThread pirSensorInputThread;
    private Thread currentCommandThread;

    private enum State { ON, OFF, MOTION_ON }

    private Color[] colors0 = {
        new Color(1, 0, 0), new Color(0, 1, 0), new Color(0, 0, 1)
    };
    private Color[] colors = {
            new Color(1F, 0, 0), new Color(0, 1F, 0), new Color(0, 0, 1F),
            new Color(1F, 1F, 0), new Color(0, 1F, 1F), new Color(1F, 0, 1F), new Color(0.2F, 0.2F, 0.2F),
            new Color(0.3F, 1F, 0), new Color(0, 0.3F, 1F), new Color(1F, 0, 0.3F),
            new Color(0.3F, 1F, 1F), new Color(1F, 0.3F, 1F), new Color(1F, 1F, 0.3F),
            new Color(0.3F, 1F, 0.3F)
    };
    private Color nightLight = new Color(0.1F, 0.05F, 0.05F);

    private State state = State.OFF;
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
        inputCommands.put(IrInput.KEY_ON, new IrKeyOn());
        inputCommands.put(IrInput.KEY_OFF, new IrKeyOff());
        inputCommands.put(IrInput.KEY_FADE, new IrKeyFade(3L));
        inputCommands.put(IrInput.KEY_SMOOTH, new IrKeyFade(50L));
        inputCommands.put(IrInput.KEY_FLASH, new IrKeyMotionDetectOn());

        inputCommands.put(IrInput.KEY_RED1, new IrKeyColor(new Color(0.8F, 0, 0)));
        inputCommands.put(IrInput.KEY_GREEN1, new IrKeyColor(new Color(0, 0.8F, 0)));
        inputCommands.put(IrInput.KEY_BLUE1, new IrKeyColor(new Color(0, 0, 0.8F)));

        inputCommands.put(IrInput.KEY_RED2, new IrKeyColor(new Color(0.8F, 0.2F, 0)));
        inputCommands.put(IrInput.KEY_GREEN2, new IrKeyColor(new Color(0, 0.8F, 0.2F)));
        inputCommands.put(IrInput.KEY_BLUE2, new IrKeyColor(new Color(0.2F, 0, 0.8F)));

        inputCommands.put(IrInput.KEY_RED3, new IrKeyColor(new Color(0.8F, 0.4F, 0)));
        inputCommands.put(IrInput.KEY_GREEN3, new IrKeyColor(new Color(0, 0.8F, 0.4F)));
        inputCommands.put(IrInput.KEY_BLUE3, new IrKeyColor(new Color(0.4F, 0, 0.8F)));

        inputCommands.put(IrInput.KEY_RED4, new IrKeyColor(new Color(0.8F, 0.6F, 0)));
        inputCommands.put(IrInput.KEY_GREEN4, new IrKeyColor(new Color(0, 0.8F, 0.6F)));
        inputCommands.put(IrInput.KEY_BLUE4, new IrKeyColor(new Color(0.6F, 0, 0.8F)));

        inputCommands.put(IrInput.KEY_RED5, new IrKeyColor(new Color(0.8F, 0.8F, 0)));
        inputCommands.put(IrInput.KEY_GREEN5, new IrKeyColor(new Color(0, 0.8F, 0.8F)));
        inputCommands.put(IrInput.KEY_BLUE5, new IrKeyColor(new Color(0.8F, 0, 0.8F)));

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
            if (command instanceof MotionDetected || state != State.MOTION_ON) {
                if (currentCommandThread != null) {
                    System.out.println("send interrupt");
                    currentCommandThread.interrupt();
                    try {
                        currentCommandThread.join(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                currentCommandThread = new Thread(command);
                currentCommandThread.start();
            }
        }
        return running;
    }

    public void fade() throws IOException {

        Color currentColor = Color.BLACK;
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
            if (state == State.MOTION_ON) {
                System.out.println("MotionDetectedStart on");
                Running running = new Running() {
                    private boolean interrupted = false;
                    @Override public boolean isRunning() {
                        if (!interrupted) {
                            interrupted = Thread.currentThread().isInterrupted();
                        }
                        if (interrupted) {
                            System.out.println("isRunning: " + interrupted);
                        }
                        return !interrupted;
                    }
                };
                try {
                    rgbLed.fadeTo(nightLight, 1, running);
                    if (running.isRunning()) {
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                        }
                    }
                    if (running.isRunning()) {
                        rgbLed.fadeTo(Color.BLACK, 1, running);
                    }
                    System.out.println("MotionDetected done");
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

    private class IrKeyOn extends Command {
        @Override
        public void run() {
            System.out.println("IrKeyOn");
            try {
                state = State.ON;
                rgbLed.fadeTo(Color.WHITE, 1L, getRunning());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class IrKeyOff extends Command {
        @Override
        public void run() {
            System.out.println("IrKeyOff");
            try {
                state = State.OFF;
                rgbLed.fadeTo(Color.BLACK, 1L, getRunning());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class IrKeyColor extends Command {
        private Color color;
        public IrKeyColor(Color color) {
            this.color = color;
        }
        @Override
        public void run() {
            System.out.println("IrKeyColor " + color);
            try {
                state = State.ON;
                rgbLed.fadeTo(color, 2L, getRunning());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class IrKeyFade extends Command {
        private long delay;

        private IrKeyFade(long delay) {
            this.delay = delay;
        }

        @Override
        public void run() {
            System.out.println("IrKeyFade");
            try {
                state = State.ON;
                Running running = getRunning();
                while (running.isRunning()) {
                    for (int i=0; running.isRunning() && i<colors.length; i++) {
                        System.out.println("color: " + colors[i]);
                        rgbLed.fadeTo(colors[i], 40L, running);
                        try {
                            Thread.sleep(delay * 200);
                        } catch (InterruptedException e) {
                        }
                    }
                }
                rgbLed.fadeTo(Color.BLACK, 100, 10L);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class IrKeyMotionDetectOn extends Command {
        @Override
        public void run() {
            System.out.println("IrKeyMotionDetectOn");
            try {
                state = State.MOTION_ON;
                rgbLed.fadeTo(Color.BLACK, 1L, getRunning());
                rgbLed.setColor(nightLight);
                try {
                    Thread.sleep(10L);
                } catch (InterruptedException e) {
                }
                rgbLed.fadeTo(Color.BLACK, 1L, getRunning());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
