#ifndef X10AUX_CONFIG_H
#define X10AUX_CONFIG_H

/*
 * The following performance macros are supported:
 *   NO_EXCEPTIONS     - remove all exception-related code
 *   NO_CHECKS         - same as NO_BOUNDS_CHECKS NO_NULL_CHECKS NO_PLACE_CHECKS
 *   NO_BOUNDS_CHECKS  - remove all bounds-checking code
 *   NO_NULL_CHECKS    - remove all null-checking code
 *   NO_PLACE_CHECKS   - remove all place-checking code
 *   NO_IOSTREAM       - remove all iostream-related code
 *
 * The following #defines make be specified by the enclosing build
 *   X10_USE_BDWGC     - enable BDW conservative GC
 *
 * The following debugging macros are supported:
 *   TRACE_ALLOC       - trace allocation operations
 *   TRACE_CONSTR      - trace object construction
 *   TRACE_INIT        - trace x10 class initialization
 *   TRACE_REF         - trace reference operations
 *   TRACE_RTT         - trace runtimetype instantiation
 *   TRACE_CAST        - trace casts
 *   TRACE_PGAS        - trace X10lib invocations
 *   TRACE_SER         - trace serialization operations
 *   DEBUG             - general debug trace printouts
 *
 *   REF_STRIP_TYPE    - experimental option: erase the exact content type in references
 */

#ifdef __CUDA_ARCH__
    #ifndef X10_USE_CUDA_HOST
        #define X10_USE_CUDA_HOST
    #endif
    #ifndef X10_USE_CUDA_DEVICE
        #define X10_USE_CUDA_DEVICE
    #endif
    #ifndef NO_IOSTREAM
        #define NO_IOSTREAM // this apparently will be fixed in a future release of cuda
    #endif
    #define GPUSAFE __host__ __device__
    #ifndef NO_CHECKS
        #define NO_CHECKS // can't abort() assert() or throw exception on the gpu
    #endif
    #ifndef NDEBUG
        #define NDEBUG // as above
    #endif
#else
    #define GPUSAFE
#endif

#ifndef USE_ANSI_COLORS
#define ANSI_RESET ""
#define ANSI_BOLD ""
#define ANSI_NOBOLD ""
#define ANSI_UNDERLINE ""
#define ANSI_NOUNDERLINE ""
#define ANSI_REVERSE ""
#define ANSI_NOREVERSE ""
#define ANSI_BLACK ""
#define ANSI_RED ""
#define ANSI_GREEN ""
#define ANSI_YELLOW ""
#define ANSI_BLUE ""
#define ANSI_MAGENTA ""
#define ANSI_CYAN ""
#define ANSI_WHITE ""
#else
#define ANSI_RESET "\x1b[0m"
#define ANSI_BOLD "\x1b[1m"
#define ANSI_NOBOLD "\x1b[22m"
#define ANSI_UNDERLINE "\x1b[4m"
#define ANSI_NOUNDERLINE "\x1b[24m"
#define ANSI_REVERSE "\x1b[6m"
#define ANSI_NOREVERSE "\x1b[27m"
#define ANSI_BLACK "\x1b[30m"
#define ANSI_RED "\x1b[31m"
#define ANSI_GREEN "\x1b[32m"
#define ANSI_YELLOW "\x1b[33m"
#define ANSI_BLUE "\x1b[34m"
#define ANSI_MAGENTA "\x1b[35m"
#define ANSI_CYAN "\x1b[36m"
#define ANSI_WHITE "\x1b[37m"
#endif



#ifndef NO_IOSTREAM
#  include <iostream>
#  include <sstream>
#endif
#include <stdint.h>

#include <x10aux/pragmas.h>

#define _DEBUG_MSG(col,type,msg) do { \
    std::stringstream ss; \
    ss << ANSI_BOLD << x10aux::here() << ": " col << type << ": " ANSI_RESET << msg; \
    fprintf(stderr,"%s\n",ss.str().c_str()); \
} while (0)

#define ANSI_ALLOC ANSI_WHITE
#define ANSI_CAST ANSI_RED
#define ANSI_CONSTR ANSI_WHITE
#define ANSI_INIT ANSI_MAGENTA
#define ANSI_REF ANSI_YELLOW
#define ANSI_RTT ANSI_GREEN
#define ANSI_SER ANSI_CYAN
#define ANSI_PGAS ANSI_BLUE

#if !defined(NO_IOSTREAM) && defined(TRACE_ALLOC)
#define _M_(x) _DEBUG_MSG(ANSI_ALLOC,"MM",x)
#else
#define _M_(x)
#endif

#if !defined(NO_IOSTREAM) && defined(TRACE_CAST)
#define _CAST_(x) _DEBUG_MSG(ANSI_CAST,"CAST",x)
#else
#define _CAST_(x)
#endif

#if !defined(NO_IOSTREAM) && defined(TRACE_CONSTR)
#define _T_(x) _DEBUG_MSG(ANSI_CONSTR,"CC",x)
#else
#define _T_(x)
#endif

#if !defined(NO_IOSTREAM) && defined(TRACE_INIT)
#define _I_(x) _DEBUG_MSG(ANSI_INIT,"INIT",x)
#else
#define _I_(x)
#endif

#if !defined(NO_IOSTREAM) && defined(TRACE_REF)
#define _R_(x) _DEBUG_MSG(ANSI_REF,"RR",x)
#else
#define _R_(x)
#endif

#if !defined(NO_IOSTREAM) && defined(TRACE_RTT)
#define _RTT_(x) _DEBUG_MSG(ANSI_GREEN,"RTT",x)
#else
#define _RTT_(x)
#endif

#if !defined(NO_IOSTREAM) && defined(TRACE_SER)
#define _S_(x) _DEBUG_MSG(ANSI_SER,"SS",x)
#define _Sd_(x) x
#else
#define _S_(x)
#define _Sd_(x)
#endif

#if !defined(NO_IOSTREAM) && defined(TRACE_PGAS)
#define _X_(x) _DEBUG_MSG(ANSI_PGAS,"XX",x)
#else
#define _X_(x)
#endif

#if !defined(NO_IOSTREAM) && defined(DEBUG)
#define _D_(x) std::cerr << x10aux::here() << ": " << x << std::endl
#else
#define _D_(x)
#endif

#ifdef NO_CHECKS
#define NO_BOUNDS_CHECKS
#define NO_NULL_CHECKS
#define NO_PLACE_CHECKS
#endif



typedef bool     x10_boolean;
typedef int8_t   x10_byte;
typedef uint16_t x10_char;
typedef int16_t  x10_short;
typedef int32_t  x10_int;
typedef uint32_t x10_unsigned_int;
typedef int64_t  x10_long;
typedef float    x10_float;
typedef double   x10_double;


// We must use the same mangling rules as the compiler backend uses.
// The c++ target has to mangle fields because c++ does not allow fields
// and methods to have the same name.
#define FMGL(x) x10__##x

//needed if you want to concat from another macro
#ifndef __CONCAT
#define __CONCAT(__x,__y) __x##__y
#endif

//if you want to turn a token into a string
#define __TOKEN_STRING(X) #X

//if you want to do the above but the token is hidden behind a macro
#define __TOKEN_STRING_DEREF(X) __TOKEN_STRING(X)

//combine __FILE__ and __LINE__ without using sprintf or other junk
#define __FILELINE__ __FILE__ ":" __TOKEN_STRING_DEREF(__LINE__) 

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
