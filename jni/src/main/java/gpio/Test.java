package gpio;

/**
 * @author Koert Zeilstra
 */
public class Test {

    public native void print(); //native method

    static {
        System.loadLibrary("poll4j");
    }

    public static void main(final String[] args) {
        EpollDescriptor test = new EpollDescriptor("/sys/class/hwmon/hwmon3/temp1_input");
        test.waitForEvent();
    }

}
