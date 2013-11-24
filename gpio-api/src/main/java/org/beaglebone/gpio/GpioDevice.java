package org.beaglebone.gpio;

import java.io.*;
import java.nio.channels.FileChannel;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Device abstraction.
 * @author Koert Zeilstra
 */
public class GpioDevice {

    public static final String DEVICE_EXPORT = "/sys/class/gpio/export";

    public enum PinUse {INPUT_DIGITAL, OUTPUT_DIGITAL, OUTPUT_PWM;}

    private boolean debug = true;
    private boolean pwmInitialized = false;
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
            String deviceName = MessageFormat.format("/sys/class/gpio/gpio{0}/direction", pinDefinition.getGpio());
            switch(pinUse) {
                case INPUT_DIGITAL:
                    writeToDevice(DEVICE_EXPORT, Integer.toString(pinDefinition.getGpio()));
                    writeToDevice(deviceName, "in");
                    break;
                case OUTPUT_DIGITAL:
                    writeToDevice(DEVICE_EXPORT, Integer.toString(pinDefinition.getGpio()));
                    writeToDevice(deviceName, "out");
                    break;
                case OUTPUT_PWM:
                    initializePwm();
                    loadDeviceTree(MessageFormat.format("bone_pwm_{0}", pinDefinition.getKey()));
                    break;
            }
            exportedPins.put(pinDefinition, pinUse);
        }
    }

    /**
     * Setup pin before use.
     * @param pinDefinition Pin to be setup.
     * @throws IOException Failed to write to device.
     * @throws PinConfigurationException Failed to configure pin.
     */
    public void setupPwm(PinDefinition pinDefinition) throws IOException, PinConfigurationException {
        if (exportedPins.containsKey(pinDefinition)) {
            throw new PinConfigurationException("Attempted to reconfigure pin: " + pinDefinition.getName());
        } else {
            initializePwm();
            loadDeviceTree(MessageFormat.format("bone_pwm_{0}", pinDefinition.getKey()));
            exportedPins.put(pinDefinition, PinUse.OUTPUT_PWM);
        }
    }

    /**
     * Setup pin before use.
     * @param pinDefinition Pin to be setup.
     * @throws IOException Failed to write to device.
     * @throws PinConfigurationException Failed to configure pin.
     */
    public void stop(PinDefinition pinDefinition) throws IOException, PinConfigurationException {
        if (!exportedPins.containsKey(pinDefinition)) {
            throw new PinConfigurationException("Attempted to stop unconfigured pin: " + pinDefinition.getName());
        } else {
            unloadDeviceTree(MessageFormat.format("bone_pwm_{0}", pinDefinition.getKey()));
            exportedPins.remove(pinDefinition);
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
        if (buffer[0] != '0') {
            value = true;
        }
        return value;
    }

    /**
     * Read state of pin.
     * @param pinDefinition Pin.
     * @return True if input is high, otherwise false.
     */
    public FileChannel getChannel(PinDefinition pinDefinition) throws IOException {
        String deviceName = MessageFormat.format("/sys/class/gpio/gpio{0}/value", pinDefinition.getGpio());
        FileInputStream fis = new FileInputStream(deviceName);
        return fis.getChannel();
    }

    /**
     * @return OCP directory.
     */
    File getOcpDir() {
        return ocpDir;
    }

    /**
     * Write text to device.
     * @param device Full device path.
     * @param text Text to write.
     * @throws IOException Failed to write to device.
     */
    void writeToDevice(String device, String text) throws IOException {
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

    private void initializePwm() throws IOException {
        if (!pwmInitialized) {
            pwmInitialized = true;
            loadDeviceTree("am33xx_pwm");
            ocpDir = findFile(new File("/sys/devices"), "ocp", true);
        }
    }

}
