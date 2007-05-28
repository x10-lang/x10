/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: aggregate.h,v 1.3 2007-05-28 06:03:52 ganeshvb Exp $ */

#ifndef __AGGREGATE_H__
#define __AGGREGATE_H__

#include <iostream>
#include "x10/assert.h"
#include <x10/err.h>
#include <x10/types.h>
#include <x10/async.h>

namespace x10lib{
 
  const int MAX_HANDLERS = 128;
  const int MAX_TASKS = 64;
  const int AGG_LIMIT = 1024;

  error_t asyncSpawnInlineAgg (place_t target, async_handler_t handler, async_arg_t arg0);

  error_t asyncSpawnInlineAgg (place_t target, async_handler_t handler, async_arg_t arg0, async_arg_t arg1);

  error_t asyncFlush (async_handler_t handler, int N);

  error_t asyncSpawnInlineAgg (place_t target, async_handler_t handler, int N,...);

  error_t asyncRegisterAgg();
}

#endif
// Local Variables:
// mode: C++
// End:

