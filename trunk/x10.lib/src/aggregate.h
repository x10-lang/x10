/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: aggregate.h,v 1.17 2007-09-13 15:20:04 ganeshvb Exp $
 * This file is part of X10 Runtime System.
 */

#ifndef __X10_AGGREGATE_H
#define __X10_AGGREGATE_H

#include <x10/err.h>
#include <x10/types.h>
#include <x10/async.h>

#define X10_MAX_AGG_HANDLERS 3
#define X10_MAX_AGG_TASKS 1024
#define X10_MAX_AGG_SIZE 1024

typedef  int (*FUNC)(ulong);

/* C++ Lang Interface */
#ifdef __cplusplus
namespace x10lib {

  x10_err_t asyncSpawnInlineAgg(x10_place_t tgt,
					x10_async_handler_t hndlr,
					x10_async_arg_t arg0);

  x10_err_t asyncSpawnInlineAgg(x10_place_t tgt,
					x10_async_handler_t hndlr,
					x10_async_arg_t arg0, x10_async_arg_t arg1);

  x10_err_t asyncFlush(x10_async_handler_t hndlr, size_t size);

  x10_err_t asyncSpawnInlineAgg(x10_place_t tgt,
					x10_async_handler_t hndlr,
				int n,...);

  x10_err_t asyncSpawnInlineAgg(x10_place_t tgt,
					x10_async_handler_t hndlr,
					void *args, size_t size);



  x10_err_t asyncSpawnInlineAgg_hc(x10_place_t tgt,
					x10_async_handler_t hndlr,
					x10_async_arg_t arg0);

  x10_err_t asyncSpawnInlineAgg_hc(x10_place_t tgt,
					x10_async_handler_t hndlr,
					x10_async_arg_t arg0, x10_async_arg_t arg1);

  x10_err_t asyncFlush_hc(x10_async_handler_t hndlr, size_t size);

  x10_err_t asyncSpawnInlineAgg_hc(x10_place_t tgt,
				   x10_async_handler_t hndlr,
				   void *args, size_t size);
  

  x10_err_t asyncFlush_ra(x10_async_handler_t hndlr, size_t size, char* buf, int len, long LogTableSize, int PLACEIDMASK);


 // template <typename FUNC>
 // x10_err_t asyncFlush_ra(x10_async_handler_t hndlr, size_t size, char* buf, int len, FUNC func);


} /* closing brace for namespace x10lib */
#endif

/* C Lang Interface */
#ifdef __cplusplus
extern "C" 
{
#endif

x10_err_t x10_async_spawn_inline_agg1(x10_place_t tgt,
					x10_async_handler_t hndlr,
					x10_async_arg_t arg0);

x10_err_t x10_async_spawn_inline_agg2(x10_place_t tgt,
					x10_async_handler_t hndlr,
					x10_async_arg_t arg0, x10_async_arg_t arg1);

x10_err_t x10_async_flush(x10_async_handler_t hndlr, size_t size);

#ifdef __cplusplus
}
#endif

#endif /* __X10_AGGREGATE_H */
