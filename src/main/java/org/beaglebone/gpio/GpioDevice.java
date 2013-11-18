package org.beaglebone.gpio;

import java.io.*;
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
    private File ocpDir;

    /**
     * Setup pin before use.
     * @param pinDefinition Pin to be setup.
     * @param pinUse How to use this pin.
     * @throws IOException Failed to write to device.
     * @throws PinConfigurationException Failed to configure pin.
     */
    public void setup(PinDefinition pinDefinition, PinUse pinUse) throws IOException, PinConfigurationException {
        if (exportedPins.containsKey(pinDefinition)) {
            throw new PinConfigurationException("Attempted to reconfigure pin: " + pinDefinition.getName());
        } else {
            switch(pinUse) {
                case INPUT_DIGITAL:
            }
            String deviceName = MessageFormat.format("/sys/class/gpio/gpio{0}/direction", pinDefinition.getGpio());
            switch(pinUse) {
                case INPUT_DIGITAL:
                    writeToDevice(deviceName, "in");
                    break;
                case OUTPUT_DIGITAL:
                    writeToDevice(deviceName, "out");
                    break;
                case OUTPUT_PWM:
                    loadDeviceTree("am33xx_pwm");
                    ocpDir = findFile(new File("/sys/devices", "ocp"), true);
                    break;
            }
            exportedPins.put(pinDefinition, pinUse);
        }
    }

    /**
     * Set value of output pin.
     * @param pinDefinition Pin.
     * @param value True: high, false: low.
     * @throws IOException Failed to write to device.
     */
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

    /**
     * Read state of pin.
     * @param pinDefinition Pin.
     * @return True if input is high, otherwise false.
     */
    public boolean getBooleanValue(PinDefinition pinDefinition) throws IOException {
        String deviceName = MessageFormat.format("/sys/class/gpio/gpio{0}/value", pinDefinition.getGpio());
        InputStreamReader reader = new InputStreamReader(new FileInputStream(deviceName));

        char[] buffer = new char[1];
        int length = reader.read(buffer);
        if (length == 0) {
            throw new IOException("Failed to read value from device '" + deviceName  + "'");
        }
        boolean value = false;
        if (buffer[1] != '0') {
            value = true;
        }
//        char ch;
//
//        if (!fd)
//        {
//            if ((fd = open_value_file(gpio)) == -1)
//                return -1;
//        }
//
//        lseek(fd, 0, SEEK_SET);
//        read(fd, &ch, sizeof(ch));
//
//        if (ch != '0') {
//            *value = 1;
//        } else {
//            *value = 0;
//        }
        return value;
    }

    /**
     * Write text to device.
     * @param device Full device path.
     * @param text Text to write.
     * @throws IOException Failed to write to device.
     */
    private void writeToDevice(String device, String text) throws IOException {
        System.out.println("writeToDevice: " + device + " - " + text);
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(device));
            writer.write(text);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    private void loadDeviceTree(final String name) throws IOException {
        File capeMgrDir = findFile(new File("/sys/devices"), name, true);
        File slotsFile = new File(capeMgrDir, "slots");
        boolean deviceFound = false;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(slotsFile));
            while(reader.ready()) {
                String line = reader.readLine();
                System.out.println("line: " + line);
                if (line != null && line.contains(name)) {
                    deviceFound = true;
                }
            }
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
        if (!deviceFound) {
            writeToDevice(slotsFile.getAbsolutePath(), name);
        }
    }

    private File findFile(File directory, String name, boolean required) {
        File found = null;
        for(File file : directory.listFiles()) {
            if (file.getName().contains(name)) {
                found = file;
            }
        }
        if (required && found == null) {
            throw new PinConfigurationException(name + " not found " + directory.getAbsolutePath());
        }
        return found;
    }
}
