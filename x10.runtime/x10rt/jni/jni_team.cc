#include <unistd.h>
#include <sys/types.h>
#include <assert.h>

#include <stdio.h>

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
        fprintf(stderr, "OOM while attempting to create GlobalRef in nativeDelImpl\n");
        abort();
    }
    barrierImplStruct *callbackArg = (barrierImplStruct*)malloc(sizeof(barrierImplStruct));
    if (NULL == callbackArg) {
        fprintf(stderr, "OOM while attempting to allocate malloced storage in nativeMakeImpl\n");
        abort();
    }
    callbackArg->globalFinishState = globalFinishState;

    x10rt_barrier(id, role, &barrierCallback, callbackArg);
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
        fprintf(stderr, "OOM while attempting to allocate malloced storage in nativeMakeImpl\n");
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
 * Method:    initializeTeamSupport
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_x10_x10rt_TeamSupport_initializeTeamSupport(JNIEnv *env, jclass klazz) {
    /* Get a hold of TeamSupport.activityTerminationBookkeeping and stash away its invoke information */
    jmethodID terminateId = env->GetStaticMethodID(klazz, "activityTerminationBookkeeping", "(Lx10/lang/FinishState;)V");
    if (NULL == terminateId) {
        fprintf(stderr, "Unable to resolve methodID for TeamSupport.activityTerminationBookkeeping");
        abort();
    }
    jclass globalClass = (jclass)env->NewGlobalRef(klazz);
    if (NULL == globalClass) {
        fprintf(stderr, "OOM while attempting to allocate global reference for TeamSupport class\n");
        abort();
    }        
    activityTerminationFunc.targetClass  = globalClass;
    activityTerminationFunc.targetMethod = terminateId;
}
