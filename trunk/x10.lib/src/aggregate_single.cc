/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: aggregate_single.cc,v 1.1 2008-01-21 11:31:15 ganeshvb Exp $
 * This file is part of X10 Runtime System.
 */

/** Implementation file for inlinable asyncs aggregation. **/

#include <x10/aggregate_single.h>
#include <x10/xmacros.h>
#include <x10/xassert.h>
#include <stdarg.h>
#include <string.h>
#include <iostream>
#include <lapi.h>
#include "__x10lib.h__"
#include "am.h"

using namespace x10lib;

using namespace std;

static char** __x10_agg_arg_buf;

static int* __x10_agg_counter;

static int __x10_agg_total;

static int __x10_max_bkt_size;

struct element 
{
  element (int i) :
    id (i), before (NULL), after (NULL) 
  {}

  int id;
  element *before;
  element *after;
};

static element** buckets;
static element** head;

typedef struct {
  ulong len;
  void* buf;
} x10_agg_cmpl_t;


static x10_err_t 
AsyncSpawnInlineAgg_i(x10_place_t tgt,size_t size);

static void
AsyncSpawnCompHandlerAgg2(lapi_handle_t *hndl, void *a)
{
  X10_DEBUG (2,  "Entry");
  x10_agg_cmpl_t *c = (x10_agg_cmpl_t *)a;
  AsyncSwitch(-1, (void *)(c->buf), c->len);
  delete[] ((char*) c->buf);
  delete c;
  X10_DEBUG (2,  "Exit");
}

static void *
AsyncSpawnHandlerAgg2(lapi_handle_t hndl, void *uhdr,
		     uint *uhdr_len, ulong *msg_len,
		     compl_hndlr_t **comp_h, void **user_info)
{
  X10_DEBUG (2,  "Entry");
  lapi_return_info_t *ret_info =
    (lapi_return_info_t *)msg_len;
  if (ret_info->udata_one_pkt_ptr || (*msg_len) == 0) {
    AsyncSwitch(-1, ret_info->udata_one_pkt_ptr, *msg_len);
    ret_info->ctl_flags = LAPI_BURY_MSG;
    *comp_h = NULL;
    X10_DEBUG (2,  "Exit");
    return NULL;
  } else {
    x10_agg_cmpl_t *c = new x10_agg_cmpl_t;
    c->len = *msg_len;
    c->buf = (void *)new char [*msg_len];
    *comp_h = AsyncSpawnCompHandlerAgg2;
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

  x10_err_t AsyncAggFlush(size_t size)
  {
    X10_DEBUG (2,  "Entry");
    lapi_cntr_t cntr;
    int tmp;

    for (int j = 0; j < __x10_num_places; j++) {
      if (__x10_agg_counter[j] > 0) {
	LRC(LAPI_Setcntr(__x10_hndl, &cntr, 0));
	LRC(LAPI_Amsend(__x10_hndl, j, (void *) ASYNC_SPAWN_HANDLER_AGG2,
			NULL, 0,	
			(void *)__x10_agg_arg_buf[j],
			size * __x10_agg_counter[j],
			NULL, &cntr, NULL));
	LRC(LAPI_Waitcntr(__x10_hndl, &cntr, 1, &tmp));
      }
      __x10_agg_total -= __x10_agg_counter[j];
      __x10_agg_counter[j] = 0;
    }

   __x10_max_bkt_size = 0;


  for (int i = 0; i < __x10_max_agg_size; i++)
    head[i] = NULL;

   X10_DEBUG (2,  "Exit");
   return X10_OK;
  }

  x10_err_t
  AsyncSpawnInlineAgg(x10_place_t tgt, void *args, size_t size)
  {
    X10_DEBUG (2,  "Entry");
    assert (tgt >=0 && tgt < __x10_num_places);
    assert (size <= X10_MAX_AGG_SIZE * sizeof(x10_async_arg_t));
    int count = __x10_agg_counter[tgt];
    memcpy(&(__x10_agg_arg_buf[tgt][count * size]), args, size);
    x10_err_t err = AsyncSpawnInlineAgg_i(tgt, size);
    X10_DEBUG (2,  "Exit");
    return err;
  }
  
  x10_err_t
  AsyncSpawnInlineAgg(x10_place_t tgt, x10_async_arg_t arg0)
  {
    X10_DEBUG (2,  "Entry");
    assert (tgt >=0 && tgt < __x10_num_places);
    size_t size = sizeof(x10_async_arg_t);
    size_t count = __x10_agg_counter[tgt];
    memcpy(&(__x10_agg_arg_buf[tgt][count * size]),
	   &arg0, sizeof(x10_async_arg_t));
    x10_err_t err = AsyncSpawnInlineAgg_i(tgt, size);
    X10_DEBUG (2,  "Exit");
    return err;
  }
  
  x10_err_t
  AsyncSpawnInlineAgg(x10_place_t tgt,
		      x10_async_arg_t arg0, x10_async_arg_t arg1)
  {
    X10_DEBUG (2,  "Entry");
     assert (tgt >=0 && tgt < __x10_num_places);
     size_t size = 2 * sizeof(x10_async_arg_t);
     int count = __x10_agg_counter[tgt];
     
     memcpy(&(__x10_agg_arg_buf[tgt][count * size]),
	    &arg0, sizeof(x10_async_arg_t));
     memcpy(&(__x10_agg_arg_buf[tgt][count * size +
				     sizeof(x10_async_arg_t)]),
	    &arg1, sizeof(x10_async_arg_t));
     x10_err_t err = AsyncSpawnInlineAgg_i(tgt, size);
    X10_DEBUG (2,  "Exit");
    return err;
  }
  
} /* closing brace for namespace x10lib */


x10_err_t 
AsyncAggSingleInit()
{
  X10_DEBUG (1,  "Entry");

  LRC(LAPI_Addr_set(__x10_hndl, (void *)AsyncSpawnHandlerAgg2, ASYNC_SPAWN_HANDLER_AGG2));
  
   __x10_agg_arg_buf = new char* [__x10_num_places];
   __x10_agg_counter = new int[__x10_num_places];
   for (int j = 0; j < __x10_num_places; j++) {
     __x10_agg_counter[j] = 0;      
     __x10_agg_arg_buf[j] = new char [__x10_max_agg_size * sizeof(x10_async_arg_t)];
   }
   
  __x10_max_bkt_size = 0;
  
  buckets = new element* [__x10_num_places];
  for (int i = 0; i < __x10_num_places; i++)
    {
      buckets[i] = new element (i);
      if (i > 0)
	{
	  buckets[i]->before = buckets[i-1];
	  buckets[i-1]->after = buckets[i];
	}
    }
  
  head = new element* [__x10_max_agg_size];
  for (int i = 0; i < __x10_max_agg_size; i++)
    head[i] = NULL;

  X10_DEBUG (1,  "Exit");
  return X10_OK;
}

void
AsyncAggSingleFinalize ()
{
  for (int j = 0; j < __x10_num_places; j++) {
    delete [] __x10_agg_arg_buf[j];
  }
  delete  __x10_agg_arg_buf;
  delete __x10_agg_counter;
  
   for (int i = 0; i < __x10_num_places; ++i)
     delete buckets[i];
   delete [] buckets;
   
   for (int i = 0; i < __x10_max_agg_size; ++i)
     delete head[i];
   delete [] head;
   
}

static void push (element* e)
{
  int count = __x10_agg_counter[e->id];

  assert (count >= 0 && count < __x10_max_agg_size);
  e->before = NULL;
  e->after = head[count];  
  head[count] = e;
  if (e->after)
    e->after->before = e;
}

static void deq (element* e)
{
  int count = __x10_agg_counter[e->id];
  assert (count >= 0 && count < __x10_max_agg_size);
  if (e->before)
    e->before->after = e->after;
  else
    head[count] = e->after;

  if (e->after)
    e->after->before = e->before;
}


static x10_err_t 
AsyncSpawnInlineAgg_i(x10_place_t tgt, size_t size)
{
  X10_DEBUG (2,  "Entry");

  element* e = buckets[tgt];
  deq(e);

  __x10_agg_counter[tgt]++;
  __x10_agg_total++;

  push(e);

  if (__x10_agg_counter[tgt] > __x10_max_bkt_size)
    __x10_max_bkt_size = __x10_agg_counter[tgt];

  if ((__x10_agg_total) * size >=
      __x10_max_agg_size * sizeof(x10_async_arg_t) ||
      (__x10_agg_total) >= __x10_max_agg_size) {

    element* e = head[__x10_max_bkt_size];
    int task = e->id;
    
    
    X10_DEBUG (1,  "FLUSH_BEGIN" << size * __x10_agg_counter[task] << " " << task);

    lapi_cntr_t cntr;
    int tmp;

    LRC(LAPI_Setcntr(__x10_hndl, &cntr, 0));
    LRC(LAPI_Amsend (__x10_hndl, task, (void *) ASYNC_SPAWN_HANDLER_AGG2, 		    
                     NULL, 0,
		     (void *)__x10_agg_arg_buf[task],
		     size * __x10_agg_counter[task],
		     NULL, &cntr, NULL));

    LRC(LAPI_Waitcntr(__x10_hndl, &cntr, 1, &tmp));

    deq(e);

    __x10_agg_total -= __x10_max_bkt_size;
    __x10_agg_counter[task] = 0;

    push(e);

    while (head[__x10_max_bkt_size] == NULL) {
      __x10_max_bkt_size--;     
    }

    X10_DEBUG (1,  "FLUSH_END");
  }
  
  X10_DEBUG (2,  "Exit");
  return X10_OK;
}

