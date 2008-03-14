/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: async_closure.h,v 1.2 2008-03-14 07:19:39 ganeshvb Exp $
 * This file is part of X10 Runtime System.
 */

/** Progress operations interface. **/

#ifndef __X10_ASYNC_CLOSURE_H
#define __X10_ASYNC_CLOSURE_H

#include <ucontext.h>

#include <x10/types.h>
#include <x10/xassert.h>

/* C++ Lang Interface */
#ifdef __cplusplus

namespace x10lib {

  struct InlineClosure {

    /* Inlined Closure -- does not yield */
    InlineClosure (x10_async_handler_t handle):
      _handle (handle) 
    {}

    virtual void run() = 0;

    static void runMain (InlineClosure* obj)
    {
      obj->run();
    }

    x10_async_handler_t _handle; /* handler id */

  };

  /*Asynchronous Closure -- can yield */
  struct AsyncClosure : InlineClosure {

    AsyncClosure (x10_async_handler_t handle, 
		  void* stack, size_t stack_sz):
      InlineClosure (handle),
	 _stack(stack), 
	 _stack_sz (stack_sz),
	 _done(false){}
			
    void* _stack;      /* stack for this closure */

    size_t _stack_sz; /* size of the stack */
	
    bool _done; /* flag to check completion */

    int _cond_number; /* which condition to wait on */

    ucontext_t _current_ctxt; /* context of this closure */
  
    ucontext_t _caller_ctxt; /*  context of the caller (generally x10lib or main thread) */

    ucontext_t _libcall_ctxt; /* context for X10LIB calls made "from" this closure */
    /* may be removed */

    virtual bool cond() = 0;
  };

} /* closing brace for namespace x10lib */

typedef struct x10lib::InlineClosure* x10_inline_closure_t;

typedef struct x10lib::AsyncClosure* x10_async_closure_t;
#endif

#endif /* __X10_ASYNC_CLOSURE_H */
