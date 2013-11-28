GPIO
====

This is a Java API library to access the GPIO ports of a Beagleboard Black.

This is a work-in-progress with the intention of implementing the API in Java with a minimal JNI module in C (necessary
for pin interrupt handling).

Implemented:
* Digital out
* PWM out
* Digital in - read value

Example for blinking an LED:
```java
Gpio gpio = new Gpio(new BeagleboneGpioFactory());
BinaryOutputPin pin = gpio.binaryOutputPin(BeagleboneGPio.P9_12);
for (int i=0; i<10; i++) {
    pin.low();
    Thread.sleep(1000);
    pin.high();
    Thread.sleep(1000);
}
pin.low();
```

Example for reading and waiting for pin value change:
```java
Gpio gpio = new Gpio(new BeagleboneGpioFactory());
BinaryInputPin pin = gpio.binaryInputPin(BeagleboneGPio.P9_11);
System.out.println("value: " + pin.isHigh());
while(true) {
    pin.waitForEdge(Edge.RISING);
    System.out.println("value2: " + pin.isHigh());
}
```