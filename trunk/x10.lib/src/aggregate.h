/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: aggregate.h,v 1.8 2007-06-18 11:29:55 ganeshvb Exp $ */

#ifndef __AGGREGATE_H__
#define __AGGREGATE_H__

#include <x10/err.h>
#include <x10/types.h>
#include <x10/async.h>

#ifdef __cplusplus
#include <iostream>
namespace x10lib{
 
  const int MAX_AGG_HANDLERS = 10;
  const int MAX_AGG_TASKS = 256;
  const int MAX_AGG_SIZE = 1024;

  error_t asyncSpawnInlineAgg (place_t target, async_handler_t handler, async_arg_t arg0);

  error_t asyncSpawnInlineAgg (place_t target, async_handler_t handler, async_arg_t arg0, async_arg_t arg1);

  error_t asyncFlush (async_handler_t handler, size_t size);

  //TODO: change varargs to async_arg_t* 
  error_t asyncSpawnInlineAgg (place_t target, async_handler_t handler, int N,...);

  error_t asyncSpawnInlineAgg (place_t target, async_handler_t handler, void* args, size_t size);

}
#endif

#ifdef __cplusplus
extern "C" 
{
#endif
  error_t x10_async_spawn_inline_agg1 (place_t target, async_handler_t handler, async_arg_t arg0);

  error_t x10_async_spawn_inline_agg2 (place_t target, async_handler_t handler, async_arg_t arg0, async_arg_t arg1);

  error_t x10_async_flush (async_handler_t handler, size_t size);

#ifdef __cplusplus
}

// Local Variables:
// mode: C++
// End:
#endif

#endif

