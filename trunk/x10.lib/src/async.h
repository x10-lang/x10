/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: async.h,v 1.7 2007-06-07 10:47:11 ganeshvb Exp $ */

#ifndef __ASYNC_NEW_H__
#define __ASYNC_NEW_H__

#include "x10/xassert.h"
#include <x10/err.h>
#include <x10/types.h>
#include <x10/gas.h>

#ifdef __cplusplus
#include <iostream>
extern "C" int asyncSwitch (async_handler_t, void*, size_t size);

namespace x10lib{
  const int MAX_ASYNC_ARGS = 6;
  error_t asyncSpawnInline (place_t target, async_handler_t handler, int N, ...);
}
#endif

#ifdef __cplusplus
extern "C" 
{
#endif
  int async_switch (async_handler_t handler, async_arg_t* args, int n);
  error_t x10_async_spawn_inline (place_t target, async_handler_t handler, int N, ...);
  error_t asnyncRegister(); 
#ifdef __cplusplus
}

// Local Variables:
// mode: C++
// End:

#endif

#endif


