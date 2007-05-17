/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: async.h,v 1.2 2007-05-17 09:48:52 ganeshvb Exp $ */

#ifndef __ASYNC_NEW_H__
#define __ASYNC_NEW_H__

#include <iostream>
#include "x10/assert.h"
#include <x10/err.h>
#include "gas.h"

using namespace x10lib;

namespace x10lib{
  
  const int MAX_ARGS = 6;
  template<int N, typename F>
  error_t asyncSpawnInline (place_t target, ...);
}

#include "async.tcc"
#endif


// Local Variables:
// mode: C++
// End:

