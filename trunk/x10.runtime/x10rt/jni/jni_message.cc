#include <unistd.h>
#include <sys/types.h>
#include <assert.h>
#include <string.h>

#include <stdio.h>

#define __int64 __int64_t
#include "x10_x10rt_ActiveMessage.h"

#include <x10rt_front.h>

/*************************************************************************
 *
 * Typedefs and static variables
 * 
 *************************************************************************/

/*
 * Static variables to hold the x10rt_msg_types returned from
 * x10rt_register_msg_receiver for the various message handlers.
 */
static x10rt_msg_type V_Handler;
static x10rt_msg_type I_Handler;
static x10rt_msg_type II_Handler;
static x10rt_msg_type III_Handler;
static x10rt_msg_type IIII_Handler;
static x10rt_msg_type J_Handler;
static x10rt_msg_type JJ_Handler;
static x10rt_msg_type IJ_Handler;
static x10rt_msg_type JI_Handler;
static x10rt_msg_type F_Handler;
static x10rt_msg_type FF_Handler;
static x10rt_msg_type IF_Handler;
static x10rt_msg_type JF_Handler;
static x10rt_msg_type FI_Handler;
static x10rt_msg_type FJ_Handler;
static x10rt_msg_type D_Handler;
static x10rt_msg_type DD_Handler;
static x10rt_msg_type ID_Handler;
static x10rt_msg_type FD_Handler;
static x10rt_msg_type JD_Handler;
static x10rt_msg_type DI_Handler;
static x10rt_msg_type DF_Handler;
static x10rt_msg_type DJ_Handler;

static x10rt_msg_type ZArray_Handler;
static x10rt_msg_type BArray_Handler;
static x10rt_msg_type CArray_Handler;
static x10rt_msg_type SArray_Handler;
static x10rt_msg_type IArray_Handler;
static x10rt_msg_type FArray_Handler;
static x10rt_msg_type JArray_Handler;
static x10rt_msg_type DArray_Handler;

static x10rt_msg_type General_Handler;


/*
 * Data structure used to cache the information needed to do a
 * JNI invocation up into the JVM
 */
typedef struct methodDesciption {
    jclass targetClass;
    jmethodID targetMethod;
} methodDescription;


static methodDescription* registeredMethods;
static methodDescription generalReceive;

static JavaVM* theJVM;

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
        buffer = x10rt_msg_realloc(NULL,0,size);
        cursor = buffer;
        bytesWritten = 0;
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

void jni_messageReceiver_V(const x10rt_msg_params *msg) {
    JNIEnv *env = getEnv();
    MessageReader reader(msg);

    int messageId = reader.readJInt();
    jclass targetClass = registeredMethods[messageId].targetClass;
    jmethodID targetMethod = registeredMethods[messageId].targetMethod;

    env->CallStaticVoidMethod(targetClass, targetMethod);
}    


void jni_messageReceiver_I(const x10rt_msg_params *msg) {
    JNIEnv *env = getEnv();
    MessageReader reader(msg);

    int messageId = reader.readJInt();
    jclass targetClass = registeredMethods[messageId].targetClass;
    jmethodID targetMethod = registeredMethods[messageId].targetMethod;

    jint arg1 = reader.readJInt();
    
    env->CallStaticVoidMethod(targetClass, targetMethod, arg1);
}    


void jni_messageReceiver_II(const x10rt_msg_params *msg) {
    JNIEnv *env = getEnv();
    MessageReader reader(msg);

    int messageId = reader.readJInt();
    jclass targetClass = registeredMethods[messageId].targetClass;
    jmethodID targetMethod = registeredMethods[messageId].targetMethod;

    jint arg1 = reader.readJInt();
    jint arg2 = reader.readJInt();
    
    env->CallStaticVoidMethod(targetClass, targetMethod, arg1, arg2);
}    


void jni_messageReceiver_III(const x10rt_msg_params *msg) {
    JNIEnv *env = getEnv();
    MessageReader reader(msg);

    int messageId = reader.readJInt();
    jclass targetClass = registeredMethods[messageId].targetClass;
    jmethodID targetMethod = registeredMethods[messageId].targetMethod;

    jint arg1 = reader.readJInt();
    jint arg2 = reader.readJInt();
    jint arg3 = reader.readJInt();
    
    env->CallStaticVoidMethod(targetClass, targetMethod, arg1, arg2, arg3);
}    


void jni_messageReceiver_IIII(const x10rt_msg_params *msg) {
    JNIEnv *env = getEnv();
    MessageReader reader(msg);

    int messageId = reader.readJInt();
    jclass targetClass = registeredMethods[messageId].targetClass;
    jmethodID targetMethod = registeredMethods[messageId].targetMethod;

    jint arg1 = reader.readJInt();
    jint arg2 = reader.readJInt();
    jint arg3 = reader.readJInt();
    jint arg4 = reader.readJInt();
    
    env->CallStaticVoidMethod(targetClass, targetMethod, arg1, arg2, arg3, arg4);
}    


void jni_messageReceiver_J(const x10rt_msg_params *msg) {
    JNIEnv *env = getEnv();
    MessageReader reader(msg);

    int messageId = reader.readJInt();
    jclass targetClass = registeredMethods[messageId].targetClass;
    jmethodID targetMethod = registeredMethods[messageId].targetMethod;

    jlong arg1 = reader.readJLong();
    
    env->CallStaticVoidMethod(targetClass, targetMethod, arg1);
}    


void jni_messageReceiver_JJ(const x10rt_msg_params *msg) {
    JNIEnv *env = getEnv();
    MessageReader reader(msg);

    int messageId = reader.readJInt();
    jclass targetClass = registeredMethods[messageId].targetClass;
    jmethodID targetMethod = registeredMethods[messageId].targetMethod;

    jlong arg1 = reader.readJLong();
    jlong arg2 = reader.readJLong();
    
    env->CallStaticVoidMethod(targetClass, targetMethod, arg1, arg2);
}    


