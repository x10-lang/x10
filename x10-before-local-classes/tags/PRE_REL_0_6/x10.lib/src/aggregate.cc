/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: aggregate.cc,v 1.22 2007-11-13 05:28:49 ganeshvb Exp $
 * This file is part of X10 Runtime System.
 */

/** Implementation file for inlinable asyncs aggregation. **/

#include <x10/aggregate.h>
#include <x10/xmacros.h>
#include <x10/xassert.h>
#include <stdarg.h>
#include <string.h>
#include <iostream>
#include <lapi.h>

using namespace x10lib;
using namespace std;

static char** __x10_agg_arg_buf[X10_MAX_AGG_HANDLERS];

static int* __x10_agg_counter[X10_MAX_AGG_HANDLERS];

static int __x10_agg_total[X10_MAX_AGG_HANDLERS];

typedef struct {
  x10_async_handler_t handler; 
  ulong len;
  int niter;
  void* buf;
} x10_agg_cmpl_t;

typedef struct {
  x10_async_handler_t handler;
  int niter;
} x10_agg_hdr_t;


static x10_err_t 
asyncSpawnInlineAgg_i(x10_place_t tgt,
		      x10_async_handler_t hndlr, size_t size);
static void
asyncSpawnCompHandlerAgg(lapi_handle_t *hndl, void *a)
{
  X10_DEBUG (2,  "Entry");
  x10_agg_cmpl_t *c = (x10_agg_cmpl_t *)a;
  asyncSwitch(c->handler, (void *)(c->buf), c->niter);
  delete[] ((char*) c->buf);
  delete c;
  X10_DEBUG (2,  "Exit");
}


static void *
asyncSpawnHandlerAgg(lapi_handle_t hndl, void *uhdr,
		     uint *uhdr_len, ulong *msg_len,
		     compl_hndlr_t **comp_h, void **user_info)
{
  X10_DEBUG (2,  "Entry");
  x10_agg_hdr_t buf = *((x10_agg_hdr_t *)uhdr);
  lapi_return_info_t *ret_info =
    (lapi_return_info_t *)msg_len;
  if (ret_info->udata_one_pkt_ptr || (*msg_len) == 0) {
    asyncSwitch(buf.handler, ret_info->udata_one_pkt_ptr,
		buf.niter);
    ret_info->ctl_flags = LAPI_BURY_MSG;
    *comp_h = NULL;
    X10_DEBUG (2,  "Exit");
    return NULL;
  } else {
    x10_agg_cmpl_t *c = new x10_agg_cmpl_t;
    c->len = *msg_len;
    c->handler = buf.handler;
    c->buf = (void *)new char [*msg_len];
    c->niter = buf.niter;
    *comp_h = asyncSpawnCompHandlerAgg;
    ret_info->ret_flags = LAPI_LOCAL_STATE;
    *user_info = (void *)c;
    X10_DEBUG (2,  "Exit");
    return c->buf;
  }
  
  X10_DEBUG (2,  "Exit");
  return NULL;
}

namespace x10lib {

  extern lapi_handle_t __x10_hndl;
  extern int __x10_max_agg_size;

  x10_err_t asyncFlush(x10_async_handler_t hndlr, size_t size)
  {
    X10_DEBUG (2,  "Entry");
    lapi_cntr_t cntr;
    int tmp;
    x10_agg_hdr_t buf;
    buf.handler = hndlr;

    for (int j = 0; j < __x10_num_places; j++) {
      if (__x10_agg_counter[hndlr][j] > 0) {
	buf.niter = __x10_agg_counter[hndlr][j];	
	LRC(LAPI_Setcntr(__x10_hndl, &cntr, 0));
	LRC(LAPI_Amsend(__x10_hndl, j, (void *) ASYNC_SPAWN_HANDLER_AGG, &buf,
			sizeof(x10_agg_hdr_t),
			(void *)__x10_agg_arg_buf[hndlr][j],
			size * __x10_agg_counter[hndlr][j],
			NULL, &cntr, NULL));
	LRC(LAPI_Waitcntr(__x10_hndl, &cntr, 1, &tmp));
      }
      __x10_agg_total[hndlr] -= __x10_agg_counter[hndlr][j];
      __x10_agg_counter[hndlr][j] = 0;
    }
    X10_DEBUG (2,  "Exit");
    return X10_OK;
  }

  x10_err_t
  asyncSpawnInlineAgg(x10_place_t tgt, x10_async_handler_t hndlr,
		      void *args, size_t size)
  {
    X10_DEBUG (2,  "Entry");
    assert (tgt >=0 && tgt < __x10_num_places);
    assert (size <= X10_MAX_AGG_SIZE * sizeof(x10_async_arg_t));
    int count = __x10_agg_counter[hndlr][tgt];
    memcpy(&(__x10_agg_arg_buf[hndlr][tgt][count * size]), args, size);
    x10_err_t err = asyncSpawnInlineAgg_i(tgt, hndlr, size);
    X10_DEBUG (2,  "Exit");
    return err;
  }

  x10_err_t
  asyncSpawnInlineAgg(x10_place_t tgt, x10_async_handler_t hndlr,
		      int n, ...)
  {
    X10_DEBUG (2,  "Entry");
    va_list list;
    va_start(list, n);
    lapi_cntr_t origin_cntr;

    assert (tgt >=0 && tgt < __x10_num_places);
    size_t size = sizeof(x10_async_arg_t);
    assert (n * size <= __x10_max_agg_size * sizeof(x10_async_arg_t));
    size_t count = __x10_agg_counter[hndlr][tgt];
    for (int i = 0; i < n; i++) {
      __x10_agg_arg_buf[hndlr][tgt][count * size +
				    i * sizeof(x10_async_arg_t)] =
	va_arg(list, x10_async_arg_t);
    }
    va_end(list);
    x10_err_t err = asyncSpawnInlineAgg_i(tgt, hndlr, size);
    X10_DEBUG (2,  "Exit");
    return err;
  }

  x10_err_t
  asyncSpawnInlineAgg(x10_place_t tgt, x10_async_handler_t hndlr,
		      x10_async_arg_t arg0)
  {
    X10_DEBUG (2,  "Entry");
    assert (tgt >=0 && tgt < __x10_num_places);
    size_t size = sizeof(x10_async_arg_t);
    size_t count = __x10_agg_counter[hndlr][tgt];
    memcpy(&(__x10_agg_arg_buf[hndlr][tgt][count * size]),
	   &arg0, sizeof(x10_async_arg_t));
    x10_err_t err = asyncSpawnInlineAgg_i(tgt, hndlr, size);
    X10_DEBUG (2,  "Exit");
    return err;
  }

  x10_err_t
  asyncSpawnInlineAgg(x10_place_t tgt, x10_async_handler_t hndlr,
		      x10_async_arg_t arg0, x10_async_arg_t arg1)
  {
    X10_DEBUG (2,  "Entry");
     assert (tgt >=0 && tgt < __x10_num_places);
    size_t size = 2 * sizeof(x10_async_arg_t);
    int count = __x10_agg_counter[hndlr][tgt];

    memcpy(&(__x10_agg_arg_buf[hndlr][tgt][count * size]),
	   &arg0, sizeof(x10_async_arg_t));
    memcpy(&(__x10_agg_arg_buf[hndlr][tgt][count * size +
					   sizeof(x10_async_arg_t)]),
	   &arg1, sizeof(x10_async_arg_t));
    x10_err_t err = asyncSpawnInlineAgg_i(tgt, hndlr, size);
    X10_DEBUG (2,  "Exit");
    return err;
  }

} /* closing brace for namespace x10lib */




