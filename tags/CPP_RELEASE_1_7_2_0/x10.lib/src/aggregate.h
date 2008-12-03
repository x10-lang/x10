/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: aggregate.h,v 1.26 2008-06-02 16:07:18 ipeshansky Exp $
 * This file is part of X10 Runtime System.
 */

#ifndef __X10_AGGREGATE_H
#define __X10_AGGREGATE_H

#include <x10/err.h>
#include <x10/types.h>

/* C++ Lang Interface */
#ifdef __cplusplus
namespace x10lib {
	
	/* User specified method execution on the remote task. */
	x10_err_t AsyncSpawnInlineAgg(x10_place_t tgt,
					x10_async_handler_t hndlr,
					x10_async_arg_t arg0); // not compiler interface

	x10_err_t AsyncSpawnInlineAgg(x10_place_t tgt,
					x10_async_handler_t hndlr,
					x10_async_arg_t arg0,
					x10_async_arg_t arg1); // not compiler interface

	x10_err_t AsyncSpawnInlineAgg(x10_place_t tgt,
					x10_async_handler_t hndlr,
					void *args, size_t size);

	/* Flush left-over asyncs during aggregation */
	x10_err_t AsyncAggFlush(x10_async_handler_t hndlr, size_t size); // call from finish/next; make internal

} /* closing brace for namespace x10lib */
#endif

/* C Lang Interface */
#ifdef __cplusplus
extern "C" 
{
#endif

/* User specified method execution on the remote task. */
x10_err_t x10_async_spawn_inline_agg1(x10_place_t tgt,
					x10_async_handler_t hndlr,
					x10_async_arg_t arg0); // not compiler interface

x10_err_t x10_async_spawn_inline_agg2(x10_place_t tgt,
					x10_async_handler_t hndlr,
					x10_async_arg_t arg0,
					x10_async_arg_t arg1); // not compiler interface

x10_err_t x10_async_spawn_inline_agg(x10_place_t tgt,
					x10_async_handler_t hndlr,
					void *args, size_t size);

/* Flush left-over asyncs during aggregation */
x10_err_t x10_async_agg_flush(x10_async_handler_t hndlr, size_t size); // call from finish/next; make internal

#ifdef __cplusplus
} /* closing brace for extern "C" */
#endif

#endif /* __X10_AGGREGATE_H */
