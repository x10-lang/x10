/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: misc.h,v 1.11 2007-10-08 15:10:22 ganeshvb Exp $
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

  /*
   * a type representing the closure for the asyncArrayCopy
   * handler = unique handle for the array.
   * dstOffset = destination offset.
   * Every closure type argument to asyncArrayCopy should have this as its base class.
   */
  struct asyncArrayCopyClosure
  {
    asyncArrayCopyClosure () {}
    asyncArrayCopyClosure (int _handle, size_t _destOffset) :
      handle (_handle),
	 destOffset (_destOffset) {}    
    
    int handle;
    size_t destOffset;
  };

  /**
    * arg0 = src address.
    * arg1 = src offset (bytes).
    * closure = pointer to closure (a derived class of asyncArrayCOpyClosure).
    * closureSize = size of the clsoure.
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
  

// Local Variables:
// mode: C++
// End:
