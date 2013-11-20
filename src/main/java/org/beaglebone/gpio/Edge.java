package org.beaglebone.gpio;

/**
 * @author Koert Zeilstra
 */
public enum Edge {

    RISING(1), FALLING(2), BOTH(3);

    private int value;

    private Edge(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
