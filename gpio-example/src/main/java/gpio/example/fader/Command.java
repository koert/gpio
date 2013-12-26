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

    protected Running getRunning() {
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
        return running;
    }

}
