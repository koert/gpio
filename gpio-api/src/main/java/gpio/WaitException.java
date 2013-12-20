package gpio;

/**
 * Failure wait for interrupt.
 * @author Koert Zeilstra
 */
public class WaitException extends RuntimeException {
    public WaitException(String message) {
        super(message);
    }
}
