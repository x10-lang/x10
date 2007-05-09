/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: async.h,v 1.1 2007-05-09 07:04:29 ganeshvb Exp $ */

#ifndef __ASYNC_H__
#define __ASYNC_H__

#include <iostream>
#include "x10/assert.h"
#include <x10/err.h>
#include "gas.h"

using namespace x10lib;

namespace x10lib{
  typedef void (*async_func0_t)();  
  typedef void (*async_func1_t)(async_arg_t);  
  typedef void (*async_func2_t)(async_arg_t, async_arg_t);  
  typedef void (*async_func3_t)(async_arg_t, async_arg_t, async_arg_t);  
  typedef void (*async_func4_t)(async_arg_t, async_arg_t, async_arg_t, 
                                async_arg_t);  

  template<int N, bool INLINE>
  error_t asyncSpawn (place_t target, async_handler_t handler,...);
}

#include "async.tcc"
#endif


// Local Variables:
// mode: C++
// End:

