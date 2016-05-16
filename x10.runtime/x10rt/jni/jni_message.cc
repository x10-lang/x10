/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2016.
 */

#ifndef __int64
#define __int64 __int64_t
#endif

#if defined(__CYGWIN__) || defined(__FreeBSD__)
#undef __STRICT_ANSI__ // Strict ANSI mode is too strict in Cygwin and FreeBSD
#endif

#include <unistd.h>
#include <sys/types.h>
#include <assert.h>
#include <string.h>

#include <stdio.h>

#undef __stdcall
#define __stdcall
#include "x10_x10rt_NativeTransport.h"
#include "x10rt_jni_helpers.h"

/*************************************************************************
 *
 * Typedefs and static variables
 * 
 *************************************************************************/

static methodDescription runClosure;
static methodDescription runSimpleAsync;
static methodDescription get;
static methodDescription getCompleted;
static methodDescription put;

/*************************************************************************
 *
 * Support for receiving messages
 * 
 *************************************************************************/

void jni_messageProcessor(const x10rt_msg_params *msg, methodDescription target) {
    JNIEnv *env = jniHelper_getEnv();
    MessageReader reader(msg);

    jint numElems = msg->len;
    jbyteArray arg = env->NewByteArray(numElems);
    if (NULL == arg) {
        char msg[64];
        snprintf(msg, 64, "OOM from NewByteArray (num elements = %d)", (int)numElems);
        jniHelper_oom(env, msg);
        return;
    }

    env->SetByteArrayRegion(arg, 0, numElems, (jbyte*)reader.cursor);

    env->CallStaticVoidMethod(target.targetClass, target.targetMethod, arg);
}


void jni_messageReceiver_runClosure(const x10rt_msg_params *msg) {
    jni_messageProcessor(msg, runClosure);
}

void jni_messageReceiver_runSimpleAsync(const x10rt_msg_params *msg) {
    jni_messageProcessor(msg, runSimpleAsync);
}

void jni_messageReceiver_get(const x10rt_msg_params *msg) {
    jni_messageProcessor(msg, get);
}

void jni_messageReceiver_getCompleted(const x10rt_msg_params *msg) {
    jni_messageProcessor(msg, getCompleted);
}

void jni_messageReceiver_put(const x10rt_msg_params *msg) {
    jni_messageProcessor(msg, put);
}


/*************************************************************************
 *
 * Support for sending messages
 * 
 *************************************************************************/


/*
 * Class:     x10_x10rt_NativeTransport
 * Method:    sendJavaRemote
 * Signature: (III[B)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_NativeTransport_sendMessage(JNIEnv *env, jclass klazz,
                                                                  jint place,
                                                                  jint msg_id,
                                                                  jint arrayLen, jbyteArray array) {
    unsigned long numBytes = arrayLen * sizeof(jbyte);
    void* buffer;

    // GetPrimitiveArrayCritical pauses the GC if it is possible and returns a pointer to
    // the actual byte array while GetByteArrayElements will return a pointer to the
    // actual array if the underlying GC supports pinning, if it does not it creates a
    // copy and returns it. On both hotspot and J9 GetByteArrayElements returns a copy
    // of the array while GetPrimitiveArrayCritical returns the pointer to the actual array.
    // The caveat is that GetPrimitiveArrayCritical pauses the GC and x10rt_send_msg
    // blocks until the message is written out  thus if there are more threads running
    // and doing allocation the user could run in to a memory issue. Thus making this
    // configurable via a environment variable.

    if (X10_PAUSE_GC_ON_SEND) {
        buffer = (void*)env->GetPrimitiveArrayCritical(array, 0);
    } else {
        buffer = (void*)env->GetByteArrayElements(array, 0);
    }

    // buffer is a byte array, so no need to endian swap
    x10rt_msg_params msg = {place, msg_id, buffer, numBytes};
    x10rt_send_msg(&msg);

    if (X10_PAUSE_GC_ON_SEND) {
        env->ReleasePrimitiveArrayCritical(array, (jbyte*)buffer, JNI_ABORT);;
    } else {
        env->ReleaseByteArrayElements(array, (jbyte*)buffer, JNI_ABORT);
    }
}

/*************************************************************************
 *
 * Support for method registration
 * 
 *************************************************************************/


/*
 * Class:     x10_x10rt_NativeTransport
 * Method:    registerHandlers
 * Signature: ()V
 *
 * NOTE: At the Java level this is a synchronized method,
 *       therefore we can freely update the backing C data
 *       structures without additional locking in the native level.
 */
