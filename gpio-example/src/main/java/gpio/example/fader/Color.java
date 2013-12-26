package gpio.example.fader;

/**
 * @author Koert Zeilstra
 */
public class Color {

    public static final Color black = new Color(0, 0, 0);
    public static final Color white = new Color(1, 1, 1);

    private short red;
    private short green;
    private short blue;

    public static final Color BLACK = new Color(0, 0, 0);
    public static final Color WHITE = new Color(1F, 1F, 1F);

    public Color(float red, float green, float blue) {
        this.red = floatToShort(red);
        this.green = floatToShort(green);
        this.blue = floatToShort(blue);
    }

    public short getRed() {
        return red;
    }

    public short getGreen() {
        return green;
    }

    public short getBlue() {
        return blue;
    }

    @Override
    public String toString() {
        return "Color{" +
                "red=" + red +
                ", green=" + green +
                ", blue=" + blue +
                '}';
    }

    private short floatToShort(float value) {
        short result = 0;
        if (value > 1.0F) {
            result = Short.MAX_VALUE;
        } else if (value < 0.0F) {
            result = 0;
        } else {
            result = (short) ((value + 0) * Short.MAX_VALUE);
        }

        return result;
    }
}
