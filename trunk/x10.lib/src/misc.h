/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: misc.h,v 1.9 2007-10-08 05:19:35 ganeshvb Exp $
 * This file is part of X10 Runtime System.
 */

#ifndef __X10_MISC_H
#define __X10_MISC_H

#include <x10/err.h>
#include <x10/types.h>
#include <x10/switch.h>
#include <x10/array.h>

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
    * handler = unique array copy handler that returns the base address of dest array.
    * args  = argument for the array copy handler.
    * arg_size = size of arguments.
    * dstOffset = destination offset.
    * target = destination node where the dest. array is residing.
    * len = number of bytes to be copied.
    * c = clock (optional).
    */

    /*
     * DESCRIPTION :  This methods invokes the arrayCopySwitch method in the destinaion
     * The arrayCopySwitch method needs to be overloaded by the programmer. It's signature
     * is void* arrayCopySwitch (x10_async_arg_t, void* args). 
    **/

  struct asyncArrayCopyClosure
  {
    asyncArrayCopyClosure () {}
    asyncArrayCopyClosure (int _handle, size_t _destOffset) :
      handle (_handle),
	 destOffset (_destOffset) {}    
    
    int handle;
    size_t destOffset;
  };
  
  x10_err_t
    asyncArrayCopy (void* src, size_t srcOffset,		  
		    asyncArrayCopyClosure* args, size_t argSize,
		    size_t len, int target, Clock* c=NULL);
  x10_err_t
    asyncArrayCopyRaw (void* src, size_t srcOffset,
		       asyncArrayCopyClosure* args, size_t argSize,
		       size_t len, int target, Clock* c=NULL);
  
  //x10_err_t asyncArrayCopy (void* src, int srcOffset, void* dst, int dstOffset,
  //	       int target, int len, x10_switch_t swch);
  
  //template <class T, int RANK>
  //x10_err_t asyncArrayCopy (Array<T, RANK>* src, int srcOffset,
  //Array<T, RANK>* dest, int dstOffset,
  //int target, int len, x10_switch_t swch);
  
  
  x10_err_t Broadcast (void* buffer, size_t nbytes, x10_place_t root);
  
  
} /* closing brace for namespace x10lib */
#endif
 

#endif
  
