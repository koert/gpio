package org.beaglebone.gpio;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Device abstraction.
 * @author Koert Zeilstra
 */
public class GpioDevice {

    public static final String DEVICE_EXPORT = "/sys/class/gpio/export";

    public enum PinUse {INPUT_DIGITAL, OUTPUT_DIGITAL, OUTPUT_PWM}

    private Map<PinDefinition, PinUse> exportedPins = new HashMap<PinDefinition, PinUse>();

    public void setup(final PinDefinition pinDefinition, PinUse pinUse) throws IOException, PinConfigurationException {
        if (exportedPins.containsKey(pinDefinition)) {
            throw new PinConfigurationException("Attempted to reconfigure pin: " + pinDefinition.getName());
        } else {
//        if ((fd = open("/sys/class/gpio/export", O_WRONLY)) < 0)
//        {
//            return -1;
//        }
//        len = snprintf(str_gpio, sizeof(str_gpio), "%d", gpio);
//        write(fd, str_gpio, len);
//        close(fd);
            writeToDevice(DEVICE_EXPORT, Integer.toString(pinDefinition.getGpio()));
//    snprintf(filename, sizeof(filename), "/sys/class/gpio/gpio%d/direction", gpio);
//    if ((fd = open(filename, O_WRONLY)) < 0)
//            return -1;
//
//    if (in_flag) {
//        strncpy(direction, "out", ARRAY_SIZE(direction) - 1);
//    } else {
//        strncpy(direction, "in", ARRAY_SIZE(direction) - 1);
//    }
//
//    write(fd, direction, strlen(direction));
//    close(fd);
            String deviceName = MessageFormat.format("/sys/class/gpio/gpio{0}/direction", pinDefinition.getGpio());
            String text = null;
            switch(pinUse) {
                case INPUT_DIGITAL:
                    text = "in";
                    break;
                case OUTPUT_DIGITAL:
                case OUTPUT_PWM:
                    text = "out";
                    break;
            }
            writeToDevice(deviceName, text);
            exportedPins.put(pinDefinition, pinUse);
        }
    }

    public void setValue(PinDefinition pinDefinition, boolean value) throws IOException {
        if (exportedPins.containsKey(pinDefinition)) {
            if (exportedPins.get(pinDefinition) == PinUse.OUTPUT_DIGITAL) {
                String text = null;
                if (value) {
                    text = "1";
                } else {
                    text = "0";
                }
                String deviceName = MessageFormat.format("/sys/class/gpio/gpio{0}/value", pinDefinition.getGpio());
                writeToDevice(deviceName, text);
            } else {
                throw new PinConfigurationException("Pin is not configured for output: " + pinDefinition.getName());
            }
        } else {
            throw new PinConfigurationException("Pin not configured: " + pinDefinition.getName());
        }
//    snprintf(filename, sizeof(filename), "/sys/class/gpio/gpio%d/value", gpio);
//
//    if ((fd = open(filename, O_WRONLY)) < 0)
//            return -1;
//
//    if (value) {
//        strncpy(vstr, "1", ARRAY_SIZE(vstr) - 1);
//    } else {
//        strncpy(vstr, "0", ARRAY_SIZE(vstr) - 1);
//    }
//
//    write(fd, vstr, strlen(vstr));
//    close(fd);
    }

    public boolean getBooleanValue(PinDefinition pinDefinition) {
        return false;
    }

    private void writeToDevice(String device, String text) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(device));
        writer.write(text);
        writer.close();
    }

}