void jni_messageReceiver_IJ(const x10rt_msg_params *msg) {
    JNIEnv *env = getEnv();
    MessageReader reader(msg);

    int messageId = reader.readJInt();
    jclass targetClass = registeredMethods[messageId].targetClass;
    jmethodID targetMethod = registeredMethods[messageId].targetMethod;

    jint arg1 = reader.readJInt();
    jlong arg2 = reader.readJLong();
    
    env->CallStaticVoidMethod(targetClass, targetMethod, arg1, arg2);
}    


void jni_messageReceiver_JI(const x10rt_msg_params *msg) {
    JNIEnv *env = getEnv();
    MessageReader reader(msg);

    int messageId = reader.readJInt();
    jclass targetClass = registeredMethods[messageId].targetClass;
    jmethodID targetMethod = registeredMethods[messageId].targetMethod;

    jlong arg1 = reader.readJLong();
    jint arg2 = reader.readJInt();
    
    env->CallStaticVoidMethod(targetClass, targetMethod, arg1, arg2);
}    


void jni_messageReceiver_F(const x10rt_msg_params *msg) {
    JNIEnv *env = getEnv();
    MessageReader reader(msg);

    int messageId = reader.readJInt();
    jclass targetClass = registeredMethods[messageId].targetClass;
    jmethodID targetMethod = registeredMethods[messageId].targetMethod;

    jfloat arg1 = reader.readJFloat();
    
    env->CallStaticVoidMethod(targetClass, targetMethod, arg1);
}    


void jni_messageReceiver_FF(const x10rt_msg_params *msg) {
    JNIEnv *env = getEnv();
    MessageReader reader(msg);

    int messageId = reader.readJInt();
    jclass targetClass = registeredMethods[messageId].targetClass;
    jmethodID targetMethod = registeredMethods[messageId].targetMethod;

    jfloat arg1 = reader.readJFloat();
    jfloat arg2 = reader.readJFloat();
    
    env->CallStaticVoidMethod(targetClass, targetMethod, arg1, arg2);
}    


void jni_messageReceiver_IF(const x10rt_msg_params *msg) {
    JNIEnv *env = getEnv();
    MessageReader reader(msg);

    int messageId = reader.readJInt();
    jclass targetClass = registeredMethods[messageId].targetClass;
    jmethodID targetMethod = registeredMethods[messageId].targetMethod;

    jint arg1 = reader.readJInt();
    jfloat arg2 = reader.readJFloat();
    
    env->CallStaticVoidMethod(targetClass, targetMethod, arg1, arg2);
}    


void jni_messageReceiver_JF(const x10rt_msg_params *msg) {
    JNIEnv *env = getEnv();
    MessageReader reader(msg);

    int messageId = reader.readJInt();
    jclass targetClass = registeredMethods[messageId].targetClass;
    jmethodID targetMethod = registeredMethods[messageId].targetMethod;

    jlong arg1 = reader.readJLong();
    jfloat arg2 = reader.readJFloat();
    
    env->CallStaticVoidMethod(targetClass, targetMethod, arg1, arg2);
}    


void jni_messageReceiver_FI(const x10rt_msg_params *msg) {
    JNIEnv *env = getEnv();
    MessageReader reader(msg);

    int messageId = reader.readJInt();
    jclass targetClass = registeredMethods[messageId].targetClass;
    jmethodID targetMethod = registeredMethods[messageId].targetMethod;

    jfloat arg1 = reader.readJFloat();
    jint arg2 = reader.readJInt();
    
    env->CallStaticVoidMethod(targetClass, targetMethod, arg1, arg2);
}    


void jni_messageReceiver_FJ(const x10rt_msg_params *msg) {
    JNIEnv *env = getEnv();
    MessageReader reader(msg);

    int messageId = reader.readJInt();
    jclass targetClass = registeredMethods[messageId].targetClass;
    jmethodID targetMethod = registeredMethods[messageId].targetMethod;

    jfloat arg1 = reader.readJFloat();
    jlong arg2 = reader.readJLong();
    
    env->CallStaticVoidMethod(targetClass, targetMethod, arg1, arg2);
}    


void jni_messageReceiver_D(const x10rt_msg_params *msg) {
    JNIEnv *env = getEnv();
    MessageReader reader(msg);

    int messageId = reader.readJInt();
    jclass targetClass = registeredMethods[messageId].targetClass;
    jmethodID targetMethod = registeredMethods[messageId].targetMethod;

    jdouble arg1 = reader.readJDouble();
    
    env->CallStaticVoidMethod(targetClass, targetMethod, arg1);
}    


void jni_messageReceiver_DD(const x10rt_msg_params *msg) {
    JNIEnv *env = getEnv();
    MessageReader reader(msg);

    int messageId = reader.readJInt();
    jclass targetClass = registeredMethods[messageId].targetClass;
    jmethodID targetMethod = registeredMethods[messageId].targetMethod;

    jdouble arg1 = reader.readJDouble();
    jdouble arg2 = reader.readJDouble();
    
    env->CallStaticVoidMethod(targetClass, targetMethod, arg1, arg2);
}    


void jni_messageReceiver_ID(const x10rt_msg_params *msg) {
    JNIEnv *env = getEnv();
    MessageReader reader(msg);

    int messageId = reader.readJInt();
    jclass targetClass = registeredMethods[messageId].targetClass;
    jmethodID targetMethod = registeredMethods[messageId].targetMethod;

    jint arg1 = reader.readJInt();
    jdouble arg2 = reader.readJDouble();
    
    env->CallStaticVoidMethod(targetClass, targetMethod, arg1, arg2);
}    


void jni_messageReceiver_FD(const x10rt_msg_params *msg) {
    JNIEnv *env = getEnv();
    MessageReader reader(msg);

    int messageId = reader.readJInt();
    jclass targetClass = registeredMethods[messageId].targetClass;
    jmethodID targetMethod = registeredMethods[messageId].targetMethod;

    jfloat arg1 = reader.readJFloat();
    jdouble arg2 = reader.readJDouble();
    
    env->CallStaticVoidMethod(targetClass, targetMethod, arg1, arg2);
}    


