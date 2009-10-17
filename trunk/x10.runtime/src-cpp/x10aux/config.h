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
 *   TRACE_REF         - trace reference operations
 *   TRACE_CAST        - trace casts
 *   TRACE_ENV_VAR     - turn on support for the tracing variables listed below
 *   REF_STRIP_TYPE    - experimental option: erase the exact content type in references
 *
 * Note, tracing is not actually enabled unless the following environment variables are defined:
 *   X10_TRACE_ALLOC       - trace allocation operations
 *   X10_TRACE_INIT        - trace x10 class initialization
 *   X10_TRACE_X10RT       - trace x10rt invocations
 *   X10_TRACE_SER         - trace serialization operations
 *   X10_TRACE_ALL         - all of the above
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

#ifdef NO_CHECKS
#define NO_BOUNDS_CHECKS
#define NO_NULL_CHECKS
#define NO_PLACE_CHECKS
#endif

#ifndef NDEBUG 
#define TRACE_ENV_VAR
#endif


#ifndef NO_IOSTREAM
#  include <iostream>
#  include <sstream>
#endif
#include <stdint.h>

#include <x10aux/pragmas.h>

struct x10_char {
    unsigned short v;
    x10_char() : v(0) { }
    x10_char(const char x) : v(x) { }
    x10_char(const int x) : v((unsigned short) x) { }
};
#ifndef NO_IOSTREAM
inline std::ostream &operator << (std::ostream &o, const x10_char &c) {
    return o<<c.v;
}
namespace x10aux {
    template<class T> std::string star_rating (void) {
        std::string str = "[";
        for (size_t i=0 ; i<sizeof(T) ; ++i) str += "*";
        return str+"]";
    }
}
#endif
inline bool operator==(const x10_char a, x10_char b) { return a.v == b.v; }
inline bool operator!=(const x10_char a, x10_char b) { return a.v != b.v; }
inline bool operator>(const x10_char a, x10_char b) { return a.v > b.v; }
inline bool operator>=(const x10_char a, x10_char b) { return a.v >= b.v; }
inline bool operator<(const x10_char a, x10_char b) { return a.v < b.v; }
inline bool operator<=(const x10_char a, x10_char b) { return a.v <= b.v; }

typedef bool     x10_boolean;
typedef int8_t   x10_byte;
typedef int16_t  x10_short;
typedef int32_t  x10_int;
typedef int64_t  x10_long;
typedef float    x10_float;
typedef double   x10_double;
typedef uint8_t  x10_ubyte;
typedef uint16_t x10_ushort;
typedef uint32_t x10_uint;
typedef uint64_t x10_ulong;


namespace x10aux {
    typedef x10_ulong x10_addr_t;

    extern bool init_config_bools_done;
    void init_config_bools (void);
    extern bool use_ansi_colors_;
    extern bool trace_alloc_;
    extern bool trace_init_;
    extern bool trace_x10rt_;
    extern bool trace_ser_;

    extern inline bool use_ansi_colors()
    { if (!init_config_bools_done) init_config_bools() ; return use_ansi_colors_; }
    extern inline bool trace_alloc()
    { if (!init_config_bools_done) init_config_bools() ; return trace_alloc_; }
    extern inline bool trace_init()
    { if (!init_config_bools_done) init_config_bools() ; return trace_init_; }
    extern inline bool trace_x10rt()
    { if (!init_config_bools_done) init_config_bools() ; return trace_x10rt_; }
    extern inline bool trace_ser()
    { if (!init_config_bools_done) init_config_bools() ; return trace_ser_; }

    extern x10_int here;
}

#define ANSI_RESET       (::x10aux::use_ansi_colors()?"\x1b[0m" :"")

#define ANSI_BOLD        (::x10aux::use_ansi_colors()?"\x1b[1m" :"")
#define ANSI_NOBOLD      (::x10aux::use_ansi_colors()?"\x1b[22m":"")

#define ANSI_UNDERLINE   (::x10aux::use_ansi_colors()?"\x1b[4m" :"")
#define ANSI_NOUNDERLINE (::x10aux::use_ansi_colors()?"\x1b[24m":"")

#define ANSI_REVERSE     (::x10aux::use_ansi_colors()?"\x1b[6m" :"")
#define ANSI_NOREVERSE   (::x10aux::use_ansi_colors()?"\x1b[27m":"")

#define ANSI_BLACK       (::x10aux::use_ansi_colors()?"\x1b[30m":"")
#define ANSI_RED         (::x10aux::use_ansi_colors()?"\x1b[31m":"")
#define ANSI_GREEN       (::x10aux::use_ansi_colors()?"\x1b[32m":"")
#define ANSI_YELLOW      (::x10aux::use_ansi_colors()?"\x1b[33m":"")
#define ANSI_BLUE        (::x10aux::use_ansi_colors()?"\x1b[34m":"")
#define ANSI_MAGENTA     (::x10aux::use_ansi_colors()?"\x1b[35m":"")
#define ANSI_CYAN        (::x10aux::use_ansi_colors()?"\x1b[36m":"")
#define ANSI_WHITE       (::x10aux::use_ansi_colors()?"\x1b[37m":"")

#define _MAYBE_DEBUG_MSG(col,type,msg,doit) do { \
    if (doit) _DEBUG_MSG(col,type,msg); \
} while (0)

#define _DEBUG_MSG(col,type,msg) do { \
    std::stringstream ss; \
    ss << ANSI_BOLD << x10aux::here << ": " << col << type << ": " << ANSI_RESET << msg; \
    fprintf(stderr,"%s\n",ss.str().c_str()); \
} while (0)

#define ANSI_ALLOC ANSI_WHITE
#define ANSI_CAST ANSI_RED
#define ANSI_INIT ANSI_MAGENTA
#define ANSI_REF ANSI_YELLOW
#define ANSI_SER ANSI_CYAN
#define ANSI_X10RT ANSI_BLUE

#if !defined(NO_IOSTREAM) && defined(TRACE_ENV_VAR)
#include <stdio.h>
#define _M_(x) _MAYBE_DEBUG_MSG(ANSI_ALLOC,"MM",x,::x10aux::trace_alloc())
#else
#define _M_(x)
#endif

#if !defined(NO_IOSTREAM) && defined(TRACE_CAST)
#include <stdio.h>
#define _CAST_(x) _DEBUG_MSG(ANSI_CAST,"CAST",x)
#else
#define _CAST_(x)
#endif

#if !defined(NO_IOSTREAM) && defined(TRACE_ENV_VAR)
#include <stdio.h>
#define _I_(x) _MAYBE_DEBUG_MSG(ANSI_INIT,"INIT",x,::x10aux::trace_init())
#else
#define _I_(x)
#endif

#if !defined(NO_IOSTREAM) && defined(TRACE_REF)
#include <stdio.h>
#define _R_(x) _DEBUG_MSG(ANSI_REF,"RR",x)
#else
#define _R_(x)
#endif

#if !defined(NO_IOSTREAM) && defined(TRACE_ENV_VAR)
#include <stdio.h>
#define _S_(x) _MAYBE_DEBUG_MSG(ANSI_SER,"SS",x,::x10aux::trace_ser())
#define _Sd_(x) x
#else
#define _S_(x)
#define _Sd_(x)
#endif

#if !defined(NO_IOSTREAM) && defined(TRACE_ENV_VAR)
#include <stdio.h>
#define _X_(x) _MAYBE_DEBUG_MSG(ANSI_X10RT,"XX",x,::x10aux::trace_x10rt())
#else
#define _X_(x)
#endif


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
