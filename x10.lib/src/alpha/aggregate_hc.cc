/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: aggregate_hc.cc,v 1.1 2007-12-09 10:34:05 srkodali Exp $
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
namespace x10lib {
  extern lapi_handle_t __x10_hndl;
  extern int __x10_max_agg_size;
};

#define X10_MAX_LOG_NUMPROCS 10

#define MIN(A, B) A > B ? B : A

static char sbuf [2 * 32 * X10_MAX_AGG_SIZE * 8];

static char* rbuf[2][X10_MAX_LOG_NUMPROCS];

static char** __x10_agg_arg_buf;

static int* __x10_agg_counter;

static int __x10_agg_total;

ulong recvMesgLen[2][X10_MAX_LOG_NUMPROCS];

lapi_cntr_t recvCntr[X10_MAX_LOG_NUMPROCS];

int ssize = 0;

static x10_err_t
sort_data_recvs (x10_async_handler_t hndlr, int& ssize, ulong size, ulong mask, int phase, int cond, int cntrVal, char* buf);

typedef struct {
  ulong len;
  ulong phase;
} x10_agg_cmpl_t;

typedef struct {
  x10_async_handler_t handler;
  int size;
  ulong phase;
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


static void *
asyncSpawnHandlerAgg(lapi_handle_t hndl, void *uhdr,
		     uint *uhdr_len, ulong *msg_len,
		     compl_hndlr_t **comp_h, void **user_info)
{
  X10_DEBUG (1,  "Entry");
  x10_agg_hdr_t buf = *((x10_agg_hdr_t *)uhdr);
  lapi_return_info_t *ret_info =
    (lapi_return_info_t *)msg_len;


  x10_agg_hdr_t* hdr = (x10_agg_hdr_t*) uhdr;

  x10_agg_cmpl_t* a = new x10_agg_cmpl_t;
  a->phase = hdr->phase;
  a->len = *msg_len;

  int cntrVal; 
  LAPI_Getcntr (__x10_hndl, &recvCntr[a->phase], &cntrVal);
  if (ret_info->udata_one_pkt_ptr || (*msg_len) == 0) {
    memcpy (rbuf[cntrVal][a->phase], ret_info->udata_one_pkt_ptr, *msg_len);    

    asyncSpawnCompHandlerAgg (&hndl, (void*) a);    
    
    //sort_data_recvs (hdr->handler, ssize, hdr->size, 0, a->phase, 1, cntrVal, (char*) ret_info->udata_one_pkt_ptr);
    /*ulong partner = (1 << (a->phase+1)) ^ (ulong)__x10_my_place;
    ulong mask = ((ulong) 1) << (a->phase+1);      
    int cond = partner > __x10_my_place ? 1 : 0;
    sort_data_recvs (hdr->handler, ssize, hdr->size, mask, a->phase, cond, cntrVal, (char*) (ret_info->udata_one_pkt_ptr));  */
 
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

  LRC(LAPI_Addr_set(__x10_hndl, (void *)asyncSpawnHandlerAgg, ASYNC_SPAWN_HANDLER_AGG_HYPER));
  
  __x10_agg_arg_buf = new char* [__x10_num_places];
  __x10_agg_counter = new int[__x10_num_places];
  
  for (int j = 0; j < __x10_num_places; j++) {
    __x10_agg_counter[j] = 0;      
    __x10_agg_arg_buf[j] = new char [X10_MAX_AGG_SIZE * 16 * sizeof(x10_async_arg_t)];
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
  for (int j = 0; j < __x10_num_places; j++) {
    delete [] __x10_agg_arg_buf[j];
  }

  delete [] __x10_agg_arg_buf;
  delete [] __x10_agg_counter;

   
   return X10_OK;
}

static x10_err_t
sort_data_args (x10_async_handler_t hndlr,  int& ssize, ulong size, ulong mask, int phase, int cond)
{ 

  for (x10_place_t p = 0; p < __x10_num_places; p++) {
    
    if (p == __x10_my_place) continue;
    
    //    cout << "Hello " << __x10_my_place << " " << (p & mask) << " " << (MIN((((ulong) p) & mask), 1)) << " " <<  p << " " << __x10_agg_counter[p] << endl;     
    if (((MIN((((ulong) p) & mask), 1)) == cond) && (__x10_agg_counter[p] > 0)) {    
            
     
      memcpy (&(sbuf[ssize]), &p, sizeof(x10_place_t)); 
      ssize += sizeof(x10_place_t);
      
      //int message_size = __x10_agg_counter[p] * size;
      memcpy (&(sbuf[ssize]), &__x10_agg_counter[p], sizeof(int));  
      ssize += sizeof(int);
      
      memcpy (&(sbuf[ssize]), __x10_agg_arg_buf[p], __x10_agg_counter[p] * size);  
      ssize += __x10_agg_counter[p] * size;
      
      __x10_agg_counter[p] = 0;	    
      
    }
  }
}

static x10_err_t
sort_data_recvs (x10_async_handler_t hndlr, int& ssize, ulong size, ulong mask, int phase, int cond, int cntrVal, char* buf)
{ 
  for (int s = 0; s <   recvMesgLen[cntrVal][phase]; ) {
    
    x10_place_t p =  *((x10_place_t*) (buf + s));

    s += sizeof (x10_place_t);

    int message_size = *((int *) (buf + s));    
    s += sizeof (int);
    
    //cout << p << " " << recvMesgLen[cntrVal][phase] << " " << s << " " << cntrVal << " " << " " << message_size << " " <<  phase << endl;
    assert (p >= 0 && p < __x10_num_places);     
    
    if (p == __x10_my_place) 
      {
	asyncSwitch(hndlr, buf + s, message_size);	
      } else if (((MIN((((ulong) p) & mask), 1)) == cond) && (message_size > 0)) {    
	
	assert ((1 << phase) != __x10_num_places);
		
	assert (message_size > 0);
	
	memcpy (&(sbuf[ssize]), &p, sizeof(x10_place_t));       
	ssize += sizeof(x10_place_t);
	
	memcpy (&(sbuf[ssize]), &message_size, sizeof(int));        
	ssize += sizeof(int);
	
	memcpy (&(sbuf[ssize]), buf + s, message_size * size);        
	ssize += message_size * size;
      } else if (message_size > 0) {	

	assert ((1 << phase) != __x10_num_places);
	
	int cntr = __x10_agg_counter[p];	
	
	assert ((cntr * size) < 1024 * 32 * sizeof(x10_async_arg_t));
	
	assert (cntrVal >= 0 && cntrVal < 2);
	
	assert ((cntr * size + message_size * size) < 1024 * 32 * sizeof(x10_async_arg_t));
	
	memcpy (&(__x10_agg_arg_buf[p][cntr * size]), buf + s, message_size * size);
	__x10_agg_counter[p] += message_size;	
      }
    
    s += message_size * size;
  }

  recvMesgLen[cntrVal][phase] = 0;
	
}

static x10_err_t
send_updates (x10_async_handler_t hndlr, int& ssize, ulong size,int phase, int partner)
{ 
  lapi_cntr_t cntr;
  int tmp;
  
  //cout << "send " << phase << " " << __x10_my_place <<" " << partner << " " << *((int*) (sbuf)) << " " << *((int*) (sbuf + 4)) << " " << ssize << endl;       

  assert (ssize < 2 * 32 * 1024 * 8);

  ulong phase_l = (ulong) phase;

  x10_agg_hdr_t hdr;
  hdr.phase = (ulong) phase;
  hdr.size = (int)size;
  hdr.handler = hndlr;
  
  LRC(LAPI_Setcntr(__x10_hndl, &cntr, 0));
  LRC(LAPI_Amsend(__x10_hndl, partner, (void *) ASYNC_SPAWN_HANDLER_AGG_HYPER, &hdr,
		  sizeof(hdr),
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
    
    ssize = 0;

    //LAPI_Gfence (__x10_hndl);              

    int factor = 1;
    
    int phase = 0;
    
    for (; factor < __x10_num_places; phase++, factor *= 2) {
      
      ulong partner = (1 << phase) ^ (ulong)__x10_my_place;
    
      ulong mask = ((ulong) 1) << phase;
      
      int cond = partner > __x10_my_place ? 1 : 0;
          
      sort_data_args (hndlr, ssize, size, mask, phase, cond);
      
      if (phase > 0) {	
	
	int cntrVal;
	
	LAPI_Waitcntr (__x10_hndl, &(recvCntr[phase-1]), 1, &cntrVal);
	
	sort_data_recvs (hndlr, ssize, size, mask, phase-1, cond, cntrVal, rbuf[cntrVal][phase-1]);		
	
      }     
      
      send_updates (hndlr, ssize, size, phase, partner);               

      ssize = 0;	    
    }      
   
    if (__x10_num_places > 1)  { 
    int cntrVal;
    
    LAPI_Waitcntr (__x10_hndl, &(recvCntr[phase-1]), 1, &cntrVal);

    sort_data_recvs (hndlr, ssize, size, 0, phase-1, 1, cntrVal, rbuf[cntrVal][phase-1]);
        
    __x10_agg_total = 0;
    }
    
    
    asyncSwitch(hndlr, __x10_agg_arg_buf[__x10_my_place], __x10_agg_counter[__x10_my_place]);
    __x10_agg_counter[__x10_my_place]=0;
  
    X10_DEBUG (1,  "Exit");
    return X10_OK;
  }
  
  
  x10_err_t
  asyncSpawnInlineAgg_hc(x10_place_t tgt, x10_async_handler_t hndlr,
			 void *args, ulong size)
  {
    X10_DEBUG (1,  "Entry");
    assert (size <= X10_MAX_AGG_SIZE * sizeof(x10_async_arg_t));
    int count = __x10_agg_counter[tgt];
    memcpy(&(__x10_agg_arg_buf[tgt][count * size]), args, size);
    
    __x10_agg_counter[tgt]++;
    __x10_agg_total++;
        
    return X10_OK;
  }
  
  x10_err_t
  asyncSpawnInlineAgg_hc (x10_place_t tgt, x10_async_handler_t hndlr,
		      x10_async_arg_t arg0)
  {
    X10_DEBUG (1,  "Entry");
    ulong size = sizeof(x10_async_arg_t);
    ulong count = __x10_agg_counter[tgt];
    memcpy(&(__x10_agg_arg_buf[tgt][count * size]),
	   &arg0, sizeof(x10_async_arg_t));
    
    __x10_agg_counter[tgt]++;
    __x10_agg_total++;
          
       
    X10_DEBUG (1,  "Exit");
    return X10_OK;
  }
  
  x10_err_t
  asyncSpawnInlineAgg_hc(x10_place_t tgt, x10_async_handler_t hndlr,
		      x10_async_arg_t arg0, x10_async_arg_t arg1)
  {
    X10_DEBUG (1,  "Entry");
    ulong size = 2 * sizeof(x10_async_arg_t);
    int count = __x10_agg_counter[tgt];

    memcpy(&(__x10_agg_arg_buf[tgt][count * size]),
	   &arg0, sizeof(x10_async_arg_t));
    memcpy(&(__x10_agg_arg_buf[tgt][count * size +
					   sizeof(x10_async_arg_t)]),
	   &arg1, sizeof(x10_async_arg_t));
    
    __x10_agg_counter[tgt]++;
    __x10_agg_total++;
   
    
    X10_DEBUG (1,  "Exit");
    return X10_OK;
  }

} /* closing brace for namespace x10lib */



