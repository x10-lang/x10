/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: async.h,v 1.1 2007-08-02 11:22:40 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

#ifndef __X10_ASYNC_H
#define __X10_ASYNC_H

#include <x10/types.h>
#include <x10/err.h>
#include <lapi.h>

/* C++ Lang Interface */
#define X10_MAX_ASYNC_ARGS 6
#define X10_MAX_ASYNC_ARGS_SIZE \
		X10_MAX_ASYNC_ARGS * sizeof(x10_async_arg_t)



#ifdef __cplusplus
extern "C" void asyncSwitch(x10_async_handler_t, void *,
				int niter);
namespace x10lib {
extern lapi_handle_t __x10_hndl;
extern int __x10_num_places;
extern int __x10_my_place;

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
