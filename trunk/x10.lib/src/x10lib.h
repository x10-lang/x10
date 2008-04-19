/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: x10lib.h,v 1.39 2008-04-19 06:12:16 ganeshvb Exp $
 * This file is part of X10 Runtime System.
 */

/** X10Lib's Public Interface for Compilers & Application Programmers. **/

#ifndef __X10_X10LIB_H
#define __X10_X10LIB_H

#include <x10/types.h>
#include <x10/err.h>
#include <x10/env.h>
#include <x10/lock.h>
#include <x10/register.h>
#include <x10/rmc.h>
#include <x10/gas.h>
#include <x10/progress.h>
#include <x10/switch.h>
#include <x10/order.h>
#include <x10/async.h>
#include <x10/aggregate.h>
#include <x10/aggregate_single.h>
#include <x10/aggregate.h>
#include <x10/clock.h>
#include <x10/xassert.h>
#include <x10/closure.h>
#include <x10/utility.h>
#include <x10/broadcast.h>
#include <x10/am.h>
#include <x10/array_copy.h>
#include <x10/array_table.h>

/* arry operations */
#ifdef __cplusplus
//#include <x10/alloc.h>
#include <x10/reduce.h>
#include <x10/xmacros.h>
#include <x10/finish.h>
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

#include <x10/utility.h>
//#include <lapi.h>

/* Maximum message size allowed. */
#define X10_MAX_MSG_SIZE LAPI_MAX_MSG_SZ


#ifdef __cplusplus
namespace x10lib {

/* x10lib global state */
extern int __x10_inited;
extern lapi_handle_t __x10_hndl;
extern lapi_thread_func_t __x10_tf;
extern lapi_cntr_t __x10_wait_cntr;
extern int __x10_num_places;
extern int __x10_my_place;
extern int __x10_addr_hndl;
extern int __x10_addrtbl_sz;
extern int __x10_max_agg_size;

/** reduction variables **/
extern lapi_cntr_t reduce_cntr;
extern void **reduce_cntr_list;
extern void *scratch;
extern void **reduce_list;
extern void *inbuf[];
extern int reduceCount;

/* Initialization */
x10_err_t Init(x10_async_handler_t *hndlrs, int n);
	
/* Termination */
x10_err_t Finalize();

} /* closing brace for namespace x10lib */
#endif

#ifdef __cplusplus
/* C Lang Interface */
extern "C" {
#endif

/* Initialization */
x10_err_t x10_init(x10_async_handler_t* hndlrs, int n);
		
/* Termination */
x10_err_t x10_finalize();

/* Cleanup */
void x10_cleanup();
	
#ifdef __cplusplus
} /* closing brance for extern "C" */
#endif
#endif /* __X10_X10LIB_H */
