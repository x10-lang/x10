/*
 * Filename:BasicExternTest1_Impl.c,
 * taken from generated stub
 * compile this into a library Extern1 e.g. Extern1.dll on windows, libExtern1.so on AIX etc
 * for windows, I use the command:

   cl "-Ic:/Program Files/IBM/Java142/include" 
       "-Ic:/Program Files/IBM/Java142/include/win32" -LD BasicExternTest1Impl.c -FeExtern1.dll
 where my java is installed in c:/Program Files/IBM/Java142

 */

#include <jni.h>
#ifdef __cplusplus
extern "C" {
#endif

/* * * * * * * */
  signed int C_doit(){ return 100;}
JNIEXPORT jint JNICALL
Java_C_C_1doit(JNIEnv *env, jobject obj){
   return C_doit();
}
/* * * * * * * */
  signed int BasicExternTest1_doit(){ return 12;}
JNIEXPORT jint JNICALL
Java_BasicExternTest1_BasicExternTest1_1doit(JNIEnv *env, jobject obj){
   return BasicExternTest1_doit();
}

/* * * * * * * */
  signed int BasicExternTest1_overload__F(float f){
    if(12 == f) return 100;
    return 2;
  }

JNIEXPORT jint JNICALL
Java_BasicExternTest1_BasicExternTest1_1overload__F(JNIEnv *env, jobject obj, jfloat f){
   return BasicExternTest1_overload__F( f);
}

/* * * * * * * */
  signed int BasicExternTest1_overload__J(jlong l){
    if(12 == l) return ++l;
    else return 44;
  }
JNIEXPORT jint JNICALL
Java_BasicExternTest1_BasicExternTest1_1overload__J(JNIEnv *env, jobject obj, jlong l){
   return BasicExternTest1_overload__J( l);
}

/* * * * * * * */
  signed int BasicExternTest1_alltypes(unsigned char bool, signed char bt, signed short ch, signed short sh, signed int i, jlong l, float f, double d){
    if(!bool) return 9;
    if(bt != 1) return 10; 
    if(ch != 2) return 11; 
    if(sh != 3) return 12; 
    if(i != 4) return 13; 
    if(l != 5) return 14; 
    if(f != (float) 12) return 15; 
    if(d - (double)1.2 > 0.0000001)  return 16;
    return 101;
  }
JNIEXPORT jint JNICALL
Java_BasicExternTest1_BasicExternTest1_1alltypes(JNIEnv *env, jobject obj, jboolean bool, jbyte bt, jchar ch, jshort sh, jint i, jlong l, jfloat f, jdouble d){
   return BasicExternTest1_alltypes( bool,  bt,  ch,  sh,  i,  l,  f,  d);
}

/* * * * * * * */
  unsigned char BasicExternTest1_returnBoolean(signed int x){
    return ( x == 11);
  }
JNIEXPORT jboolean JNICALL
Java_BasicExternTest1_BasicExternTest1_1returnBoolean(JNIEnv *env, jobject obj, jint x){
   return BasicExternTest1_returnBoolean( x);
}

/* * * * * * * */
  signed char BasicExternTest1_returnByte(signed int x){
    if(x == 12) return 12;
    return 33;
  }
JNIEXPORT jbyte JNICALL
Java_BasicExternTest1_BasicExternTest1_1returnByte(JNIEnv *env, jobject obj, jint x){
   return BasicExternTest1_returnByte( x);
}


/* * * * * * * */
  jlong BasicExternTest1_returnLong(signed int x){
  
    if(x == 101) return 55;
    return 66;
  }
JNIEXPORT jlong JNICALL
Java_BasicExternTest1_BasicExternTest1_1returnLong(JNIEnv *env, jobject obj, jint x){
   return BasicExternTest1_returnLong( x);
}

/* * * * * * * */
 float BasicExternTest1_returnFloat(signed int x){
    if(x == 122) return 1007;
    return -1;
  }
JNIEXPORT jfloat JNICALL
Java_BasicExternTest1_BasicExternTest1_1returnFloat(JNIEnv *env, jobject obj, jint x){
   return BasicExternTest1_returnFloat( x);
}

/* * * * * * * */
  double BasicExternTest1_returnDouble(signed int x){
    if(x == 99) return 100.3;
    return 10.1;
  }
JNIEXPORT jdouble JNICALL
Java_BasicExternTest1_BasicExternTest1_1returnDouble(JNIEnv *env, jobject obj, jint x){
   return BasicExternTest1_returnDouble( x);
}

#ifdef __cplusplus
}
#endif