void jni_messageReceiver_JD(const x10rt_msg_params *msg) {
    JNIEnv *env = getEnv();
    MessageReader reader(msg);

    int messageId = reader.readJInt();
    jclass targetClass = registeredMethods[messageId].targetClass;
    jmethodID targetMethod = registeredMethods[messageId].targetMethod;

    jlong arg1 = reader.readJLong();
    jdouble arg2 = reader.readJDouble();
    
    env->CallStaticVoidMethod(targetClass, targetMethod, arg1, arg2);
}    


void jni_messageReceiver_DI(const x10rt_msg_params *msg) {
    JNIEnv *env = getEnv();
    MessageReader reader(msg);

    int messageId = reader.readJInt();
    jclass targetClass = registeredMethods[messageId].targetClass;
    jmethodID targetMethod = registeredMethods[messageId].targetMethod;

    jdouble arg1 = reader.readJDouble();
    jint arg2 = reader.readJInt();
    
    env->CallStaticVoidMethod(targetClass, targetMethod, arg1, arg2);
}    


void jni_messageReceiver_DF(const x10rt_msg_params *msg) {
    JNIEnv *env = getEnv();
    MessageReader reader(msg);

    int messageId = reader.readJInt();
    jclass targetClass = registeredMethods[messageId].targetClass;
    jmethodID targetMethod = registeredMethods[messageId].targetMethod;

    jdouble arg1 = reader.readJDouble();
    jfloat arg2 = reader.readJFloat();
    
    env->CallStaticVoidMethod(targetClass, targetMethod, arg1, arg2);
}    


void jni_messageReceiver_DJ(const x10rt_msg_params *msg) {
    JNIEnv *env = getEnv();
    MessageReader reader(msg);

    int messageId = reader.readJInt();
    jclass targetClass = registeredMethods[messageId].targetClass;
    jmethodID targetMethod = registeredMethods[messageId].targetMethod;

    jdouble arg1 = reader.readJDouble();
    jlong arg2 = reader.readJLong();
    
    env->CallStaticVoidMethod(targetClass, targetMethod, arg1, arg2);
}    


void jni_messageReceiver_ZArray(const x10rt_msg_params *msg) {
    JNIEnv *env = getEnv();
    MessageReader reader(msg);

    int messageId = reader.readJInt();
    jclass targetClass = registeredMethods[messageId].targetClass;
    jmethodID targetMethod = registeredMethods[messageId].targetMethod;

    jint numElems = reader.readJInt();
    // No endian swap needed for Boolean array

    jbooleanArray arg = env->NewBooleanArray(numElems);
    if (NULL == arg) {
        fprintf(stderr, "OOM from NewArray (num elements = %d)\n", (int)numElems);
        abort();
    }

    env->SetBooleanArrayRegion(arg, 0, numElems, (jboolean*)reader.cursor);
    env->CallStaticVoidMethod(targetClass, targetMethod, arg);
}


void jni_messageReceiver_BArray(const x10rt_msg_params *msg) {
    JNIEnv *env = getEnv();
    MessageReader reader(msg);

    int messageId = reader.readJInt();
    jclass targetClass = registeredMethods[messageId].targetClass;
    jmethodID targetMethod = registeredMethods[messageId].targetMethod;

    jint numElems = reader.readJInt();
    // No endian swap needed for Byte array
    
    jbyteArray arg = env->NewByteArray(numElems);
    if (NULL == arg) {
        fprintf(stderr, "OOM from NewArray (num elements = %d)\n", (int)numElems);
        abort();
    }

    env->SetByteArrayRegion(arg, 0, numElems, (jbyte*)reader.cursor);
    env->CallStaticVoidMethod(targetClass, targetMethod, arg);
}


void jni_messageReceiver_CArray(const x10rt_msg_params *msg) {
    JNIEnv *env = getEnv();
    MessageReader reader(msg);

    int messageId = reader.readJInt();
    jclass targetClass = registeredMethods[messageId].targetClass;
    jmethodID targetMethod = registeredMethods[messageId].targetMethod;

    jint numElems = reader.readJInt();
    reader.endianSwapIfNeeded(numElems, sizeof(jchar));

    jcharArray arg = env->NewCharArray(numElems);
    if (NULL == arg) {
        fprintf(stderr, "OOM from NewArray (num elements = %d)\n", (int)numElems);
        abort();
    }

    env->SetCharArrayRegion(arg, 0, numElems, (jchar*)reader.cursor);
    env->CallStaticVoidMethod(targetClass, targetMethod, arg);
}


void jni_messageReceiver_SArray(const x10rt_msg_params *msg) {
    JNIEnv *env = getEnv();
    MessageReader reader(msg);

    int messageId = reader.readJInt();
    jclass targetClass = registeredMethods[messageId].targetClass;
    jmethodID targetMethod = registeredMethods[messageId].targetMethod;

    jint numElems = reader.readJInt();
    reader.endianSwapIfNeeded(numElems, sizeof(jshort));

    jshortArray arg = env->NewShortArray(numElems);
    if (NULL == arg) {
        fprintf(stderr, "OOM from NewArray (num elements = %d)\n", (int)numElems);
        abort();
    }

    env->SetShortArrayRegion(arg, 0, numElems, (jshort*)reader.cursor);
    env->CallStaticVoidMethod(targetClass, targetMethod, arg);
}


void jni_messageReceiver_IArray(const x10rt_msg_params *msg) {
    JNIEnv *env = getEnv();
    MessageReader reader(msg);

    int messageId = reader.readJInt();
    jclass targetClass = registeredMethods[messageId].targetClass;
    jmethodID targetMethod = registeredMethods[messageId].targetMethod;

    jint numElems = reader.readJInt();
    reader.endianSwapIfNeeded(numElems, sizeof(jint));

    jintArray arg = env->NewIntArray(numElems);
    if (NULL == arg) {
        fprintf(stderr, "OOM from NewArray (num elements = %d)\n", (int)numElems);
        abort();
    }

    env->SetIntArrayRegion(arg, 0, numElems, (jint*)reader.cursor);
    env->CallStaticVoidMethod(targetClass, targetMethod, arg);
}


