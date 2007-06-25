/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: async.cc,v 1.6 2007-06-18 11:29:55 ganeshvb Exp $ */

#include <iostream>
#include <stdarg.h>

#include "async.h"
#include "xmacros.h"

using namespace x10lib;
using namespace std;

async_arg_t args[MAX_ASYNC_ARGS];

void*
asyncSpawnHandler (lapi_handle_t handle, void* uhdr,
		   uint *uhdr_len, ulong* msg_len, 
		   compl_hndlr_t**  comp_h,
		   void** user_info)
{
  lapi_return_info_t* tmp = (lapi_return_info_t*) msg_len;
  asyncSwitch (*((async_handler_t*)uhdr), (async_arg_t*) tmp->udata_one_pkt_ptr, 1);
  *comp_h = NULL;
  return NULL;
}

error_t
asyncRegister()
{
  LAPI_Addr_set (GetHandle(), (void*) asyncSpawnHandler, 2);
  return X10_OK;
}

/** 
 * N = number of arguments
 * F = type of the Functor  
 * target = target processor
 * ... = one or more handler arguments 
 */
error_t
x10lib::asyncSpawnInline (place_t target, async_handler_t handler, int N...) 
{ 
  va_list  list;

  va_start (list, N);

  lapi_cntr_t origin_cntr;

  for (int i =0; i < N; i++)
    args[i] = va_arg(list, async_arg_t);

  va_end (list);
 
  int tmp;

  async_handler_t buf = handler;
  LAPI_Setcntr (GetHandle(), &origin_cntr, 0);
  LAPI_Amsend (GetHandle(),
 		target,
	 	(void*) 2, 
		 &buf,
		 sizeof(buf),
		 args, 
		 N * sizeof(async_arg_t), 
		 NULL,
		 &origin_cntr, 
		 NULL);
    LAPI_Waitcntr (GetHandle(), &origin_cntr, 1, &tmp); 

  return X10_OK;
}

extern "C" 
error_t
x10_async_spawn_inline (place_t target, async_handler_t handler, int N,...)
{
  return x10lib::asyncSpawnInline (target, handler, N);
}

// Local Variables:
// mode: C++
// End:


