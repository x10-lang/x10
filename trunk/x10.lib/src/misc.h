/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: misc.h,v 1.1 2007-08-07 06:21:52 ganeshvb Exp $
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
namespace x10lib {
  extern lapi_handle_t __x10_hndl;
  extern int __x10_num_places;
  extern int __x10_my_place;

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
  
