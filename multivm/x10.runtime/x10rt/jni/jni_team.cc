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
    makeImplStruct* callbackStruct = (makeImplStruct*)arg;
    JNIEnv *env = jniHelper_getEnv();
    jint tmp = (jint)team;

    // put team id into backing int[]
    env->SetIntArrayRegion(callbackStruct->globalResult, 0, 1, &tmp);

    // notify that the activity that was creating the team has finished.
    env->CallStaticVoidMethod(activityTerminationFunc.targetClass,
                              activityTerminationFunc.targetMethod,
                              callbackStruct->globalFinishState);

    // Free resources;
    env->DeleteGlobalRef(callbackStruct->globalResult);
    env->DeleteGlobalRef(callbackStruct->globalFinishState);
    free(callbackStruct->inputPlaces);
    free(callbackStruct);
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
    makeImplStruct* callbackStruct = (makeImplStruct*)malloc(sizeof(makeImplStruct));
    if (NULL == nativePlaces || NULL == callbackStruct) {
        fprintf(stderr, "OOM while attempting to allocate malloced storage in nativeMakeImpl\n");
        abort();
    }        
    callbackStruct->globalResult = (jintArray)globalResult;
    callbackStruct->globalFinishState = globalFinishState;
    callbackStruct->inputPlaces = nativePlaces;
    env->GetIntArrayRegion(places, 0, count, (jint*)nativePlaces);

    x10rt_team_new(count, nativePlaces, &nativeMakeCallback, callbackStruct);
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
