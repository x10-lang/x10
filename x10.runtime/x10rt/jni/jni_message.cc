#include <unistd.h>
#include <sys/types.h>
#include <assert.h>
#include <string.h>

#include <stdio.h>

#define __int64 __int64_t
#include "x10_x10rt_ActiveMessage.h"

#include <x10rt_front.h>

/*
 * An enumeration to encode the types of primitive Java arrays
 */
enum ArrayTypes { ArrayType_Z = 1, ArrayType_B,
                  ArrayType_C, ArrayType_S,
                  ArrayType_I, ArrayType_F,
                  ArrayType_J, ArrayType_D };


/*
 * Static variables to hold the x10rt_msg_types returned from
 * x10rt_register_msg_receiver for the various message handlers.
 */
static x10rt_msg_type V_Handler;


// static x10rt_msg_type I_Handler;
// static x10rt_msg_type II_Handler;
// static x10rt_msg_type III_Handler;
// static x10rt_msg_type IIII_Handler;
// static x10rt_msg_type J_Handler;
// static x10rt_msg_type JJ_Handler;
// static x10rt_msg_type IJ_Handler;
// static x10rt_msg_type JI_Handler;
// static x10rt_msg_type F_Handler;
// static x10rt_msg_type FF_Handler;
// static x10rt_msg_type IF_Handler;
// static x10rt_msg_type JF_Handler;
// static x10rt_msg_type FI_Handler;
// static x10rt_msg_type FJ_Handler;
// static x10rt_msg_type D_Handler;
// static x10rt_msg_type DD_Handler;
// static x10rt_msg_type ID_Handler;
// static x10rt_msg_type FD_Handler;
// static x10rt_msg_type JD_Handler;
// static x10rt_msg_type DI_Handler;
// static x10rt_msg_type DF_Handler;
// static x10rt_msg_type DJ_Handler;
// static x10rt_msg_type ArrayHandler;
// static x10rt_msg_type GenericHandler;


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


/**********************************************************************************************************************
 *
 * Support for receiving messages
 * 
 **********************************************************************************************************************/

void jni_messageReceiver_V(const x10rt_msg_params *msg) {
    fprintf(stderr, "HI DAVE in messageReceiver_V at place %d", (jint)x10rt_here());

    JNIEnv *env;
    if (JNI_OK != theJVM->GetEnv((void**)&env, JNI_VERSION_1_4)) {
        fprintf(stderr, "Unable to acquire JNIEnv* from JavaVM!!!");
        abort();
    }
    
    assert(4 == msg->len); // Should be exactly the messageId as the payload.
    int messageId = *((int*)msg->msg);
    
    jclass targetClass = registeredMethods[messageId].targetClass;
    jmethodID targetMethod = registeredMethods[messageId].targetMethod;
    
    env->CallStaticVoidMethod(targetClass, targetMethod);
}    



/**********************************************************************************************************************
 *
 * Support for sending messages
 * 
 **********************************************************************************************************************/



