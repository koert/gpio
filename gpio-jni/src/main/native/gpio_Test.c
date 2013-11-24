#include "gpio_Test.h"
#include "jni.h"
#include "stdio.h"

JNIEXPORT void JNICALL Java_gpio_Test_print(JNIEnv *env, jobject obj) {
    printf("Hello world\n");
    return;
}
