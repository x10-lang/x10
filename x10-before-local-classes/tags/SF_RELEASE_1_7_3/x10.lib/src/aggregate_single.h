/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: aggregate_single.h,v 1.1 2008-01-21 11:31:15 ganeshvb Exp $
 * This file is part of X10 Runtime System.
 */

#ifndef __X10_AGGREGATE_SINGLE_H
#define __X10_AGGREGATE_SINGLE_H

#include <x10/err.h>
#include <x10/types.h>

/* C++ Lang Interface */
#ifdef __cplusplus
namespace x10lib {
	
	/* User specified method execution on the remote task. */
	x10_err_t AsyncSpawnInlineAgg(x10_place_t tgt,
					x10_async_arg_t arg0);

	x10_err_t AsyncSpawnInlineAgg(x10_place_t tgt,
					x10_async_arg_t arg0,
					x10_async_arg_t arg1);

	x10_err_t AsyncSpawnInlineAgg(x10_place_t tgt,
					void *args, size_t size);

	/* Flush left-over asyncs during aggregation */
	x10_err_t AsyncAggFlush(size_t size);

} /* closing brace for namespace x10lib */
#endif

#endif /* __X10_AGGREGATE_SINGLE_H */
