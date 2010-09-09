#ifndef X10AUX_PRAGMAS_H
#define X10AUX_PRAGMAS_H

/*
 * A header file to define portable names for compiler-specific pragmas
 */

/*
 * use this to indicate that a function will not return (eg throw)
 */
#if !defined __CUDA_ARCH__
#define X10_PRAGMA_NORETURN __attribute__ ((noreturn))
#else
#define X10_PRAGMA_NORETURN
#endif

/*
 * Use this to prevent the function from being inlined.
 * Intended use case: infrequently executed code that
 * appears in header files due to our use of templates.
 */
#define X10_PRAGMA_NOINLINE __attribute__ ((noinline))

#endif 

