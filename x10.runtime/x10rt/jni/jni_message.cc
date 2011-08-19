#ifndef __int64
#define __int64 __int64_t
#endif

#include <unistd.h>
#include <sys/types.h>
#include <assert.h>
#include <string.h>

#include <stdio.h>

#undef __stdcall
#define __stdcall
#include "x10_x10rt_MessageHandlers.h"
#include "x10rt_jni_helpers.h"

/*************************************************************************
 *
 * Typedefs and static variables
 * 
 *************************************************************************/

static methodDescription runClosureAt;

#define DEBUG 0

/*************************************************************************
 *
 * Support for receiving messages
 * 
 *************************************************************************/

void jni_messageReceiver_runClosureAt(const x10rt_msg_params *msg) {
    JNIEnv *env = jniHelper_getEnv();
    MessageReader reader(msg);

    jint numElems = msg->len;
    int type = msg->type;
    jint jtype = type;
    jbyteArray arg = env->NewByteArray(numElems);
    if (NULL == arg) {
        fprintf(stderr, "OOM from NewByteArray (num elements = %d)\n", (int)numElems);
        abort();
    }

    env->SetByteArrayRegion(arg, 0, numElems, (jbyte*)reader.cursor);

    env->CallStaticVoidMethod(runClosureAt.targetClass, runClosureAt.targetMethod, arg, jtype);
}

/*************************************************************************
 *
 * Support for sending messages
 * 
 *************************************************************************/


/*
 * Class:     x10_x10rt_MessageHandlers
 * Method:    sendJavaRemote
 * Signature: (III[B)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_MessageHandlers_runClosureAtSendImpl(JNIEnv *env, jclass klazz,
                                                                           jint place,
                                                                           jint arrayLen, jbyteArray array, jint msg_id) {


#if DEBUG
    fprintf(stdout, "MessageHandlers.runClosureAtSendImpl is invoked from jni_message.cc\n");
    //FILE *fp;
    //fp = fopen("/tmp/jnilog.txt","a");
    fprintf(stdout, "jni_message:cc: runClosureAtSendImpl is called\n");
    fprintf(stdout, "jni_message:cc: messagewriter: placeId=%d, arraylen=%d\n", place, arrayLen);
#endif
    
    unsigned long numBytes = arrayLen * sizeof(jbyte);
    void *buffer;

    // GetPrimitiveArrayCritical pauses the GC if its possible and returns a pointer to
    // the actual byte array while GetByteArrayElements will return a pointer to the
    // actual array if the underlying GC supports pinning, if it does not it creates a
    // copy and returns it. On both hotspot and J9 GetByteArrayElements returns a copy
     // of the array while GetPrimitiveArrayCritical returns the pointer to the actual array.
     // The caveat is that GetPrimitiveArrayCritical pauses the GC and x10rt_send_msg
     // blocks until the message is written out  thus if there are more threads running
     // and doing allocation the user could run in to a memory issue. Thus making this
      // configurable via a environment variable.

    if (X10_PAUSE_GC_ON_SEND) {
        buffer = (void *) env->GetPrimitiveArrayCritical(array, 0);
    } else {
        buffer = (void *) env->GetByteArrayElements(array, 0);
    }
    // Byte array, so no need to endian swap

    x10rt_msg_params msg = {place, msg_id, buffer, numBytes, 0};
    x10rt_send_msg(&msg);
    if (X10_PAUSE_GC_ON_SEND) {
        env->ReleasePrimitiveArrayCritical(array, (jbyte*)buffer, JNI_ABORT);;
    } else {
        env->ReleaseByteArrayElements(array, (jbyte*)buffer, JNI_ABORT);
    }

    //fclose(fp);
}


/*************************************************************************
 *
 * Support for method registration
 * 
 *************************************************************************/


/*
 * Class:     x10_x10rt_MessageHandlers
 * Method:    registerHandlers
 * Signature: ()V
 *
 * NOTE: At the Java level this is a synchronized method,
 *       therefore we can freely update the backing C data
 *       structures without additional locking in the native level.
 */
JNIEXPORT void JNICALL Java_x10_x10rt_MessageHandlers_registerHandlers(JNIEnv *env, jclass klazz,
                                                                           jint arrayLen) {

    /* Get a hold of MessageHandlers.receiveAsync and stash away its invoke information */
    jmethodID receiveId1 = env->GetStaticMethodID(klazz, "receiveAsync", "([BI)V");
    if (NULL == receiveId1) {
        fprintf(stderr, "Unable to resolve methodID for MessageHandlers.receiveAsync");
        abort();
    }
    jclass globalClass = (jclass)env->NewGlobalRef(klazz);
    if (NULL == globalClass) {
        fprintf(stderr, "OOM while attempting to allocate global reference for MessageHandlers class\n");
        abort();
    }
    runClosureAt.targetClass  = globalClass;
    runClosureAt.targetMethod = receiveId1;

    int i;
    int len = arrayLen;
    jint * jids = new jint[len];
    for (i = 0; i < arrayLen; i++) {
        // Register each async  and obtain a message ID
        x10rt_msg_type msg_id =  x10rt_register_msg_receiver(&jni_messageReceiver_runClosureAt, NULL, NULL, NULL, NULL);
        int id = msg_id;
        jids[i] = id;
    }

    jmethodID callback = env->GetStaticMethodID(klazz, "registerHandlersCallback", "([I)V");
    if (NULL == callback) {
        fprintf(stderr, "Unable to resolve methodID for MessageHandlers.registerHandlersCallback");
        abort();
    }

    jintArray arg = env->NewIntArray(arrayLen);
    if (NULL == arg) {
        fprintf(stderr, "OOM from NewIntArray (num elements = %d)\n", (int)arrayLen);
        abort();
    }

    env->SetIntArrayRegion(arg, 0, arrayLen, jids);

    delete [] jids;

    // Pass the obtained message ID's to Java
    env->CallStaticVoidMethod(klazz, callback, arg);

    // We are done with registering message handlers
    x10rt_registration_complete();
}
