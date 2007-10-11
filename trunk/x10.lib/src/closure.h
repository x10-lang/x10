/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: closure.h,v 1.1 2007-10-11 08:27:15 ganeshvb Exp $
 * This file is part of X10 Runtime System.
 */

#ifndef __X10_CLOSURE_H
#define __X10_CLOSURE_H

#include "types.h"

#ifdef __cplusplus
namespace x10lib {

  /*
   * a type representing the closure for the asyncs
   * handler = unique handle for the array.
   * Every closure type argument should have this as its base class.
   */
  struct Closure
  {
    Closure () :
      len (0), handler (0) {}
    Closure (int _len, int _handler) :
      len (_len), handler (_handler) {}
    
    size_t len;
    x10_async_handler_t handler;
  };
}
typedef x10lib::Closure* x10_closure_t;
#else
struct Closure;
typedef Closure* x10_closure_t;
#endif

#endif
