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

#if defined(__CYGWIN__) || defined(__FreeBSD__)
#undef __STRICT_ANSI__ // Strict ANSI mode is too strict in Cygwin and FreeBSD
#endif

#ifndef __int64
#define __int64 __int64_t
#endif

#undef __stdcall
#define __stdcall
#include "x10rt_jni_helpers.h"

#include <stdio.h>

#include <x10rt_internal.h>

static JavaVM* theJVM;

const char* X10_PAUSE_GC_ON_SEND = getenv("X10_PAUSE_GC_ON_SEND");

/*
 * Use theJVM pointer we cached during initialization to acquire the JNIEnv* for the current thread
 */
JNIEnv* jniHelper_getEnv() {
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
        jniHelper_abort("Failed to attach unattached thread to JavaVM\n");
        return NULL;
    }
    jniHelper_abort("GetEnv failed with return code %d\n", (int)rc);
    return NULL;
}



/*
 * Stash away JavaVM so we can later use it in callback functions invoked by x10rt
 */
void initCachedJVM(JNIEnv* env) {
    if (env->GetJavaVM(&theJVM) != 0) {
        jniHelper_abort("Unable to acquire JavaVM\n");
    }
}


/*
 * Print error message and abort JavaVM
 */
void jniHelper_abort(const char* format, ...) {
    va_list ap;
    va_start(ap, format);
    vfprintf(stderr, format, ap);
    va_end(ap);
    if (ABORT_NEEDED && !x10rt_run_as_library()) abort();
}


/*
 * Raise an OutOfMemoryError.
 * If throwing the exception fails, abort.
 */
void jniHelper_oom(JNIEnv* env, const char* msg) {
    jclass oom = env->FindClass("java/lang/OutOfMemoryError");
    if (NULL == oom) {
        jniHelper_abort(msg);
    }
    jint rc = env->ThrowNew(oom, msg);
    if (rc != 0) {
        jniHelper_abort(msg);
    }
}
