#ifndef __int64
#define __int64 __int64_t
#endif

#ifdef __CYGWIN__
#include <windows.h>
#endif

//#undef __stdcall
//#define __stdcall

#include <sys/types.h>
#include "H.h"
#include <stdio.h>
#include <x10rt_front.h>

//#ifndef __WIN32__
//#ifndef __stdcall
//#define __stdcall
//#endif
//#ifndef __declspec
//#define __declspec(X)
//#endif
//#endif

#ifdef __cplusplus
#define EXTERN extern "C"
#else
#define EXTERN
#endif

static void init() {
#ifdef __CYGWIN__
  char exe[MAX_PATH];
  long res = GetModuleFileName(NULL, exe, MAX_PATH);
  if (res == 0 || res == MAX_PATH) {
    strcpy(exe, "java");
  }
#else
  char* exe = "java";
#endif
  char* av_0[] = { exe, NULL };
  int ac = 1;
  char** av = av_0;
  x10rt_init(&ac, &av);
}

JNIEXPORT void JNICALL Java_H_nat(JNIEnv *e, jobject o) {
  printf("here2\n");
  init();
  printf("Nplaces=%d\n", x10rt_nplaces());
  printf("I am place=%d\n", x10rt_here());
} 

JNIEXPORT void JNICALL Java_H_x10_1init(JNIEnv *e, jobject o) {
  init();
}

JNIEXPORT jint JNICALL Java_H_x10_1nplaces(JNIEnv *e, jobject o) {
  return (jint)x10rt_nplaces();
}

JNIEXPORT jint JNICALL Java_H_x10_1here(JNIEnv *e, jobject o) {
  return (jint)x10rt_here();
}
