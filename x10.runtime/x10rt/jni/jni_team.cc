#ifndef __int64
#define __int64 __int64_t
#endif

#include <unistd.h>
#include <sys/types.h>
#include <assert.h>

#include <stdio.h>

#undef __stdcall
#define __stdcall
#include "x10rt_jni_helpers.h"
#include "x10_x10rt_TeamSupport.h"

static methodDescription activityTerminationFunc;


/*****************************************************
 * nativeMakeImpl types and functions
 *****************************************************/

typedef struct makeImplStruct {
    jintArray globalResult;
    jobject globalFinishState;
    x10rt_place* inputPlaces;
} makeImplStruct;

static void nativeMakeCallback(x10rt_team team, void *arg) {
    makeImplStruct* callbackArg = (makeImplStruct*)arg;
    JNIEnv *env = jniHelper_getEnv();
    jint tmp = (jint)team;

    // put team id into backing int[]
    env->SetIntArrayRegion(callbackArg->globalResult, 0, 1, &tmp);

    // notify that the activity that was creating the team has finished.
    env->CallStaticVoidMethod(activityTerminationFunc.targetClass,
                              activityTerminationFunc.targetMethod,
                              callbackArg->globalFinishState);

    // Free resources;
    env->DeleteGlobalRef(callbackArg->globalResult);
    env->DeleteGlobalRef(callbackArg->globalFinishState);
    free(callbackArg->inputPlaces);
    free(callbackArg);
}
    
/*
 * Class:     x10_x10rt_TeamSupport
 * Method:    nativeMakeImpl
 * Signature: ([Lx10/lang/Place;I[I)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_TeamSupport_nativeMakeImpl(JNIEnv *env, jclass klazz,
                                                                 jintArray places, jint count,
                                                                 jintArray result, jobject finishState) {
    
    jobject globalResult = env->NewGlobalRef(result);
    jobject globalFinishState = env->NewGlobalRef(finishState);
    if (NULL == globalResult || NULL == globalFinishState) {
        fprintf(stderr, "OOM while attempting to create GlobalRef in nativeMakeImpl\n");
        abort();
    }
    x10rt_place* nativePlaces = (x10rt_place*)malloc(count*(sizeof(x10rt_place)));
    makeImplStruct* callbackArg = (makeImplStruct*)malloc(sizeof(makeImplStruct));
    if (NULL == nativePlaces || NULL == callbackArg) {
        fprintf(stderr, "OOM while attempting to allocate malloced storage in nativeMakeImpl\n");
        abort();
    }        
    callbackArg->globalResult = (jintArray)globalResult;
    callbackArg->globalFinishState = globalFinishState;
    callbackArg->inputPlaces = nativePlaces;
    env->GetIntArrayRegion(places, 0, count, (jint*)nativePlaces);

    x10rt_team_new(count, nativePlaces, &nativeMakeCallback, callbackArg);
}


/*****************************************************
 * nativeSizeImpl
 *****************************************************/

/*
 * Class:     x10_x10rt_TeamSupport
 * Method:    nativeSizeImpl
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_x10_x10rt_TeamSupport_nativeSizeImpl(JNIEnv *env, jclass klazz, jint id) {
    return x10rt_team_sz(id);
}



/*****************************************************
 * nativeBarrierImpl
 *****************************************************/

typedef struct barrierImplStruct {
    jobject globalFinishState;
} barrierImplStruct;

static void barrierCallback(void *arg) {
    barrierImplStruct* callbackArg = (barrierImplStruct*)arg;
    JNIEnv *env = jniHelper_getEnv();

    // notify that the activity that was performing the barrier has finished.
    env->CallStaticVoidMethod(activityTerminationFunc.targetClass,
                              activityTerminationFunc.targetMethod,
                              callbackArg->globalFinishState);

    // Free resources
    env->DeleteGlobalRef(callbackArg->globalFinishState);
    free(callbackArg);
}
    

/*
 * Class:     x10_x10rt_TeamSupport
 * Method:    nativeBarrierImpl
 * Signature: (IILx10/lang/FinishState;)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_TeamSupport_nativeBarrierImpl(JNIEnv *env, jclass klazz,
                                                                    jint id, jint role, jobject finishState) {
    jobject globalFinishState = env->NewGlobalRef(finishState);
    if (NULL == globalFinishState) {
        fprintf(stderr, "OOM while attempting to create GlobalRef in nativeBarrierImpl\n");
        abort();
    }
    barrierImplStruct *callbackArg = (barrierImplStruct*)malloc(sizeof(barrierImplStruct));
    if (NULL == callbackArg) {
        fprintf(stderr, "OOM while attempting to allocate malloced storage in nativeBarrierImpl\n");
        abort();
    }
    callbackArg->globalFinishState = globalFinishState;

    x10rt_barrier(id, role, &barrierCallback, callbackArg);
}


/*****************************************************
 * nativeBcastImpl
 *****************************************************/

