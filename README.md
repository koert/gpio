GPIO
====

This is a Java API library to access the GPIO ports of a Beagleboard Black.

This is a work-in-progress with the intention of implementing the API in pure Java.

Implemented:
* Digital out
* PWM out
* Digital in - read value

Example for blinking an LED:
```java
OutputPin pin = gpio.pin(BeagleboneGPio.P9_12).output();
for (int i=0; i<10; i++) {
    pin.low();
    Thread.sleep(1000);
    pin.high();
    Thread.sleep(1000);
}
pin.low();
```