/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: clock.h,v 1.1 2007-08-07 06:21:52 ganeshvb Exp $
 * This file is part of X10 Runtime System.
 */

/** X10Lib's (DUMMY) clock interface. **/

#ifndef __X10_CLOCK_H
#define __X10_CLOCK_H

#include <x10/types.h>
#include <x10/err.h>
#include <lapi.h>

/* C++ Lang Interface */
#ifdef __cplusplus

//dummy clock implementation

namespace x10lib {
 
  extern lapi_handle_t __x10_hndl;  
  class Clock 
    {
      
    }; 
  
  x10_err_t clockNext (Clock* c, int n);
  
}  /* closing brace for namespace x10lib */
#endif

#endif /* __X10_SWITCH_H */