typedef struct bcastStruct {
    jobject globalFinishState;
    jobject globalDstArray;
    jint typecode;
    jint dstOffset;
    jint count;
    void *srcData;
    void *dstData;
} bcastStruct;

static void bcastCallback(void *arg) {
    bcastStruct *callbackArg = (bcastStruct*)arg;
    JNIEnv *env = jniHelper_getEnv();

    // Copy from native buffer to dstArray
    switch(callbackArg->typecode) {
    case 1:
        // byte[]
        env->SetByteArrayRegion((jbyteArray)callbackArg->globalDstArray,
                                callbackArg->dstOffset,
                                callbackArg->count,
                                (jbyte*)callbackArg->dstData);
        break;
    case 2:
        // short[]
        env->SetShortArrayRegion((jshortArray)callbackArg->globalDstArray,
                                 callbackArg->dstOffset,
                                 callbackArg->count,
                                 (jshort*)callbackArg->dstData);
        break;
    case 4:
        // int[]
        env->SetIntArrayRegion((jintArray)callbackArg->globalDstArray,
                               callbackArg->dstOffset,
                               callbackArg->count,
                               (jint*)callbackArg->dstData);
        break;
    case 6:
        // long[]
        env->SetLongArrayRegion((jlongArray)callbackArg->globalDstArray,
                               callbackArg->dstOffset,
                               callbackArg->count,
                               (jlong*)callbackArg->dstData);
        break;
    case 8:
        // double[]
        env->SetDoubleArrayRegion((jdoubleArray)callbackArg->globalDstArray,
                                  callbackArg->dstOffset,
                                  callbackArg->count,
                                  (jdouble*)callbackArg->dstData);
        break;
    case 9:
        // float[]
        env->SetFloatArrayRegion((jfloatArray)callbackArg->globalDstArray,
                                 callbackArg->dstOffset,
                                 callbackArg->count,
                                 (jfloat*)callbackArg->dstData);
        break;
    default:
        fprintf(stderr, "Unsupported typecode %d in bcastCallback\n", callbackArg->typecode);
        abort();
    }
    
    // notify that the activity that was performing the barrier has finished.
    env->CallStaticVoidMethod(activityTerminationFunc.targetClass,
                              activityTerminationFunc.targetMethod,
                              callbackArg->globalFinishState);

    // Free resources
    env->DeleteGlobalRef(callbackArg->globalFinishState);
    env->DeleteGlobalRef(callbackArg->globalDstArray);
    if (callbackArg->srcData != NULL)
    free(callbackArg->srcData);
    free(callbackArg->dstData);
    free(callbackArg);
}