/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendRemote
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendRemote__II(JNIEnv *env, jclass klazz, jint place, jint messageId) {
    void *tmp = x10rt_msg_realloc(NULL,0,4);

    // TODO proper endian-safe writes of data.
    *((int*)(tmp)) = messageId;

    x10rt_msg_params msg = {place, V_Handler, tmp, 4};
    x10rt_send_msg(&msg);
}

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendRemote
 * Signature: (III)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendRemote__III(JNIEnv *env, jclass klazz, jint, jint, jint) {
    fprintf(stderr, "Unimplemented native function\n");
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendRemote
 * Signature: (IIII)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendRemote__IIII(JNIEnv *env, jclass klazz, jint, jint, jint, jint) {
    fprintf(stderr, "Unimplemented native function\n");
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendRemote
 * Signature: (IIIII)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendRemote__IIIII(JNIEnv *env, jclass klazz, jint, jint, jint, jint, jint) {
    fprintf(stderr, "Unimplemented native function\n");
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendRemote
 * Signature: (IIIIII)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendRemote__IIIIII(JNIEnv *env, jclass klazz, jint, jint, jint, jint, jint, jint) {
    fprintf(stderr, "Unimplemented native function\n");
}

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendRemote
 * Signature: (IIJ)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendRemote__IIJ(JNIEnv *env, jclass klazz, jint, jint, jlong) {
    fprintf(stderr, "Unimplemented native function\n");
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendRemote
 * Signature: (IIJJ)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendRemote__IIJJ(JNIEnv *env, jclass klazz, jint, jint, jlong, jlong) {
    fprintf(stderr, "Unimplemented native function\n");
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendRemote
 * Signature: (IIIJ)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendRemote__IIIJ(JNIEnv *env, jclass klazz, jint, jint, jint, jlong) {
    fprintf(stderr, "Unimplemented native function\n");
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendRemote
 * Signature: (IIJI)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendRemote__IIJI(JNIEnv *env, jclass klazz, jint, jint, jlong, jint) {
    fprintf(stderr, "Unimplemented native function\n");
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendRemote
 * Signature: (IIF)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendRemote__IIF(JNIEnv *env, jclass klazz, jint, jint, jfloat) {
    fprintf(stderr, "Unimplemented native function\n");
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendRemote
 * Signature: (IIFF)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendRemote__IIFF(JNIEnv *env, jclass klazz, jint, jint, jfloat, jfloat) {
    fprintf(stderr, "Unimplemented native function\n");
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendRemote
 * Signature: (IIIF)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendRemote__IIIF(JNIEnv *env, jclass klazz, jint, jint, jint, jfloat) {
    fprintf(stderr, "Unimplemented native function\n");
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendRemote
 * Signature: (IIJF)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendRemote__IIJF(JNIEnv *env, jclass klazz, jint, jint, jlong, jfloat) {
    fprintf(stderr, "Unimplemented native function\n");
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendRemote
 * Signature: (IIFI)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendRemote__IIFI(JNIEnv *env, jclass klazz, jint, jint, jfloat, jint) {
    fprintf(stderr, "Unimplemented native function\n");
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendRemote
 * Signature: (IIFJ)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendRemote__IIFJ(JNIEnv *env, jclass klazz, jint, jint, jfloat, jlong) {
    fprintf(stderr, "Unimplemented native function\n");
}

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendRemote
 * Signature: (IID)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendRemote__IID(JNIEnv *env, jclass klazz, jint, jint, jdouble) {
    fprintf(stderr, "Unimplemented native function\n");
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendRemote
 * Signature: (IIDD)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendRemote__IIDD(JNIEnv *env, jclass klazz, jint, jint, jdouble, jdouble) {
    fprintf(stderr, "Unimplemented native function\n");
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendRemote
 * Signature: (IIID)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendRemote__IIID(JNIEnv *env, jclass klazz, jint, jint, jint, jdouble) {
    fprintf(stderr, "Unimplemented native function\n");
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendRemote
 * Signature: (IIFD)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendRemote__IIFD(JNIEnv *env, jclass klazz, jint, jint, jfloat, jdouble) {
    fprintf(stderr, "Unimplemented native function\n");
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendRemote
 * Signature: (IIJD)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendRemote__IIJD(JNIEnv *env, jclass klazz, jint, jint, jlong, jdouble) {
    fprintf(stderr, "Unimplemented native function\n");
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendRemote
 * Signature: (IIDI)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendRemote__IIDI(JNIEnv *env, jclass klazz, jint, jint, jdouble, jint) {
    fprintf(stderr, "Unimplemented native function\n");
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendRemote
 * Signature: (IIDJ)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendRemote__IIDJ(JNIEnv *env, jclass klazz, jint, jint, jdouble, jlong) {
    fprintf(stderr, "Unimplemented native function\n");
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendRemote
 * Signature: (IIDF)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendRemote__IIDF(JNIEnv *env, jclass klazz, jint, jint, jdouble, jfloat) {
    fprintf(stderr, "Unimplemented native function\n");
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendArrayRemote
 * Signature: (III[Z)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendArrayRemote__III_3Z(JNIEnv *env, jclass klazz, jint, jint, jint, jbooleanArray) {
    fprintf(stderr, "Unimplemented native function\n");
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendArrayRemote
 * Signature: (III[B)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendArrayRemote__III_3B(JNIEnv *env, jclass klazz, jint, jint, jint, jbyteArray) {
    fprintf(stderr, "Unimplemented native function\n");
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendArrayRemote
 * Signature: (III[S)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendArrayRemote__III_3S(JNIEnv *env, jclass klazz, jint, jint, jint, jshortArray) {
    fprintf(stderr, "Unimplemented native function\n");
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendArrayRemote
 * Signature: (III[C)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendArrayRemote__III_3C(JNIEnv *env, jclass klazz, jint, jint, jint, jcharArray) {
    fprintf(stderr, "Unimplemented native function\n");
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendArrayRemote
 * Signature: (III[I)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendArrayRemote__III_3I(JNIEnv *env, jclass klazz, jint, jint, jint, jintArray) {
    fprintf(stderr, "Unimplemented native function\n");
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendArrayRemote
 * Signature: (III[F)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendArrayRemote__III_3F(JNIEnv *env, jclass klazz, jint, jint, jint, jfloatArray) {
    fprintf(stderr, "Unimplemented native function\n");
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendArrayRemote
 * Signature: (III[J)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendArrayRemote__III_3J(JNIEnv *env, jclass klazz, jint, jint, jint, jlongArray) {
    fprintf(stderr, "Unimplemented native function\n");
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    sendArrayRemote
 * Signature: (III[D)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendArrayRemote__III_3D(JNIEnv *env, jclass klazz, jint, jint, jint, jdoubleArray) {
    fprintf(stderr, "Unimplemented native function\n");
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
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_sendGeneralRemote(JNIEnv *env, jclass klazz, jint, jint, jint, jbyteArray) {
    fprintf(stderr, "Unimplemented native function\n");
}    

/*
 * Class:     x10_x10rt_ActiveMessage
 * Method:    processQueue
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_ActiveMessage_processQueue(JNIEnv *env, jclass klazz) {
}    


/**********************************************************************************************************************
 *
 * Support for method registration
 * 
 **********************************************************************************************************************/


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

    /* Get a hold of ActiveMessage.receiveGeneral and stash away its invoke information for use by GenericHandler */
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

    /*
    
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

    Array_Handler = x10rt_register_msg_receiver(&jni_messageReceiver_array, NULL, NULL, NULL, NULL);

    Generic_Handler = x10rt_register_msg_receiver(&jni_messageReceiver_general, NULL, NULL, NULL, NULL);
    */
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
