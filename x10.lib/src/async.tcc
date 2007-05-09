/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: async.tcc,v 1.2 2007-05-09 07:06:54 ganeshvb Exp $ */

#include <iostream>
#include <stdarg.h>

#include "async.h"

using namespace x10lib;
using namespace std;

template <int N>
struct  asyncDescr
{
  async_handler_t handler;
  async_arg_t args[N];
};

template <int N>
void
asyncDispatch (asyncDescr<N>* a)
{
  switch (N) {

  case 0: 
    (*((async_func0_t)(handlerTable[a->handler].fptr)))();
    break;
  case 1: 
    (*((async_func1_t)(handlerTable[a->handler].fptr)))(a->args[0]);
    break;
  case 2: 
    (*((async_func2_t)(handlerTable[a->handler].fptr)))(a->args[0], a->args[1]);
    break;
  case 3: 
    (*((async_func3_t)(handlerTable[a->handler].fptr)))(a->args[0], a->args[1], a->args[2]);
    break;
  case 4: 
    (*((async_func4_t)(handlerTable[a->handler].fptr)))(a->args[0], a->args[1], a->args[2], a->args[3]);
    break;
  default:
    //assert (false);
    break;
  }
}

template <int N>
void
asyncSpawnCompHandler (lapi_handle_t *handle, void* args)
{
  asyncDescr<N>* a = (asyncDescr<N>*) args;

  asyncDispatch (a);

  delete a;
}

template <int N, bool INLINE> 
void*
asyncSpawnHandler (lapi_handle_t handle, void* uhdr,
		   uint *uhdr_len, ulong* msg_len, 
		   compl_hndlr_t**  comp_h,
		   void** user_info)
{
  asyncDescr<N>* a = new asyncDescr<N>;
  memcpy (a, uhdr, sizeof(asyncDescr<N>));
   
  if (INLINE) {
    asyncDispatch<N> (a);
    *comp_h = NULL;
    delete a;
  } else { 
    *comp_h = asyncSpawnCompHandler<N>; 
    *user_info = (void*) a;
  }

  return NULL;
}


/** 
 * N = number of arguments
 * INLINE = true -> handler inlined in header handler.
 *        = false -> added to deque (currently to seperate completion handler)
 * target = target processor
 * handler = handler method
 * ... = one or more handler arguments 
 */
template <int N, bool INLINE>
error_t
x10lib::asyncSpawn (place_t target, async_handler_t handler, ...) 
{
  va_list  list;

  va_start (list, handler);

  lapi_cntr_t origin_cntr;

  int buffer_size = sizeof (asyncDescr<N>); 

  //IS PowerPC word size 8 ?? 
  buffer_size = buffer_size % 8 ? 8 - (buffer_size % 8) : buffer_size; 

  void* buffer = (void*) new char [buffer_size];
  asyncDescr<N>* a = new (buffer) asyncDescr<N>; 
  a->handler = handler;

  for (int i =0; i < N; i++)
    a->args[i] = va_arg(list, async_arg_t);

  va_end (list);
 
  int tmp;

  if (target != here()) {
    LAPI_Setcntr (GetHandle(), &origin_cntr, 0);
    LAPI_Amsend (GetHandle(),
		 target,
		 asyncSpawnHandler<N, INLINE>, 
		 buffer,
		 buffer_size, 
		 NULL,
		 0,
		 NULL,
		 &origin_cntr, 
		 NULL);
    LAPI_Waitcntr (GetHandle(), &origin_cntr, 1, &tmp);
  } else { 
    asyncDispatch<N> (a);
  }

  delete buffer;

  return X10_OK;
}


// Local Variables:
// mode: C++
// End:


