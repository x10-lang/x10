/*
 *
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: xthreads.h,v 1.4 2008-03-14 07:19:39 ganeshvb Exp $
 * This file is part of X10 Runtime System.
 */

/** Implementation file for X10Lib's async interface. **/

/**
 * X10 THREADS Interface 
 * The file implements portable, co-operative and non-premptive user-level threads.
 * This is NOT a general threads library; its functionality is restricted to scope of X10.
 * Specifically, this interface allows
 *  a) X10LIB to call-back a user routine. 
 *  b) the user routine to yield and add itself to a ReadyQueue.
 *  c) the user routine to wait on a condition and add to WaitQueue (not implemented yet)
 *  d) to "continue" the user routine using its saved context. 
 *  e) the user routine to call X10LIB methods with a X10LIB context.
 *
 * The implementation uses POSIX standard makecontext, swapcontext and getcontext system calls.
*/

#include <iostream>
#include <x10/queue.h>
#include <x10/async_closure.h>

using namespace std;

typedef void(*func)();

extern x10_async_queue_t ReadyQueue;
extern x10_async_queue_t WaitQueue;

/* provide an alternate impl. for CYGWIN */
#ifdef CYGWIN

#define x10_thread_create
#define x10_thread_run(closure)
#define x10_thread_yield
#define x10_thread_wait(c)
#define x10_thread_free
#define x10_thread_libcall0(fptr)
#define x10_thread_libcall1(fptr, arg0)
#define x10_thread_libcall2(fptr, arg0, arg1)
#define x10_thread_libcall3(fptr, arg0, arg1, arg2)
#define x10_thread_libcall4(fptr, arg0, arg1, arg2, arg3)
#define x10_thread_libcall5(fptr, arg0, arg1, ar2, ar3, arg4)

#else
#include <ucontext.h>
#define x10_thread_create  \
	do {\
	     getcontext(&this->_current_ctxt); \
             this->_current_ctxt.uc_stack.ss_sp = this->_stack; \
             this->_current_ctxt.uc_stack.ss_size = this->_stack_sz;\
             this->_current_ctxt.uc_stack.ss_flags = 0; \
             this->_current_ctxt.uc_link = &this->_caller_ctxt;\
	     makecontext (&this->_current_ctxt,  (func)AsyncClosure::runMain, 1, this); \
	}while(0);

#define x10_thread_run(closure)  \
	do {\
		swapcontext (&closure->_caller_ctxt, &closure->_current_ctxt); \
	}while(0);

#define x10_thread_yield  \
	do {\
		x10lib::PushQueue(ReadyQueue, this);\
		swapcontext (&this->_current_ctxt, &this->_caller_ctxt);  \
	}while(0);

#define x10_thread_wait(c)  \
	do {\
		x10lib::PushQueue(WaitQueue, this);\
 		this->_cond_number = c;\
		swapcontext (&this->_current_ctxt, &this->_caller_ctxt);  \
	}while(0);

#define x10_thread_free this->_done = true;

#ifdef X86 
#define set_stack_ptr \
		this->_libcall_ctxt.uc_mcontext.gregs[REG_ESP] =  this->_caller_ctxt.uc_mcontext.gregs[REG_ESP] + 4;
#else 
#define set_stack_ptr  \
		this->_libcall_ctxt.uc_mcontext.jmp_context.gpr[1] =  this->_caller_ctxt.uc_mcontext.jmp_context.gpr[1];
#endif

#define x10_thread_libcall0(fptr)\
	do {\
                getcontext(&this->_libcall_ctxt);\
		this->_libcall_ctxt.uc_stack.ss_sp = this->_caller_ctxt.uc_stack.ss_sp;\
		this->_libcall_ctxt.uc_stack.ss_size = this->_caller_ctxt.uc_stack.ss_size;\
             	this->_libcall_ctxt.uc_stack.ss_flags = 0;\
             	this->_libcall_ctxt.uc_link = &this->_current_ctxt;\
		makecontext (&this->_libcall_ctxt, (func) fptr,0);\ 
		set_stack_ptr; \
                swapcontext (&this->_current_ctxt, &this->_libcall_ctxt); \
	}while(0);

