/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

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
static methodDescription copyDoubleToComplex;

static jfieldID DoubleIdx_valueId;
static jfieldID DoubleIdx_idxId;


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
 * Signature: ([II[ILx10/lang/FinishState;)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_TeamSupport_nativeMakeImpl(JNIEnv *env, jclass klazz,
                                                                 jintArray places, jint count,
                                                                 jintArray result, jobject finishState) {
    
    jobject globalResult = env->NewGlobalRef(result);
    jobject globalFinishState = env->NewGlobalRef(finishState);
    if (NULL == globalResult || NULL == globalFinishState) {
        jniHelper_abort("OOM while attempting to create GlobalRef in nativeMakeImpl\n");
    }
    x10rt_place* nativePlaces = (x10rt_place*)malloc(count*(sizeof(x10rt_place)));
    makeImplStruct* callbackArg = (makeImplStruct*)malloc(sizeof(makeImplStruct));
    if (NULL == nativePlaces || NULL == callbackArg) {
        jniHelper_abort("OOM while attempting to allocate malloced storage in nativeMakeImpl\n");
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


/*
 * Common struct and callback for finish only
 */

typedef struct finishOnlyStruct {
    jobject globalFinishState;
} finishOnlyStruct;

static void finishOnlyCallback(void *arg) {
    finishOnlyStruct* callbackArg = (finishOnlyStruct*)arg;
    JNIEnv *env = jniHelper_getEnv();

    // notify that the activity has finished.
    env->CallStaticVoidMethod(activityTerminationFunc.targetClass,
                              activityTerminationFunc.targetMethod,
                              callbackArg->globalFinishState);

    // Free resources
    env->DeleteGlobalRef(callbackArg->globalFinishState);
    free(callbackArg);
}
    

/*****************************************************
 * nativeBarrierImpl
 *****************************************************/

/*
 * Class:     x10_x10rt_TeamSupport
 * Method:    nativeBarrierImpl
 * Signature: (IILx10/lang/FinishState;)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_TeamSupport_nativeBarrierImpl(JNIEnv *env, jclass klazz,
                                                                    jint id, jint role, jobject finishState) {
    jobject globalFinishState = env->NewGlobalRef(finishState);
    if (NULL == globalFinishState) {
        jniHelper_abort("OOM while attempting to create GlobalRef in nativeBarrierImpl\n");
    }
    finishOnlyStruct *callbackArg = (finishOnlyStruct*)malloc(sizeof(finishOnlyStruct));
    if (NULL == callbackArg) {
        jniHelper_abort("OOM while attempting to allocate malloced storage in nativeBarrierImpl\n");
    }
    callbackArg->globalFinishState = globalFinishState;

    x10rt_barrier(id, role, &finishOnlyCallback, callbackArg);
}



/*
 * Common struct and callback for copying temporary buffer to dest array
 */

typedef struct postCopyStruct {
    jobject globalFinishState;
    jobject globalDstArray;
    jint typecode;
    jint dstOffset;
    jint count;
    void *srcData;
    void *dstData;
} postCopyStruct;

static void postCopyCallback(void *arg) {
    postCopyStruct *callbackArg = (postCopyStruct*)arg;
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
    case 11:
        // double[] representing a Complex[]
        {
        jdoubleArray res = env->NewDoubleArray(2*callbackArg->count);
        env->SetDoubleArrayRegion(res,
                                  2*callbackArg->dstOffset,
                                  2*callbackArg->count,
                                  (jdouble*)callbackArg->dstData);
        env->CallStaticVoidMethod(copyDoubleToComplex.targetClass,
                                  copyDoubleToComplex.targetMethod,
                                  res,
                                  (jdoubleArray)callbackArg->globalDstArray,
                                  callbackArg->dstOffset,
                                  callbackArg->count);
        }
        break;
    case 12:
        // boolean[]
        env->SetBooleanArrayRegion((jbooleanArray)callbackArg->globalDstArray,
                                 callbackArg->dstOffset,
                                 callbackArg->count,
                                 (jboolean*)callbackArg->dstData);
        break;
    default:
        jniHelper_abort("Unsupported typecode %d in postCopyCallback\n", callbackArg->typecode);
    }
    
    // notify that the activity that was performing post copy has finished.
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


/*****************************************************
 * nativeScatterImpl
 *****************************************************/

/*
 * Class:     x10_x10rt_TeamSupport
 * Method:    nativeScatterImpl
 * Signature: (IIILjava/lang/Object;ILjava/lang/Object;IIILx10/lang/FinishState;)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_TeamSupport_nativeScatterImpl(JNIEnv *env, jclass klazz,
                                                                      jint id, jint role, jint root,
                                                                      jobject src, jint src_off,
                                                                      jobject dst, jint dst_off,
                                                                      jint count, jint typecode,
                                                                      jobject finishState) {
    jobject globalDst = env->NewGlobalRef(dst);
    jobject globalFinishState = env->NewGlobalRef(finishState);
    if (NULL == globalDst || NULL == globalFinishState) {
        jniHelper_abort("OOM while attempting to create GlobalRef in nativeScatterImpl\n");
    }

    int el = 0;
    int size = x10rt_team_sz(id);
    int rcount = (count +  size - 1) / size;
    void *srcData = NULL;
    void *dstData = NULL;
    switch(typecode) {
    case 1:
        // byte []
        el = sizeof(jbyte);
        dstData = malloc(rcount*sizeof(jbyte));
        if (NULL == dstData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeScatterImpl\n");
        }
        if (role == root) {
        srcData = malloc(count*sizeof(jbyte));
        if (NULL == srcData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeScatterImpl\n");
        }
        env->GetByteArrayRegion((jbyteArray)src, src_off, count, (jbyte*)srcData);
        }
        break;
    case 2:
        // short []
        el = sizeof(jshort);
        dstData = malloc(rcount*sizeof(jshort));
        if (NULL == dstData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeScatterImpl\n");
        }
        if (role == root) {
        srcData = malloc(count*sizeof(jshort));
        if (NULL == srcData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeScatterImpl\n");
        }
        env->GetShortArrayRegion((jshortArray)src, src_off, count, (jshort*)srcData);
        }
        break;
    case 4:
        // int[]
        el = sizeof(jint);
        dstData = malloc(rcount*sizeof(jint));
        if (NULL == dstData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeScatterImpl\n");
        }
        if (role == root) {
        srcData = malloc(count*sizeof(jint));
        if (NULL == srcData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeScatterImpl\n");
        }
        env->GetIntArrayRegion((jintArray)src, src_off, count, (jint*)srcData);
        }
        break;
    case 6:
        // long[]
        el = sizeof(jlong);
        dstData = malloc(rcount*sizeof(jlong));
        if (NULL == dstData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeScatterImpl\n");
        }
        if (role == root) {
        srcData = malloc(count*sizeof(jlong));
        if (NULL == srcData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeScatterImpl\n");
        }
        env->GetLongArrayRegion((jlongArray)src, src_off, count, (jlong*)srcData);
        }
        break;
    case 8:
        // double[]
        el = sizeof(jdouble);
        dstData = malloc(rcount*sizeof(jdouble));
        if (NULL == dstData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeScatterImpl\n");
        }
        if (role == root) {
        srcData = malloc(count*sizeof(jdouble));
        if (NULL == srcData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeScatterImpl\n");
        }
        env->GetDoubleArrayRegion((jdoubleArray)src, src_off, count, (jdouble*)srcData);
        }
        break;
    case 9:
        // float[]
        el = sizeof(jfloat);
        dstData = malloc(rcount*sizeof(jfloat));
        if (NULL == dstData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeScatterImpl\n");
        }
        if (role == root) {
        srcData = malloc(count*sizeof(jfloat));
        if (NULL == srcData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeScatterImpl\n");
        }
        env->GetFloatArrayRegion((jfloatArray)src, src_off, count, (jfloat*)srcData);
        }
        break;
    case 11:
        // double[] representing Complex[]
        el = 2*sizeof(jdouble);
        dstData = malloc(rcount*2*sizeof(jdouble));
        if (NULL == dstData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeScatterImpl\n");
        }
        if (role == root) {
        srcData = malloc(count*2*sizeof(jdouble));
        if (NULL == srcData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeScatterImpl\n");
        }
        env->GetDoubleArrayRegion((jdoubleArray)src, 2*src_off, 2*count, (jdouble*)srcData);
        }
        break;
    case 12:
        // boolean[]
        el = sizeof(jboolean);
        dstData = malloc(rcount*sizeof(jboolean));
        if (NULL == dstData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeScatterImpl\n");
        }
        if (role == root) {
        srcData = malloc(count*sizeof(jboolean));
        if (NULL == srcData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeScatterImpl\n");
        }
        env->GetBooleanArrayRegion((jbooleanArray)src, src_off, count, (jboolean*)srcData);
        }
        break;
    default:
        jniHelper_abort("Unsupported typecode %d in nativeScatterImpl\n", typecode);
    }

    postCopyStruct* callbackArg = (postCopyStruct*)malloc(sizeof(postCopyStruct));
    callbackArg->globalFinishState = globalFinishState;
    callbackArg->globalDstArray = globalDst;
    callbackArg->typecode = typecode;
    callbackArg->dstOffset = dst_off;
    callbackArg->count = rcount;
    callbackArg->srcData = srcData;
    callbackArg->dstData = dstData;

    x10rt_scatter(id, role, root, srcData, dstData, el, count, &postCopyCallback, callbackArg);
}


/*****************************************************
 * nativeBcastImpl
 *****************************************************/

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
        jniHelper_abort("OOM while attempting to create GlobalRef in nativeBcastImpl\n");
    }

    int el = 0;
    void *srcData = NULL;
    void *dstData = NULL;
    switch(typecode) {
    case 1:
        // byte []
        el = sizeof(jbyte);
        dstData = malloc(count*sizeof(jbyte));
        if (NULL == dstData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeBcastImpl\n");
        }
        if (role == root) {
        srcData = malloc(count*sizeof(jbyte));
        if (NULL == srcData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeBcastImpl\n");
        }
        env->GetByteArrayRegion((jbyteArray)src, src_off, count, (jbyte*)srcData);
        }
        break;
    case 2:
        // short []
        el = sizeof(jshort);
        dstData = malloc(count*sizeof(jshort));
        if (NULL == dstData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeBcastImpl\n");
        }
        if (role == root) {
        srcData = malloc(count*sizeof(jshort));
        if (NULL == srcData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeBcastImpl\n");
        }
        env->GetShortArrayRegion((jshortArray)src, src_off, count, (jshort*)srcData);
        }
        break;
    case 4:
        // int[]
        el = sizeof(jint);
        dstData = malloc(count*sizeof(jint));
        if (NULL == dstData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeBcastImpl\n");
        }
        if (role == root) {
        srcData = malloc(count*sizeof(jint));
        if (NULL == srcData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeBcastImpl\n");
        }
        env->GetIntArrayRegion((jintArray)src, src_off, count, (jint*)srcData);
        }
        break;
    case 6:
        // long[]
        el = sizeof(jlong);
        dstData = malloc(count*sizeof(jlong));
        if (NULL == dstData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeBcastImpl\n");
        }
        if (role == root) {
        srcData = malloc(count*sizeof(jlong));
        if (NULL == srcData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeBcastImpl\n");
        }
        env->GetLongArrayRegion((jlongArray)src, src_off, count, (jlong*)srcData);
        }
        break;
    case 8:
        // double[]
        el = sizeof(jdouble);
        dstData = malloc(count*sizeof(jdouble));
        if (NULL == dstData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeBcastImpl\n");
        }
        if (role == root) {
        srcData = malloc(count*sizeof(jdouble));
        if (NULL == srcData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeBcastImpl\n");
        }
        env->GetDoubleArrayRegion((jdoubleArray)src, src_off, count, (jdouble*)srcData);
        }
        break;
    case 9:
        // float[]
        el = sizeof(jfloat);
        dstData = malloc(count*sizeof(jfloat));
        if (NULL == dstData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeBcastImpl\n");
        }
        if (role == root) {
        srcData = malloc(count*sizeof(jfloat));
        if (NULL == srcData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeBcastImpl\n");
        }
        env->GetFloatArrayRegion((jfloatArray)src, src_off, count, (jfloat*)srcData);
        }
        break;
    case 11:
        // double[] (representing a Complex[])
        el = 2* sizeof(jdouble);
        dstData = malloc(count*2*sizeof(jdouble));
        if (NULL == dstData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeBcastImpl\n");
        }
        if (role == root) {
        srcData = malloc(count*2*sizeof(jdouble));
        if (NULL == srcData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeBcastImpl\n");
        }
        env->GetDoubleArrayRegion((jdoubleArray)src, 2*src_off, 2*count, (jdouble*)srcData);
        }
        break;
    case 12:
        // boolean []
        el = sizeof(jboolean);
        dstData = malloc(count*sizeof(jboolean));
        if (NULL == dstData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeBcastImpl\n");
        }
        if (role == root) {
        srcData = malloc(count*sizeof(jboolean));
        if (NULL == srcData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeBcastImpl\n");
        }
        env->GetBooleanArrayRegion((jbooleanArray)src, src_off, count, (jboolean*)srcData);
        }
        break;
    default:
        jniHelper_abort("Unsupported typecode %d in nativeBcastImpl\n", typecode);
    }

    postCopyStruct* callbackArg = (postCopyStruct*)malloc(sizeof(postCopyStruct));
    callbackArg->globalFinishState = globalFinishState;
    callbackArg->globalDstArray = globalDst;
    callbackArg->typecode = typecode;
    callbackArg->dstOffset = dst_off;
    callbackArg->count = count;
    callbackArg->srcData = srcData;
    callbackArg->dstData = dstData;

    x10rt_bcast(id, role, root, srcData, dstData, el, count, &postCopyCallback, callbackArg);
}


/*****************************************************
 * nativeAllToAllImpl
 *****************************************************/

/*
 * Class:     x10_x10rt_TeamSupport
 * Method:    nativeAllToAllImpl
 * Signature: (IILjava/lang/Object;ILjava/lang/Object;IIILx10/lang/FinishState;)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_TeamSupport_nativeAllToAllImpl(JNIEnv *env, jclass klazz,
                                                                      jint id, jint role,
                                                                      jobject src, jint src_off,
                                                                      jobject dst, jint dst_off,
                                                                      jint count, jint typecode,
                                                                      jobject finishState) {

    jobject globalDst = env->NewGlobalRef(dst);
    jobject globalFinishState = env->NewGlobalRef(finishState);
    if (NULL == globalDst || NULL == globalFinishState) {
        jniHelper_abort("OOM while attempting to create GlobalRef in nativeAllToAllImpl\n");
    }

    int el = 0;
    void *srcData = NULL;
    void *dstData = NULL;
    int srcElemCount = env->GetArrayLength((jarray)src) - src_off;
    int dstElemCount = env->GetArrayLength((jarray)dst) - dst_off;
    switch(typecode) {
    case 1:
        // byte []
        el = sizeof(jbyte);
        dstData = malloc(dstElemCount*sizeof(jbyte));
        if (NULL == dstData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeAllToAllImpl\n");
        }
        srcData = malloc(srcElemCount*sizeof(jbyte));
        if (NULL == srcData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeAllToAllImpl\n");
        }
        env->GetByteArrayRegion((jbyteArray)src, src_off, srcElemCount, (jbyte*)srcData);
        break;
    case 2:
        // short []
        el = sizeof(jshort);
        dstData = malloc(dstElemCount*sizeof(jshort));
        if (NULL == dstData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeAllToAllImpl\n");
        }
        srcData = malloc(srcElemCount*sizeof(jshort));
        if (NULL == srcData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeAllToAllImpl\n");
        }
        env->GetShortArrayRegion((jshortArray)src, src_off, srcElemCount, (jshort*)srcData);
        break;
    case 4:
        // int[]
        el = sizeof(jint);
        dstData = malloc(dstElemCount*sizeof(jint));
        if (NULL == dstData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeAllToAllImpl\n");
        }
        srcData = malloc(srcElemCount*sizeof(jint));
        if (NULL == srcData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeAllToAllImpl\n");
        }
        env->GetIntArrayRegion((jintArray)src, src_off, srcElemCount, (jint*)srcData);
        break;
    case 6:
        // long[]
        el = sizeof(jlong);
        dstData = malloc(dstElemCount*sizeof(jlong));
        if (NULL == dstData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeAllToAllImpl\n");
        }
        srcData = malloc(srcElemCount*sizeof(jlong));
        if (NULL == srcData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeAllToAllImpl\n");
        }
        env->GetLongArrayRegion((jlongArray)src, src_off, srcElemCount, (jlong*)srcData);
        break;
    case 8:
        // double[]
        el = sizeof(jdouble);
        dstData = malloc(dstElemCount*sizeof(jdouble));
        if (NULL == dstData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeAllToAllImpl\n");
        }
        srcData = malloc(srcElemCount*sizeof(jdouble));
        if (NULL == srcData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeAllToAllImpl\n");
        }
        env->GetDoubleArrayRegion((jdoubleArray)src, src_off, srcElemCount, (jdouble*)srcData);
        break;
    case 9:
        // float[]
        el = sizeof(jfloat);
        dstData = malloc(dstElemCount*sizeof(jfloat));
        if (NULL == dstData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeAllToAllImpl\n");
        }
        srcData = malloc(srcElemCount*sizeof(jfloat));
        if (NULL == srcData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeAllToAllImpl\n");
        }
        env->GetFloatArrayRegion((jfloatArray)src, src_off, srcElemCount, (jfloat*)srcData);
        break;
    case 11:
        // double[] representing Complex[]
        el = 2*sizeof(jdouble);
        dstData = malloc(dstElemCount*2*sizeof(jdouble));
        if (NULL == dstData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeAllToAllImpl\n");
        }
        srcData = malloc(srcElemCount*2*sizeof(jdouble));
        if (NULL == srcData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeAllToAllImpl\n");
        }
        env->GetDoubleArrayRegion((jdoubleArray)src, 2*src_off, 2*srcElemCount, (jdouble*)srcData);
        break;
    case 12:
        // boolean []
        el = sizeof(jboolean);
        dstData = malloc(dstElemCount*sizeof(jboolean));
        if (NULL == dstData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeAllToAllImpl\n");
        }
        srcData = malloc(srcElemCount*sizeof(jboolean));
        if (NULL == srcData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeAllToAllImpl\n");
        }
        env->GetBooleanArrayRegion((jbooleanArray)src, src_off, srcElemCount, (jboolean*)srcData);
        break;
    default:
        jniHelper_abort("Unsupported typecode %d in nativeAllToAllImpl\n", typecode);
    }

    postCopyStruct* callbackArg = (postCopyStruct*)malloc(sizeof(postCopyStruct));
    callbackArg->globalFinishState = globalFinishState;
    callbackArg->globalDstArray = globalDst;
    callbackArg->typecode = typecode;
    callbackArg->dstOffset = dst_off;
    callbackArg->count = dstElemCount;
    callbackArg->srcData = srcData;
    callbackArg->dstData = dstData;

    x10rt_alltoall(id, role, srcData, dstData, el, count, &postCopyCallback, callbackArg);
}


/*****************************************************
 * nativeReduceImpl
 *****************************************************/

/*
 * Class:     x10_x10rt_TeamSupport
 * Method:    nativeReduceImpl
 * Signature: (IIILjava/lang/Object;ILjava/lang/Object;IIIILx10/lang/FinishState;)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_TeamSupport_nativeReduceImpl(JNIEnv *env, jclass klazz,
                                                                   jint id, jint role, jint root,
                                                                   jobject src, jint src_off,
                                                                   jobject dst, jint dst_off,
                                                                   jint count, jint op, jint typecode,
                                                                   jobject finishState) {
    jobject globalDst = env->NewGlobalRef(dst);
    jobject globalFinishState = env->NewGlobalRef(finishState);
    if (NULL == globalDst || NULL == globalFinishState) {
        jniHelper_abort("OOM while attempting to create GlobalRef in nativeReduceImpl\n");
    }

    void *srcData = NULL;
    void *dstData = NULL;
    switch(typecode) {
    case 1:
        // byte []
        srcData = malloc(count*sizeof(jbyte));
        dstData = malloc(count*sizeof(jbyte));
        if (NULL == srcData || NULL == dstData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeReduceImpl\n");
        }
        env->GetByteArrayRegion((jbyteArray)src, src_off, count, (jbyte*)srcData);
        break;
    case 2:
        // short []
        srcData = malloc(count*sizeof(jshort));
        dstData = malloc(count*sizeof(jshort));
        if (NULL == srcData || NULL == dstData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeReduceImpl\n");
        }
        env->GetShortArrayRegion((jshortArray)src, src_off, count, (jshort*)srcData);
        break;
    case 4:
        // int[]
        srcData = malloc(count*sizeof(jint));
        dstData = malloc(count*sizeof(jint));
        if (NULL == srcData || NULL == dstData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeReduceImpl\n");
        }
        env->GetIntArrayRegion((jintArray)src, src_off, count, (jint*)srcData);
        break;
    case 6:
        // long[]
        srcData = malloc(count*sizeof(jlong));
        dstData = malloc(count*sizeof(jlong));
        if (NULL == srcData || NULL == dstData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeReduceImpl\n");
        }
        env->GetLongArrayRegion((jlongArray)src, src_off, count, (jlong*)srcData);
        break;
    case 8:
        // double[]
        srcData = malloc(count*sizeof(jdouble));
        dstData = malloc(count*sizeof(jdouble));
        if (NULL == srcData || NULL == dstData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeReduceImpl\n");
        }
        env->GetDoubleArrayRegion((jdoubleArray)src, src_off, count, (jdouble*)srcData);
        break;
    case 9:
        // float[]
        srcData = malloc(count*sizeof(jfloat));
        dstData = malloc(count*sizeof(jfloat));
        if (NULL == srcData || NULL == dstData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeReduceImpl\n");
        }
        env->GetFloatArrayRegion((jfloatArray)src, src_off, count, (jfloat*)srcData);
        break;
    case 11:
        // double[] representing a Complex[]
        srcData = malloc(count*2*sizeof(jdouble));
        dstData = malloc(count*2*sizeof(jdouble));
        if (NULL == srcData || NULL == dstData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeReduceImpl\n");
        }
        env->GetDoubleArrayRegion((jdoubleArray)src, 2*src_off, 2*count, (jdouble*)srcData);
        break;
    case 12:
        // boolean []
        srcData = malloc(count*sizeof(jboolean));
        dstData = malloc(count*sizeof(jboolean));
        if (NULL == srcData || NULL == dstData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeReduceImpl\n");
        }
        env->GetBooleanArrayRegion((jbooleanArray)src, src_off, count, (jboolean*)srcData);
        break;
    default:
        jniHelper_abort("Unsupported typecode %d in nativeReduceImpl\n", typecode);
    }        

    postCopyStruct* callbackArg = (postCopyStruct*)malloc(sizeof(postCopyStruct));
    callbackArg->globalFinishState = globalFinishState;
    callbackArg->globalDstArray = globalDst;
    callbackArg->typecode = typecode;
    callbackArg->dstOffset = dst_off;
    callbackArg->count = count;
    callbackArg->srcData = srcData;
    callbackArg->dstData = dstData;

    x10rt_reduce(id, role, root, srcData, dstData, (x10rt_red_op_type)op, (x10rt_red_type)typecode,
                 count, &postCopyCallback, callbackArg);
}


/*****************************************************
 * nativeAllReduceImpl
 *****************************************************/

/*
 * Class:     x10_x10rt_TeamSupport
 * Method:    nativeAllReduceImpl
 * Signature: (IILjava/lang/Object;ILjava/lang/Object;IIIILx10/lang/FinishState;)V
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
        jniHelper_abort("OOM while attempting to create GlobalRef in nativeAllReduceImpl\n");
    }

    void *srcData = NULL;
    void *dstData = NULL;
    switch(typecode) {
    case 1:
        // byte []
        srcData = malloc(count*sizeof(jbyte));
        dstData = malloc(count*sizeof(jbyte));
        if (NULL == srcData || NULL == dstData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeAllReduceImpl\n");
        }
        env->GetByteArrayRegion((jbyteArray)src, src_off, count, (jbyte*)srcData);
        break;
    case 2:
        // short []
        srcData = malloc(count*sizeof(jshort));
        dstData = malloc(count*sizeof(jshort));
        if (NULL == srcData || NULL == dstData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeAllReduceImpl\n");
        }
        env->GetShortArrayRegion((jshortArray)src, src_off, count, (jshort*)srcData);
        break;
    case 4:
        // int[]
        srcData = malloc(count*sizeof(jint));
        dstData = malloc(count*sizeof(jint));
        if (NULL == srcData || NULL == dstData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeAllReduceImpl\n");
        }
        env->GetIntArrayRegion((jintArray)src, src_off, count, (jint*)srcData);
        break;
    case 6:
        // long[]
        srcData = malloc(count*sizeof(jlong));
        dstData = malloc(count*sizeof(jlong));
        if (NULL == srcData || NULL == dstData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeAllReduceImpl\n");
        }
        env->GetLongArrayRegion((jlongArray)src, src_off, count, (jlong*)srcData);
        break;
    case 8:
        // double[]
        srcData = malloc(count*sizeof(jdouble));
        dstData = malloc(count*sizeof(jdouble));
        if (NULL == srcData || NULL == dstData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeAllReduceImpl\n");
        }
        env->GetDoubleArrayRegion((jdoubleArray)src, src_off, count, (jdouble*)srcData);
        break;
    case 9:
        // float[]
        srcData = malloc(count*sizeof(jfloat));
        dstData = malloc(count*sizeof(jfloat));
        if (NULL == srcData || NULL == dstData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeAllReduceImpl\n");
        }
        env->GetFloatArrayRegion((jfloatArray)src, src_off, count, (jfloat*)srcData);
        break;
    case 11:
        // double[] representing a Complex[]
        srcData = malloc(count*2*sizeof(jdouble));
        dstData = malloc(count*2*sizeof(jdouble));
        if (NULL == srcData || NULL == dstData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeAllReduceImpl\n");
        }
        env->GetDoubleArrayRegion((jdoubleArray)src, 2*src_off, 2*count, (jdouble*)srcData);
        break;
    case 12:
        // boolean []
        srcData = malloc(count*sizeof(jboolean));
        dstData = malloc(count*sizeof(jboolean));
        if (NULL == srcData || NULL == dstData) {
            jniHelper_abort("OOM while attempting to allocate malloced storage in nativeAllReduceImpl\n");
        }
        env->GetBooleanArrayRegion((jbooleanArray)src, src_off, count, (jboolean*)srcData);
        break;
    default:
        jniHelper_abort("Unsupported typecode %d in nativeAllReduceImpl\n", typecode);
    }        

    postCopyStruct* callbackArg = (postCopyStruct*)malloc(sizeof(postCopyStruct));
    callbackArg->globalFinishState = globalFinishState;
    callbackArg->globalDstArray = globalDst;
    callbackArg->typecode = typecode;
    callbackArg->dstOffset = dst_off;
    callbackArg->count = count;
    callbackArg->srcData = srcData;
    callbackArg->dstData = dstData;

    x10rt_allreduce(id, role, srcData, dstData, (x10rt_red_op_type)op, (x10rt_red_type)typecode,
                    count, &postCopyCallback, callbackArg);
}


/*****************************************************
 * nativeIndexOfMinImpl and nativeIndexOfMaxImpl
 *****************************************************/

typedef struct DoubleIdx {
    jdouble value;
    jint idx;
} DoubleIdx;

typedef struct minmaxStruct {
    jobject globalFinishState;
    jobject globalDstTuple;
    DoubleIdx *srcData;
    DoubleIdx *dstData;
} minmaxStruct;

static void minmaxCallback(void *arg) {
    minmaxStruct *callbackArg = (minmaxStruct*)arg;
    JNIEnv *env = jniHelper_getEnv();

    env->SetDoubleField(callbackArg->globalDstTuple, DoubleIdx_valueId,
                        callbackArg->dstData[0].value);
    env->SetIntField(callbackArg->globalDstTuple, DoubleIdx_idxId,
                        callbackArg->dstData[0].idx);
    
    // notify that the activity that was performing min max has finished.
    env->CallStaticVoidMethod(activityTerminationFunc.targetClass,
                              activityTerminationFunc.targetMethod,
                              callbackArg->globalFinishState);

    // Free resources
    env->DeleteGlobalRef(callbackArg->globalFinishState);
    env->DeleteGlobalRef(callbackArg->globalDstTuple);
    free(callbackArg->srcData);
    free(callbackArg->dstData);
    free(callbackArg);
}


static void indexOfImpl(JNIEnv *env, jint id, jint role,
                        jobject srcTuple, jobject dstTuple,
                        x10rt_red_op_type op,
                        jobject finishState)
{
    jobject globalDstTuple = env->NewGlobalRef(dstTuple);
    jobject globalFinishState = env->NewGlobalRef(finishState);
    if (NULL == globalDstTuple || NULL == globalFinishState) {
        jniHelper_abort("OOM while attempting to create GlobalRef in nativeIndexOfImpl\n");
    }

    DoubleIdx* srcData = (DoubleIdx*)malloc(sizeof(DoubleIdx));
    DoubleIdx* dstData = (DoubleIdx*)malloc(sizeof(DoubleIdx));
    if (NULL == srcData || NULL == dstData) {
        jniHelper_abort("OOM while attempting to allocate malloced storage in nativeIndexOfImpl\n");
    }
    srcData->value = env->GetDoubleField(srcTuple, DoubleIdx_valueId);
    srcData->idx = env->GetIntField(srcTuple, DoubleIdx_idxId);
    
    minmaxStruct* callbackArg = (minmaxStruct*)malloc(sizeof(minmaxStruct));
    callbackArg->globalFinishState = globalFinishState;
    callbackArg->globalDstTuple = globalDstTuple;
    callbackArg->srcData = srcData;
    callbackArg->dstData = dstData;

    x10rt_allreduce(id, role, srcData, dstData, op, X10RT_RED_TYPE_DBL_S32, 1, &minmaxCallback, callbackArg);
}


/*
 * Class:     x10_x10rt_TeamSupport
 * Method:    nativeIndexOfMaxImpl
 * Signature: (IILx10/util/Team$DoubleIdx;Lx10/util/Team$DoubleIdx;Lx10/lang/FinishState;)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_TeamSupport_nativeIndexOfMaxImpl(JNIEnv *env, jclass klazz,
                                                                       jint id, jint role,
                                                                       jobject srcTuple, jobject dstTuple,
                                                                       jobject finishState) {
    indexOfImpl(env, id, role, srcTuple, dstTuple, X10RT_RED_OP_MAX, finishState);
}


/*
 * Class:     x10_x10rt_TeamSupport
 * Method:    nativeIndexOfMinImpl
 * Signature: (IILx10/util/Team$DoubleIdx;Lx10/util/Team$DoubleIdx;Lx10/lang/FinishState;)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_TeamSupport_nativeIndexOfMinImpl(JNIEnv *env, jclass klazz,
                                                                       jint id, jint role,
                                                                       jobject srcTuple, jobject dstTuple,
                                                                       jobject finishState) {
    indexOfImpl(env, id, role, srcTuple, dstTuple, X10RT_RED_OP_MIN, finishState);
}


/*****************************************************
 * nativeSplitImpl
 *****************************************************/

typedef struct splitImplStruct {
    jobject globalFinishState;
    jintArray globalResult;
} splitImplStruct;

static void splitCallback(x10rt_team team, void *arg) {
    splitImplStruct* callbackArg = (splitImplStruct*)arg;
    JNIEnv *env = jniHelper_getEnv();
    jint tmp = (jint)team;

    // put team id into backing int[]
    env->SetIntArrayRegion(callbackArg->globalResult, 0, 1, &tmp);

    // notify that the activity that was performing the split has finished.
    env->CallStaticVoidMethod(activityTerminationFunc.targetClass,
                              activityTerminationFunc.targetMethod,
                              callbackArg->globalFinishState);

    // Free resources
    env->DeleteGlobalRef(callbackArg->globalFinishState);
    env->DeleteGlobalRef(callbackArg->globalResult);
    free(callbackArg);
}
    

/*
 * Class:     x10_x10rt_TeamSupport
 * Method:    nativeSplitImpl
 * Signature: (IIII[ILx10/lang/FinishState;)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_TeamSupport_nativeSplitImpl(JNIEnv *env, jclass klazz,
                                                                jint id, jint role,
                                                                jint color, jint new_role, jintArray nr,
                                                                jobject finishState) {
    jobject globalResult = env->NewGlobalRef(nr);
    jobject globalFinishState = env->NewGlobalRef(finishState);
    if (NULL == globalResult || NULL == globalFinishState) {
        jniHelper_abort("OOM while attempting to create GlobalRef in nativeSplitImpl\n");
    }

    splitImplStruct *callbackArg = (splitImplStruct*)malloc(sizeof(splitImplStruct));
    if (NULL == callbackArg) {
        jniHelper_abort("OOM while attempting to allocate malloced storage in nativeSplitImpl\n");
    }

    callbackArg->globalFinishState = globalFinishState;
    callbackArg->globalResult = (jintArray) globalResult;
    
    x10rt_team_split(id, role, color, new_role, &splitCallback, callbackArg);
}


/*****************************************************
 * nativeDelImpl
 *****************************************************/

/*
 * Class:     x10_x10rt_TeamSupport
 * Method:    nativeDelImpl
 * Signature: (IILx10/lang/FinishState;)V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_TeamSupport_nativeDelImpl(JNIEnv *env, jclass klazz,
                                                                jint id, jint role, jobject finishState) {
    jobject globalFinishState = env->NewGlobalRef(finishState);
    if (NULL == globalFinishState) {
        jniHelper_abort("OOM while attempting to create GlobalRef in nativeDelImpl\n");
    }

    finishOnlyStruct *callbackArg = (finishOnlyStruct*)malloc(sizeof(finishOnlyStruct));
    if (NULL == callbackArg) {
        jniHelper_abort("OOM while attempting to allocate malloced storage in nativeDelImpl\n");
    }
    callbackArg->globalFinishState = globalFinishState;
    
    x10rt_team_del(id, role, &finishOnlyCallback, callbackArg);
}


/*****************************************************
 * initialize
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
        jniHelper_abort("Unable to find class x10.x10rt.ActivityManagement\n");
    }
    jmethodID terminateId = env->GetStaticMethodID(amClass, "activityTerminationBookkeeping", "(Lx10/xrx/FinishState;)V");
    if (NULL == terminateId) {
        jniHelper_abort("Unable to resolve methodID for ActivityManagement.activityTerminationBookkeeping\n");
    }
    jclass globalClass = (jclass)env->NewGlobalRef(amClass);
    if (NULL == globalClass) {
        jniHelper_abort("OOM while attempting to allocate global reference for ActivityManagement class\n");
    }
    activityTerminationFunc.targetClass  = globalClass;
    activityTerminationFunc.targetMethod = terminateId;


    /* Get a hold of TeamSupport.copyDoubleToComplex and stash away its invoke information */
    jclass teamClass = env->FindClass("Lx10/x10rt/TeamSupport;");
    if (NULL == teamClass) {
        jniHelper_abort("Unable to find class x10.x10rt.TeamSupport\n");
    }
    jmethodID copyId = env->GetStaticMethodID(teamClass, "copyDoubleToComplex", "([DLjava/lang/Object;II)V");
    if (NULL == copyId) {
        jniHelper_abort("Unable to resolve methodID for TeamSupport.copyDoubleToComplex\n");
    }
    jclass globalClass2 = (jclass)env->NewGlobalRef(teamClass);
    if (NULL == globalClass2) {
        jniHelper_abort("OOM while attempting to allocate global reference for Team class\n");
    }
    copyDoubleToComplex.targetClass  = globalClass2;
    copyDoubleToComplex.targetMethod = copyId;

    /* Get the value and idx fields of Team.DoubleIdx */
    jclass doubleIdxClass = env->FindClass("Lx10/util/Team$DoubleIdx;");
    if (NULL == doubleIdxClass) {
        jniHelper_abort("Unable to find class x10.util.Team.DoubleIdx\n");
    }
    DoubleIdx_valueId = env->GetFieldID(doubleIdxClass, "value", "D");
    if (NULL == DoubleIdx_valueId) {
        jniHelper_abort("Unable to resolve fieldID for Team.DoubleIdx.value\n");
    }        
    DoubleIdx_idxId = env->GetFieldID(doubleIdxClass, "idx", "I");
    if (NULL == DoubleIdx_idxId) {
        jniHelper_abort("Unable to resolve fieldID for Team.DoubleIdx.idx\n");
    }        
    
}