void jni_messageReceiver_FArray(const x10rt_msg_params *msg) {
    JNIEnv *env = getEnv();
    MessageReader reader(msg);

    int messageId = reader.readJInt();
    jclass targetClass = registeredMethods[messageId].targetClass;
    jmethodID targetMethod = registeredMethods[messageId].targetMethod;

    jint numElems = reader.readJInt();
    reader.endianSwapIfNeeded(numElems, sizeof(jfloat));

    jfloatArray arg = env->NewFloatArray(numElems);
    if (NULL == arg) {
        fprintf(stderr, "OOM from NewArray (num elements = %d)\n", (int)numElems);
        abort();
    }

    env->SetFloatArrayRegion(arg, 0, numElems, (jfloat*)reader.cursor);
    env->CallStaticVoidMethod(targetClass, targetMethod, arg);
}


void jni_messageReceiver_JArray(const x10rt_msg_params *msg) {
    JNIEnv *env = getEnv();
    MessageReader reader(msg);

    int messageId = reader.readJInt();
    jclass targetClass = registeredMethods[messageId].targetClass;
    jmethodID targetMethod = registeredMethods[messageId].targetMethod;

    jint numElems = reader.readJInt();
    reader.endianSwapIfNeeded(numElems, sizeof(jlong));

    jlongArray arg = env->NewLongArray(numElems);
    if (NULL == arg) {
        fprintf(stderr, "OOM from NewArray (num elements = %d)\n", (int)numElems);
        abort();
    }

    env->SetLongArrayRegion(arg, 0, numElems, (jlong*)reader.cursor);
    env->CallStaticVoidMethod(targetClass, targetMethod, arg);
}


void jni_messageReceiver_DArray(const x10rt_msg_params *msg) {
    JNIEnv *env = getEnv();
    MessageReader reader(msg);

    int messageId = reader.readJInt();
    jclass targetClass = registeredMethods[messageId].targetClass;
    jmethodID targetMethod = registeredMethods[messageId].targetMethod;

    jint numElems = reader.readJInt();
    reader.endianSwapIfNeeded(numElems, sizeof(jdouble));

    jdoubleArray arg = env->NewDoubleArray(numElems);
    if (NULL == arg) {
        fprintf(stderr, "OOM from NewArray (num elements = %d)\n", (int)numElems);
        abort();
    }

    env->SetDoubleArrayRegion(arg, 0, numElems, (jdouble*)reader.cursor);
    env->CallStaticVoidMethod(targetClass, targetMethod, arg);
}

void jni_messageReceiver_general(const x10rt_msg_params *msg) {
    JNIEnv *env = getEnv();
    MessageReader reader(msg);

    int messageId = reader.readJInt();
    jint numElems = reader.readJInt();
    jbyteArray arg = env->NewByteArray(numElems);
    if (NULL == arg) {
        fprintf(stderr, "OOM from NewArray (num elements = %d)\n", (int)numElems);
        abort();
    }

    env->SetByteArrayRegion(arg, 0, numElems, (jbyte*)reader.cursor);

    env->CallStaticVoidMethod(generalReceive.targetClass, generalReceive.targetMethod, messageId, arg);
}


/*************************************************************************
 *
 * Support for sending messages
 * 
 *************************************************************************/


/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendRemote
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendRemote__II(JNIEnv *env, jclass klazz, jint place, jint messageId) {
    unsigned long numBytes = sizeof(jint);
    MessageWriter writer(numBytes);
    writer.writeJInt(messageId);
    
    x10rt_msg_params msg = {place, V_Handler, writer.buffer, numBytes};
    x10rt_send_msg(&msg);
}


/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendRemote
 * Signature: (III)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendRemote__III(JNIEnv *env, jclass klazz, jint place, jint messageId,
                                                                    jint arg1) {
    unsigned long numBytes = sizeof(jint) + sizeof(jint);
    MessageWriter writer(numBytes);
    writer.writeJInt(messageId);
    writer.writeJInt(arg1);
    
    x10rt_msg_params msg = {place, I_Handler, writer.buffer, numBytes};
    x10rt_send_msg(&msg);
}    


/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendRemote
 * Signature: (IIII)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendRemote__IIII(JNIEnv *env, jclass klazz, jint place, jint messageId,
                                                                     jint arg1, jint arg2) {
    unsigned long numBytes = sizeof(jint) + 2*sizeof(jint);
    MessageWriter writer(numBytes);
    writer.writeJInt(messageId);
    writer.writeJInt(arg1);
    writer.writeJInt(arg2);
    
    x10rt_msg_params msg = {place, II_Handler, writer.buffer, numBytes};
    x10rt_send_msg(&msg);
}    


