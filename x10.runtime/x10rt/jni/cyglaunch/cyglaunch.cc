#include <sys/types.h>
#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <dlfcn.h>
#include <sys/cygwin.h>
#include <windows.h>

//#define TRACE_JNI

#define JRE_BIN_PATH "/jre/bin"
#define JRE_CLIENT_PATH JRE_BIN_PATH "/client/"
#define JRE_SERVER_PATH JRE_BIN_PATH "/server/"
#define JRE_J9VM_PATH JRE_BIN_PATH "/j9vm/"
#define JRE_CLASSIC_PATH JRE_BIN_PATH "/classic/"
#define JVM_DLL "jvm.dll"
void* try_load_jvm_dll(const char* JAVA_HOME, const char* JAVA_HOME_REL_PATH) {
  int JAVA_HOME_length = strlen(JAVA_HOME);
  char dll_name[JAVA_HOME_length+strlen(JAVA_HOME_REL_PATH)+strlen(JVM_DLL)+10];
  strcpy(dll_name, JAVA_HOME);
  strcpy(dll_name+JAVA_HOME_length, JAVA_HOME_REL_PATH);
  strcat(dll_name+JAVA_HOME_length, JVM_DLL);
  int len = cygwin_conv_path(CCP_POSIX_TO_WIN_A|CCP_RELATIVE, dll_name, NULL, 0);
  char* dll_name_w = (char*) malloc((len+1) * sizeof(char));
  cygwin_conv_path(CCP_POSIX_TO_WIN_A, dll_name, dll_name_w, len);
#ifdef TRACE_JNI
    fprintf(stderr, "Loading %s\n", dll_name_w);
#endif
  void* jvm_dll = LoadLibrary(dll_name_w);
  return jvm_dll;
}
void* load_jvm_dll() {
  char* JAVA_HOME = getenv("JAVA_HOME");
  if (JAVA_HOME == NULL) {
    // JAVA_HOME is not set -- try the PATH
#ifdef TRACE_JNI
    fprintf(stderr, "Loading %s\n", JVM_DLL);
#endif
    void* jvm_dll = LoadLibrary(JVM_DLL);
    if (jvm_dll == NULL) {
      fprintf(stderr, "JAVA_HOME is not set.  Unable to load '%s': %s\n", JVM_DLL, dlerror());
      exit(2);
    }
    return jvm_dll;
  }
  void* jvm_dll = try_load_jvm_dll(JAVA_HOME, JRE_SERVER_PATH);
  if (jvm_dll != NULL) return jvm_dll;
  jvm_dll = try_load_jvm_dll(JAVA_HOME, JRE_CLIENT_PATH);
  if (jvm_dll != NULL) return jvm_dll;
  jvm_dll = try_load_jvm_dll(JAVA_HOME, JRE_J9VM_PATH);
  if (jvm_dll != NULL) return jvm_dll;
  jvm_dll = try_load_jvm_dll(JAVA_HOME, JRE_CLASSIC_PATH);
  if (jvm_dll != NULL) return jvm_dll;
  fprintf(stderr, "JAVA_HOME ('%s') may not point to a JRE.  Unable to load '%s': %s\n", JAVA_HOME, JVM_DLL, dlerror());
  exit(2);
}
void release_jvm_dll(void* jvm_dll) {
  if (dlclose(jvm_dll) != 0) {
    fprintf(stderr, "Unable to unload '%s': %s\n", JVM_DLL, dlerror());
    exit(2);
  }
}
void* load_jni_symbol(void* jvm_dll, const char* symbol) {
  void* ptr = dlsym(jvm_dll, symbol);
  if (ptr == NULL) {
    fprintf(stderr, "Unable to load symbol %s from %s: %s\n", symbol, JVM_DLL, dlerror());
    exit(2);
  }
  return ptr;
}

