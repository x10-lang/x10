/*
 * Filename:BasicExternTest1Impl.c
 * implements the methods in the generated stub.
 *
 * Compile this into a library BasicExtern1 e.g. BasicExtern1.dll on windows,
 * libBasicExtern1.so on AIX, etc.
 *
 * [CMD] For windows, I use the command:

   cl "-Ic:/Program Files/IBM/Java142/include" \
      "-Ic:/Program Files/IBM/Java142/include/win32" \
      -LD BasicExternTest1Impl.c -FeBasicExtern1.dll

 * where my java is installed in c:/Program Files/IBM/Java142
 *
 * [IP] In Cygwin, I used

   gcc -mno-cygwin -I"$JAVA_HOME/include" -I"$JAVA_HOME/include/win32" \
       -shared -o /dev/null -Wl,--output-def=BasicExtern1.din \
       BasicExternTest1Impl.c
   sed -e '/Java_/ {; h; s|Java_|_Java_|; G; s| @[0-9]\+\n *| = |' \
       -e 's| @[0-9]\+$||; H; s|00024||; x; G }' < BasicExtern1.din \
       > BasicExtern1.def
   dllwrap -mno-cygwin -I"$JAVA_HOME/include" -I"$JAVA_HOME/include/win32" \
       -shared -o BasicExtern1.dll --def BasicExtern1.def \
       BasicExternTest1Impl.c

 * where JAVA_HOME was set to my java installation directory:
 * "/cygdrive/c/Program Files/IBM/Java50"
 *
 * [IP] In Linux/AIX, a simple

   gcc -I"$JAVA_HOME/include" -I"$JAVA_HOME/include/win32" \
       -shared -o libBasicExtern1.so BasicExternTest1Impl.c

 * worked fine (again, JAVA_HOME being the java install directory)
 *
 * The Makefile in this directory will take care of the latter two
 * cases, using GNU make and gcc/binutils.
 *
 * [AHK] On AIX, I used
   xlc_r -I $JAVA_HOME/include -g -qnolm -bM:SHR -bnoentry -bexpall \
         -o libBasicExtern1.a BasicExternTest1Impl.c
 * [CMD] On AIX, use
   xlC -I/usr/java131/include -qmkshrobj -o libBasicExtern1.so \
       BasicExternTest1Impl.c
 */
#include <sys/types.h>
#include <jni.h>

#include "BasicExternTest1_x10stub.c"
#include "BasicExternTest1_00024C_x10stub.c"

#ifdef __cplusplus
extern "C" {
#endif

  signed int BasicExternTest1_00024C_doit() { return 100; }

  signed int BasicExternTest1_doit() { return 12; }

  signed int BasicExternTest1_overload__F(float f) {
    if (12 == f) return 100;
    return 2;
  }

  signed int BasicExternTest1_overload__J(jlong l) {
    if (12 == l) return ++l;
    else return 44;
  }

  signed int BasicExternTest1_alltypes(unsigned char b, signed char bt, signed short ch, signed short sh, signed int i, jlong l, float f, double d) {
    if (!b) return 9;
    if (bt != 1) return 10; 
    if (ch != 2) return 11; 
    if (sh != 3) return 12; 
    if (i != 4) return 13; 
    if (l != 5) return 14; 
    if (f != (float) 12) return 15; 
    if (d - (double)1.2 > 0.0000001) return 16;
    return 101;
  }

  unsigned char BasicExternTest1_returnBoolean(signed int x) {
    return (x == 11);
  }

  signed char BasicExternTest1_returnByte(signed int x) {
    if (x == 12) return 12;
    return 33;
  }

  jlong BasicExternTest1_returnLong(signed int x) {
    if (x == 101) return 55;
    return 66;
  }

  float BasicExternTest1_returnFloat(signed int x) {
    if (x == 122) return 1007;
    return -1;
  }

  double BasicExternTest1_returnDouble(signed int x) {
    if (x == 99) return 100.3;
    return 10.1;
  }

#ifdef __cplusplus
}
#endif