/*
 * Class:     x10_x10rt_TeamSupport
 * Method:    nativeBcastImpl
 * Signature: (IIILjava/lang/Object;ILjava/lang/Object;IIILx10/lang/FinishState;)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_TeamSupport_nativeBcastImpl(JNIEnv *env, jclass klazz,
                                                                      jint id, jint role, jint root,
                                                                      jobject src, jint src_off,
                                                                      jobject dst, jint dst_off,
                                                                      jint count, jint typecode,
                                                                      jobject finishState) {
    jobject globalDst = env->NewGlobalRef(dst);
    jobject globalFinishState = env->NewGlobalRef(finishState);
    if (NULL == globalDst || NULL == globalFinishState) {
        fprintf(stderr, "OOM while attempting to create GlobalRef in nativeBcastImpl\n");
        abort();
    }

    int el = 0;
    void *srcData = NULL;
    void *dstData = NULL;
    switch(typecode) {
    case 1:
        // byte []
        el = 1;
        dstData = malloc(count*sizeof(jbyte));
        if (NULL == dstData) {
            fprintf(stderr, "OOM while attempting to allocate malloced storage in nativeBcastImpl\n");
            abort();
        }
        if (role == root) {
        srcData = malloc(count*sizeof(jbyte));
        if (NULL == srcData) {
            fprintf(stderr, "OOM while attempting to allocate malloced storage in nativeBcastImpl\n");
            abort();
        }
        env->GetByteArrayRegion((jbyteArray)src, src_off, count, (jbyte*)srcData);
        }
        break;
    case 2:
        // short []
        el = 2;
        dstData = malloc(count*sizeof(jshort));
        if (NULL == dstData) {
            fprintf(stderr, "OOM while attempting to allocate malloced storage in nativeBcastImpl\n");
            abort();
        }
        if (role == root) {
        srcData = malloc(count*sizeof(jshort));
        if (NULL == srcData) {
            fprintf(stderr, "OOM while attempting to allocate malloced storage in nativeBcastImpl\n");
            abort();
        }
        env->GetShortArrayRegion((jshortArray)src, src_off, count, (jshort*)srcData);
        }
        break;
    case 4:
        // int[]
        el = 4;
        dstData = malloc(count*sizeof(jint));
        if (NULL == dstData) {
            fprintf(stderr, "OOM while attempting to allocate malloced storage in nativeBcastImpl\n");
            abort();
        }
        if (role == root) {
        srcData = malloc(count*sizeof(jint));
        if (NULL == srcData) {
            fprintf(stderr, "OOM while attempting to allocate malloced storage in nativeBcastImpl\n");
            abort();
        }
        env->GetIntArrayRegion((jintArray)src, src_off, count, (jint*)srcData);
        }
        break;
    case 6:
        // long[]
        el = 8;
        dstData = malloc(count*sizeof(jlong));
        if (NULL == dstData) {
            fprintf(stderr, "OOM while attempting to allocate malloced storage in nativeBcastImpl\n");
            abort();
        }
        if (role == root) {
        srcData = malloc(count*sizeof(jlong));
        if (NULL == srcData) {
            fprintf(stderr, "OOM while attempting to allocate malloced storage in nativeBcastImpl\n");
            abort();
        }
        env->GetLongArrayRegion((jlongArray)src, src_off, count, (jlong*)srcData);
        }
        break;
    case 8:
        // double[]
        el = 8;
        dstData = malloc(count*sizeof(jdouble));
        if (NULL == dstData) {
            fprintf(stderr, "OOM while attempting to allocate malloced storage in nativeBcastImpl\n");
            abort();
        }
        if (role == root) {
        srcData = malloc(count*sizeof(jdouble));
        if (NULL == srcData) {
            fprintf(stderr, "OOM while attempting to allocate malloced storage in nativeBcastImpl\n");
            abort();
        }
        env->GetDoubleArrayRegion((jdoubleArray)src, src_off, count, (jdouble*)srcData);
        }
        break;
    case 9:
        // float[]
        el = 4;
        dstData = malloc(count*sizeof(jfloat));
        if (NULL == dstData) {
            fprintf(stderr, "OOM while attempting to allocate malloced storage in nativeBcastImpl\n");
            abort();
        }
        if (role == root) {
        srcData = malloc(count*sizeof(jfloat));
        if (NULL == srcData) {
            fprintf(stderr, "OOM while attempting to allocate malloced storage in nativeBcastImpl\n");
            abort();
        }
        env->GetFloatArrayRegion((jfloatArray)src, src_off, count, (jfloat*)srcData);
        }
        break;
    default:
        fprintf(stderr, "Unsupported typecode %d in nativeBcastImpl\n", typecode);
        abort();
    }        

    bcastStruct* callbackArg = (bcastStruct*)malloc(sizeof(bcastStruct));
    callbackArg->globalFinishState = globalFinishState;
    callbackArg->globalDstArray = globalDst;
    callbackArg->typecode = typecode;
    callbackArg->dstOffset = dst_off;
    callbackArg->count = count;
    callbackArg->srcData = srcData;
    callbackArg->dstData = dstData;

    x10rt_bcast(id, role, root, srcData, dstData, el, count, &bcastCallback, callbackArg);
}


/*****************************************************
 * nativeAllReduceImpl
 *****************************************************/

typedef struct allReduceStruct {
    jobject globalFinishState;
    jobject globalDstArray;
    jint typecode;
    jint dstOffset;
    jint count;
    void *srcData;
    void *dstData;
} allReduceStruct;

static void allReduceCallback(void *arg) {
    allReduceStruct *callbackArg = (allReduceStruct*)arg;
    JNIEnv *env = jniHelper_getEnv();

    // Copy from native buffer to dstArray
    switch(callbackArg->typecode) {
    case 1:
        // byte[]
        env->SetByteArrayRegion((jbyteArray)callbackArg->globalDstArray,
                                callbackArg->dstOffset,
                                callbackArg->count,
                                (jbyte*)callbackArg->dstData);
        break;
    case 2:
        // short[]
        env->SetShortArrayRegion((jshortArray)callbackArg->globalDstArray,
                                 callbackArg->dstOffset,
                                 callbackArg->count,
                                 (jshort*)callbackArg->dstData);
        break;
    case 4:
        // int[]
        env->SetIntArrayRegion((jintArray)callbackArg->globalDstArray,
                               callbackArg->dstOffset,
                               callbackArg->count,
                               (jint*)callbackArg->dstData);
        break;
    case 6:
        // long[]
        env->SetLongArrayRegion((jlongArray)callbackArg->globalDstArray,
                               callbackArg->dstOffset,
                               callbackArg->count,
                               (jlong*)callbackArg->dstData);
        break;
    case 8:
        // double[]
        env->SetDoubleArrayRegion((jdoubleArray)callbackArg->globalDstArray,
                                  callbackArg->dstOffset,
                                  callbackArg->count,
                                  (jdouble*)callbackArg->dstData);
        break;
    case 9:
        // float[]
        env->SetFloatArrayRegion((jfloatArray)callbackArg->globalDstArray,
                                 callbackArg->dstOffset,
                                 callbackArg->count,
                                 (jfloat*)callbackArg->dstData);
        break;
    default:
        fprintf(stderr, "Unsupported typecode %d in allReduceCallback\n", callbackArg->typecode);
        abort();
    }
    
    // notify that the activity that was performing the barrier has finished.
    env->CallStaticVoidMethod(activityTerminationFunc.targetClass,
                              activityTerminationFunc.targetMethod,
                              callbackArg->globalFinishState);

    // Free resources
    env->DeleteGlobalRef(callbackArg->globalFinishState);
    env->DeleteGlobalRef(callbackArg->globalDstArray);
    free(callbackArg->srcData);
    free(callbackArg->dstData);
    free(callbackArg);
}


/*
 * Class:     x10_x10rt_TeamSupport
 * Method:    nativeAllReduceImpl
 * Signature: (IILjava/lang/Object;ILjava/lang/Object;IIILx10/lang/FinishState;)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_TeamSupport_nativeAllReduceImpl(JNIEnv *env, jclass klazz,
                                                                      jint id, jint role,
                                                                      jobject src, jint src_off,
                                                                      jobject dst, jint dst_off,
                                                                      jint count, jint op, jint typecode,
                                                                      jobject finishState) {
    jobject globalDst = env->NewGlobalRef(dst);
    jobject globalFinishState = env->NewGlobalRef(finishState);
    if (NULL == globalDst || NULL == globalFinishState) {
        fprintf(stderr, "OOM while attempting to create GlobalRef in nativeAllReduceImpl\n");
        abort();
    }

    void *srcData;
    void *dstData;
    switch(typecode) {
    case 1:
        // byte []
        srcData = malloc(count*sizeof(jbyte));
        dstData = malloc(count*sizeof(jbyte));
        if (NULL == srcData || NULL == dstData) {
            fprintf(stderr, "OOM while attempting to allocate malloced storage in nativeAllReduceImpl\n");
            abort();
        }
        env->GetByteArrayRegion((jbyteArray)src, src_off, count, (jbyte*)srcData);
        break;
    case 2:
        // short []
        srcData = malloc(count*sizeof(jshort));
        dstData = malloc(count*sizeof(jshort));
        if (NULL == srcData || NULL == dstData) {
            fprintf(stderr, "OOM while attempting to allocate malloced storage in nativeAllReduceImpl\n");
            abort();
        }
        env->GetShortArrayRegion((jshortArray)src, src_off, count, (jshort*)srcData);
        break;
    case 4:
        // int[]
        srcData = malloc(count*sizeof(jint));
        dstData = malloc(count*sizeof(jint));
        if (NULL == srcData || NULL == dstData) {
            fprintf(stderr, "OOM while attempting to allocate malloced storage in nativeAllReduceImpl\n");
            abort();
        }
        env->GetIntArrayRegion((jintArray)src, src_off, count, (jint*)srcData);
        break;
    case 6:
        // long[]
        srcData = malloc(count*sizeof(jlong));
        dstData = malloc(count*sizeof(jlong));
        if (NULL == srcData || NULL == dstData) {
            fprintf(stderr, "OOM while attempting to allocate malloced storage in nativeAllReduceImpl\n");
            abort();
        }
        env->GetLongArrayRegion((jlongArray)src, src_off, count, (jlong*)srcData);
        break;
    case 8:
        // double[]
        srcData = malloc(count*sizeof(jdouble));
        dstData = malloc(count*sizeof(jdouble));
        if (NULL == srcData || NULL == dstData) {
            fprintf(stderr, "OOM while attempting to allocate malloced storage in nativeAllReduceImpl\n");
            abort();
        }
        env->GetDoubleArrayRegion((jdoubleArray)src, src_off, count, (jdouble*)srcData);
        break;
    case 9:
        // float[]
        srcData = malloc(count*sizeof(jfloat));
        dstData = malloc(count*sizeof(jfloat));
        if (NULL == srcData || NULL == dstData) {
            fprintf(stderr, "OOM while attempting to allocate malloced storage in nativeAllReduceImpl\n");
            abort();
        }
        env->GetFloatArrayRegion((jfloatArray)src, src_off, count, (jfloat*)srcData);
        break;
    default:
        fprintf(stderr, "Unsupported typecode %d in nativeAllReduceImpl\n", typecode);
        abort();
    }        

    allReduceStruct* callbackArg = (allReduceStruct*)malloc(sizeof(allReduceStruct));
    callbackArg->globalFinishState = globalFinishState;
    callbackArg->globalDstArray = globalDst;
    callbackArg->typecode = typecode;
    callbackArg->dstOffset = dst_off;
    callbackArg->count = count;
    callbackArg->srcData = srcData;
    callbackArg->dstData = dstData;

    x10rt_allreduce(id, role, srcData, dstData, (x10rt_red_op_type)op, (x10rt_red_type)typecode,
                    count, &allReduceCallback, callbackArg);
}


/*****************************************************
 * nativeDelImpl
 *****************************************************/

