#ifndef __int64
#define __int64 __int64_t
#endif

#include <unistd.h>
#include <sys/types.h>
#include <assert.h>

#include <stdio.h>

#include "x10_x10rt_X10RT.h"
#include "x10rt_jni_helpers.h"

#include <x10rt_front.h>

/*
 * Class:     x10_x10rt_X10RT
 * Method:    x10rt_init
 * Signature: (I[Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_x10_x10rt_X10RT_x10rt_1init(JNIEnv *env, jclass, jint numArgs, jobjectArray args) {
    initCachedJVM(env);

    assert(numArgs == 0);
    int argc = 0;
    char **argv = NULL;
    x10rt_init(&argc, &argv);
    return 0;
}
    

/*
 * Class:     x10_x10rt_X10RT
 * Method:    x10rt_finalize
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_x10_x10rt_X10RT_x10rt_1finalize(JNIEnv *, jclass) {
    x10rt_finalize();
    return 0;
}


/*
 * Class:     x10_x10rt_X10RT
 * Method:    x10rt_nplaces
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_x10_x10rt_X10RT_x10rt_1nplaces(JNIEnv *, jclass) {
    return (jint)x10rt_nplaces();
}


/*
 * Class:     x10_x10rt_X10RT
 * Method:    x10rt_here
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_x10_x10rt_X10RT_x10rt_1here(JNIEnv *, jclass) {
    return (jint)x10rt_here();
}


/*
 * Class:     x10_x10rt_X10RT
 * Method:    x10rt_probe
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_X10RT_x10rt_1probe(JNIEnv *, jclass) {
    x10rt_probe();
}

