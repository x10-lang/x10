/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: aggregate_ra.cc,v 1.1 2007-12-09 10:34:05 srkodali Exp $
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

static char kbuf [2 * 32 * X10_MAX_AGG_SIZE * 8];

static char* rbuf[2][X10_MAX_LOG_NUMPROCS];

static size_t recvMesgLen[2][X10_MAX_LOG_NUMPROCS];

static lapi_cntr_t recvCntr[X10_MAX_LOG_NUMPROCS];

int nsend= 0;

int nkeep = 0;

int nkept = 0;

int PLACEIDMASK;

long LogTableSize;

static x10_err_t
sort_data_recvs (x10_async_handler_t hndlr, int& nsend, size_t size, ulong mask, int phase, int cond, int cntrVal, char* buf);

typedef struct {
  ulong len;
  ulong phase;
} x10_agg_cmpl_t;

typedef struct {
  x10_async_handler_t handler;
  size_t size;
  long phase;
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
    
    //sort_data_recvs (hdr->handler, nsend, hdr->size, 0, a->phase, 1, cntrVal, (char*) ret_info->udata_one_pkt_ptr);
    /* ulong partner = (1 << (a->phase+1)) ^ (ulong)__x10_my_place;
    ulong mask = ((ulong) 1) << (a->phase+1);      
    int cond = partner > __x10_my_place ? 1 : 0;
    sort_data_recvs (hdr->handler, nsend, hdr->size, mask, a->phase, cond, cntrVal, (char*) ret_info->udata_one_pkt_ptr);*/
 
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
asyncAggInit_ra()
{
  X10_DEBUG (1,  "Entry");

  LRC(LAPI_Addr_set(__x10_hndl, (void *)asyncSpawnHandlerAgg, ASYNC_SPAWN_HANDLER_AGG_RA));
  
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
asyncAggFinalize_ra ()
{  
   return X10_OK;
}

static x10_err_t
sort_data (size_t size, ulong mask, int cond, char *inbuf, int len)
{
  long* buf = (long*) inbuf;
  long* sendbuf = (long*) (sbuf + nsend);
  long* keepbuf = (long*) (kbuf + nkeep);
  int j = 0;
  int k = 0;

  for (int i = 0; i < len / size; i++)
    {
      long ran = buf[i];
      int p = ((int) (ran >> LogTableSize) & PLACEIDMASK);
      if (p != __x10_my_place && ((MIN((((ulong) p) & mask), 1)) == cond)) 
	{
         sendbuf[j++] = buf[i];
	  nsend+= size;
	} else {
          keepbuf[k++] =buf[i];
	  nkeep += size;
	}
    }  
}

static x10_err_t
send_updates (x10_async_handler_t hndlr, size_t size,int phase, int partner)
{ 
  lapi_cntr_t cntr;
  int tmp;
  
  //  cout << "send " << phase << " " << __x10_my_place <<" " << partner << " " << nsend<< endl;       
  
  assert (nsend< 2 * 32 * 1024 * 8);

  ulong phase_l = (ulong) phase;

  x10_agg_hdr_t hdr;
  hdr.phase = phase;
  hdr.size = size;
  hdr.handler = hndlr;
  
  //{ cout << "phase : " << phase << " " << sizeof(phase_l) << "  nsend: " << nsend<< " p : " << __x10_my_place << " " << partner << endl; }
  LRC(LAPI_Setcntr(__x10_hndl, &cntr, 0));
  LRC(LAPI_Amsend(__x10_hndl, partner, (void *) ASYNC_SPAWN_HANDLER_AGG_RA, &hdr,
		  sizeof(hdr),
		  (void *) sbuf,
		  nsend,
		  NULL, &cntr, 0));
  LRC(LAPI_Waitcntr(__x10_hndl, &cntr, 1, &tmp));

}


namespace x10lib {

  //template <typename FUNC>  
  x10_err_t 
  asyncFlush_ra (x10_async_handler_t hndlr, size_t size, char* data, int len, long log_table_size,
                 int place_id_mask)
  {
    X10_DEBUG (1,  "Entry");
    
    LogTableSize = log_table_size;
    
    PLACEIDMASK = place_id_mask;
    
    nsend= 0;

    nkeep = 0;
    
    //LAPI_Gfence (__x10_hndl);              
    int factor = 1;
    int phase = 0;
    
    char* buf;
    int ndata;

    nkept = len;

    for (; factor < __x10_num_places; phase++, factor *= 2) {
      
      nsend= 0;	    

      nkeep = 0;
      
      ulong partner = (1 << phase) ^ (ulong)__x10_my_place;
      
      ulong mask = ((ulong) 1) << phase;
      
      int cond = partner > __x10_my_place ? 1 : 0;
      
      if (factor == 1) {
	buf = data;
      } else { 
	buf = kbuf;
      }

      sort_data (size, mask,cond, buf, nkept);
      
      if (phase > 0) 
	{	
	  int cntrVal;	
	  LAPI_Waitcntr (__x10_hndl, &(recvCntr[phase-1]), 1, &cntrVal);
	  sort_data (size, mask, cond, rbuf[cntrVal][phase-1], recvMesgLen[cntrVal][phase-1]);		
	  recvMesgLen[cntrVal][phase-1] = 0;	  
	}     
      
      send_updates (hndlr, size, phase, partner);                    

      nkept = nkeep;
    }
    
    int cntrVal;    
    LAPI_Waitcntr (__x10_hndl, &(recvCntr[phase-1]), 1, &cntrVal);
    sort_data (size, 0, 1, rbuf[cntrVal][phase-1], recvMesgLen[cntrVal][phase-1]);		
    recvMesgLen[cntrVal][phase-1] = 0;
    
    //asyncSwitch(hndlr, __x10_agg_arg_buf[__x10_my_place], __x10_agg_counter[__x10_my_place]);
    // __x10_agg_counter[__x10_my_place]=0;

    //    cout << "here " << __x10_my_place << " " << nkeep << " " << nkept << endl;

    asyncSwitch (hndlr, kbuf, nkeep / size);

    X10_DEBUG (1,  "Exit");
    return X10_OK;
  }
}
 

