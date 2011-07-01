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

static x10rt_msg_type runClosureAt_HandlerID;

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

    jint numElems = reader.readJInt();
    jbyteArray arg = env->NewByteArray(numElems);
    if (NULL == arg) {
        fprintf(stderr, "OOM from NewByteArray (num elements = %d)\n", (int)numElems);
        abort();
    }

    env->SetByteArrayRegion(arg, 0, numElems, (jbyte*)reader.cursor);

    env->CallStaticVoidMethod(runClosureAt.targetClass, runClosureAt.targetMethod, arg);
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
                                                                           jint arrayLen, jbyteArray array) {


#if DEBUG
    fprintf(stdout, "MessageHandlers.runClosureAtSendImpl is invoked from jni_message.cc\n");
    //FILE *fp;
    //fp = fopen("/tmp/jnilog.txt","a");
    fprintf(stdout, "jni_message:cc: runClosureAtSendImpl is called\n");
    fprintf(stdout, "jni_message:cc: messagewriter: placeId=%d, arraylen=%d\n", place, arrayLen);
#endif
    
    unsigned long numBytes = sizeof(jint) + arrayLen * sizeof(jbyte);
    MessageWriter writer(numBytes);
    writer.writeJInt(arrayLen);
    env->GetByteArrayRegion(array, 0, arrayLen, (jbyte*)writer.cursor);
    // Byte array, so no need to endian swap

    x10rt_msg_params msg = {place, runClosureAt_HandlerID, writer.buffer, numBytes};
    x10rt_send_msg(&msg);

    //fclose(fp);

    free(msg.msg);

}


/*************************************************************************
 *
 * Support for method registration
 * 
 *************************************************************************/


/*
 * Class:     x10_x10rt_MessageHandlers
 * Method:    initialize
 * Signature: ()V
 *
 * NOTE: At the Java level this is a synchronized method,
 *       therefore we can freely update the backing C data
 *       structures without additional locking in the native level.
 */
JNIEXPORT void JNICALL Java_x10_x10rt_MessageHandlers_initialize(JNIEnv *env, jclass klazz) {

#if DEBUG
	printf("jni_message.cc: MessageHandlers_initialize\n");
#endif

    /* Get a hold of MessageHandlers.runClosureAtReceive and stash away its invoke information */
    jmethodID receiveId1 = env->GetStaticMethodID(klazz, "runClosureAtReceive", "([B)V");
    if (NULL == receiveId1) {
        fprintf(stderr, "Unable to resolve methodID for MessageHandlers.runClosureAtReceive");
        abort();
    }
    jclass globalClass = (jclass)env->NewGlobalRef(klazz);
    if (NULL == globalClass) {
        fprintf(stderr, "OOM while attempting to allocate global reference for MessageHandlers class\n");
        abort();
    }        
    runClosureAt.targetClass  = globalClass;
    runClosureAt.targetMethod = receiveId1;

    /* Register message receiver functions with X10RT native layer*/
    runClosureAt_HandlerID    = x10rt_register_msg_receiver(&jni_messageReceiver_runClosureAt, NULL, NULL, NULL, NULL);
    x10rt_registration_complete();
}
