/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: aggregate.h,v 1.1 2007-05-17 09:48:52 ganeshvb Exp $ */

#ifndef __AGGREGATE_H__
#define __AGGREGATE_H__

#include <iostream>
#include "x10/assert.h"
#include <x10/err.h>


namespace x10lib{
 
  const int MAX_HANDLERS = 128;
  const int MAX_TASKS = 64;
  const int AGG_LIMIT = 1024;

  typedef void (*async_funcp_t)(async_arg_t*, int);
 
  template<int N, typename F>
  error_t asyncSpawnInlineAgg (place_t target, async_handler_t handler, ...);

  template <int N, typename F>
  error_t flush (async_handler_t handler);
}

#if FUNCTOR==1
#include "aggregate_func.tcc"
#else 
#include "aggregate_fptr.tcc"
#endif

#endif


// Local Variables:
// mode: C++
// End:

