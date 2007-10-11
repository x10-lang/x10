/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: misc.h,v 1.14 2007-10-11 10:55:56 ganeshvb Exp $
 * This file is part of X10 Runtime System.
 */

#ifndef __X10_MISC_H
#define __X10_MISC_H

#include <lapi.h>
#include <x10/err.h>
#include <x10/types.h>
/* C++ Lang Interface */
#ifdef __cplusplus
namespace x10lib {
  
  extern lapi_handle_t __x10_hndl;
  extern int __x10_num_places;
  extern int __x10_my_place;
     
  x10_err_t Broadcast (void* buffer, size_t nbytes);
  
  
} /* closing brace for namespace x10lib */
#endif

#ifdef __cplusplus
extern "C" {
#endif
  x10_err_t x10_broadcast (void* buffer, size_t nbytes);

#ifdef __cplusplus
}
#endif 

#endif
  

// Local Variables:
// mode: C++
// End:
