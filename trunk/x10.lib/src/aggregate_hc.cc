/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: aggregate_hc.cc,v 1.2 2007-08-18 18:33:16 ganeshvb Exp $
 * This file is part of X10 Runtime System.
 */

/** Implementation file for inlinable asyncs aggregation. **/

#include <x10/aggregate.h>
#include <x10/xmacros.h>
#include <x10/xassert.h>
#include <stdarg.h>
#include <string.h>
#include <iostream>

using namespace x10lib;
using namespace std;

#define X10_MAX_INTMEDIATE_MESSGS 16

static char sbuf [16384 * 8];
static char** __x10_agg_arg_buf[X10_MAX_AGG_HANDLERS];

static int* __x10_agg_counter[X10_MAX_AGG_HANDLERS];

static int __x10_agg_total[X10_MAX_AGG_HANDLERS];

typedef struct {
  x10_async_handler_t handler; 
  ulong len;
  int niter[X10_MAX_INTMEDIATE_MESSGS];
  int dest[X10_MAX_INTMEDIATE_MESSGS];
  void* buf;
  int scount;
  size_t argSize;
} x10_agg_cmpl_t;

typedef struct {
  x10_async_handler_t handler;
  int scount;
  size_t argSize;
  int niter[X10_MAX_INTMEDIATE_MESSGS];
  int dest[X10_MAX_INTMEDIATE_MESSGS];
} x10_agg_hdr_t;


static void
asyncSpawnCompHandlerAgg(lapi_handle_t *hndl, void *a)
{
  X10_DEBUG (1,  "Entry");

  x10_agg_cmpl_t *c = (x10_agg_cmpl_t *)a;

  char* ptr = (char*) c->buf;
  for (int i = 0; i < c->scount; i++)
    {      
      int arg_size = c->argSize;
      if (c->dest[i] == __x10_my_place) {
	asyncSwitch(c->handler, ptr, c->niter[i]);
      } else {
	int cntr =   __x10_agg_counter[c->handler][c->dest[i]];
	memcpy (&(__x10_agg_arg_buf[c->handler][c->dest[i]][arg_size * cntr]), 
		(void*) ptr, c->niter[i] * arg_size); 
	__x10_agg_counter[c->handler][c->dest[i]] += c->niter[i];
      }
      ptr += c->niter[i] * arg_size;
    }
  
  delete[] ((char*) c->buf);
  delete c;

  X10_DEBUG (1,  "Exit");
}


static void *
asyncSpawnHandlerAgg(lapi_handle_t hndl, void *uhdr,
		     uint *uhdr_len, ulong *msg_len,
		     compl_hndlr_t **comp_h, void **user_info)
{
  X10_DEBUG (1,  "Entry");
  x10_agg_hdr_t buf = *((x10_agg_hdr_t *)uhdr);
  lapi_return_info_t *ret_info =
    (lapi_return_info_t *)msg_len;

  if (ret_info->udata_one_pkt_ptr || (*msg_len) == 0) {
    
    char* ptr = (char*) ret_info->udata_one_pkt_ptr;
    
    int arg_size = buf.argSize;
    for (int i = 0; i < buf.scount; i++)
      {
	if (buf.dest[i] == __x10_my_place) {
	  asyncSwitch(buf.handler, ptr, buf.niter[i]);
	} else {
	  int cntr =   __x10_agg_counter[buf.handler][buf.dest[i]];
	  memcpy (&(__x10_agg_arg_buf[buf.handler][buf.dest[i]][arg_size * cntr]), 
		  ptr, arg_size * buf.niter[i]);
	  __x10_agg_counter[buf.handler][buf.dest[i]] += buf.niter[i];
	  
	}
	ptr += buf.niter[i] * arg_size;
      }

    ret_info->ctl_flags = LAPI_BURY_MSG;    
    *comp_h = NULL;
    
    X10_DEBUG (1,  "Exit");
    return NULL;
  } else {
    x10_agg_cmpl_t *c = new x10_agg_cmpl_t;
    c->len = *msg_len;
    c->handler = buf.handler;
    c->buf = (void *)new char [*msg_len];

    for (int i = 0; i < buf.scount; i++) 
      {
	c->niter[i] = buf.niter[i];
	c->dest[i] = buf.dest[i];
      }

    c->scount = buf.scount;
    c->argSize = buf.argSize;

    *comp_h = asyncSpawnCompHandlerAgg;
    ret_info->ret_flags = LAPI_LOCAL_STATE;
    *user_info = (void *)c;
    X10_DEBUG (1,  "Exit");
    return c->buf;
  }
  
  X10_DEBUG (1,  "Exit");
  return NULL;
}

