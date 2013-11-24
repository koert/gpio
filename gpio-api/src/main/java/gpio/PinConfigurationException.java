package gpio;

/**
 * Failure to configure pin.
 * @author Koert Zeilstra
 */
public class PinConfigurationException extends RuntimeException {
    public PinConfigurationException(String message) {
        super(message);
    }
}