x10_err_t 
asyncAggInit()
{
  X10_DEBUG (1,  "Entry");

  LRC(LAPI_Addr_set(__x10_hndl, (void *)asyncSpawnHandlerAgg, ASYNC_SPAWN_HANDLER_AGG));
  
  for (int i = 0; i < X10_MAX_AGG_HANDLERS; i++) {
    __x10_agg_arg_buf[i] = new char* [__x10_num_places];
    __x10_agg_counter[i] = new int[__x10_num_places];
    for (int j = 0; j < __x10_num_places; j++) {
      __x10_agg_counter[i][j] = 0;      
      __x10_agg_arg_buf[i][j] = new char [__x10_max_agg_size * sizeof(x10_async_arg_t)];
    }
  } 
  
  X10_DEBUG (1,  "Exit");
  return X10_OK;
}

x10_err_t
asyncAggFinalize ()
{
  for (int i = 0; i < X10_MAX_AGG_HANDLERS; i++) {
     for (int j = 0; j < __x10_num_places; j++) {
       delete [] __x10_agg_arg_buf[i][j];
     }
     delete [] __x10_agg_arg_buf[i];
     delete [] __x10_agg_counter[i];
   } 
   
   return X10_OK;
}

static x10_err_t 
asyncSpawnInlineAgg_i(x10_place_t tgt,
		      x10_async_handler_t hndlr, size_t size)
{
  X10_DEBUG (2,  "Entry");
  
  __x10_agg_counter[hndlr][tgt]++;
  __x10_agg_total[hndlr]++;

  if ((__x10_agg_total[hndlr]+1) * size >=
      __x10_max_agg_size * sizeof(x10_async_arg_t) ||
      (__x10_agg_total[hndlr]+1) >= __x10_max_agg_size) {
    int max = 0;
    int task = 0;
    
    for (x10_place_t i = 0; i < __x10_num_places; i++) {
      if (__x10_agg_counter[hndlr][i] > max) {
	max = __x10_agg_counter[hndlr][i];
	task = i;
      }
    }
    
    x10_agg_hdr_t buf;
    
    X10_DEBUG (1,  "FLUSH_BEGIN" << size * __x10_agg_counter[hndlr][task] << " " << task);
    buf.handler = hndlr;
    buf.niter = __x10_agg_counter[hndlr][task];
    lapi_cntr_t cntr;
    int tmp;
    LRC(LAPI_Setcntr(__x10_hndl, &cntr, 0));
    LRC(LAPI_Amsend (__x10_hndl, task, (void *) ASYNC_SPAWN_HANDLER_AGG, 
		     &buf, sizeof(x10_agg_hdr_t),
		     (void *)__x10_agg_arg_buf[hndlr][task],
		     size * __x10_agg_counter[hndlr][task],
		     NULL, &cntr, NULL));
    LRC(LAPI_Waitcntr(__x10_hndl, &cntr, 1, &tmp));
    __x10_agg_total[hndlr] -= max;
    __x10_agg_counter[hndlr][task] = 0;
       X10_DEBUG (1,  "FLUSH_END");
  }
  
  X10_DEBUG (2,  "Exit");
  return X10_OK;
}


x10_err_t
x10_async_spawn_inline_agg1(x10_place_t tgt,
			    x10_async_handler_t hndlr, x10_async_arg_t arg0)
{
  X10_DEBUG (2,  "Entry");
  x10_err_t err = x10lib::asyncSpawnInlineAgg(tgt, hndlr, arg0);
  X10_DEBUG (2,  "Exit");
  return err;
}

x10_err_t
x10_async_spawn_inline_agg2(x10_place_t tgt,
			    x10_async_handler_t hndlr,
			    x10_async_arg_t arg0, x10_async_arg_t arg1)
{
  X10_DEBUG (2,  "Entry");
  x10_err_t err = x10lib::asyncSpawnInlineAgg(tgt, hndlr, arg0, arg1);
  X10_DEBUG (2,  "Exit");
  return err;
}

x10_err_t
x10_async_flush(x10_async_handler_t hndl, size_t size)
{
  X10_DEBUG (2,  "Entry");
  x10_err_t err = x10lib::asyncFlush(hndl, size);
  X10_DEBUG (2,  "Exit");
  return err;
}
