/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: async.h,v 1.12 2007-06-25 15:47:47 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

#ifndef __X10_ASYNC_H
#define __X10_ASYNC_H

#include <x10/types.h>
#include <x10/err.h>

/* C++ Lang Interface */
#define X10_MAX_ASYNC_ARGS 6
#define X10_MAX_ASYNC_ARGS_SIZE \
		X10_MAX_ASYNC_ARGS * sizeof(x10_async_arg_t)

#ifdef __cplusplus
namespace x10lib {

extern "C" int asyncSwitch(x10_async_handler_t, void *,
				size_t size, int niter);

x10_err_t asyncSpawnInline(x10_place_t tgt,
				x10_async_handler_t hndlr, int n, ...);

} /* closing brace for namespace x10lib */
#endif

/* C Lang Interface */
#ifdef __cplusplus
extern "C" {
#endif

int x10_async_switch(x10_async_handler_t hndlr,
			x10_async_arg_t *args, int n);

x10_err_t x10_async_spawn_inline(x10_place_t tgt,
			x10_async_handler_t hndlr, int n, ...);

x10_err_t x10_async_register(void);

#ifdef __cplusplus
}
#endif

#endif /* __X10_ASYNC_H */
