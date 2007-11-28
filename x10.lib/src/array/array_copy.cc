/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: array_copy.cc,v 1.11 2007-11-28 14:14:19 ganeshvb Exp $
 * This file is part of X10 Runtime System.
 */

#include <x10/array_copy.h>
#include <x10/xmacros.h>
#include <x10/xassert.h>
#include <lapi.h>
#include "array_table.h"

using namespace std;

static int max_uhdr_sz;

static void* asyncArrayCopyHandler (lapi_handle_t hndl, void* uhdr, uint* uhdr_len, 
			     ulong* msg_len,  compl_hndlr_t **comp_h, void **user_info)
{
  lapi_return_info_t *ret_info = (lapi_return_info_t *)msg_len;
  
  int handler = *((int*) uhdr);
  
  if (ret_info->udata_one_pkt_ptr) {
    
    memcpy ((char*) arrayCopySwitch (handler, (char*) uhdr + sizeof (x10lib::Closure) - sizeof(size_t) ),
	    ret_info->udata_one_pkt_ptr, *msg_len);
   
    ret_info->ctl_flags = LAPI_BURY_MSG;
    *comp_h = NULL;
   
    return NULL;
  } else {	  
  
    ret_info->ret_flags = LAPI_LOCAL_STATE;
    *comp_h = NULL;
    
    return (char*) arrayCopySwitch (handler,(char*)  uhdr + sizeof (x10lib::Closure) - sizeof(size_t));
  }
  
  return NULL; 
}

static lapi_vec_t* asyncGenArrayCopyHandler (lapi_handle_t hndl, void* uhdr, uint* uhdr_len, 
					     ulong* msg_len,  compl_hndlr_t **comp_h, void **user_info)
{
  lapi_return_info_t *ret_info = (lapi_return_info_t *)msg_len;
  
  int handler = *((int*) uhdr);
  
  ret_info->ret_flags = LAPI_LOCAL_STATE;
  *comp_h = NULL;
  
  return  genArrayCopySwitch (handler,(char*)  uhdr + sizeof (x10lib::Closure) - sizeof(size_t));
}

namespace x10lib {

  extern lapi_handle_t __x10_hndl;

  //TODO: take care of switch operations
  x10_err_t
  asyncArrayIput (void* src, 
		     x10_closure_t closure,
		     size_t len, 
		     int target, 
		     x10_switch_t c)
  {          
    assert (closure->len + sizeof(Closure) - sizeof(size_t) >= 0 
	    && closure->len + sizeof(Closure) - sizeof(size_t) < max_uhdr_sz);
    
    LRC (LAPI_Amsend (__x10_hndl, 
		      target,
		      (void*) ASYNC_ARRAY_COPY_HANDLER, 
		      (void*) &(closure->handler),
		      closure->len + sizeof(Closure) - sizeof(size_t),
		      src,
		      len,
		      NULL,
		      NULL,
		      NULL));
    
    return X10_OK;
  }

  x10_err_t
  freeIOVector (lapi_vec_t* vec)
  {    
    delete [] (char*)vec->info;
    delete [] vec->len;   
    //cout << "delete " << (long) vec << endl; 
    delete vec;
    vec = NULL;
  }

  lapi_vec_t*
  getIOVector  (void* data,
		int rank,
		int* lda,
		size_t el_size,
		int* origin,
		int* diagonal)
  {

    lapi_vec_t* vec = (lapi_vec_t*) new char [sizeof(lapi_vec_t)];
    //cout << "create " << (long) vec << " " << __x10_my_place << " " << sizeof(lapi_vec_t) <<  endl; 

    int offset = 0;    
    
    int total_num_vecs = 1;
    
    int num_vecs[X10_MAX_RANK];
    
    ulong vec_size = diagonal[rank-1] - origin[rank-1] + 1;
    
    for (int i = 0; i < rank-1; i++)
      {
	num_vecs [i] = (diagonal[i] - origin[i] + 1);
	
	X10_DEBUG (1, "num vec [" << i << "]: " << num_vecs[i]);
	
	total_num_vecs *= num_vecs[i];
      }

    num_vecs[rank-1] = 1;
    for (int i = rank-2; i >= 0; i--)
      {
	num_vecs [i] *= num_vecs[i+1];
      }
    
    X10_DEBUG (1, "num_vecs: " << total_num_vecs);
    
    vec->num_vecs = total_num_vecs;
    vec->vec_type = LAPI_GEN_IOVECTOR;
    vec->len = new ulong [total_num_vecs];
    vec->info = new void* [total_num_vecs];
    
    //compute the dot product of two vectors p & linearStep_
    for (int i = 0; i < rank; i++)
      {
	offset += (lda[i] * origin[i]);
      }
    
    char* raw = (char*) data + el_size * offset;    
    char* raw_d [X10_MAX_RANK];

    for (int i = 0; i < rank; i++)
      raw_d[i] = raw;

    for (int v = 0; v < total_num_vecs; v++) 
      {	
	vec->info[v] = raw;
	vec->len[v] = vec_size * el_size;

        if (v > 0)
  	  X10_DEBUG (1, "raw " << (char*) vec->info[v] - (char*) vec->info[v-1]);
	
	for (int j = 0; j < rank-1; j++)
	  {
	    if ((v + 1) %  num_vecs[j+1] == 0) 
	      {
		raw_d[j] += el_size * lda[j];

                for (int k = j+1; k < rank-1; k++)
                   raw_d[k] = raw_d[j];
                    
		raw = raw_d[j];

		X10_DEBUG (1,  "here " << num_vecs[j+1] << " " << v+1 << " " << el_size * lda[j] << endl);
		break;
	      }
	  }
      }    

    return vec;
  }
  
  x10_err_t
  asyncArrayPut (lapi_vec_t* vec,
		 x10_closure_t closure,
		 int target, 
		 x10_switch_t c)
  {
    assert (closure->len + sizeof(Closure) - sizeof(size_t) >= 0 
	    && closure->len + sizeof(Closure) - sizeof(size_t) < max_uhdr_sz);

    
    lapi_cntr_t origin_cntr;
    LRC (LAPI_Setcntr (__x10_hndl, &origin_cntr, 0));
    int tmp = -1;
    
    LRC (LAPI_Amsendv (__x10_hndl, 
		      target,
		      (void*) ASYNC_GEN_ARRAY_COPY_HANDLER, 
		      (void*) &(closure->handler),
		      closure->len + sizeof(Closure) - sizeof(size_t),
		      vec,
		      NULL,
		      &origin_cntr,
		      NULL));
    
    LAPI_Waitcntr (__x10_hndl,  &origin_cntr,  1, &tmp);

    //delete vec;    
    return X10_OK;
  } 

  x10_err_t
  asyncArrayPut (void* src, 
		  x10_closure_t closure, 
		  size_t len, 
		  int target, 
		  x10_switch_t c)
  {          
    assert (closure->len + sizeof(Closure) - sizeof(size_t) >= 0 
	    && closure->len + sizeof(Closure) - sizeof(size_t) < max_uhdr_sz);
    
    lapi_cntr_t origin_cntr;
    LRC (LAPI_Setcntr (__x10_hndl, &origin_cntr, 0));
    int tmp = -1;
   
    LRC (LAPI_Amsend (__x10_hndl, 
		      target,
		      (void*) ASYNC_ARRAY_COPY_HANDLER, 
		      (void*) &(closure->handler),
		      closure->len + sizeof(Closure) - sizeof(size_t),
		      src,
		      len,
		      NULL,
		      &origin_cntr,
		      NULL));

    LAPI_Waitcntr (__x10_hndl,  &origin_cntr,  1, &tmp);


    return X10_OK;
  } 

  x10_err_t
  asyncArrayCopy (void* src,
                  x10_closure_t closure,
                  size_t len,
                  int target,
                  x10_switch_t c)
  {
    X10_DEPRECATED ("asyncArrayPut");
    return asyncArrayPut (src, closure, len, target, c);
  } 

  x10_err_t
  asyncArrayCopyRaw (void* src,
                  x10_closure_t closure,
                  size_t len,
                  int target,
                  x10_switch_t c)
  {
    X10_DEPRECATED ("asyncArrayIput");
    return asyncArrayIput (src, closure, len, target, c);
  }

}

x10_err_t
arrayCopyInit ()
{
  (void) LAPI_Qenv(x10lib::__x10_hndl, MAX_UHDR_SZ, &max_uhdr_sz);

  LRC (LAPI_Addr_set (x10lib::__x10_hndl, (void*) asyncArrayCopyHandler, ASYNC_ARRAY_COPY_HANDLER));  

  LRC (LAPI_Addr_set (x10lib::__x10_hndl, (void*) asyncGenArrayCopyHandler, ASYNC_GEN_ARRAY_COPY_HANDLER));  

}
extern "C"
x10_err_t 
x10_async_array_copy (void* src, x10_closure_t args, size_t len, int target, x10_switch_t c)
{
  return x10lib::asyncArrayCopy (src, args, len, target, c);
}

extern "C"
x10_err_t 
x10_async_array_copy_raw (void* src, x10_closure_t args, size_t len, int target, x10_switch_t c)
{
  return x10lib::asyncArrayCopyRaw (src, args, len, target, c);
}
