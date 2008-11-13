#ifndef X10AUX_CONFIG_H
#define X10AUX_CONFIG_H

/*
 * The following performance macros are supported:
 *   NO_EXCEPTIONS     - remove all exception-related code
 *   NO_BOUNDS_CHECKS  - remove all bounds-checking code
 *   NO_IOSTREAM       - remove all iostream-related code
 *
 *   USE_LONG_ARRAYS   - use 'long' as the type of array indices
 *
 * The following debugging macros are supported:
 *   TRACE_ALLOC       - trace allocation operations
 *   TRACE_CONSTR      - trace object construction
 *   TRACE_REF         - trace reference operations
 *   TRACE_SER         - trace serialization operations
 *   TRACE_X10LIB      - trace X10lib invocations
 *   DEBUG             - general debug trace printouts
 *
 *   REF_STRIP_TYPE    - experimental option: erase the exact content type in references
 *   REF_COUNTING      - experimental option: enable reference counting
 */

//#include <stdio.h>
//#include <stdlib.h>
//#include <string.h>
//#include <string>
#ifndef NO_IOSTREAM
#  include <iostream>
#endif
#include <stdint.h>
//#include <stdarg.h>
//#include <unistd.h>
//#include <math.h>
//#include <errno.h>
#ifdef __GNUC__
#  include <cxxabi.h>
#endif
//#include <x10/x10.h>

#undef clock // because something defines clock as macro in above headers

//using namespace std;

#if !defined(NO_IOSTREAM) && defined(TRACE_ALLOC)
#define _M_(x) cerr << x10_here() << ": MM: " << x << endl
#else
#define _M_(x)
#endif

#if !defined(NO_IOSTREAM) && defined(TRACE_CONSTR)
#define _T_(x) cerr << x10_here() << ": CC: " << x << endl
#else
#define _T_(x)
#endif

#if !defined(NO_IOSTREAM) && defined(TRACE_REF)
#define _R_(x) cerr << x10_here() << ": RR: " << x << endl
#else
#define _R_(x)
#endif

#if !defined(NO_IOSTREAM) && defined(TRACE_SER)
#define _S_(x) cerr << x10_here() << ": SS: " << x << endl
#define _Sd_(x) x
#else
#define _S_(x)
#define _Sd_(x)
#endif

#if !defined(NO_IOSTREAM) && defined(TRACE_X10LIB)
#define _X_(x) cerr << x10_here() << ": XX: " << x << endl
#else
#define _X_(x)
#endif

#if !defined(NO_IOSTREAM) && defined(DEBUG)
#define _D_(x) cerr << x10_here() << ": " << x << endl
#else
#define _D_(x)
#endif

#ifdef IGNORE_EXCEPTIONS
#define NO_EXCEPTIONS
#endif

/* disabled because we now use different mechanism
#define TYPENAME(T) typeid(T).name()
#ifdef __GNUC__
#define MAX_NAME_LENGTH 512
namespace x10 {
    inline const char* __demangle(const char* N) {
        // FIXME: not thread-safe
        static char buffer[MAX_NAME_LENGTH];
        char* d_N = abi::__cxa_demangle(N, NULL, NULL, NULL);
        ::strncpy(buffer, d_N, MAX_NAME_LENGTH-1);
        buffer[MAX_NAME_LENGTH-1] = '\0';
        ::free(d_N);
        return (const char*) buffer;
    }
}
#undef MAX_NAME_LENGTH
#define DEMANGLE(N) x10::__demangle(N)
#define TYPEID(T,D) TYPENAME(T)
#else
#define DEMANGLE(N) N
#define TYPEID(T,D) D
#endif
*/



typedef bool     x10_boolean;
typedef int8_t   x10_byte;
typedef uint16_t x10_char;
typedef int16_t  x10_short;
typedef int32_t  x10_int;
typedef int64_t  x10_long;
typedef float    x10_float;
typedef double   x10_double;

typedef void   x10_void;

// these probably give the wrong impression - that x10_Boolean is a class like
// it is in x10
typedef bool     x10_Boolean;
typedef int8_t   x10_Byte;
typedef uint16_t x10_Char;
typedef int16_t  x10_Short;
typedef int32_t  x10_Int;
typedef int64_t  x10_Long;
typedef float    x10_Float;
typedef double   x10_Double;

typedef void   x10_Void;

/* disabled because arrays are now defined in x10
// Array index type
#ifdef USE_LONG_ARRAYS
typedef x10_long x10_index_t;
#else
typedef x10_int x10_index_t;
#endif
*/

// We must use same mangling rules as the compiler backend uses.
// The c++ target has to mangle fields because c++ does not allow fields
// and methods to have the same name.
#define FMGL(x) x10__##x
#define MMGL(x) x


#endif
