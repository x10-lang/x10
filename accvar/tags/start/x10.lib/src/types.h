/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: types.h,v 1.1.1.1 2007-04-25 09:57:46 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

#ifndef __X10_TYPES_H
#define __X10_TYPES_H

#include <sys/types.h>

typedef uint32_t x10_place_t;
typedef uint64_t x10_gas_ref_t;
/* class Switch;
typedef Switch& x10_switch_t; */
typedef void *x10_switch_t;
typedef int32_t x10_async_arg_t;
typedef uint8_t x10_async_handler_t;
/*
class Clock;
typedef Clock& x10_clock_t; */
/* class Iov;
typedef Iov& x10_giov_ref_t; */
typedef void *x10_giov_ref_t;

#endif /* __X10_TYPES_H */
