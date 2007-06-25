/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: aggregate.cc,v 1.10 2007-06-25 12:06:29 ganeshvb Exp $ */

#include <iostream>
#include <x10/aggregate.h>
#include <stdarg.h>
#include "xmacros.h"
using namespace std;

using namespace x10lib;

char argbuf[MAX_AGG_HANDLERS][MAX_AGG_TASKS][MAX_AGG_SIZE*MAX_ARGS_SIZE];
int counter[MAX_AGG_HANDLERS][MAX_AGG_TASKS];
int maxCounter[MAX_AGG_HANDLERS];
int total [MAX_AGG_HANDLERS];

struct comp 
{
  async_handler_t handler; 
  ulong len;
  int niter;
  void* buf;
};

struct header
{
  async_handler_t handler;
  int niter;
};

void
asyncSpawnCompHandlerAgg (lapi_handle_t *handle, void* a)
{
  comp* c = (comp*) a;	
  asyncSwitch (c->handler, (void*) (c->buf), c->niter);
  delete [] ((char*) c->buf);
  delete c;
}

error_t
x10lib::asyncFlush (async_handler_t handler, size_t size)
{
  lapi_cntr_t cntr;
  int tmp;
  header buf;
  buf.handler = handler;
  for (int j =0; j < MAX_PLACES; j++) {
    if ( j!= here() && counter[handler][j] != 0) {
      buf.niter = counter[handler][j];
      LRC (LAPI_Setcntr (GetHandle(), &cntr, 0));
      LRC (LAPI_Amsend (GetHandle(),
		   j,
                   (void*) 1,
		   &buf,
		   sizeof(header),
		   (void*) argbuf[handler][j],
		   size*counter[handler][j],
		   NULL,
		   &cntr, 
		   NULL));
      LRC (LAPI_Waitcntr (GetHandle(), &cntr, 1, &tmp));
    }
    total[handler] -= counter[handler][j];
    counter[handler][j] =0;
  }
  return X10_OK;
}

void*
asyncSpawnHandlerAgg (lapi_handle_t handle, void* uhdr,
		      uint *uhdr_len, ulong* msg_len, 
		      compl_hndlr_t**  comp_h,
		      void** user_info)
{
  struct header buf = *((header*) uhdr);
  lapi_return_info_t *ret_info = (lapi_return_info_t *) msg_len;
  if (ret_info->udata_one_pkt_ptr || (*msg_len) == 0) {
    asyncSwitch(buf.handler, ret_info->udata_one_pkt_ptr, buf.niter);
    ret_info->ctl_flags = LAPI_BURY_MSG;
    *comp_h = NULL;
    return NULL;
  } else {
     comp* c = new comp;
     c->len =*msg_len;
     c->handler = buf.handler;
     c->buf = (void*) new char[*msg_len];
     c->niter = buf.niter;
    *comp_h = asyncSpawnCompHandlerAgg ;
    ret_info->ret_flags = LAPI_LOCAL_STATE; 
    *user_info = (void*) c;    
    return c->buf;
  }
}

error_t
asyncRegisterAgg()
{
  LAPI_Addr_set (GetHandle(), (void*) asyncSpawnHandlerAgg, 1);
  return X10_OK;
}

error_t
asyncSpawnInlineAgg_i (place_t target, async_handler_t handler, size_t size)
{
 counter[handler][target]++;
 total[handler]++;

 if (total[handler]*size >= MAX_AGG_SIZE*sizeof(async_arg_t) ||
     total[handler] >= MAX_AGG_SIZE)
  {
    int max = 0;
    int task = 0;
    for (place_t i = 0; i < MAX_PLACES; i++) {
      if (counter[handler][i]> max) {
        max = counter[handler][i];
        task = i;
      }
    }
    
    struct header buf;
    buf.handler = handler;
    buf.niter = counter[handler][task];
    lapi_cntr_t cntr;
    int tmp;
    LRC (LAPI_Setcntr (GetHandle(), &cntr, 0));
    LRC (LAPI_Amsend (GetHandle(),
		 task,
		 (void*) 1, 
		 &buf,
		 sizeof(header),
		 (void*) argbuf[handler][task],
		 size*counter[handler][task],
		 NULL,
		 &cntr, //NULL,
		 NULL));
    LRC (LAPI_Waitcntr (GetHandle(), &cntr, 1, &tmp));
    total[handler] -= max;
    counter[handler][task] = 0;
  } 
 
 return X10_OK;
}

error_t
x10lib::asyncSpawnInlineAgg (place_t target, async_handler_t handler, void* args, size_t size)
{
  int  count = counter [handler][target];
  memcpy (&(argbuf[handler][target][count*size]), args, size);
  return asyncSpawnInlineAgg_i (target, handler, size);
}


error_t
x10lib::asyncSpawnInlineAgg (place_t target, async_handler_t handler, int N ...)
{
 va_list  list;
 
 va_start (list, N);
 
 lapi_cntr_t origin_cntr;
 
  size_t size = sizeof(async_arg_t);
 size_t count = counter [handler][target];
 for (int i =0; i < N; i++)
   argbuf[handler][target][count*size+i*sizeof(async_arg_t)] = va_arg (list, async_arg_t); 
 
 va_end (list);
  return asyncSpawnInlineAgg_i (target, handler, size);
}

error_t
x10lib::asyncSpawnInlineAgg (place_t target, async_handler_t handler, async_arg_t arg0)
{
  size_t size = sizeof(async_arg_t);
  size_t count = counter [handler][target];
  memcpy (&(argbuf[handler][target][count*size]), &arg0, sizeof(async_arg_t));
  return asyncSpawnInlineAgg_i (target, handler, size);
}

error_t
x10lib::asyncSpawnInlineAgg (place_t target, async_handler_t handler, async_arg_t arg0, async_arg_t arg1)
{
  size_t size = 2*sizeof(async_arg_t);
  int count = counter [handler][target];
  memcpy (&(argbuf[handler][target][count*size]), &arg0, sizeof(async_arg_t));
  memcpy (&(argbuf[handler][target][count*size+sizeof(async_arg_t)]), &arg1, sizeof(async_arg_t));
  return asyncSpawnInlineAgg_i (target, handler, size);
}

error_t
x10_async_spawn_inline_agg1 (place_t target, async_handler_t handler, async_arg_t arg0)
{
  return x10lib::asyncSpawnInlineAgg (target, handler, arg0);
}

error_t
x10_async_spawn_inline_agg2 (place_t target, async_handler_t handler, async_arg_t arg0, async_arg_t arg1)
{
  return x10lib::asyncSpawnInlineAgg (target, handler, arg0, arg1);
}

error_t
x10_async_flush (async_handler_t handle, size_t size)
{
  return x10lib::asyncFlush (handle, size);
}

// Local Variables:
// mode: C++
// End:

