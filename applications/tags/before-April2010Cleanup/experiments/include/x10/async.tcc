/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: async.tcc,v 1.1 2007-08-02 11:22:41 srkodali Exp $ */

#include <iostream>
#include <stdarg.h>

#include "async.h"

namespace x10lib {
using namespace std;

async_arg_t args[MAX_ARGS];

template <int N, typename F>
void
asyncDispatch (async_arg_t* a)
{
  F func;
  func(a, N);
}

template <int N, typename F>
void*
asyncSpawnHandler (lapi_handle_t handle, void* uhdr,
		   uint *uhdr_len, ulong* msg_len, 
		   compl_hndlr_t**  comp_h,
		   void** user_info)
{
  memcpy (args, uhdr, N * sizeof(async_arg_t));
  asyncDispatch<N, F> (args);

  *comp_h = NULL;
  return NULL;
}


/** 
 * N = number of arguments
 * F = type of the Functor  
 * target = target processor
 * ... = one or more handler arguments 
 */
template <int N, typename F>
error_t
x10lib::asyncSpawnInline (place_t target, ...) 
{ 
  assert (N <= MAX_ARGS);

  va_list  list;

  va_start (list, target);

  lapi_cntr_t origin_cntr;

  for (int i =0; i < N; i++)
    args[i] = va_arg(list, async_arg_t);

  va_end (list);
 
  int tmp;

  if (target != here()) {
    LAPI_Setcntr (GetHandle(), &origin_cntr, 0);
    LAPI_Amsend (GetHandle(),
		 target,
		 asyncSpawnHandler<N, F>,
		 args, 
		 N * sizeof(async_arg_t), 
		 NULL,
		 0,
		 NULL,
		 &origin_cntr, 
		 NULL);
    LAPI_Waitcntr (GetHandle(), &origin_cntr, 1, &tmp); //(???) 
  } else { 
    asyncDispatch<N, F> (args);
  }


  return X10_OK;
}
}

// Local Variables:
// mode: C++
// End:


