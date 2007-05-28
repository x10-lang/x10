/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: async.h,v 1.4 2007-05-28 06:03:53 ganeshvb Exp $ */

#ifndef __ASYNC_NEW_H__
#define __ASYNC_NEW_H__

#include <iostream>
#include "x10/assert.h"
#include <x10/err.h>
#include <x10/types.h>
#include <x10/gas.h>

extern int asyncSwitch (async_handler_t, async_arg_t*, int n);

namespace x10lib{
  const int MAX_ARGS = 6;
  error_t asyncSpawnInline (place_t target, async_handler_t handler, int N, ...);
  error_t asyncRegister();
}

#endif

// Local Variables:
// mode: C++
// End:

