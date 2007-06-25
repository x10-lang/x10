/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: types.h,v 1.12 2007-06-25 14:08:25 ganeshvb Exp $
 * This file is part of X10 Runtime System.
 */

#ifndef __X10_TYPES_H
#define __X10_TYPES_H

#include <lapi.h>
#include <sys/types.h>
#include <stdint.h>

//typedef u_int32_t uint32_t; // FIXME [IP] Removed due to conflict
//typedef u_int64_t uint64_t; // FIXME [IP] Removed due to conflict
typedef uint32_t place_t;
typedef uint64_t gas_ref_t;
typedef int64_t async_arg_t;
typedef int32_t async_handler_t;
typedef void (*void_func_t) ();
typedef  struct
{
  void (* fptr)();
} func_t; 
typedef uint32_t x10_place_t;
typedef uint64_t x10_gas_ref_t;
typedef lapi_cntr_t switch_t;
typedef lapi_query_t x10_query_t;
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
