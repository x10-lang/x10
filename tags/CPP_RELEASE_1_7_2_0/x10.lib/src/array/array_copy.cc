/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: array_copy.cc,v 1.16 2008-03-26 13:25:38 ganeshvb Exp $
 * This file is part of X10 Runtime System.
 */

#include <lapi.h>

#include <x10/array_copy.h>
#include <x10/xmacros.h>
#include <x10/xassert.h>
#include <x10/array_table.h>
#include "__x10lib.h__"

using namespace std;

static int max_uhdr_sz;

/* LAPI handlers */
static lapi_vec_t* AsyncGenArrayCopyHandler (lapi_handle_t hndl, void* uhdr, uint* uhdr_len, 
					     ulong* msg_len,  compl_hndlr_t **comp_h, void **user_info);

static void* AsyncArrayCopyHandler (lapi_handle_t hndl, void* uhdr, uint* uhdr_len, 
			     ulong* msg_len,  compl_hndlr_t **comp_h, void **user_info);

namespace x10lib {

  extern lapi_handle_t __x10_hndl;

  //TODO: take care of switch operations
  x10_err_t
  AsyncArrayIput (void* src, 
		     x10_closure_t closure,
		     size_t len, 
		     int target, 
		     x10_switch_t c)
  {          
    assert (closure->len + sizeof(Closure) - sizeof(size_t) >= 0 
	    && closure->len + sizeof(Closure) - sizeof(size_t) < max_uhdr_sz);
 
   cout << "len " << closure->len << " " << len << " " << src  << " " << closure->handler << " " << target << endl;
 
    LRC (LAPI_Amsend (__x10_hndl, 
		      target,
		      (void*) ASYNC_ARRAY_COPY_HANDLER, 
		      (void*) &(closure->handler),
		      closure->len  - sizeof(size_t),
		      src,
		      len,
		      NULL,
		      NULL,
		      NULL));
    
    return X10_OK;
  }

  x10_err_t
  FreeIOVector (lapi_vec_t* vec)
  {    
    delete [] (char*)vec->info;
    delete [] vec->len;   
    //cout << "delete " << (long) vec << endl; 
    delete vec;
    vec = NULL;

    return X10_OK;
  }

  lapi_vec_t*
  GetIOVector  (void* data,
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
  AsyncArrayPut (lapi_vec_t* vec,
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
  AsyncArrayPut (void* src, 
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
    size_t aligned_size = closure->len % sizeof(long) == 0 ? closure->len :
				 closure->len + sizeof(long) - closure->len % sizeof(long);
   cout << "len " << closure->len << " " << len <<  " " << aligned_size << " " << src  << " " << closure->handler << " " << target << endl;
   
    LRC (LAPI_Amsend (__x10_hndl, 
		      target,
		      (void*) ASYNC_ARRAY_COPY_HANDLER, 
		      (void*) &(closure->handler),
		      aligned_size + sizeof(Closure) - sizeof(size_t),
		      src,
		      len,
		      NULL,
		      &origin_cntr,
		      NULL));

    LAPI_Waitcntr (__x10_hndl,  &origin_cntr,  1, &tmp);


    return X10_OK;
  } 

  x10_err_t
  AsyncArrayCopy (void* src,
                  x10_closure_t closure,
                  size_t len,
                  int target,
                  x10_switch_t c)
  {
    X10_DEPRECATED ("AsyncArrayPut");
    return AsyncArrayPut (src, closure, len, target, c);
  } 

  x10_err_t
  AsyncArrayCopyRaw (void* src,
                  x10_closure_t closure,
                  size_t len,
                  int target,
                  x10_switch_t c)
  {
    X10_DEPRECATED ("AsyncArrayIput");
    return AsyncArrayIput (src, closure, len, target, c);
  }

}

x10_err_t
ArrayCopyInit ()
{
  (void) LAPI_Qenv(x10lib::__x10_hndl, MAX_UHDR_SZ, &max_uhdr_sz);

  LRC (LAPI_Addr_set (x10lib::__x10_hndl, (void*) AsyncArrayCopyHandler, ASYNC_ARRAY_COPY_HANDLER));  

  LRC (LAPI_Addr_set (x10lib::__x10_hndl, (void*) AsyncGenArrayCopyHandler, ASYNC_GEN_ARRAY_COPY_HANDLER));  

 return X10_OK;

}

static void* AsyncArrayCopyHandler (lapi_handle_t hndl, void* uhdr, uint* uhdr_len, 
			     ulong* msg_len,  compl_hndlr_t **comp_h, void **user_info)
{
  lapi_return_info_t *ret_info = (lapi_return_info_t *)msg_len;
  
  int handler = *((int*) uhdr);
  
    cout << "Hello " << sizeof(x10lib::Closure) - sizeof(size_t) << endl;
    cout << "Hello2 " << sizeof(x10lib::Closure) << endl;
   cout << "hello "  << handler << endl; 
  if (ret_info->udata_one_pkt_ptr) {

    memcpy ((char*) ArrayCopySwitch (handler, (char*) uhdr + sizeof (x10lib::Closure) - sizeof(size_t) ),
	    ret_info->udata_one_pkt_ptr, *msg_len);
   
    ret_info->ctl_flags = LAPI_BURY_MSG;
    *comp_h = NULL;
   
    return NULL;
  } else {	  
  
    ret_info->ret_flags = LAPI_LOCAL_STATE;
    *comp_h = NULL;
    
    return (char*) ArrayCopySwitch (handler,(char*)  uhdr + sizeof (x10lib::Closure) - sizeof(size_t));
  }
  
  return NULL; 
}

static lapi_vec_t* AsyncGenArrayCopyHandler (lapi_handle_t hndl, void* uhdr, uint* uhdr_len, 
					     ulong* msg_len,  compl_hndlr_t **comp_h, void **user_info)
{
  lapi_return_info_t *ret_info = (lapi_return_info_t *)msg_len;
  
  int handler = *((int*) uhdr);
  
  ret_info->ret_flags = LAPI_LOCAL_STATE;
  *comp_h = NULL;
  
  return  GenArrayCopySwitch (handler,(char*)  uhdr + sizeof (x10lib::Closure) - sizeof(size_t));
}

extern "C"
x10_err_t 
x10_async_array_copy (void* src, x10_closure_t args, size_t len, int target, x10_switch_t c)
{
  return x10lib::AsyncArrayCopy (src, args, len, target, c);
}

extern "C"
x10_err_t 
x10_async_array_copy_raw (void* src, x10_closure_t args, size_t len, int target, x10_switch_t c)
{
  return x10lib::AsyncArrayCopyRaw (src, args, len, target, c);
}
