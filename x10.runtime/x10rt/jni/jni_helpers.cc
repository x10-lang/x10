#ifndef __int64
#define __int64 __int64_t
#endif

#undef __stdcall
#define __stdcall
#include "x10rt_jni_helpers.h"

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
        fprintf(stderr, "Failed to attach unattached thread to JavaVM\n");
        abort();
    }
    fprintf(stderr, "GetEnv failed with return code %d\n", (int)rc);
    abort();

    return NULL;
}



/*
 * Stash away JavaVM* so we can later use it in callback functions invoked by x10rt
 */
void initCachedJVM(JNIEnv* env) {
    if (env->GetJavaVM(&theJVM) != 0) {
        fprintf(stderr, "Unable to acquire JavaVM*");
        abort();
    }
}
