#include <unistd.h>
#include <sys/types.h>
#include <assert.h>

#include <stdio.h>

#define __int64 __int64_t
#include "x10_x10rt_X10RT.h"

#include <x10rt_front.h>

/*
 * Class:     x10_x10rt_X10RT
 * Method:    initializeImpl
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_x10_x10rt_X10RT_initializeImpl(JNIEnv *, jclass) {
    int argc = 0;
    char **argv = NULL;
    x10rt_init(argc, argv);
    return 0;
}

/*
 * Class:     x10_x10rt_X10RT
 * Method:    hereImpl
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_x10_x10rt_X10RT_hereImpl(JNIEnv *, jclass) {
    return (jint)x10rt_here();
}

/*
 * Class:     x10_x10rt_X10RT
 * Method:    finishImpl
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_x10_x10rt_X10RT_finishImpl(JNIEnv *, jclass) {
    fprintf(stderr, "finishImpl called; doing nothing\n");
    return 0;
}

/*
 * Class:     x10_x10rt_X10RT
 * Method:    numNodesImpl
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_x10_x10rt_X10RT_numNodesImpl(JNIEnv *, jclass) {
    return (jint)x10rt_nplaces();
}

/*
 * Class:     x10_x10rt_X10RT
 * Method:    barrierImpl
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_X10RT_barrierImpl(JNIEnv *, jclass) {
    fprintf(stderr, "barrierImpl called; doing nothing\n");
}

/*
 * Class:     x10_x10rt_X10RT
 * Method:    fenceImpl
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_X10RT_fenceImpl(JNIEnv *, jclass) {
    fprintf(stderr, "fenceImpl called; doing nothing\n");
}

/*
 * Class:     x10_x10rt_X10RT
 * Method:    pollImpl
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_X10RT_pollImpl(JNIEnv *, jclass) {
    fprintf(stderr, "pollImpl called; doing nothing\n");
}
