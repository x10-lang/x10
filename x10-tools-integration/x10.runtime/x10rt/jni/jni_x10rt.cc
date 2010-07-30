#include <unistd.h>
#include <sys/types.h>
#include <assert.h>

#include <stdio.h>

#define __int64 __int64_t
#include "x10_x10rt_X10RT.h"

#include <x10rt_front.h>

/*
 * Class:     x10_x10rt_X10RT
 * Method:    x10rt_init
 * Signature: (I[Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_x10_x10rt_X10RT_x10rt_1init(JNIEnv *, jclass, jint numArgs, jobjectArray args) {
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
 * Method:    x10rt_nhosts
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_x10_x10rt_X10RT_x10rt_1nhosts(JNIEnv *, jclass) {
    return (jint)x10rt_nhosts();

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
 * Method:    x10rt_is_host
 * Signature: (I)Z
 */
JNIEXPORT jboolean JNICALL Java_x10_x10rt_X10RT_x10rt_1is_1host(JNIEnv *, jclass, jint place) {
    return (jint)x10rt_is_host((x10rt_place)place);
}


/*
 * Class:     x10_x10rt_X10RT
 * Method:    x10rt_is_cuda
 * Signature: (I)Z
 */
JNIEXPORT jboolean JNICALL Java_x10_x10rt_X10RT_x10rt_1is_1cuda(JNIEnv *, jclass, jint place) {
    return (jint)x10rt_is_cuda((x10rt_place)place);
}


/*
 * Class:     x10_x10rt_X10RT
 * Method:    x10rt_is_spe
 * Signature: (I)Z
 */
JNIEXPORT jboolean JNICALL Java_x10_x10rt_X10RT_x10rt_1is_1spe(JNIEnv *, jclass, jint place) {
    return (jint)x10rt_is_spe((x10rt_place)place);
}


/*
 * Class:     x10_x10rt_X10RT
 * Method:    x10rt_parent
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_x10_x10rt_X10RT_x10rt_1parent(JNIEnv *, jclass, jint place) {
    return (jint)x10rt_parent((x10rt_place)place);
}


/*
 * Class:     x10_x10rt_X10RT
 * Method:    x10rt_children
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_x10_x10rt_X10RT_x10rt_1nchildren(JNIEnv *, jclass, jint place) {
    return (jint)x10rt_nchildren((x10rt_place)place);
}


/*
 * Class:     x10_x10rt_X10RT
 * Method:    x10rt_child
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_x10_x10rt_X10RT_x10rt_1child(JNIEnv *, jclass, jint host, jint index) {
    return (jint)x10rt_child((x10rt_place)host, (x10rt_place)index);
}


/*
 * Class:     x10_x10rt_X10RT
 * Method:    x10rt_index
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_x10_x10rt_X10RT_x10rt_1child_1index(JNIEnv *, jclass, jint place) {
    return (jint)x10rt_child_index((x10rt_place)place);
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
 * Method:    x10rt_remote_op_fence
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_X10RT_x10rt_1remote_1op_1fence(JNIEnv *, jclass) {
    x10rt_remote_op_fence();
}


/*
 * Class:     x10_x10rt_X10RT
 * Method:    x10rt_barrier
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_X10RT_x10rt_1barrier(JNIEnv *, jclass) {
    x10rt_barrier();
}
