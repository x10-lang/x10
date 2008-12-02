/*
 * Filename:RandomAccess_longExternImpl.cpp
 * Generated: 1/28/05 1:15 PM */
/* [CMD] compiled on windows with:
 cl /DWIN32 "-Ic:/Program Files/IBM/Java142/include" \
    "-Ic:/Program Files/IBM/Java142/include/win32" -LD \
    RandomAccess_longExternImpl.cpp -FeRandomAccessLong.dll

 * on AIX with:
 xlC -I/usr/java131/include RandomAccess_longExternImpl.cpp -qmkshrobj \
     -o libRandomAccessLong.so
*/
#include <sys/types.h>
#include <jni.h>

#include "RandomAccess_1longExtern_x10stub.c"

#if defined(__GNUC__) || defined(_AIX)
#define LL(NUM) NUM##LL
#else
#define LL(NUM) NUM
#endif
#if defined(WIN32) || defined(J9WINCE) || defined(RIM386) || \
    (defined(BREW) && defined(AEE_SIMULATOR)) || \
    (defined(J9EPOC32) && defined(J9X86))
/* WARNING: the %I64L is valid only on windows platforms! */
#define LLFMT "%I64Ld"
#else
#define LLFMT "%lld"
#endif

#ifdef __cplusplus
extern "C" {
#endif

  /* * * * * * * */
  static jlong POLY = 7;
  static jlong PERIOD = LL(1317624576693539401);

  class C {
    public:

      /**
       * Utility routine to start random number generator at Nth step
       * (original "starts" routine from RandomAccess)
       * <code>
	Functional synopsis:
	jlong starts(jlong n) :=
	jlong n1=for(jlong t=n; t<0 return t; next t=t+PERIOD){} ;
	jlong n2=for(jlong t=n1; t>PERIOD return t; next t=t-PERIOD){};
	if (n2==0) return 0x1;
	jlong m2[]= new jlong[0..63](i) {i==0?1:(nextRandom**2)(m2[i-1]);}
	int lastSetBit= findFirstSatisfying(int j:62..0)(getBit(n2,j));
	mutable jlong ran=2;
	for(int i=lastSetBit..1) {
	       jlong ranXor= Xor(int j:0..63 where getBit(ran,j))(m2[j]);
	       ran= getBit(n2,i-1)?nextRandom(ranXor):ranXor;}
	return ran;
       * </code>
       */
      static jlong starts(jlong n) {
	int i, j;
	jlong m2[64];
	jlong temp, ran;

	while (n < 0) n += PERIOD;
	while (n > PERIOD) n -= PERIOD;
	if (n == 0) return 1;

	temp = 1;
	for (i = 0; i < 64; i++) {
	  m2[i] = temp;
	  temp = nextRandom(temp);
	  temp = nextRandom(temp);
	}

	for (i = 62; i >= 0; i--)
	  if (getBit(n, i))
	    break;

	ran = 2;
	while (i > 0) {
	  temp = 0;
	  for (j = 0; j < 64; j++)
	    if (getBit(ran, j))
	      temp ^= m2[j];
	  ran = temp;
	  i -= 1;
	  if (getBit(n, i))
	    ran = nextRandom(ran);
	}

	return ran;
      }

    private:
      // self contained constants for C routines

      static jlong nextRandom(jlong temp) {
	return (temp << 1) ^ (temp < 0 ? POLY : 0);
      }

      static bool getBit(jlong n, int i) {
	return  ((n>>i)&1)!=0;
      }

  };

  jlong RandomAccess_1longExtern_starts(jlong n) {
    jlong result = C::starts(n);
    printf("NativeCode==> C::starts("LLFMT") returns "LLFMT"\n", n, result);
    fflush(stdout);
    return result;
  }

#ifdef __cplusplus
}
#endif
