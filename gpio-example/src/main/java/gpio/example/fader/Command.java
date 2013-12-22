package gpio.example.fader;

/**
 * @author Koert Zeilstra
 */
public abstract class Command implements Runnable {

    private boolean running = true;

    public void stop() {
        running = false;
    }

    protected boolean isRunning() {
        return running;
    }
}
