#ifndef __int64
#define __int64 __int64_t
#endif

#undef __stdcall
#define __stdcall
#include "x10rt_jni_helpers.h"

#include <stdio.h>

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

    // FIXME don't call abort in library mode
    abort();
}
