/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: x10lib.h,v 1.1 2007-08-02 11:22:46 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

/** X10Lib's Public Interface for Compilers & Application Programmers. **/

#ifndef __X10_X10LIB_H
#define __X10_X10LIB_H

#include <x10/types.h>
#include <x10/xmacros.h>
#include <x10/xassert.h>
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
#ifdef __cplusplus
#include <x10/finish.h>
#include <x10/array.h>
#endif
#include <x10/utility.h>
#include <lapi.h>

/* Maximum message size allowed. */
#define X10_MAX_MSG_SIZE LAPI_MAX_MSG_SZ


#ifdef __cplusplus
namespace x10lib {

/* global variables that can be referenced everywhere */
extern lapi_handle_t __x10_hndl;
extern lapi_thread_func_t __x10_tf;
extern lapi_cntr_t __x10_wait_cntr;
extern int __x10_num_places;
extern int __x10_my_place;
extern int __x10_addr_hndl;
extern int __x10_addrtbl_sz;

/* Initialization */
x10_err_t Init(x10_async_handler_t *hndlrs, int n);
	
/* Termination */
x10_err_t Finalize();

/* Cleanup */
void Cleanup();

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
