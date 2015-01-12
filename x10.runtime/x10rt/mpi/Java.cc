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

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <jni.h>

int main(int ac, char* av[]) {
    JavaVM *jvm;
    JNIEnv *env;
    JavaVMInitArgs vm_args;

    int clsIndex;
    for (clsIndex = 1; clsIndex < ac; ++clsIndex) {
        if (*av[clsIndex] != '-') break;
    }

    if (clsIndex >= ac) {
        fprintf(stderr, "Specify Java class.\n");
        exit(1);
    }

    char** vmOptions = av + 1;
    int vmNOptions = clsIndex - 1;
    char* clsName = av[clsIndex];
    char* clsNameFQ = strdup(clsName);
    for (int i = 0; i < strlen(clsNameFQ); ++i) {
        if (clsNameFQ[i] == '.') clsNameFQ[i] = '/';
    }
    char** clsOptions = av + clsIndex + 1;
    int clsNOptions = ac - (clsIndex + 1);

    JavaVMOption* options = new JavaVMOption[vmNOptions];
    for (int i = 0; i < vmNOptions; ++i) {
        options[i].optionString = vmOptions[i];
    }

    vm_args.version = JNI_VERSION_1_6;
    vm_args.nOptions = vmNOptions;
    vm_args.options = options;
    vm_args.ignoreUnrecognized = JNI_TRUE;

    /* load and initialize a Java VM, return a JNI interface pointer in env */
    jint ret = JNI_CreateJavaVM(&jvm, (void**)&env, (void*)&vm_args);
    if (ret != JNI_OK) {
        fprintf(stderr, "Error in JNI_CreateJavaVM. Exiting.\n");
        exit(1);
    }
    delete options;

    /* invoke the static main method of the Java class (clsName) using the JNI */
    jclass cls = env->FindClass(clsNameFQ);
    //free(clsNameFQ);
    if (cls == NULL) {
        fprintf(stderr, "Cannot find %s class. Exiting.\n", clsName);
        exit(1);
    }
    jmethodID mainMethod = env->GetStaticMethodID(cls, "main", "([Ljava/lang/String;)V");
    if (mainMethod == NULL) {
        fprintf(stderr, "Cannot find main method. Exiting.\n");
        exit(1);
    }

    jclass stringCls = env->FindClass("Ljava/lang/String;");
    if (stringCls == NULL) {
        fprintf(stderr, "Cannot find String class. Please check class path. Exiting.\n");
        exit(1);
    }
    jobjectArray mainArgs = env->NewObjectArray(clsNOptions, stringCls, (jobject) NULL);
    if (mainArgs == NULL) {
        fprintf(stderr, "OOM while allocating String array for parameters. Exiting.\n");
        exit(1);
    }
    for (int i = 0; i < clsNOptions; ++i) {
        env->SetObjectArrayElement(mainArgs, i, env->NewStringUTF(clsOptions[i]));
        jthrowable ex = env->ExceptionOccurred();
        if (ex != NULL) {
            fprintf(stderr, "Exception occurred while copying parameters. Exiting.\n");
            exit(1);
        }
    }

    env->CallStaticVoidMethod(cls, mainMethod, mainArgs);
    jthrowable ex = env->ExceptionOccurred();
    if (ex != NULL) {
        fprintf(stderr, "Exception occurred in main method. Exiting.\n");
        env->Throw(ex);
        exit(1);
    }

    /* We are done. */
    jvm->DestroyJavaVM();

    return 0;
}
