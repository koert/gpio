package gpio;

import gpio.epoll.FileMonitor;

import java.io.*;

/**
 * Device abstraction.
 * @author Koert Zeilstra
 */
public abstract class GpioDevice {

    public static final String DEVICE_EXPORT = "/sys/class/gpio/export";
    public static final String DEVICE_UNEXPORT = "/sys/class/gpio/unexport";

    public enum PinUse {INPUT_DIGITAL, OUTPUT_DIGITAL, OUTPUT_PWM;}

    protected boolean debug = true;

    /**
     * Setup pin before use.
     * @param pinDefinition Pin to be setup.
     * @param pinUse How to use this pin.
     * @throws IOException Failed to write to device.
     * @throws PinConfigurationException Failed to configure pin.
     */
    public abstract void setup(PinDefinition pinDefinition, PinUse pinUse) throws IOException, PinConfigurationException;
    /**
     * Setup pin before use.
     * @param pinDefinition Pin to be setup.
     * @throws IOException Failed to write to device.
     * @throws PinConfigurationException Failed to configure pin.
     */
    public abstract void setupPwm(PinDefinition pinDefinition) throws IOException, PinConfigurationException;

    /**
     * Setup pin before use.
     * @param pinDefinition Pin to be setup.
     * @throws IOException Failed to write to device.
     * @throws PinConfigurationException Failed to configure pin.
     */
    public abstract void close(PinDefinition pinDefinition) throws IOException, PinConfigurationException;

    /**
     * Set value of output pin.
     * @param pinDefinition Pin.
     * @param value True: high, false: low.
     * @throws IOException Failed to write to device.
     */
    public abstract void setValue(PinDefinition pinDefinition, boolean value) throws IOException;

    /**
     * Read state of pin.
     * @param pinDefinition Pin.
     * @return True if input is high, otherwise false.
     */
    public abstract boolean getBooleanValue(PinDefinition pinDefinition) throws IOException;

    /**
     * Create monitor for interrupts on file/device.
     * @return File/device monitor.
     */
    public abstract FileMonitor createFileMonitor();

        /**
         * Write text to device.
         * @param device Full device path.
         * @param text Text to write.
         * @throws IOException Failed to write to device.
         */
    public void writeToDevice(String device, String text) throws IOException {
        if (debug) {
            System.out.println("writeToDevice: " + device + " - " + text);
        }
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
        File capeMgrDir = findFile(new File("/sys/devices"), "bone_capemgr", true);
        File slotsFile = new File(capeMgrDir, "slots");
        boolean deviceFound = false;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(slotsFile));
            String line = reader.readLine();
            while(line != null) {
                if (debug) {
                    System.out.println("line: " + line);
                }
                if (line != null && line.contains(name)) {
                    deviceFound = true;
                }
                line = reader.readLine();
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
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
        }
    }

    private void unloadDeviceTree(final String name) throws IOException {
        File capeMgrDir = findFile(new File("/sys/devices"), "bone_capemgr", true);
        File slotsFile = new File(capeMgrDir, "slots");
        boolean deviceFound = false;
        BufferedReader reader = null;
        try {
            String lineNumber = null;
            reader = new BufferedReader(new FileReader(slotsFile));
            String line = reader.readLine();
            while(line != null) {
                if (debug) {
                    System.out.println("line: " + line);
                }
                int index = line.indexOf(':');
                if (index >= 0) {
                    lineNumber = line.substring(0, index);
                }
                line = reader.readLine();
            }
            try {
                reader.close();
            } catch (IOException e) {
            }
            if (lineNumber != null) {
                writeToDevice(slotsFile.getAbsolutePath(), "-" + lineNumber);
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

    File findFile(File directory, String name, boolean required) {
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
