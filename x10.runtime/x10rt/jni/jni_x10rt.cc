/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

#if defined(__CYGWIN__) || defined(__FreeBSD__)
#undef __STRICT_ANSI__ // Strict ANSI mode is too strict in Cygwin and FreeBSD
#endif

#ifndef __int64
#define __int64 __int64_t
#endif

#include <unistd.h>
#include <sys/types.h>
#include <assert.h>

#include <stdio.h>

#ifdef __CYGWIN__
#include <windows.h>
#endif

#undef __stdcall
#define __stdcall
#include "x10_x10rt_X10RT.h"
#include "x10rt_jni_helpers.h"

#include <x10rt_front.h>
#include <x10rt_internal.h>
#include <string.h>


/*
 * Class:     x10_x10rt_X10RT
 * Method:    x10rt_net_preinit
 * Signature: ()java/lang/String
 */
JNIEXPORT jstring JNICALL Java_x10_x10rt_X10RT_x10rt_1preinit(JNIEnv *env, jclass) {
    initCachedJVM(env);
    char buffer[1024];
    int errorcode = x10rt_preinit(buffer, sizeof(buffer));
    if (errorcode == X10RT_ERR_OK)
    	return env->NewStringUTF(buffer);
    else
    	return NULL;
}

/*
 * Class:     x10_x10rt_X10RT
 * Method:    x10rt_init
 * Signature: (I[Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_x10_x10rt_X10RT_x10rt_1init(JNIEnv *env, jclass, jint numArgs, jobjectArray args) {
    initCachedJVM(env);
    if (args == NULL) {
#ifdef __CYGWIN__
		char exe[MAX_PATH];
		long res = GetModuleFileName(NULL, exe, MAX_PATH);
		if (res == 0 || res == MAX_PATH) {
			strcpy(exe, "java");
		}
#else
		char *exe = const_cast<char*>("java");
#endif
		char *argv_0[] = { exe, NULL };
		int argc = 1;
		char** argv = argv_0;
		x10rt_error err = x10rt_init(&argc, &argv);
		if (err != X10RT_ERR_OK) {
			if (x10rt_error_msg() != NULL)
				fprintf(stderr,"X10RT fatal initialization error: %s\n", x10rt_error_msg());
			if (ABORT_NEEDED && !x10rt_run_as_library()) abort();
			return err;
		}
    }
	else {
		// this is the second part of a 2-phase init
		// numArgs is our place ID
		// args contains an array of connection strings, one per place
		jsize nplaces = env->GetArrayLength(args);
		char** connStrings = (char**)malloc(nplaces*sizeof(char*));
		for (int i=0; i<nplaces; i++) {
			if (i==numArgs)
				connStrings[i] = NULL;
			else {
				jstring connString = (jstring)env->GetObjectArrayElement(args, i);
				connStrings[i] = (char *)env->GetStringUTFChars(connString, 0);
			}
		}
		x10rt_error err = x10rt_init((int*)(&nplaces), &connStrings);
		for (int i=0; i<nplaces; i++)
			env->ReleaseStringUTFChars((jstring)env->GetObjectArrayElement(args, i), connStrings[i]);
		free(connStrings);
		return err;
	}
    return X10RT_ERR_OK;
}
    

/*
 * Class:     x10_x10rt_X10RT
 * Method:    x10rt_registration_complete
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_x10_x10rt_X10RT_x10rt_1registration_1complete(JNIEnv *, jclass) {
    x10rt_registration_complete();
    return 0;
}


/*
 * Class:     x10_x10rt_X10RT
 * Method:    x10rt_finalize
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_x10_x10rt_X10RT_x10rt_1finalize(JNIEnv *, jclass) {
    x10rt_finalize();
    return 0;
}


/*
 * Class:     x10_x10rt_X10RT
 * Method:    x10rt_nplaces
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_x10_x10rt_X10RT_x10rt_1nplaces(JNIEnv *, jclass) {
    return (jint)x10rt_nplaces();
}


/*
 * Class:     x10_x10rt_X10RT
 * Method:    x10rt_ndead
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_x10_x10rt_X10RT_x10rt_1ndead(JNIEnv *, jclass) {
    return (jint)x10rt_ndead();
}


/*
 * Class:     x10_x10rt_X10RT
 * Method:    x10rt_is_place_dead
 * Signature: (I)Z
 */
JNIEXPORT jboolean JNICALL Java_x10_x10rt_X10RT_x10rt_1is_1place_1dead(JNIEnv *, jclass, jint place) {
    return (jboolean)x10rt_is_place_dead(place);
}


/*
 * Class:     x10_x10rt_X10RT
 * Method:    x10rt_here
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_x10_x10rt_X10RT_x10rt_1here(JNIEnv *, jclass) {
    return (jint)x10rt_here();
}

/*
 * Class:     x10_x10rt_X10RT
 * Method:    x10rt_coll_support
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_x10_x10rt_X10RT_x10rt_1coll_1support(JNIEnv *, jclass) {
    return (jint)x10rt_coll_support();
}

/*
 * Class:     x10_x10rt_X10RT
 * Method:    x10rt_probe
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_x10_x10rt_X10RT_x10rt_1probe(JNIEnv *, jclass) {
    x10rt_error err = x10rt_probe();
    if (err != X10RT_ERR_OK) {
        if (x10rt_error_msg() != NULL)
            fprintf(stderr,"X10RT fatal error: %s\n", x10rt_error_msg());
        if (ABORT_NEEDED && !x10rt_run_as_library()) abort();
        return err;
    }
    return X10RT_ERR_OK;
}


/*
 * Class:     x10_x10rt_X10RT
 * Method:    x10rt_blocking_probe_support
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_x10_x10rt_X10RT_x10rt_1blocking_1probe_1support(JNIEnv *, jclass) {
    return x10rt_blocking_probe_support();
}


/*
 * Class:     x10_x10rt_X10RT
 * Method:    x10rt_blocking_probe
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_x10_x10rt_X10RT_x10rt_1blocking_1probe(JNIEnv *, jclass) {
    x10rt_error err = x10rt_blocking_probe();
    if (err != X10RT_ERR_OK) {
        if (x10rt_error_msg() != NULL)
            fprintf(stderr,"X10RT fatal error: %s\n", x10rt_error_msg());
        if (ABORT_NEEDED && !x10rt_run_as_library()) abort();
        return err;
    }
    return X10RT_ERR_OK;
}

/*
 * Class:     x10_x10rt_X10RT
 * Method:    x10rt_unblock_probe
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_x10_x10rt_X10RT_x10rt_1unblock_1probe(JNIEnv *, jclass) {
    x10rt_error err = x10rt_unblock_probe();
    if (err != X10RT_ERR_OK) {
        if (x10rt_error_msg() != NULL)
            fprintf(stderr,"X10RT fatal error: %s\n", x10rt_error_msg());
        if (ABORT_NEEDED && !x10rt_run_as_library()) abort();
        return err;
    }
    return X10RT_ERR_OK;
}


/*
 * Following functions are optionally called by JVM to determine
 * what level of JNI functions are used with this library.
 */
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *, void *) { return JNI_VERSION_1_4; }
/*JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *, void *) {}*/
