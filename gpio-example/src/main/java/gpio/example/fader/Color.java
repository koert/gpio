package gpio.example.fader;

/**
 * @author Koert Zeilstra
 */
public class Color {

    public static final Color black = new Color(0, 0, 0);
    public static final Color white = new Color(1, 1, 1);

    public float red;
    public float green;
    public float blue;

    public Color(float red, float green, float blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }
}
