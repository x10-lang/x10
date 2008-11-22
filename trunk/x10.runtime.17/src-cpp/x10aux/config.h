#ifndef X10AUX_CONFIG_H
#define X10AUX_CONFIG_H

/*
 * The following performance macros are supported:
 *   NO_EXCEPTIONS     - remove all exception-related code
 *   NO_BOUNDS_CHECKS  - remove all bounds-checking code
 *   NO_IOSTREAM       - remove all iostream-related code
 *
 * The following debugging macros are supported:
 *   TRACE_ALLOC       - trace allocation operations
 *   TRACE_CONSTR      - trace object construction
 *   TRACE_REF         - trace reference operations
 *   TRACE_RTT         - trace runtimetype instantiation
 *   TRACE_CAST        - trace casts
 *   TRACE_SER         - trace serialization operations
 *   TRACE_PGAS      - trace X10lib invocations
 *   DEBUG             - general debug trace printouts
 *
 *   REF_STRIP_TYPE    - experimental option: erase the exact content type in references
 *   REF_COUNTING      - experimental option: enable reference counting
 */



#ifndef NO_IOSTREAM
#  include <iostream>
#endif
#include <stdint.h>

#include <x10/x10.h> //pgas

#if !defined(NO_IOSTREAM) && defined(TRACE_ALLOC)
#define _M_(x) std::cerr << x10_here() << ": MM: " << x << std::endl
#else
#define _M_(x)
#endif

#if !defined(NO_IOSTREAM) && defined(TRACE_CAST)
#define _CAST_(x) std::cerr << x10_here() << ": CAST: " << x << std::endl
#else
#define _CAST_(x)
#endif

#if !defined(NO_IOSTREAM) && defined(TRACE_CONSTR)
#define _T_(x) std::cerr << x10_here() << ": CC: " << x << std::endl
#else
#define _T_(x)
#endif

#if !defined(NO_IOSTREAM) && defined(TRACE_REF)
#define _R_(x) std::cerr << x10_here() << ": RR: " << x << std::endl
#else
#define _R_(x)
#endif

#if !defined(NO_IOSTREAM) && defined(TRACE_RTT)
#define _RTT_(x) std::cerr << x10_here() << ": RTT: " << x << std::endl
#else
#define _RTT_(x)
#endif

#if !defined(NO_IOSTREAM) && defined(TRACE_SER)
#define _S_(x) std::cerr << x10_here() << ": SS: " << x << std::endl
#define _Sd_(x) x
#else
#define _S_(x)
#define _Sd_(x)
#endif

#if !defined(NO_IOSTREAM) && defined(TRACE_PGAS)
#define _X_(x) std::cerr << x10_here() << ": XX: " << x << std::endl
#else
#define _X_(x)
#endif

#if !defined(NO_IOSTREAM) && defined(DEBUG)
#define _D_(x) std::cerr << x10_here() << ": " << x << std::endl
#else
#define _D_(x)
#endif

#ifdef IGNORE_EXCEPTIONS
#define NO_EXCEPTIONS
#endif



typedef bool     x10_boolean;
typedef int8_t   x10_byte;
typedef uint16_t x10_char;
typedef int16_t  x10_short;
typedef int32_t  x10_int;
typedef int64_t  x10_long;
typedef float    x10_float;
typedef double   x10_double;

typedef void   x10_void;

// We must use the same mangling rules as the compiler backend uses.
// The c++ target has to mangle fields because c++ does not allow fields
// and methods to have the same name.
#define FMGL(x) x10__##x


#endif
// vim:tabstop=4:shiftwidth=4:expandtab
