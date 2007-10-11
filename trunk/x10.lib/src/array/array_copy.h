/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: array_copy.h,v 1.1 2007-10-11 09:48:22 ganeshvb Exp $
 * This file is part of X10 Runtime System.
 */

#ifndef __X10_ARRAY_COPY_H
#define __X10_ARRAY_COPY_H

#include <x10/err.h>
#include <x10/types.h>
#include <x10/switch.h>
#include <x10/array.h>
#include <x10/closure.h>

/* C++ Lang Interface */
#ifdef __cplusplus
extern "C" void* arrayCopySwitch (void * args);
namespace x10lib {
  
  extern lapi_handle_t __x10_hndl;
  extern int __x10_num_places;
  extern int __x10_my_place;

  /**
    * arg0 = src address.
    * arg1 = src offset (bytes).
    * closure = pointer to closure (a derived class of asyncArrayCOpyClosure).
    * target = destination node where the dest. array is residing.
    * len = number of bytes to be copied.
    * c = clock (optional).
    */

    /*
     * DESCRIPTION :  This methods invokes the arrayCopySwitch method in the destinaion
     * The arrayCopySwitch method needs to be overloaded by the programmer. It's signature
     * is void* arrayCopySwitch (void* args). 
    **/

  
  x10_err_t
  asyncArrayCopy (void* src, 
		    x10_closure_t args,
		    size_t len, 
		    int target, 
		    x10_switch_t c=NULL);
  x10_err_t
  asyncArrayCopyRaw (void* src, 
		     x10_closure_t args,
		     size_t len, 
		     int target, 
		     x10_switch_t c=NULL);
  
 } /* closing brace for namespace x10lib */
#endif


#ifdef __cplusplus
extern "C" {
#endif
  x10_err_t x10_async_array_copy (void* src, x10_closure_t args, size_t len, int target, x10_switch_t c=NULL);
  x10_err_t x10_async_array_copy_raw (void* src, x10_closure_t args, size_t len, int target,x10_switch_t c=NULL);

#ifdef __cplusplus
}
#endif 

#endif
  

// Local Variables:
// mode: C++
// End:
