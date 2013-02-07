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

static methodDescription runClosure;
static methodDescription runSimpleAsync;

#define DEBUG 0

/*************************************************************************
 *
 * Support for receiving messages
 * 
 *************************************************************************/

void jni_messageReceiver_runClosure(const x10rt_msg_params *msg) {
    JNIEnv *env = jniHelper_getEnv();
    MessageReader reader(msg);

    jint numElems = msg->len;
    int type = msg->type;
    jint jtype = type;
    jbyteArray arg = env->NewByteArray(numElems);
    if (NULL == arg) {
        jniHelper_abort("OOM from NewByteArray (num elements = %d)\n", (int)numElems);
    }

    env->SetByteArrayRegion(arg, 0, numElems, (jbyte*)reader.cursor);

    env->CallStaticVoidMethod(runClosure.targetClass, runClosure.targetMethod, arg, jtype);
}

void jni_messageReceiver_runSimpleAsync(const x10rt_msg_params *msg) {
    JNIEnv *env = jniHelper_getEnv();
    MessageReader reader(msg);

    jint numElems = msg->len;
    int type = msg->type;
    jint jtype = type;
    jbyteArray arg = env->NewByteArray(numElems);
    if (NULL == arg) {
        jniHelper_abort("OOM from NewByteArray (num elements = %d)\n", (int)numElems);
    }

    env->SetByteArrayRegion(arg, 0, numElems, (jbyte*)reader.cursor);

    env->CallStaticVoidMethod(runSimpleAsync.targetClass, runSimpleAsync.targetMethod, arg, jtype);
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
JNIEXPORT void JNICALL Java_x10_x10rt_MessageHandlers_sendMessage(JNIEnv *env, jclass klazz,
                                                                  jint place,
                                                                  jint msg_id,
                                                                  jint arrayLen, jbyteArray array) {


#if DEBUG
    fprintf(stdout, "MessageHandlers.sendMessage is invoked from jni_message.cc\n");
    //FILE *fp;
    //fp = fopen("/tmp/jnilog.txt","a");
    fprintf(stdout, "jni_message:cc: sendMessage is called\n");
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
JNIEXPORT void JNICALL Java_x10_x10rt_MessageHandlers_registerHandlers(JNIEnv *env, jclass klazz) {

    /* Get a hold of MessageHandlers.receiveAsync and stash away its invoke information */
    jmethodID receiveId1 = env->GetStaticMethodID(klazz, "runClosureAtReceive", "([B)V");
    if (NULL == receiveId1) {
        jniHelper_abort("Unable to resolve methodID for MessageHandlers.runClosureAtReceive\n");
    }
    jmethodID receiveId2 = env->GetStaticMethodID(klazz, "runSimpleAsyncAtReceive", "([B)V");
    if (NULL == receiveId1) {
        jniHelper_abort("Unable to resolve methodID for MessageHandlers.runSimpleAsyncAtReceive\n");
    }
    jclass globalClass = (jclass)env->NewGlobalRef(klazz);
    if (NULL == globalClass) {
        jniHelper_abort("OOM while attempting to allocate global reference for MessageHandlers class\n");
    }
    runClosure.targetClass  = globalClass;
    runClosure.targetMethod = receiveId1;

    runSimpleAsync.targetClass  = globalClass;
    runSimpleAsync.targetMethod = receiveId2;

    jint closureId = x10rt_register_msg_receiver(&jni_messageReceiver_runClosure, NULL, NULL, NULL, NULL);
    jint simpleAsyncId = x10rt_register_msg_receiver(&jni_messageReceiver_runSimpleAsync, NULL, NULL, NULL, NULL);

    jfieldID clsFieldId = env->GetStaticFieldID(klazz, "closureMessageID", "I");
    if (NULL == clsFieldId) {
        jniHelper_abort("Unable to resolve fieldID for MessageHandlers.closureMessageID\n");
    }

    jfieldID asyncFieldId = env->GetStaticFieldID(klazz, "simpleAsyncMessageID", "I");
    if (NULL == asyncFieldId) {
        jniHelper_abort("Unable to resolve fieldID for MessageHandlers.simpleAsyncMessageID\n");
    }
    
    env->SetStaticIntField(klazz, clsFieldId, closureId);
    env->SetStaticIntField(klazz, asyncFieldId, simpleAsyncId);
    
    // We are done with registering message handlers
    x10rt_registration_complete();
}
