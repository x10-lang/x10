/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: array_copy.cc,v 1.1 2007-10-11 08:27:15 ganeshvb Exp $
 * This file is part of X10 Runtime System.
 */

#include <x10/array_copy.h>
#include <x10/xmacros.h>

static int max_uhdr_sz;

static void* asyncArrayCopyHandler (lapi_handle_t hndl, void* uhdr, uint* uhdr_len, 
			     ulong* msg_len,  compl_hndlr_t **comp_h, void **user_info)
{
  x10lib::Closure* header = (x10lib::Closure*) uhdr;	

  lapi_return_info_t *ret_info = (lapi_return_info_t *)msg_len;
  
  if (ret_info->udata_one_pkt_ptr) {

    memcpy ((char*) arrayCopySwitch (header), 
	    ret_info->udata_one_pkt_ptr, *msg_len);
   
    ret_info->ctl_flags = LAPI_BURY_MSG;
    *comp_h = NULL;
   
    return NULL;
  } else {	  
  
    ret_info->ret_flags = LAPI_LOCAL_STATE;
    *comp_h = NULL;

    return (char*) arrayCopySwitch (header);
  }
  
  return NULL; 
}

x10_err_t
arrayCopyInit ()
{
  (void) LAPI_Qenv(x10lib::__x10_hndl, MAX_UHDR_SZ, &max_uhdr_sz);

  LRC (LAPI_Addr_set (x10lib::__x10_hndl, (void*) asyncArrayCopyHandler, ASYNC_ARRAY_COPY_HANDLER)); 
}

namespace x10lib {

  //TODO: take care of switch operations
  x10_err_t
  asyncArrayCopyRaw (void* src, 
		     x10_closure_t closure,
		     size_t len, 
		     int target, 
		     x10_switch_t c)
  {          
    assert (closure->len >= 0 && closure->len < max_uhdr_sz);
    
    LRC (LAPI_Amsend (__x10_hndl, 
		      target,
		      (void*) ASYNC_ARRAY_COPY_HANDLER, 
		      (void*) closure,
		      closure->len,
		      src,
		      len,
		      NULL,
		      NULL,
		      NULL));
    
    return X10_OK;
  }

  x10_err_t
  asyncArrayCopy (void* src, 
		  x10_closure_t closure, 
		  size_t len, 
		  int target, 
		  x10_switch_t c)
  {          
    assert (closure->len >= 0 && closure->len < max_uhdr_sz);
    
    lapi_cntr_t origin_cntr;
    LRC (LAPI_Setcntr (__x10_hndl, &origin_cntr, 0));
    int tmp = -1;
    
    LRC (LAPI_Amsend (__x10_hndl, 
		      target,
		      (void*) ASYNC_ARRAY_COPY_HANDLER, 
		      (void*) closure,
		      closure->len,
		      src,
		      len,
		      NULL,
		      &origin_cntr,
		      NULL));

    LAPI_Waitcntr (__x10_hndl, &origin_cntr, 1, &tmp); 
   
    return X10_OK;
  }  
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
