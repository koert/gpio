package gpio.example.fader;

/**
 * @author Koert Zeilstra
 */
public enum IrInput {

    KEY_1("18.9.1.1.1.1.1.1.1.1.1.3.1.1.1.3.1.1.1.3.1.3.1.3.1.3.1.1.1.3.1.1.1.3.1.1.1.3.1.1.1.1.1.3.1.1.1.1.1.1.1.3.1.1.1.3.1.3.1.1.1.3.1.3.1.3.1.79.18.4.1."),
    KEY_2("xxx18.9.1.1.1.1.1.1.1.1.1.3.1.1.1.3.1.1.1.3.1.3.1.3.1.3.1.1.1.3.1.1.1.3.1.1.1.3.1.1.1.1.1.3.1.1.1.1.1.1.1.3.1.1.1.3.1.3.1.1.1.3.1.3.1.3.1.79.18.4.1.");

    private String sequence;

    private IrInput(String sequence) {
        this.sequence = sequence;
    }

    public static IrInput valueOfSequence(String sequence) {
        IrInput foundValue = null;
        for (IrInput value : values()) {
            if (value.sequence.equals(sequence)) {
                foundValue = value;
            }
        }
        return foundValue;
    }
}