JNIEXPORT void JNICALL Java_x10_x10rt_NativeTransport_registerHandlers(JNIEnv *env, jclass klazz) {

    jclass globalClass = (jclass)env->NewGlobalRef(klazz);
    if (NULL == globalClass) {
        jniHelper_abort("OOM while attempting to allocate global reference for NativeTransport class\n");
        return;
    }
    runClosure.targetClass  = globalClass;
    runSimpleAsync.targetClass  = globalClass;
    get.targetClass  = globalClass;
    getCompleted.targetClass  = globalClass;
    put.targetClass = globalClass;
    
    runClosure.targetMethod = env->GetStaticMethodID(klazz, "runClosureAtReceive", "([B)V");
    if (NULL == runClosure.targetMethod) {
        jniHelper_abort("Unable to resolve methodID for NativeTransport.runClosureAtReceive\n");
        return;
    }
    runSimpleAsync.targetMethod = env->GetStaticMethodID(klazz, "runSimpleAsyncAtReceive", "([B)V");
    if (NULL == runSimpleAsync.targetMethod) {
        jniHelper_abort("Unable to resolve methodID for NativeTransport.runSimpleAsyncAtReceive\n");
        return;
    }
    get.targetMethod = env->GetStaticMethodID(klazz, "getReceive", "([B)V");
    if (NULL == get.targetMethod) {
        jniHelper_abort("Unable to resolve methodID for NativeTransport.getReceive\n");
        return;
    }
    getCompleted.targetMethod = env->GetStaticMethodID(klazz, "getCompletedReceive", "([B)V");
    if (NULL == getCompleted.targetMethod) {
        jniHelper_abort("Unable to resolve methodID for NativeTransport.getCompletedReceive\n");
        return;
    }
    put.targetMethod = env->GetStaticMethodID(klazz, "putReceive", "([B)V");
    if (NULL == put.targetMethod) {
        jniHelper_abort("Unable to resolve methodID for NativeTransport.putReceive\n");
        return;
    }
    
    jint closureId = x10rt_register_msg_receiver(&jni_messageReceiver_runClosure, NULL, NULL, NULL, NULL);
    jint simpleAsyncId = x10rt_register_msg_receiver(&jni_messageReceiver_runSimpleAsync, NULL, NULL, NULL, NULL);
    jint getId = x10rt_register_msg_receiver(&jni_messageReceiver_get, NULL, NULL, NULL, NULL);
    jint getCompletedId = x10rt_register_msg_receiver(&jni_messageReceiver_getCompleted, NULL, NULL, NULL, NULL);
    jint putId = x10rt_register_msg_receiver(&jni_messageReceiver_put, NULL, NULL, NULL, NULL);

    jfieldID clsFieldId = env->GetStaticFieldID(klazz, "closureMessageID", "I");
    if (NULL == clsFieldId) {
        jniHelper_abort("Unable to resolve fieldID for NativeTransport.closureMessageID\n");
        return;
    }
    jfieldID asyncFieldId = env->GetStaticFieldID(klazz, "simpleAsyncMessageID", "I");
    if (NULL == asyncFieldId) {
        jniHelper_abort("Unable to resolve fieldID for NativeTransport.simpleAsyncMessageID\n");
        return;
    }
    jfieldID getFieldId = env->GetStaticFieldID(klazz, "getMessageID", "I");
    if (NULL == getFieldId) {
        jniHelper_abort("Unable to resolve fieldID for NativeTransport.getMessageID\n");
        return;
    }
    jfieldID getCompletedFieldId = env->GetStaticFieldID(klazz, "getCompletedMessageID", "I");
    if (NULL == getCompletedFieldId) {
        jniHelper_abort("Unable to resolve fieldID for NativeTransport.getCompletedMessageID\n");
        return;
    }
    jfieldID putFieldId = env->GetStaticFieldID(klazz, "putMessageID", "I");
    if (NULL == putFieldId) {
        jniHelper_abort("Unable to resolve fieldID for NativeTransport.putMessageID\n");
        return;
    }
    
    env->SetStaticIntField(klazz, clsFieldId, closureId);
    env->SetStaticIntField(klazz, asyncFieldId, simpleAsyncId);
    env->SetStaticIntField(klazz, getFieldId, getId);
    env->SetStaticIntField(klazz, getCompletedFieldId, getCompletedId);
    env->SetStaticIntField(klazz, putFieldId, putId);
    
    // We are done with registering message handlers
    x10rt_registration_complete();
}
