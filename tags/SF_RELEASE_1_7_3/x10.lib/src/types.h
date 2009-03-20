/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: types.h,v 1.27 2008-06-05 04:52:21 ipeshansky Exp $
 * This file is part of X10 Runtime System.
 */

/** X10Lib's Primitive Types. **/

#ifndef __X10_TYPES_H
#define __X10_TYPES_H

#include <sys/types.h>
#include <stdint.h>

/* x10lang types
 * previously part of x10lang.h
 */
#ifndef __cplusplus
typedef enum { false = 0, true } bool;
#endif
typedef bool x10_boolean;
typedef int8_t x10_byte;
typedef uint16_t x10_char;
typedef int16_t x10_short;
typedef int32_t x10_int;
typedef int64_t x10_long;
typedef float x10_float;
typedef double x10_double;

/* x10lang array index */
#ifdef USE_LONG_ARRAYS
typedef x10_long x10_index_t;
#else
typedef x10_int x10_index_t;
#endif

/* async handler */
typedef long x10_async_arg_t;
typedef int x10_async_handler_t;

/* x10 place index */
typedef int x10_place_t;

#endif /* __X10_TYPES_H */