/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendRemote
 * Signature: (IIIII)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendRemote__IIIII(JNIEnv *env, jclass klazz, jint place, jint messageId,
                                                                      jint arg1, jint arg2, jint arg3) {
    unsigned long numBytes = sizeof(jint) + 3*sizeof(jint);
    MessageWriter writer(numBytes);
    writer.writeJInt(messageId);
    writer.writeJInt(arg1);
    writer.writeJInt(arg2);
    writer.writeJInt(arg3);
    
    x10rt_msg_params msg = {place, III_Handler, writer.buffer, numBytes};
    x10rt_send_msg(&msg);
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendRemote
 * Signature: (IIIIII)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendRemote__IIIIII(JNIEnv *env, jclass klazz, jint place, jint messageId,
                                                                       jint arg1, jint arg2, jint arg3, jint arg4) {
    unsigned long numBytes = sizeof(jint) + 4*sizeof(jint);
    MessageWriter writer(numBytes);
    writer.writeJInt(messageId);
    writer.writeJInt(arg1);
    writer.writeJInt(arg2);
    writer.writeJInt(arg3);
    writer.writeJInt(arg4);
    
    x10rt_msg_params msg = {place, IIII_Handler, writer.buffer, numBytes};
    x10rt_send_msg(&msg);
}

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendRemote
 * Signature: (IIJ)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendRemote__IIJ(JNIEnv *env, jclass klazz, jint place, jint messageId,
                                                                    jlong arg1) {
    unsigned long numBytes = sizeof(jint) + sizeof(jlong);
    MessageWriter writer(numBytes);
    writer.writeJInt(messageId);
    writer.writeJLong(arg1);
    
    x10rt_msg_params msg = {place, J_Handler, writer.buffer, numBytes};
    x10rt_send_msg(&msg);
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendRemote
 * Signature: (IIJJ)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendRemote__IIJJ(JNIEnv *env, jclass klazz, jint place, jint messageId,
                                                                     jlong arg1, jlong arg2) {
    unsigned long numBytes = sizeof(jint) + sizeof(jlong) + sizeof(jlong);
    MessageWriter writer(numBytes);
    writer.writeJInt(messageId);
    writer.writeJLong(arg1);
    writer.writeJLong(arg2);
    
    x10rt_msg_params msg = {place, JJ_Handler, writer.buffer, numBytes};
    x10rt_send_msg(&msg);
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendRemote
 * Signature: (IIIJ)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendRemote__IIIJ(JNIEnv *env, jclass klazz, jint place, jint messageId,
                                                                     jint arg1, jlong arg2) {
    unsigned long numBytes = sizeof(jint) + sizeof(jint) + sizeof(jlong);
    MessageWriter writer(numBytes);
    writer.writeJInt(messageId);
    writer.writeJInt(arg1);
    writer.writeJLong(arg2);
    
    x10rt_msg_params msg = {place, IJ_Handler, writer.buffer, numBytes};
    x10rt_send_msg(&msg);
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendRemote
 * Signature: (IIJI)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendRemote__IIJI(JNIEnv *env, jclass klazz, jint place, jint messageId,
                                                                     jlong arg1, jint arg2) {
    unsigned long numBytes = sizeof(jint) + sizeof(jlong) + sizeof(jint);
    MessageWriter writer(numBytes);
    writer.writeJInt(messageId);
    writer.writeJLong(arg1);
    writer.writeJInt(arg2);
    
    x10rt_msg_params msg = {place, JI_Handler, writer.buffer, numBytes};
    x10rt_send_msg(&msg);
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendRemote
 * Signature: (IIF)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendRemote__IIF(JNIEnv *env, jclass klazz, jint place, jint messageId,
                                                                    jfloat arg1) {
    unsigned long numBytes = sizeof(jint) + sizeof(jfloat);
    MessageWriter writer(numBytes);
    writer.writeJInt(messageId);
    writer.writeJInt(arg1);
    
    x10rt_msg_params msg = {place, F_Handler, writer.buffer, numBytes};
    x10rt_send_msg(&msg);
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendRemote
 * Signature: (IIFF)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendRemote__IIFF(JNIEnv *env, jclass klazz, jint place, jint messageId,
                                                                     jfloat arg1, jfloat arg2) {
    unsigned long numBytes = sizeof(jint) + sizeof(jfloat) + sizeof(jfloat);
    MessageWriter writer(numBytes);
    writer.writeJInt(messageId);
    writer.writeJFloat(arg1);
    writer.writeJFloat(arg2);
    
    x10rt_msg_params msg = {place, FF_Handler, writer.buffer, numBytes};
    x10rt_send_msg(&msg);
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendRemote
 * Signature: (IIIF)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendRemote__IIIF(JNIEnv *env, jclass klazz, jint place, jint messageId,
                                                                     jint arg1, jfloat arg2) {
    unsigned long numBytes = sizeof(jint) + sizeof(jint) + sizeof(jfloat);
    MessageWriter writer(numBytes);
    writer.writeJInt(messageId);
    writer.writeJInt(arg1);
    writer.writeJFloat(arg2);
    
    x10rt_msg_params msg = {place, IF_Handler, writer.buffer, numBytes};
    x10rt_send_msg(&msg);
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendRemote
 * Signature: (IIJF)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendRemote__IIJF(JNIEnv *env, jclass klazz, jint place, jint messageId,
                                                                     jlong arg1, jfloat arg2) {
    unsigned long numBytes = sizeof(jint) + sizeof(jlong) + sizeof(jfloat);
    MessageWriter writer(numBytes);
    writer.writeJInt(messageId);
    writer.writeJLong(arg1);
    writer.writeJFloat(arg2);
    
    x10rt_msg_params msg = {place, JF_Handler, writer.buffer, numBytes};
    x10rt_send_msg(&msg);
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendRemote
 * Signature: (IIFI)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendRemote__IIFI(JNIEnv *env, jclass klazz, jint place, jint messageId,
                                                                     jfloat arg1, jint arg2) {
    unsigned long numBytes = sizeof(jint) + sizeof(jfloat) + sizeof(jint);
    MessageWriter writer(numBytes);
    writer.writeJInt(messageId);
    writer.writeJFloat(arg1);
    writer.writeJInt(arg2);
    
    x10rt_msg_params msg = {place, FI_Handler, writer.buffer, numBytes};
    x10rt_send_msg(&msg);
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendRemote
 * Signature: (IIFJ)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendRemote__IIFJ(JNIEnv *env, jclass klazz, jint place, jint messageId,
                                                                     jfloat arg1, jlong arg2) {
    unsigned long numBytes = sizeof(jint) + sizeof(jfloat) + sizeof(jlong);
    MessageWriter writer(numBytes);
    writer.writeJInt(messageId);
    writer.writeJFloat(arg1);
    writer.writeJLong(arg2);
    
    x10rt_msg_params msg = {place, FJ_Handler, writer.buffer, numBytes};
    x10rt_send_msg(&msg);
}

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendRemote
 * Signature: (IID)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendRemote__IID(JNIEnv *env, jclass klazz, jint place, jint messageId,
                                                                    jdouble arg1) {
    unsigned long numBytes = sizeof(jint) + sizeof(jdouble);
    MessageWriter writer(numBytes);
    writer.writeJInt(messageId);
    writer.writeJDouble(arg1);
    
    x10rt_msg_params msg = {place, D_Handler, writer.buffer, numBytes};
    x10rt_send_msg(&msg);
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendRemote
 * Signature: (IIDD)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendRemote__IIDD(JNIEnv *env, jclass klazz, jint place, jint messageId,
                                                                     jdouble arg1, jdouble arg2) {
    unsigned long numBytes = sizeof(jint) + sizeof(jdouble) + sizeof(jdouble);
    MessageWriter writer(numBytes);
    writer.writeJInt(messageId);
    writer.writeJDouble(arg1);
    writer.writeJDouble(arg2);
    
    x10rt_msg_params msg = {place, DD_Handler, writer.buffer, numBytes};
    x10rt_send_msg(&msg);
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendRemote
 * Signature: (IIID)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendRemote__IIID(JNIEnv *env, jclass klazz, jint place, jint messageId,
                                                                     jint arg1, jdouble arg2) {
    unsigned long numBytes = sizeof(jint) + sizeof(jint) + sizeof(jdouble);
    MessageWriter writer(numBytes);
    writer.writeJInt(messageId);
    writer.writeJInt(arg1);
    writer.writeJDouble(arg2);
    
    x10rt_msg_params msg = {place, ID_Handler, writer.buffer, numBytes};
    x10rt_send_msg(&msg);
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendRemote
 * Signature: (IIFD)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendRemote__IIFD(JNIEnv *env, jclass klazz, jint place, jint messageId,
                                                                     jfloat arg1, jdouble arg2) {
    unsigned long numBytes = sizeof(jint) + sizeof(jfloat) + sizeof(jdouble);
    MessageWriter writer(numBytes);
    writer.writeJInt(messageId);
    writer.writeJFloat(arg1);
    writer.writeJDouble(arg2);
    
    x10rt_msg_params msg = {place, FD_Handler, writer.buffer, numBytes};
    x10rt_send_msg(&msg);
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendRemote
 * Signature: (IIJD)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendRemote__IIJD(JNIEnv *env, jclass klazz, jint place, jint messageId,
                                                                     jlong arg1, jdouble arg2) {
    unsigned long numBytes = sizeof(jint) + sizeof(jlong) + sizeof(jdouble);
    MessageWriter writer(numBytes);
    writer.writeJInt(messageId);
    writer.writeJLong(arg1);
    writer.writeJDouble(arg2);
    
    x10rt_msg_params msg = {place, JD_Handler, writer.buffer, numBytes};
    x10rt_send_msg(&msg);
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendRemote
 * Signature: (IIDI)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendRemote__IIDI(JNIEnv *env, jclass klazz, jint place, jint messageId,
                                                                     jdouble arg1, jint arg2) {
    unsigned long numBytes = sizeof(jint) + sizeof(jdouble) + sizeof(jint);
    MessageWriter writer(numBytes);
    writer.writeJInt(messageId);
    writer.writeJDouble(arg1);
    writer.writeJInt(arg2);
    
    x10rt_msg_params msg = {place, DI_Handler, writer.buffer, numBytes};
    x10rt_send_msg(&msg);
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendRemote
 * Signature: (IIDJ)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendRemote__IIDJ(JNIEnv *env, jclass klazz, jint place, jint messageId,
                                                                     jdouble arg1, jlong arg2) {
    unsigned long numBytes = sizeof(jint) + sizeof(jdouble) + sizeof(jlong);
    MessageWriter writer(numBytes);
    writer.writeJInt(messageId);
    writer.writeJDouble(arg1);
    writer.writeJLong(arg2);
    
    x10rt_msg_params msg = {place, DJ_Handler, writer.buffer, numBytes};
    x10rt_send_msg(&msg);
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendRemote
 * Signature: (IIDF)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendRemote__IIDF(JNIEnv *env, jclass klazz, jint place, jint messageId,
                                                                     jdouble arg1, jfloat arg2) {
    unsigned long numBytes = sizeof(jint) + sizeof(jdouble) + sizeof(jfloat);
    MessageWriter writer(numBytes);
    writer.writeJInt(messageId);
    writer.writeJDouble(arg1);
    writer.writeJFloat(arg2);
    
    x10rt_msg_params msg = {place, DF_Handler, writer.buffer, numBytes};
    x10rt_send_msg(&msg);
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendArrayRemote
 * Signature: (III[Z)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendArrayRemote__III_3Z(JNIEnv *env, jclass klazz, jint place, jint messageId,
                                                                            jint arrayLen, jbooleanArray array) {
    unsigned long numBytes = sizeof(jint) + sizeof(jint) + arrayLen * sizeof(jboolean);
    MessageWriter writer(numBytes);
    writer.writeJInt(messageId);
    writer.writeJInt(arrayLen);
    env->GetBooleanArrayRegion(array, 0, arrayLen, (jboolean*)writer.cursor);
    // Boolean array, so no need to endian swap
    
    x10rt_msg_params msg = {place, ZArray_Handler, writer.buffer, numBytes};
    x10rt_send_msg(&msg);
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendArrayRemote
 * Signature: (III[B)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendArrayRemote__III_3B(JNIEnv *env, jclass klazz, jint place, jint messageId,
                                                                            jint arrayLen, jbyteArray array) {
    unsigned long numBytes = sizeof(jint) + sizeof(jint) + arrayLen * sizeof(jbyte);
    MessageWriter writer(numBytes);
    writer.writeJInt(messageId);
    writer.writeJInt(arrayLen);
    env->GetByteArrayRegion(array, 0, arrayLen, (jbyte*)writer.cursor);
    // Byte array, so no need to endian swap
    
    x10rt_msg_params msg = {place, BArray_Handler, writer.buffer, numBytes};
    x10rt_send_msg(&msg);
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendArrayRemote
 * Signature: (III[S)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendArrayRemote__III_3S(JNIEnv *env, jclass klazz, jint place, jint messageId,
                                                                            jint arrayLen, jshortArray array) {
    unsigned long numBytes = sizeof(jint) + sizeof(jint) + arrayLen * sizeof(jshort);
    MessageWriter writer(numBytes);
    writer.writeJInt(messageId);
    writer.writeJInt(arrayLen);
    env->GetShortArrayRegion(array, 0, arrayLen, (jshort*)writer.cursor);
    writer.endianSwapIfNeeded(arrayLen, sizeof(jshort));
    
    x10rt_msg_params msg = {place, SArray_Handler, writer.buffer, numBytes};
    x10rt_send_msg(&msg);
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendArrayRemote
 * Signature: (III[C)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendArrayRemote__III_3C(JNIEnv *env, jclass klazz, jint place, jint messageId,
                                                                            jint arrayLen, jcharArray array) {
    unsigned long numBytes = sizeof(jint) + sizeof(jint) + arrayLen * sizeof(jchar);
    MessageWriter writer(numBytes);
    writer.writeJInt(messageId);
    writer.writeJInt(arrayLen);
    env->GetCharArrayRegion(array, 0, arrayLen, (jchar*)writer.cursor);
    writer.endianSwapIfNeeded(arrayLen, sizeof(jchar));
    
    x10rt_msg_params msg = {place, CArray_Handler, writer.buffer, numBytes};
    x10rt_send_msg(&msg);
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendArrayRemote
 * Signature: (III[I)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendArrayRemote__III_3I(JNIEnv *env, jclass klazz, jint place, jint messageId,
                                                                            jint arrayLen, jintArray array) {
    unsigned long numBytes = sizeof(jint) + sizeof(jint) + arrayLen * sizeof(jint);
    MessageWriter writer(numBytes);
    writer.writeJInt(messageId);
    writer.writeJInt(arrayLen);
    env->GetIntArrayRegion(array, 0, arrayLen, (jint*)writer.cursor);
    writer.endianSwapIfNeeded(arrayLen, sizeof(jint));
    
    x10rt_msg_params msg = {place, IArray_Handler, writer.buffer, numBytes};
    x10rt_send_msg(&msg);
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendArrayRemote
 * Signature: (III[F)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendArrayRemote__III_3F(JNIEnv *env, jclass klazz, jint place, jint messageId,
                                                                            jint arrayLen, jfloatArray array) {
    unsigned long numBytes = sizeof(jint) + sizeof(jint) + arrayLen * sizeof(jfloat);
    MessageWriter writer(numBytes);
    writer.writeJInt(messageId);
    writer.writeJInt(arrayLen);
    env->GetFloatArrayRegion(array, 0, arrayLen, (jfloat*)writer.cursor);
    writer.endianSwapIfNeeded(arrayLen, sizeof(jfloat));
    
    x10rt_msg_params msg = {place, FArray_Handler, writer.buffer, numBytes};
    x10rt_send_msg(&msg);
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendArrayRemote
 * Signature: (III[J)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendArrayRemote__III_3J(JNIEnv *env, jclass klazz, jint place, jint messageId,
                                                                            jint arrayLen, jlongArray array) {
    unsigned long numBytes = sizeof(jint) + sizeof(jint) + arrayLen * sizeof(jlong);
    MessageWriter writer(numBytes);
    writer.writeJInt(messageId);
    writer.writeJInt(arrayLen);
    env->GetLongArrayRegion(array, 0, arrayLen, (jlong*)writer.cursor);
    writer.endianSwapIfNeeded(arrayLen, sizeof(jlong));
    
    x10rt_msg_params msg = {place, JArray_Handler, writer.buffer, numBytes};
    x10rt_send_msg(&msg);
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendArrayRemote
 * Signature: (III[D)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendArrayRemote__III_3D(JNIEnv *env, jclass klazz, jint place, jint messageId,
                                                                            jint arrayLen, jdoubleArray array) {
    unsigned long numBytes = sizeof(jint) + sizeof(jint) + arrayLen * sizeof(jdouble);
    MessageWriter writer(numBytes);
    writer.writeJInt(messageId);
    writer.writeJInt(arrayLen);
    env->GetDoubleArrayRegion(array, 0, arrayLen, (jdouble*)writer.cursor);
    writer.endianSwapIfNeeded(arrayLen, sizeof(jdouble));
    
    x10rt_msg_params msg = {place, DArray_Handler, writer.buffer, numBytes};
    x10rt_send_msg(&msg);
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendArrayRemote
 * Signature: (IIII[I)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendArrayRemote__IIII_3I(JNIEnv *env, jclass klazz, jint, jint, jint, jint, jintArray) {

    fprintf(stderr, "Unimplemented native function\n");


}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendGeneralRemote
 * Signature: (III[B)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendGeneralRemote(JNIEnv *env, jclass klazz, jint place, jint messageId,
                                                                      jint arrayLen, jbyteArray array) {
    unsigned long numBytes = sizeof(jint) + sizeof(jint) + arrayLen * sizeof(jbyte);
    MessageWriter writer(numBytes);
    writer.writeJInt(messageId);
    writer.writeJInt(arrayLen);
    env->GetByteArrayRegion(array, 0, arrayLen, (jbyte*)writer.cursor);
    // Byte array, so no need to endian swap
    
    x10rt_msg_params msg = {place, General_Handler, writer.buffer, numBytes};
    x10rt_send_msg(&msg);
}    

/*************************************************************************
 *
 * Support for method registration
 * 
 *************************************************************************/


/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    initializeMessageHandlers
 * Signature: ()V
 *
 * NOTE: At the Java level this is a synchronized method,
 *       therefore we can freely update the backing C data
 *       structures without additional locking in the native level.
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_initializeMessageHandlers(JNIEnv *env, jclass klazz) {

    /* Initialize native data structures used for mapping active messages to JNI info */
    registeredMethods = (methodDescription*)malloc(x10_x10rt_ActiveMessage_MAX_MESSAGE_ID*sizeof(methodDescription));
    if (NULL == registeredMethods) {
        fprintf(stderr, "OOM while attempting to allocate registredMethods\n");
        abort();
    }        
    memset(registeredMethods, 0, x10_x10rt_ActiveMessage_MAX_MESSAGE_ID*sizeof(methodDescription));

    /* Get a hold of ActiveMessage.receiveGeneral and stash away its invoke information for use by General_Handler */
    jmethodID receiveId = env->GetStaticMethodID(klazz, "receiveGeneral", "(I[B)V");
    if (NULL == receiveId) {
        fprintf(stderr, "Unable to resolve methodID for ActiveMessage.receiveGeneral");
        abort();
    }        
    jclass globalClass = (jclass)env->NewGlobalRef(klazz);
    if (NULL == globalClass) {
        fprintf(stderr, "OOM while attempting to allocate global reference for ActiveMessage class\n");
        abort();
    }        
    generalReceive.targetClass = globalClass;
    generalReceive.targetMethod = receiveId;


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

    /* Register message receiver functions */
    V_Handler = x10rt_register_msg_receiver(&jni_messageReceiver_V, NULL, NULL, NULL, NULL);
    
    I_Handler = x10rt_register_msg_receiver(&jni_messageReceiver_I, NULL, NULL, NULL, NULL);
    II_Handler = x10rt_register_msg_receiver(&jni_messageReceiver_II, NULL, NULL, NULL, NULL);
    III_Handler = x10rt_register_msg_receiver(&jni_messageReceiver_III, NULL, NULL, NULL, NULL);
    IIII_Handler = x10rt_register_msg_receiver(&jni_messageReceiver_IIII, NULL, NULL, NULL, NULL);

    J_Handler = x10rt_register_msg_receiver(&jni_messageReceiver_J, NULL, NULL, NULL, NULL);
    JJ_Handler = x10rt_register_msg_receiver(&jni_messageReceiver_JJ, NULL, NULL, NULL, NULL);
    IJ_Handler = x10rt_register_msg_receiver(&jni_messageReceiver_IJ, NULL, NULL, NULL, NULL);
    JI_Handler = x10rt_register_msg_receiver(&jni_messageReceiver_JI, NULL, NULL, NULL, NULL);
    
    F_Handler = x10rt_register_msg_receiver(&jni_messageReceiver_F, NULL, NULL, NULL, NULL);
    FF_Handler = x10rt_register_msg_receiver(&jni_messageReceiver_FF, NULL, NULL, NULL, NULL);
    IF_Handler = x10rt_register_msg_receiver(&jni_messageReceiver_IF, NULL, NULL, NULL, NULL);
    JF_Handler = x10rt_register_msg_receiver(&jni_messageReceiver_JF, NULL, NULL, NULL, NULL);
    FI_Handler = x10rt_register_msg_receiver(&jni_messageReceiver_FI, NULL, NULL, NULL, NULL);
    FJ_Handler = x10rt_register_msg_receiver(&jni_messageReceiver_FJ, NULL, NULL, NULL, NULL);

    D_Handler = x10rt_register_msg_receiver(&jni_messageReceiver_D, NULL, NULL, NULL, NULL);
    DD_Handler = x10rt_register_msg_receiver(&jni_messageReceiver_DD, NULL, NULL, NULL, NULL);
    ID_Handler = x10rt_register_msg_receiver(&jni_messageReceiver_ID, NULL, NULL, NULL, NULL);
    FD_Handler = x10rt_register_msg_receiver(&jni_messageReceiver_FD, NULL, NULL, NULL, NULL);
    JD_Handler = x10rt_register_msg_receiver(&jni_messageReceiver_JD, NULL, NULL, NULL, NULL);
    DI_Handler = x10rt_register_msg_receiver(&jni_messageReceiver_DI, NULL, NULL, NULL, NULL);
    DF_Handler = x10rt_register_msg_receiver(&jni_messageReceiver_DF, NULL, NULL, NULL, NULL);
    DJ_Handler = x10rt_register_msg_receiver(&jni_messageReceiver_DJ, NULL, NULL, NULL, NULL);

    ZArray_Handler = x10rt_register_msg_receiver(&jni_messageReceiver_ZArray, NULL, NULL, NULL, NULL);
    BArray_Handler = x10rt_register_msg_receiver(&jni_messageReceiver_BArray, NULL, NULL, NULL, NULL);
    CArray_Handler = x10rt_register_msg_receiver(&jni_messageReceiver_CArray, NULL, NULL, NULL, NULL);
    SArray_Handler = x10rt_register_msg_receiver(&jni_messageReceiver_SArray, NULL, NULL, NULL, NULL);
    IArray_Handler = x10rt_register_msg_receiver(&jni_messageReceiver_IArray, NULL, NULL, NULL, NULL);
    FArray_Handler = x10rt_register_msg_receiver(&jni_messageReceiver_FArray, NULL, NULL, NULL, NULL);
    JArray_Handler = x10rt_register_msg_receiver(&jni_messageReceiver_JArray, NULL, NULL, NULL, NULL);
    DArray_Handler = x10rt_register_msg_receiver(&jni_messageReceiver_DArray, NULL, NULL, NULL, NULL);
    
    General_Handler = x10rt_register_msg_receiver(&jni_messageReceiver_general, NULL, NULL, NULL, NULL);
}


/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    registerMethodImpl
 * Signature: (Ljava/lang/reflect/Method;Ljava/lang/Class;I)V
 *
 * NOTE: At the Java level this is a synchronized method,
 *       therefore we can freely update the backing C data
 *       structures without additional locking in the native level.
 */
void JNICALL Java_x10_x10rt_ActiveMessage_registerMethodImpl(JNIEnv *env, jclass klazz,
                                                             jobject targetMethod, jclass targetClass, jint messageId) {
    jmethodID targetMethodID = env->FromReflectedMethod(targetMethod);
    jclass globalTargetClass = (jclass)env->NewGlobalRef(targetClass);
    if (NULL == targetMethodID || NULL == globalTargetClass) {
        fprintf(stderr, "Unable to allocate memory to register message with native code.");
        abort();
    }        

    registeredMethods[messageId].targetClass = globalTargetClass;
    registeredMethods[messageId].targetMethod = targetMethodID;
}
