#include "gpio_EpollDescriptor.h"
#include <jni.h>
#include <stdio.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/epoll.h>

/*
 * Class:     gpio_EpollDescriptor
 * Method:    createEpFd
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_gpio_epoll_EpollDescriptor_createEpFd(JNIEnv *env, jobject obj) {
    return epoll_create(1);
}

/*
 * Class:     gpio_EpollDescriptor
 * Method:    addFile
 * Signature: (ILjava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_gpio_epoll_EpollDescriptor_addFile (JNIEnv *env, jobject obj, jint epFd, jstring fileName) {
    const char *nativeFileName = (*env)->GetStringUTFChars(env, fileName, 0);
    int fd;
    struct epoll_event ev;

    fd = open(nativeFileName, O_RDONLY | O_NONBLOCK);
    if (fd < 0) {
        (*env)->ThrowNew(env, (*env)->FindClass(env, "java/io/FileNotFoundException"), nativeFileName);
    }

    ev.events = EPOLLIN | EPOLLET | EPOLLPRI;
    ev.data.fd = fd;
    if (epoll_ctl(epFd, EPOLL_CTL_ADD, fd, &ev) == -1) {
          (*env)->ThrowNew(env, (*env)->FindClass(env, "gpio/WaitException"), "Failed to epoll_ctl");
    }
    return fd;
}

/*
 * Class:     gpio_EpollDescriptor
 * Method:    removeFile
 * Signature: (II)I
 */
JNIEXPORT void JNICALL Java_gpio_epoll_EpollDescriptor_removeFile (JNIEnv *env, jobject obj, jint epFd, jint fd) {
    struct epoll_event ev;
    if (epoll_ctl(epFd, EPOLL_CTL_DEL, fd, &ev) == -1) {
          (*env)->ThrowNew(env, (*env)->FindClass(env, "gpio/WaitException"), "Failed to epoll_ctl");
    }
}

/*
 * Class:     gpio_epoll_EpollDescriptor
 * Method:    epollWait
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_gpio_epoll_EpollDescriptor_epollWait (JNIEnv *env, jobject obj, jint epFd, jint timeout) {
//    int i;
    int n;
    struct epoll_event events;

//    epoll_wait(epFd, &events, 1, -1);
    if ((n = epoll_wait(epFd, &events, 1, timeout)) == -1) {
       (*env)->ThrowNew(env, (*env)->FindClass(env, "gpio/WaitException"), "Failed to epoll_wait");
    }

/*
    for (i = 0; i<2; i++) { // first time triggers with current state, so ignore
       if ((n = epoll_wait(epFd, &events, 1, timeout)) == -1) {
          (*env)->ThrowNew(env, (*env)->FindClass(env, "gpio/WaitException"), "Failed to epoll_wait");
       }
    }
    */

    return n;
}

/*
 * Class:     gpio_EpollDescriptor
 * Method:    closeEpFd
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_gpio_epoll_EpollDescriptor_closeEpFd(JNIEnv *env, jobject obj, jint epFd) {
    close(epFd);
}
