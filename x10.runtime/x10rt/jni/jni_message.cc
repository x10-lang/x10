#include <unistd.h>
#include <sys/types.h>
#include <assert.h>
#include <string.h>

#include <stdio.h>

#define __int64 __int64_t

#include "x10_x10rt_MessageHandlers.h"

#include <x10rt_front.h>

/*************************************************************************
 *
 * Typedefs and static variables
 * 
 *************************************************************************/

static x10rt_msg_type runClosureAt_HandlerID;

/*
 * Data structure used to cache the information needed to do a
 * JNI invocation up into the JVM
 */
typedef struct methodDesciption {
    jclass targetClass;
    jmethodID targetMethod;
} methodDescription;


static methodDescription runClosureAt;

static JavaVM* theJVM;

#define DEBUG 1

/*************************************************************************
 *
 * Helper functions
 * 
 *************************************************************************/

/*
 * Use theJVM pointer we cached during initialization to acquire the JNIEnv* for the current thread
 */
static JNIEnv* getEnv() {
    JNIEnv *env;
    jint rc = theJVM->GetEnv((void**)&env, JNI_VERSION_1_4);
    if (JNI_OK == rc) return env;
    if (JNI_EDETACHED == rc) {
        JavaVMAttachArgs args;
        args.version = JNI_VERSION_1_4;
        args.name = const_cast<char*>("X10RT Attached Thread");
        args.group = NULL;
        // Not attached to JVM.  Attempt to attach
        rc = theJVM->AttachCurrentThread((void**)&env, &args);
        if (0 == rc) return env;
        fprintf(stderr, "Failed to attach unattached thread to JavaVM\n");
        abort();
    }
    fprintf(stderr, "GetEnv failed with return code %d\n", (int)rc);
    abort();

    return NULL;
}


// TODO: MessageReader and MessageWriter should probably be
//       provided by X10RT itself to enable sharing of one
//       optimized implementation of reading/writing values
//       from/to buffers in network order.

class MessageReader {
public:
    const x10rt_msg_params *msg;
    void *cursor;
#ifndef NDEBUG
    unsigned long bytesRead;
#endif    
    
    MessageReader(const x10rt_msg_params *msg_) : msg(msg_) {
        cursor = msg->msg;
#ifndef NDEBUG
        bytesRead = 0;
#endif
    }

    jboolean readJBoolean() {
#ifndef NDEBUG
        bytesRead += sizeof(jboolean);
        assert(bytesRead <= msg->len);
#endif
        // TODO: Proper endian encoding/decoding
        jboolean ans = *((jboolean*)cursor);
        cursor = (void*) (((char*)cursor)+sizeof(jboolean));
        return ans;
    }

    jint readJInt() {
#ifndef NDEBUG
        bytesRead += sizeof(jint);
        assert(bytesRead <= msg->len);
#endif
        // TODO: Proper endian encoding/decoding
        jint ans = *((jint*)cursor);
        cursor = (void*) (((char*)cursor)+sizeof(jint));
        return ans;
    }

    jfloat readJFloat() {
#ifndef NDEBUG
        bytesRead += sizeof(jfloat);
        assert(bytesRead <= msg->len);
#endif
        // TODO: Proper endian encoding/decoding
        jfloat ans = *((jfloat*)cursor);
        cursor = (void*) (((char*)cursor)+sizeof(jfloat));
        return ans;
    }

    jlong readJLong() {
#ifndef NDEBUG
        bytesRead += sizeof(jlong);
        assert(bytesRead <= msg->len);
#endif
        // TODO: Proper endian encoding/decoding
        jlong ans = *((jlong*)cursor);
        cursor = (void*) (((char*)cursor)+sizeof(jlong));
        return ans;
    }

    jdouble readJDouble() {
#ifndef NDEBUG
        bytesRead += sizeof(jdouble);
        assert(bytesRead <= msg->len);
#endif
        // TODO: Proper endian encoding/decoding
        jdouble ans = *((jdouble*)cursor);
        cursor = (void*) (((char*)cursor)+sizeof(jdouble));
        return ans;
    }

    void endianSwapIfNeeded(int numElems, int elemSizeInBytes) {
        // TODO: Proper endian encoding/decoding.
    }
    
};