#define x10_thread_libcall1(fptr, arg0)\
	do {\
                getcontext(&this->_libcall_ctxt);\
		this->_libcall_ctxt.uc_stack.ss_sp = this->_caller_ctxt.uc_stack.ss_sp;\
		this->_libcall_ctxt.uc_stack.ss_size = this->_caller_ctxt.uc_stack.ss_size;\
             	this->_libcall_ctxt.uc_stack.ss_flags = 0;\
             	this->_libcall_ctxt.uc_link = &this->_current_ctxt;\
		makecontext (&this->_libcall_ctxt, (func) fptr, 1, arg0); \
		set_stack_ptr; \
                swapcontext (&this->_current_ctxt, &this->_libcall_ctxt); \
	}while(0);

#define x10_thread_libcall2(fptr, arg0, arg1)\
	do {\
                getcontext(&this->_libcall_ctxt);\
		this->_libcall_ctxt.uc_stack.ss_sp = this->_caller_ctxt.uc_stack.ss_sp;\
		this->_libcall_ctxt.uc_stack.ss_size = this->_caller_ctxt.uc_stack.ss_size;\
             	this->_libcall_ctxt.uc_stack.ss_flags = 0;\
             	this->_libcall_ctxt.uc_link = &this->_current_ctxt;\
		makecontext (&this->_libcall_ctxt, (func) fptr, 2, arg0, arg1);\
		set_stack_ptr; \
                swapcontext (&this->_current_ctxt, &this->_libcall_ctxt); \
	}while(0);

#define x10_thread_libcall3(fptr, arg0, arg1, arg2)\
	do {\
                getcontext(&this->_libcall_ctxt);\
		this->_libcall_ctxt.uc_stack.ss_sp = this->_caller_ctxt.uc_stack.ss_sp;\
		this->_libcall_ctxt.uc_stack.ss_size = this->_caller_ctxt.uc_stack.ss_size;\
             	this->_libcall_ctxt.uc_stack.ss_flags = 0;\
             	this->_libcall_ctxt.uc_link = &this->_current_ctxt;\
		makecontext (&this->_libcall_ctxt, (func) fptr, 3, arg0, arg1, arg2); \
		set_stack_ptr; \
                swapcontext (&this->_current_ctxt, &this->_libcall_ctxt); \
	}while(0);

#define x10_thread_libcall4(fptr, arg0, arg1, arg2, arg3)\
	do {\
                getcontext(&this->_libcall_ctxt);\
		this->_libcall_ctxt.uc_stack.ss_sp = this->_caller_ctxt.uc_stack.ss_sp;\
		this->_libcall_ctxt.uc_stack.ss_sp = (void*) this->_caller_ctxt.uc_mcontext.gregs[REG_ESP];\
		this->_libcall_ctxt.uc_stack.ss_size = this->_caller_ctxt.uc_stack.ss_size;\
             	this->_libcall_ctxt.uc_stack.ss_flags = 0;\
             	this->_libcall_ctxt.uc_link = &this->_current_ctxt;\
		makecontext (&this->_libcall_ctxt, (func) fptr, 4, arg0, arg1, arg2, arg3);\
		set_stack_ptr; \
                swapcontext (&this->_current_ctxt, &this->_libcall_ctxt); \
	}while(0);

		//set_stack_ptr; \
#define x10_thread_libcall5(fptr, arg0, arg1, arg2, arg3, arg4)\
	do {\
                getcontext(&this->_libcall_ctxt);\
		this->_libcall_ctxt.uc_stack.ss_sp = this->_caller_ctxt.uc_stack.ss_sp;\
		this->_libcall_ctxt.uc_stack.ss_size = this->_caller_ctxt.uc_stack.ss_size;\
             	this->_libcall_ctxt.uc_stack.ss_flags = 0;\
             	this->_libcall_ctxt.uc_link = &this->_current_ctxt;\
		makecontext (&this->_libcall_ctxt, (func) fptr, 5, arg0, arg1, arg2, arg3, arg4);\
		set_stack_ptr; \
                swapcontext (&this->_current_ctxt, &this->_libcall_ctxt); \
	}while(0);

#endif 
