package gpio;

/**
 * GPIO PinDefinition.
 * @author Koert Zeilstra
 */
public class PinDefinition {
    private final String name;
    private final String key;
    private final int gpio;
    private final int pwmMuxMode;
    private final int ain;

    public PinDefinition(final String name, final String key, final int gpio, final int pwmMuxMode, final int ain) {
        this.name = name;
        this.key = key;
        this.gpio = gpio;
        this.pwmMuxMode = pwmMuxMode;
        this.ain = ain;
    }

    /**
     * @return Name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return Key.
     */
    public String getKey() {
        return key;
    }

    /**
     * @return GPIO number.
     */
    public int getGpio() {
        return gpio;
    }

    /**
     * @return PWM mux mode.
     */
    public int getPwmMuxMode() {
        return pwmMuxMode;
    }

    /**
     * @return AIN.
     */
    public int getAin() {
        return ain;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PinDefinition that = (PinDefinition) o;

        if (gpio != that.gpio) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return gpio;
    }

    @Override
    public String toString() {
        return "PinDefinition{" +
                "name='" + name + '\'' +
                '}';
    }
}
