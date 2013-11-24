package gpio.gpio.epoll;

/**
 * Failed to wait.
 * @author Koert Zeilstra
 */
public class WaitException extends RuntimeException {

    /**
     * Constructor.
     * @param message Error message.
     */
    public WaitException(final String message) {
        super(message);
    }
}
