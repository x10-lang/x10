/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: array_copy.cc,v 1.6 2007-10-22 11:00:37 ganeshvb Exp $
 * This file is part of X10 Runtime System.
 */

#include <x10/array_copy.h>
#include <x10/xmacros.h>
#include <x10/xassert.h>
#include <lapi.h>

using namespace std;


static int max_uhdr_sz;

static void* asyncArrayCopyHandler (lapi_handle_t hndl, void* uhdr, uint* uhdr_len, 
			     ulong* msg_len,  compl_hndlr_t **comp_h, void **user_info)
{
  lapi_return_info_t *ret_info = (lapi_return_info_t *)msg_len;
  
  int handler = *((int*) uhdr);

   //cout << "handler " << handler << " " << *((int*) ((char*) uhdr + sizeof (handler) )) << endl;
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


namespace x10lib {

  extern lapi_handle_t __x10_hndl;
  //TODO: take care of switch operations
  x10_err_t
  asyncArrayCopyRaw (void* src, 
		     x10_closure_t closure,
		     size_t len, 
		     int target, 
		     x10_switch_t c)
  {          
    assert (closure->len + sizeof(x10_async_handler_t) >= 0 
	    && closure->len + sizeof(x10_async_handler_t) < max_uhdr_sz);
    
    LRC (LAPI_Amsend (__x10_hndl, 
		      target,
		      (void*) ASYNC_ARRAY_COPY_HANDLER, 
		      (void*) &(closure->handler),
		      closure->len + sizeof(x10_async_handler_t),
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
    assert (closure->len + sizeof(x10_async_handler_t) >= 0 
	    && closure->len + sizeof(x10_async_handler_t) < max_uhdr_sz);
    
    lapi_cntr_t origin_cntr;
    LRC (LAPI_Setcntr (__x10_hndl, &origin_cntr, 0));
    int tmp = -1;
   
//cout << "asyncArrayCopy  " << closure->len << " " << sizeof(x10_async_handler_t) 
 //    << " " << closure->handler << " " << &(closure->handler)
   //  << " " <<  *((int*)((char*) &(closure->handler) + sizeof(int))) << endl;
  
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
}

x10_err_t
arrayCopyInit ()
{
  (void) LAPI_Qenv(x10lib::__x10_hndl, MAX_UHDR_SZ, &max_uhdr_sz);

  LRC (LAPI_Addr_set (x10lib::__x10_hndl, (void*) asyncArrayCopyHandler, ASYNC_ARRAY_COPY_HANDLER));  

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
