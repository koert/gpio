package org.beaglebone.gpio;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;

/**
 * Output PWM pin.
 * @author Koert Zeilstra
 */
public class OutputPwmPin {
    private PinDefinition pinDefinition;
    private GpioDevice device;
    private OutputStreamWriter period;
    private OutputStreamWriter duty;
    private OutputStreamWriter polarity;
    private long periodNs;
    private long dutyCycle;

    /**
     * Constructor.
     * @param pinDefinition Pin.
     * @param device Device abstraction.
     * @throws java.io.IOException Failed to read/write device.
     */
    OutputPwmPin(PinDefinition pinDefinition, GpioDevice device) throws IOException {
        //To change body of created methods use File | Settings | File Templates.
        this.pinDefinition = pinDefinition;
        this.device = device;
        device.setup(pinDefinition, GpioDevice.PinUse.OUTPUT_PWM);
        File pwmTest = device.findFile(device.getOcpDir(), "pwm_test_" + pinDefinition.getKey(), true);
        period = new OutputStreamWriter(new FileOutputStream(new File(pwmTest, "period")));
        duty = new OutputStreamWriter(new FileOutputStream(new File(pwmTest, "duty")));
        polarity = new OutputStreamWriter(new FileOutputStream(new File(pwmTest, "polarity")));
        frequency((float) 2000.0).dutyCycle((float) 0.0).polarity(false);
    }

    /**
     * @param frequency Frequency.
     * @throws java.io.IOException Failed to read/write device.
     */
    public OutputPwmPin frequency(float frequency) throws IOException {
        if (frequency <= 0.0) {
            throw new IllegalArgumentException("frequency must be greater than 0");
        }
        this.periodNs = BigDecimal.valueOf(1e9).divide(new BigDecimal(frequency)).longValue();
        this.period.write(Long.toString(this.periodNs));
        return this;
    }

    /**
     * @param polarity Polarity.
     * @throws java.io.IOException Failed to read/write device.
     */
    public OutputPwmPin polarity(boolean polarity) throws IOException {
        if (polarity) {
            this.polarity.write("1");
        } else {
            this.polarity.write("0");
        }
        this.polarity.flush();
        return this;
    }

    /**
     * @param dutyCycle Duty cycle percentage.
     * @throws java.io.IOException Failed to read/write device.
     */
    public OutputPwmPin dutyCycle(float dutyCycle) throws IOException {
        if (dutyCycle < 0.0 || dutyCycle > 100.0) {
            new IllegalArgumentException("dutyCycle must have a value from 0.0 to 100.0");
        }
        this.dutyCycle = (long) (this.periodNs * (dutyCycle / 100.0));
        this.duty.write(Long.toString(this.dutyCycle));
        this.duty.flush();
        return this;
    }

    /**
     * Stop using this pin.
     */
    public void close() throws IOException {
        try {
            period.close();
        } catch (IOException e) {
        }
        try {
            duty.close();
        } catch (IOException e) {
        }
        try {
            polarity.close();
        } catch (IOException e) {
        }
        device.stop(pinDefinition);
    }
}
