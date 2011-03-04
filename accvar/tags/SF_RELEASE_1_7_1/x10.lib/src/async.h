/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: async.h,v 1.22 2008-06-02 16:07:40 ipeshansky Exp $
 * This file is part of X10 Runtime System.
 */

/** X10Lib's Async Interface. **/

#ifndef __X10_ASYNC_H
#define __X10_ASYNC_H

#include <x10/types.h>
#include <x10/err.h>

#define X10_MAX_ASYNC_ARGS 6
#define X10_MAX_ASYNC_ARGS_SIZE \
		(X10_MAX_ASYNC_ARGS * sizeof(x10_async_arg_t))

#define NO_HANDLER ((x10_async_handler_t) -1)

/* C++ Lang Interface */
#ifdef __cplusplus
namespace x10lib {
	
	/* Spawn an inlinable async on a given place. */
	x10_err_t AsyncSpawnInline(x10_place_t tgt,
					x10_async_handler_t hndlr,
					void *args, size_t size);

} /* closing brace for namespace x10lib */
#endif

/* C Lang Interface */
#ifdef __cplusplus
extern "C" {
#endif

/* Spawn an inlinable async on a given place. */
x10_err_t x10_async_spawn_inline(x10_place_t tgt,
				x10_async_handler_t hndlr,
				void *args, size_t size);

/* Expected to be defined in the user code */
void AsyncSwitch(x10_async_handler_t h, void *args, int niter);

#ifdef __cplusplus
} /* closing brace for extern "C" */
#endif

#endif /* __X10_ASYNC_H */