x10_err_t 
asyncAggInit_hc()
{
  X10_DEBUG (1,  "Entry");

  LRC(LAPI_Addr_set(__x10_hndl, (void *)asyncSpawnHandlerAgg, 8));
  
  for (int i = 0; i < X10_MAX_AGG_HANDLERS; i++) {
    __x10_agg_arg_buf[i] = new char* [__x10_num_places];
    __x10_agg_counter[i] = new int[__x10_num_places];
    for (int j = 0; j < __x10_num_places; j++) {
      __x10_agg_counter[i][j] = 0;      
      __x10_agg_arg_buf[i][j] = new char [X10_MAX_AGG_SIZE * 16 * sizeof(x10_async_arg_t)];
    }
  } 
  
  X10_DEBUG (1,  "Exit");
  return X10_OK;
}

x10_err_t
asyncAggFinalize_hc ()
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
packUpdates (x10_async_handler_t hndlr, int p, int& scount, int& ssize, size_t size, x10_agg_hdr_t* buf)
{ 

  buf->niter[scount] = __x10_agg_counter[hndlr][p];
  buf->dest[scount] = p;
  
  memcpy (&(sbuf[ssize]), __x10_agg_arg_buf[hndlr][p], __x10_agg_counter[hndlr][p] * size);  
  ssize += __x10_agg_counter[hndlr][p] * size;
  scount++;  
  __x10_agg_counter[hndlr][p] = 0;	    
}

static x10_err_t
sendUpdates (int ssize, int partner, x10_agg_hdr_t* buf)
{ 
  lapi_cntr_t cntr;
  int tmp;
  
  LRC(LAPI_Setcntr(__x10_hndl, &cntr, 0));
  LRC(LAPI_Amsend(__x10_hndl, partner, (void *)8, buf,
		  sizeof(x10_agg_hdr_t),
		  (void *) sbuf,
		  ssize,
		  NULL, &cntr, NULL));
  LRC(LAPI_Waitcntr(__x10_hndl, &cntr, 1, &tmp));
}

static x10_err_t 
asyncSpawnInlineAgg_i (x10_place_t tgt,
		      x10_async_handler_t hndlr, size_t size)
{
  X10_DEBUG (1,  "Entry");
  
  __x10_agg_counter[hndlr][tgt]++;
  __x10_agg_total[hndlr]++;
  
  if ((__x10_agg_total[hndlr]+1) * size >=
      X10_MAX_AGG_SIZE * sizeof(x10_async_arg_t) ||
      (__x10_agg_total[hndlr]+1) >= X10_MAX_AGG_SIZE) {
    
    //LAPI_Gfence (__x10_hndl);              
    int factor = 1;
    for (int phase = 0; factor < __x10_num_places; phase++, factor *= 2) {
      
      uint64_t partner = (1 << phase) ^ (uint64_t)__x10_my_place;
      uint64_t mask = ((uint64_t) 1) << phase;
      
      int ssize = 0;
      int scount = 0;
      x10_agg_hdr_t buf;
      if (partner > __x10_my_place) {
	for (uint64_t  p = 0; p < (uint64_t)__x10_num_places; p++) {
	  
	  if ( p == __x10_my_place) continue;
	  
	  if ((p & mask) && __x10_agg_counter[hndlr][p]) {
	    //cout << "before sending " << __x10_my_place << " " << partner << " " << scount << " " << ssize <<" " << __x10_agg_counter[hndlr][p] <<  endl;
	    packUpdates (hndlr, p, scount, ssize, size, &buf);
	  }	  
	}

	buf.handler = hndlr;
	buf.scount = scount;
	buf.argSize = size;
	if (scount)
	  sendUpdates (ssize, partner, &buf);
      } else {
	for (uint64_t  p = 0; p < (uint64_t)__x10_num_places; p++) {
	  
	  if ( p == __x10_my_place) continue;
	  
	  if ((!(p & mask)) && __x10_agg_counter[hndlr][p]) {
	    //cout << "before sending " << __x10_my_place << " " << partner << " " << scount << " " << ssize <<" " << __x10_agg_counter[hndlr][p] <<  endl;
	    packUpdates (hndlr, p, scount, ssize, size, &buf);
	  }
	}     
     
	buf.handler = hndlr;
	buf.scount = scount;
	buf.argSize = size;
	
	if (scount)
	  sendUpdates (ssize, partner, &buf);

      }     
    }
    
    LAPI_Gfence (__x10_hndl);  
    __x10_agg_total[hndlr] = 0;

    //assert (__x10_agg_counter[hndlr][__x10_my_place] == 0);           
    
    asyncSwitch(hndlr, __x10_agg_arg_buf[hndlr][__x10_my_place], __x10_agg_counter[hndlr][__x10_my_place]);
    __x10_agg_counter[hndlr][__x10_my_place]=0;
  }
  
  X10_DEBUG (1,  "Exit");
  return X10_OK;
}