class MessageWriter {
public:
    void *buffer;
    void *cursor;
    unsigned long size;
    unsigned long bytesWritten;
    
    MessageWriter(unsigned long initialSizeInBytes) : size(initialSizeInBytes) {
        buffer = malloc(size);
        cursor = buffer;
        bytesWritten = 0;
    }

    void writeJBoolean(jboolean val) {
        bytesWritten += sizeof(jboolean);
        assert(bytesWritten <= size);

        // TODO: Proper endian encoding/decoding
        *((jboolean*)cursor) = val;
        cursor = (void*) (((char*)cursor)+sizeof(jboolean));
    }

    void writeJInt(jint val) {
        bytesWritten += sizeof(jint);
        assert(bytesWritten <= size);

        // TODO: Proper endian encoding/decoding
        *((jint*)cursor) = val;
        cursor = (void*) (((char*)cursor)+sizeof(jint));
    }

    void writeJFloat(jfloat val) {
        bytesWritten += sizeof(jfloat);
        assert(bytesWritten <= size);

        // TODO: Proper endian encoding/decoding
        *((jfloat*)cursor) = val;
        cursor = (void*) (((char*)cursor)+sizeof(jfloat));
    }

    void writeJLong(jlong val) {
        bytesWritten += sizeof(jlong);
        assert(bytesWritten <= size);

        // TODO: Proper endian encoding/decoding
        *((jlong*)cursor) = val;
        cursor = (void*) (((char*)cursor)+sizeof(jlong));
    }

    void writeJDouble(jdouble val) {
        bytesWritten += sizeof(jdouble);
        assert(bytesWritten <= size);

        // TODO: Proper endian encoding/decoding
        *((jdouble*)cursor) = val;
        cursor = (void*) (((char*)cursor)+sizeof(jdouble));
    }

    void endianSwapIfNeeded(int numElems, int elemSizeInBytes) {
        // TODO: Proper endian encoding/decoding.
    }
};



/*************************************************************************
 *
 * Support for receiving messages
 * 
 *************************************************************************/

void jni_messageReceiver_runClosureAt(const x10rt_msg_params *msg) {
    JNIEnv *env = getEnv();
    MessageReader reader(msg);

    jint numElems = reader.readJInt();
    jbyteArray arg = env->NewByteArray(numElems);
    if (NULL == arg) {
        fprintf(stderr, "OOM from NewArray (num elements = %d)\n", (int)numElems);
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


    fprintf(stdout, "MessageHandlers.runClosureAtSendImpl is invoked from jni_message.cc\n");
    //FILE *fp;
    //fp = fopen("/tmp/jnilog.txt","a");
    fprintf(stdout, "jni_message:cc: runClosureAtSendImpl is called\n");
    fprintf(stdout, "jni_message:cc: messagewriter: placeId=%d, arraylen=%d\n", place, arrayLen);

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
 * Method:    initializeMessageHandlers
 * Signature: ()V
 *
 * NOTE: At the Java level this is a synchronized method,
 *       therefore we can freely update the backing C data
 *       structures without additional locking in the native level.
 */
JNIEXPORT void JNICALL Java_x10_x10rt_MessageHandlers_initializeMessageHandlers(JNIEnv *env, jclass klazz) {

#ifdef DEBUG
	printf("jni_message.cc: MessageHandlers_initializeMessageHandlers\n");
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


    /* Stash away JavaVM* so we can use it in receive functions to attach to the JVM.
     * TODO: We actually could modify the X10RT APIs to allow us to pass the JNIEnv*
     *       down through x10rt_probe() to the callback functions, so we didn't have
     *       to reacquire the thread's JNIEnv, but that would require a fairly extensive
     *       plumbing change to X10RT.  Wait to do that until we have measurements that
     *       show us that the cost of calling getEnv actually matters.
     */
    if (env->GetJavaVM(&theJVM) != 0) {
        fprintf(stderr, "Unable to acquire JavaVM*");
        abort();
    }

    /* Register message receiver functions with X10RT native layer*/
    runClosureAt_HandlerID    = x10rt_register_msg_receiver(&jni_messageReceiver_runClosureAt, NULL, NULL, NULL, NULL);
}
