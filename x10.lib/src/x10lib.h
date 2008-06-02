/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: x10lib.h,v 1.40 2008-06-02 16:08:09 ipeshansky Exp $
 * This file is part of X10 Runtime System.
 */

/** X10Lib's Public Interface for Compilers & Application Programmers. **/

#ifndef __X10_X10LIB_H
#define __X10_X10LIB_H

#include <x10/types.h>
#include <x10/err.h>
#include <x10/gas.h>
#include <x10/progress.h>
#include <x10/order.h>
#include <x10/async.h>
#include <x10/aggregate.h>
#include <x10/aggregate_single.h>
#include <x10/clock.h>
#include <x10/closure.h>
#include <x10/finish.h>
#include <x10/utility.h>
#include <x10/broadcast.h>
#include <x10/array_copy.h>
#include <x10/array_table.h>

/* array operations */
#ifdef __cplusplus
#include <x10/alloc.h>
#include <x10/xmacros.h>
#include <x10/region.h>
#include <x10/iter.h>
#include <x10/dist.h>
#include <x10/point.h>
#include <x10/local_array.h>
#include <x10/dist_array.h>
#include <x10/async_closure.h>
#include <x10/xthreads.h>
#include <x10/queue.h>
#endif

#ifdef __cplusplus
namespace x10lib {

/* x10lib global state */
//extern int __x10_num_places; // not needed -- use method
//extern int __x10_my_place; // not needed -- use method

/* Initialization */
x10_err_t Init(x10_async_handler_t *hndlrs, int n);

/* Termination */
x10_err_t Finalize();

} /* namespace x10lib */
#endif

#ifdef __cplusplus
/* C Lang Interface */
extern "C" {
#endif

/* Initialization */
x10_err_t x10_init(x10_async_handler_t* hndlrs, int n);

/* Termination */
x10_err_t x10_finalize();

#ifdef __cplusplus
} /* extern "C" */
#endif
#endif /* __X10_X10LIB_H */

// TODO: Move C wrapper declarations into another directory