int main(int ac, char** av) {
#ifdef TRACE_JNI
  fprintf(stderr, "Started %s\n", av[0]);
#endif
  void* jvm_dll = load_jvm_dll();
  jint JNICALL (*JNI_CreateJavaVM)(JavaVM **pvm, void **penv, void *args);
  JNI_CreateJavaVM = (jint JNICALL (*)(JavaVM**,void**,void*))load_jni_symbol(jvm_dll, "JNI_CreateJavaVM");

  JavaVM* jvm;
  JNIEnv* env;
  JavaVMInitArgs args;
  args.version = JNI_VERSION_1_2;
  int c;
  int i;
  int nomx = 1;
  for (c = 1; c < ac; c++) {
    if (!strcmp(av[c], "-classpath") || !strcmp(av[c], "-cp")) {
      c++;
      continue;
    }
    if (!strncmp(av[c], "-Xmx", 4) || !strncmp(av[c], "-mx", 3)) {
      nomx = 0;
      continue;
    }
    if (*av[c] != '-')
      break;
  }
  args.nOptions = c - 1 + nomx;
  args.options = (JavaVMOption*) malloc((c - 1 + nomx) * sizeof(JavaVMOption));
  int j = 0;
  for (i = 1; i < c; i++, j++) {
    if (!strcmp(av[i], "-classpath") || !strcmp(av[i], "-cp")) {
      if (i == c-1) {
        fprintf(stderr, "Option %s requires an argument\n", av[i]);
        exit(2);
      }
      i++;
      const char* prefix = "-Djava.class.path=";
      int sz = strlen(prefix) + strlen(av[i]) + 1;
      char* buf = (char*) malloc(sz * sizeof(char));
      strcpy(buf, prefix);
      strcat(buf, av[i]);
      args.options[j].optionString = buf;
      args.options[j].extraInfo = NULL;
      args.nOptions--;
      continue;
    }
    args.options[j].optionString = av[i];
    args.options[j].extraInfo = NULL;
  }
  if (nomx) {
    args.options[j].optionString = strdup("-Xmx512m");
    args.options[j].extraInfo = NULL;
    ++j;
  }
  args.ignoreUnrecognized = JNI_TRUE;
  char* className = av[c];
  if (c == ac) {
    fprintf(stderr, "Usage: %s [jvm_args] <classname> [prog_args]\n", av[0]);
    exit(2);
  }
  jint r1 = JNI_CreateJavaVM(&jvm, (void**)&env, &args);
#ifdef TRACE_JNI
  fprintf(stderr, "JNI_CreateJavaVM returned %d\n", r1);
#endif
  jclass cls = env->FindClass(className); 
#ifdef TRACE_JNI
  fprintf(stderr, "\tFound class %s: %p\n", className, cls);
#endif
  if (env->ExceptionOccurred()) {
    env->ExceptionDescribe();
  }
  jmethodID mid = env->GetStaticMethodID(cls, "main", "([Ljava/lang/String;)V"); 
#ifdef TRACE_JNI
  fprintf(stderr, "\tFound method main([Ljava/lang/String;)V: %p\n", mid);
#endif
  if (env->ExceptionOccurred()) {
    env->ExceptionDescribe();
  }
  const char* STRING = "java/lang/String";
  jclass java_lang_String = env->FindClass(STRING); 
#ifdef TRACE_JNI
  fprintf(stderr, "\tFound class %s: %p\n", STRING, java_lang_String);
#endif
  if (env->ExceptionOccurred()) {
    env->ExceptionDescribe();
  }
  jobjectArray applicationArgs = env->NewObjectArray(ac - c - 1, java_lang_String, NULL);
#ifdef TRACE_JNI
  fprintf(stderr, "\tCreated args array: %p\n", applicationArgs);
#endif
  if (env->ExceptionOccurred()) {
    env->ExceptionDescribe();
  }
  for (i = c + 1; i < ac; i++) {
    jstring applicationArg = env->NewStringUTF(av[i]);
    env->SetObjectArrayElement(applicationArgs, i - c - 1, applicationArg);
  }
  if (env->ExceptionOccurred()) {
    env->ExceptionDescribe();
  }
  env->CallStaticVoidMethod(cls, mid, applicationArgs); 
#ifdef TRACE_JNI
  fprintf(stderr, "\tInvoked main\n");
#endif
  if (env->ExceptionOccurred()) {
    env->ExceptionDescribe();
  }
  jint r2 = jvm->DestroyJavaVM();
#ifdef TRACE_JNI
  fprintf(stderr, "DestroyJavaVM returned %d\n", r2);
#endif
  release_jvm_dll(jvm_dll);
}
