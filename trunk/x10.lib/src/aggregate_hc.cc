/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: aggregate_hc.cc,v 1.12 2007-08-30 05:54:12 ganeshvb Exp $
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


#define X10_MAX_LOG_NUMPROCS 8

#define MIN(A, B) A > B ? B : A

static char sbuf [2 * 32 * X10_MAX_AGG_SIZE * 8];

static char* rbuf[2][X10_MAX_LOG_NUMPROCS];

static char** __x10_agg_arg_buf[X10_MAX_AGG_HANDLERS];

static int* __x10_agg_counter[X10_MAX_AGG_HANDLERS];

static int __x10_agg_total[X10_MAX_AGG_HANDLERS];

size_t recvMesgLen[2][X10_MAX_LOG_NUMPROCS];

lapi_cntr_t recvCntr[X10_MAX_LOG_NUMPROCS];

typedef struct {
  ulong len;
  ulong phase;
} x10_agg_cmpl_t;

typedef struct {
  x10_async_handler_t handler;
  int niter;
} x10_agg_hdr_t;

static void
asyncSpawnCompHandlerAgg(lapi_handle_t *hndl, void *a)
{
  X10_DEBUG (1,  "Entry");

  x10_agg_cmpl_t* b = (x10_agg_cmpl_t*) a;
 
  //  if (__x10_my_place == 2) cout << "HI " << endl;

  int tmp;

  LAPI_Getcntr (__x10_hndl, &recvCntr[b->phase], &tmp);

  assert (tmp < 2);

  LAPI_Setcntr (__x10_hndl, &recvCntr[b->phase], tmp + 1);
  
  recvMesgLen[tmp][b->phase] = b->len;

  assert (b->len < 2 * 16384 * 16);
  
  delete b;

  X10_DEBUG (1,  "Exit");
}


typedef struct {
  void* buf;
  x10_async_handler_t handler; 
  int niter;
} x10_agg_flush_cmpl_t;

static void
asyncFlushComplHandler (lapi_handle_t *hndl, void *a)
{
  X10_DEBUG (1,  "Entry");
  x10_agg_flush_cmpl_t *c = (x10_agg_flush_cmpl_t *)a;
  asyncSwitch(c->handler, (void *)(c->buf), c->niter);
  delete[] ((char*) c->buf);
  delete c;
  X10_DEBUG (1,  "Exit");
}


static void *
asyncFlushHandler(lapi_handle_t hndl, void *uhdr,
		     uint *uhdr_len, ulong *msg_len,
		     compl_hndlr_t **comp_h, void **user_info)
{
  X10_DEBUG (1,  "Entry");
  x10_agg_hdr_t buf = *((x10_agg_hdr_t *)uhdr);
  lapi_return_info_t *ret_info =
    (lapi_return_info_t *)msg_len;

  if (ret_info->udata_one_pkt_ptr || (*msg_len) == 0) {
    asyncSwitch(buf.handler, ret_info->udata_one_pkt_ptr,
		buf.niter);
    ret_info->ctl_flags = LAPI_BURY_MSG;
    *comp_h = NULL;
    X10_DEBUG (1,  "Exit");
    return NULL;  
  } else {
    x10_agg_flush_cmpl_t *c = new x10_agg_flush_cmpl_t;
    c->buf = (void *)new char [*msg_len];
    c->handler = buf.handler;
    c->niter = buf.niter;
    *comp_h = asyncFlushComplHandler;
    ret_info->ret_flags = LAPI_LOCAL_STATE;
    *user_info = (void *)c;
    X10_DEBUG (1,  "Exit");
    return c->buf;
  }

  X10_DEBUG (1,  "Exit");
  return NULL;
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


  x10_agg_cmpl_t* a = new x10_agg_cmpl_t;
  a->phase = *((ulong*) uhdr);
  a->len = *msg_len;
  int cntrVal; 
  LAPI_Getcntr (__x10_hndl, &recvCntr[a->phase], &cntrVal);
  if (ret_info->udata_one_pkt_ptr || (*msg_len) == 0) {
    memcpy (rbuf[cntrVal][a->phase], ret_info->udata_one_pkt_ptr, *msg_len);
    asyncSpawnCompHandlerAgg (&hndl, (void*) a);
    ret_info->ctl_flags = LAPI_BURY_MSG;
    *comp_h = NULL;
    return NULL;
  } else {
    lapi_return_info_t *ret_info =
      (lapi_return_info_t *)msg_len;
    
    *comp_h = asyncSpawnCompHandlerAgg;
    ret_info->ret_flags = LAPI_LOCAL_STATE;
    *user_info = (void*) a;
    return rbuf[cntrVal][a->phase];  
  }
    
  X10_DEBUG (1,  "Exit");  
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
      __x10_agg_arg_buf[i][j] = new char [X10_MAX_AGG_SIZE * 32 * sizeof(x10_async_arg_t)];
    }
  } 

  for (int i = 0; i < X10_MAX_LOG_NUMPROCS; i++) {
    rbuf[0][i] = new char [16384 * 32];
    rbuf[1][i] = new char [16384 * 32];
    recvMesgLen[0][i] = 0;
    recvMesgLen[1][i] = 0;
    LAPI_Setcntr (__x10_hndl, &(recvCntr[i]), 0);  
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
sort_data_args (x10_async_handler_t hndlr,  int& ssize, size_t size, ulong mask, int phase, int cond)
{ 

  for (x10_place_t p = 0; p < __x10_num_places; p++) {
    
    if (p == __x10_my_place) continue;
    
    //    cout << "Hello " << __x10_my_place << " " << (p & mask) << " " << (MIN((((ulong) p) & mask), 1)) << " " <<  p << " " << __x10_agg_counter[hndlr][p] << endl;     
    if (((MIN((((ulong) p) & mask), 1)) == cond) && (__x10_agg_counter[hndlr][p] > 0)) {    
            
     
      memcpy (&(sbuf[ssize]), &p, sizeof(x10_place_t)); 
      ssize += sizeof(x10_place_t);
      
      //int message_size = __x10_agg_counter[hndlr][p] * size;
      memcpy (&(sbuf[ssize]), &__x10_agg_counter[hndlr][p], sizeof(int));  
      ssize += sizeof(int);
      
      memcpy (&(sbuf[ssize]), __x10_agg_arg_buf[hndlr][p], __x10_agg_counter[hndlr][p] * size);  
      ssize += __x10_agg_counter[hndlr][p] * size;
      
      __x10_agg_counter[hndlr][p] = 0;	    
      
    }
  }
}

static x10_err_t
sort_data_recvs (x10_async_handler_t hndlr, int& ssize, size_t size, ulong mask, int phase, int cond, int cntrVal)
{ 
  for (int s = 0; s < recvMesgLen[cntrVal][phase]; ) {
    
    x10_place_t p =  *((x10_place_t*) (rbuf[cntrVal][phase] + s));

    s += sizeof (x10_place_t);

    int message_size = *((int *) (rbuf[cntrVal][phase] + s));    
    s += sizeof (int);
    
    //cout << p << " " << recvMesgLen[cntrVal][phase] << " " << s << " " << cntrVal << " " << " " << message_size << " " <<  phase << endl;
    assert (p >= 0 && p < __x10_num_places); 
    
    //if (phase == 2) {cout << " P : " << p << endl; assert (p == __x10_my_place);}
    
    if (p == __x10_my_place) 
      {
	//int cntr = __x10_agg_counter[hndlr][p];
	asyncSwitch(hndlr, rbuf[cntrVal][phase] + s, message_size);
	//memcpy (&(__x10_agg_arg_buf[hndlr][__x10_my_place][cntr]), rbuf + s, message_size * size);
	//__x10_agg_counter[hndlr][p] += message_size;	
	
      } else if (((MIN((((ulong) p) & mask), 1)) == cond) && (message_size > 0)) {    
	
	assert (message_size > 0);
	
	memcpy (&(sbuf[ssize]), &p, sizeof(x10_place_t));       
	ssize += sizeof(x10_place_t);
	
	memcpy (&(sbuf[ssize]), &message_size, sizeof(int));        
	ssize += sizeof(int);
	
	memcpy (&(sbuf[ssize]), rbuf[cntrVal][phase] + s, message_size * size);        
	ssize += message_size * size;
      } else if (message_size > 0) {	
	
	int cntr = __x10_agg_counter[hndlr][p];	
	
	assert ((cntr * size) < 1024 * 32 * sizeof(x10_async_arg_t));
	
	assert (cntrVal >= 0 && cntrVal < 2);
	
	assert ((cntr * size + message_size * size) < 1024 * 32 * sizeof(x10_async_arg_t));

	memcpy (&(__x10_agg_arg_buf[hndlr][p][cntr * size]), rbuf[cntrVal][phase] + s, message_size * size);
	__x10_agg_counter[hndlr][p] += message_size;	
      }
    
    s += message_size * size;
  }
}

static x10_err_t
send_updates (int& ssize, int phase, int partner)
{ 
  lapi_cntr_t cntr;
  int tmp;

  //cout << "send " << phase << " " << __x10_my_place <<" " << partner << " " << *((int*) (sbuf)) << " " << *((int*) (sbuf + 4)) << " " << ssize << endl;       

  assert (ssize < 2 * 32 * 1024 * 8);

  ulong phase_l = (ulong) phase;
  //{ cout << "phase : " << phase << " " << sizeof(phase_l) << "  ssize: " << ssize << " p : " << __x10_my_place << " " << partner << endl; }
  LRC(LAPI_Setcntr(__x10_hndl, &cntr, 0));
  LRC(LAPI_Amsend(__x10_hndl, partner, (void *)8, &phase_l,
		  sizeof(phase_l),
		  (void *) sbuf,
		  ssize,
		  NULL, &cntr, 0));
  LRC(LAPI_Waitcntr(__x10_hndl, &cntr, 1, &tmp));

  ssize = 0;
}


namespace x10lib {

  x10_err_t 
  asyncFlush_hc (x10_async_handler_t hndlr, size_t size)
  {
    X10_DEBUG (1,  "Entry");
    
    //LAPI_Gfence (__x10_hndl);              
    int factor = 1;
    int phase = 0;
    for (; factor < __x10_num_places; phase++, factor *= 2) {
      
      ulong partner = (1 << phase) ^ (ulong)__x10_my_place;
      ulong mask = ((ulong) 1) << phase;
      
      int cond = partner > __x10_my_place ? 1 : 0;
    
      int ssize = 0;
      
      sort_data_args (hndlr, ssize, size, mask, phase, cond);
      
      if (phase > 0) {	
	int cntrVal;
	
	LAPI_Waitcntr (__x10_hndl, &(recvCntr[phase-1]), 1, &cntrVal);
	
	//LAPI_Setcntr (__x10_hndl, &(recvCntr[phase-1]), cntrVal - 1);
	
	sort_data_recvs (hndlr, ssize, size, mask, phase-1, cond, cntrVal);	
	
	recvMesgLen[cntrVal][phase-1] = 0;
	
      }     
      send_updates (ssize, phase, partner);               
    }      
    
    int cntrVal;
    
    LAPI_Waitcntr (__x10_hndl, &(recvCntr[phase-1]), 1, &cntrVal);
    //    LAPI_Setcntr (__x10_hndl, &(recvCntr[phase-1]), cntrVal - 1);
    int ssize = 0;
    
    sort_data_recvs (hndlr, ssize, size, 0, phase-1, 1, cntrVal);
    
    recvMesgLen[cntrVal][phase-1] = 0;
    
    __x10_agg_total[hndlr] = 0;
    
    
    asyncSwitch(hndlr, __x10_agg_arg_buf[hndlr][__x10_my_place], __x10_agg_counter[hndlr][__x10_my_place]);
    __x10_agg_counter[hndlr][__x10_my_place]=0;
    
    
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
    
    __x10_agg_counter[hndlr][tgt]++;
    __x10_agg_total[hndlr]++;
        
    return X10_OK;
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
    
    __x10_agg_counter[hndlr][tgt]++;
    __x10_agg_total[hndlr]++;
          
       
    X10_DEBUG (1,  "Exit");
    return X10_OK;
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
    
    __x10_agg_counter[hndlr][tgt]++;
    __x10_agg_total[hndlr]++;
   
    
    X10_DEBUG (1,  "Exit");
    return X10_OK;
  }

} /* closing brace for namespace x10lib */



