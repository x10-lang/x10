#ifndef __int64
#define __int64 __int64_t
#endif

#include <unistd.h>
#include <sys/types.h>
#include <assert.h>

#include <stdio.h>

#ifdef __CYGWIN__
#include <windows.h>
#endif

#undef __stdcall
#define __stdcall
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
#ifdef __CYGWIN__
    char exe[MAX_PATH];
    long res = GetModuleFileName(NULL, exe, MAX_PATH);
    if (res == 0 || res == MAX_PATH) {
        strcpy(exe, "java");
    }
#else
    char *exe = const_cast<char*>("java");
#endif
    char *argv_0[] = { exe, NULL };
    int argc = 1;
    char** argv = argv_0;
    x10rt_init(&argc, &argv);
    return 0;
}
    

/*
 * Class:     x10_x10rt_X10RT
 * Method:    x10rt_registration_complete
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_x10_x10rt_X10RT_x10rt_1registration_1complete(JNIEnv *, jclass) {
    x10rt_registration_complete();
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


/*
 * Class:     x10_x10rt_X10RT
 * Method:    x10rt_blocking_probe
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_X10RT_x10rt_1blocking_1probe(JNIEnv *, jclass) {
    x10rt_probe();
}
