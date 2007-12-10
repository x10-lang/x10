/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: array_copy.h,v 1.7 2007-12-10 13:15:45 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

#ifndef __X10_ARRAY_COPY_H
#define __X10_ARRAY_COPY_H

#include <x10/err.h>
#include <x10/types.h>
#include <x10/switch.h>
#include <x10/closure.h>
#include <lapi.h>

/* C++ Lang Interface */
#ifdef __cplusplus
namespace x10lib {

  /**
   * arg0 = src address
   * args = pointer to closure
   * (a derived class of asyncArrayCopyClosure)
   * len = number of bytes to be copied
   * target = destination node where the dest. array is residing
   * c = clock (optional)
   */
  
  /**
   * DESCRIPTION :  This methods invokes the arrayCopySwitch method
   * in the destinaion. The arrayCopySwitch method needs to be overloaded by the programmer. It's signature
   * is void* arrayCopySwitch (void* args). 
   **/
  
  /**
   * The input buffers (src and args) can be re-used
   * or deleted safely upon return from this funcion
   */
  
  x10_err_t
  asyncArrayPut (void* src, 
		  x10_closure_t args,
		  size_t len, 
		  int target, 
		  x10_switch_t c=NULL);
  
  /**
   * Same as above, but does not guarantee the buffer 
   * re-use or safe deletion after its return. 
   * It is the user's responsibility to ensure safe
   * buffer re-use & de-allocation.
   */
  
  x10_err_t
  asyncArrayIput (void* src, 
		     x10_closure_t args,
		     size_t len, 
		     int target, 
		     x10_switch_t c=NULL);
 


  lapi_vec_t*
  getIOVector  (void* data,
		int rank,
		int* lda,
		size_t el_size,
		int* origin,
		int* diagonal);

  x10_err_t
  freeIOVector (lapi_vec_t* vec);

		
  x10_err_t
  asyncArrayPut (lapi_vec_t* vec,
		 x10_closure_t args,       
		 int target, 
		 x10_switch_t c = NULL);

  /* DEPRECATED */
  x10_err_t
  asyncArray (void* src,
	      x10_closure_t args,
	      size_t len,
	      int target,
	      x10_switch_t c=NULL);


  /* DEPRECATED */
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

  /* The C bindings for the above functions */

  x10_err_t x10_async_array_copy (void* src, x10_closure_t args, size_t len, int target, x10_switch_t c);

  x10_err_t x10_async_array_copy_raw (void* src, x10_closure_t args, size_t len, int target,x10_switch_t c);

#ifdef __cplusplus
}
#endif 

#endif /* __X10_ARRAY_COPY_H */