namespace x10lib {

  x10_err_t asyncFlush_hc(x10_async_handler_t hndlr, size_t size)
  {
    X10_DEBUG (1,  "Entry");
    lapi_cntr_t cntr;
    int tmp;
    x10_agg_hdr_t buf;
    buf.handler = hndlr;

    for (int j = 0; j < __x10_num_places; j++) {
      if (__x10_agg_counter[hndlr][j] > 0) {

	buf.niter[0] = __x10_agg_counter[hndlr][j];
	buf.dest[0] = j;
	buf.scount = 1;
	buf.argSize = size;

	LRC(LAPI_Setcntr(__x10_hndl, &cntr, 0));
	LRC(LAPI_Amsend(__x10_hndl, j, (void *)8, &buf,
			sizeof(x10_agg_hdr_t),
			(void *)__x10_agg_arg_buf[hndlr][j],
			size * __x10_agg_counter[hndlr][j],
			NULL, &cntr, NULL));
	LRC(LAPI_Waitcntr(__x10_hndl, &cntr, 1, &tmp));
      }
      __x10_agg_total[hndlr] -= __x10_agg_counter[hndlr][j];
      __x10_agg_counter[hndlr][j] = 0;
    }
    X10_DEBUG (1,  "Exit");
    return X10_OK;
  }

  x10_err_t
  asyncSpawnInlineAgg_hc(x10_place_t tgt, x10_async_handler_t hndlr,
		      void *args, size_t size)
  {
    X10_DEBUG (1,  "Entry");
    assert (size <= X10_MAX_AGG_SIZE * sizeof(x10_async_arg_t));
    int count = __x10_agg_counter[hndlr][tgt];
    memcpy(&(__x10_agg_arg_buf[hndlr][tgt][count * size]), args, size);
    x10_err_t err = asyncSpawnInlineAgg_i(tgt, hndlr, size);
    X10_DEBUG (1,  "Exit");
    return err;
  }

  x10_err_t
  asyncSpawnInlineAgg_hc (x10_place_t tgt, x10_async_handler_t hndlr,
		      x10_async_arg_t arg0)
  {
    X10_DEBUG (1,  "Entry");
    size_t size = sizeof(x10_async_arg_t);
    size_t count = __x10_agg_counter[hndlr][tgt];
    memcpy(&(__x10_agg_arg_buf[hndlr][tgt][count * size]),
	   &arg0, sizeof(x10_async_arg_t));
    x10_err_t err = asyncSpawnInlineAgg_i(tgt, hndlr, size);
    X10_DEBUG (1,  "Exit");
    return err;
  }

  x10_err_t
  asyncSpawnInlineAgg_hc(x10_place_t tgt, x10_async_handler_t hndlr,
		      x10_async_arg_t arg0, x10_async_arg_t arg1)
  {
    X10_DEBUG (1,  "Entry");
    size_t size = 2 * sizeof(x10_async_arg_t);
    int count = __x10_agg_counter[hndlr][tgt];

    memcpy(&(__x10_agg_arg_buf[hndlr][tgt][count * size]),
	   &arg0, sizeof(x10_async_arg_t));
    memcpy(&(__x10_agg_arg_buf[hndlr][tgt][count * size +
					   sizeof(x10_async_arg_t)]),
	   &arg1, sizeof(x10_async_arg_t));
    x10_err_t err = asyncSpawnInlineAgg_i(tgt, hndlr, size);
    X10_DEBUG (1,  "Exit");
    return err;
  }

} /* closing brace for namespace x10lib */