typedef struct delImplStruct {
    jobject globalFinishState;
} delImplStruct;

static void delCallback(void *arg) {
    delImplStruct* callbackArg = (delImplStruct*)arg;
    JNIEnv *env = jniHelper_getEnv();

    // notify that the activity that was deleting the team has finished.
    env->CallStaticVoidMethod(activityTerminationFunc.targetClass,
                              activityTerminationFunc.targetMethod,
                              callbackArg->globalFinishState);

    // Free resources
    env->DeleteGlobalRef(callbackArg->globalFinishState);
    free(callbackArg);
}
    

/*
 * Class:     x10_x10rt_TeamSupport
 * Method:    nativeDelImpl
 * Signature: (IILx10/lang/FinishState;)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_TeamSupport_nativeDelImpl(JNIEnv *env, jclass klazz,
                                                                jint id, jint role, jobject finishState) {
    jobject globalFinishState = env->NewGlobalRef(finishState);
    if (NULL == globalFinishState) {
        fprintf(stderr, "OOM while attempting to create GlobalRef in nativeDelImpl\n");
        abort();
    }

    delImplStruct *callbackArg = (delImplStruct*)malloc(sizeof(delImplStruct));
    if (NULL == callbackArg) {
        fprintf(stderr, "OOM while attempting to allocate malloced storage in nativeDelImpl\n");
        abort();
    }
    callbackArg->globalFinishState = globalFinishState;
    
    x10rt_team_del(id, role, &delCallback, callbackArg);
}


/*****************************************************
 * initializeTeamSupport 
 *****************************************************/

/*
 * Class:     x10_x10rt_TeamSupport
 * Method:    initialize
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_TeamSupport_initialize(JNIEnv *env, jclass klazz) {
    /* Get a hold of ActivityManagement.activityTerminationBookkeeping and stash away its invoke information */
    jclass amClass = env->FindClass("Lx10/x10rt/ActivityManagement;");
    if (NULL == amClass) {
        fprintf(stderr, "Unable to find class x10.x10rt.ActivityManagement");
        abort();
    }
    jmethodID terminateId = env->GetStaticMethodID(amClass, "activityTerminationBookkeeping", "(Lx10/lang/FinishState;)V");
    if (NULL == terminateId) {
        fprintf(stderr, "Unable to resolve methodID for ActivityManagement.activityTerminationBookkeeping");
        abort();
    }
    jclass globalClass = (jclass)env->NewGlobalRef(amClass);
    if (NULL == globalClass) {
        fprintf(stderr, "OOM while attempting to allocate global reference for ActivityManagement class\n");
        abort();
    }        
    activityTerminationFunc.targetClass  = globalClass;
    activityTerminationFunc.targetMethod = terminateId;
}
