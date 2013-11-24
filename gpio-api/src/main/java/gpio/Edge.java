package gpio;

/**
 * @author Koert Zeilstra
 */
public enum Edge {

    RISING(1, "rising"), FALLING(2, "falling"), BOTH(3, "both");

    private int value;
    private String code;

    private Edge(int value, String code) {
        this.value = value;
        this.code = code;
    }

    public int getValue() {
        return value;
    }

    public String getCode() {
        return code;
    }
}
