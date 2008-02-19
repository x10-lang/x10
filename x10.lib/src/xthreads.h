
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
#include <ucontext.h>
#include <x10/queue.h>
#include <x10/async_closure.h>

using namespace std;

typedef void(*func)();

extern x10_async_queue_t ReadyQueue;
extern x10_async_queue_t WaitQueue;

#define x10_thread_create  \
	     getcontext(&this->_current_ctxt); \
             this->_current_ctxt.uc_stack.ss_sp = this->_stack; \
             this->_current_ctxt.uc_stack.ss_size = this->_stack_sz;\
             this->_current_ctxt.uc_stack.ss_flags = 0; \
             this->_current_ctxt.uc_link = &this->_caller_ctxt;\
	     makecontext (&this->_current_ctxt,  (func)AsyncClosure::runMain, 1, this);

#define x10_thread_run(closure)  \
swapcontext (&closure->_caller_ctxt, &closure->_current_ctxt);

#define x10_thread_yield x10lib::PushQueue(ReadyQueue, this);\
				swapcontext (&this->_current_ctxt, &this->_caller_ctxt); 

#define x10_thread_wait(c) x10lib::PushQueue(WaitQueue, this);\
 				this->_cond_number = c;\
				swapcontext (&this->_current_ctxt, &this->_caller_ctxt); 

#define x10_thread_free this->_done = true;

/* Beware : Switching stack pointers before function calls will not work always */
#define x10_switch_stack(newsp) \
__asm__ __volatile__ ("movl %%eax, %%esp" \
                       : \
                       : "eax"(newsp));

/* This is a better way to switch to X10LIB context */
#ifdef X86
#define x10_thread_async_spawn(place, handler, args, size) \
                getcontext(&this->_libcall_ctxt);\
		this->_libcall_ctxt.uc_stack.ss_sp = (void*) this->_caller_ctxt.uc_mcontext.gregs[REG_ESP] ;\
		this->_libcall_ctxt.uc_stack.ss_size = this->_caller_ctxt.uc_stack.ss_size;\
             	this->_libcall_ctxt.uc_stack.ss_flags = 0;\
             	this->_libcall_ctxt.uc_link = &this->_current_ctxt;\
		makecontext (&this->_libcall_ctxt, (func) x10lib::AsyncSpawnInline, 4, place, handler, args, size);\
                swapcontext (&this->_current_ctxt, &this->_libcall_ctxt);
#else 
#define x10_thread_async_spawn(place, handler, args, size) \
                getcontext(&this->_libcall_ctxt);\
		this->_libcall_ctxt.uc_stack.ss_sp = (void*) this->_caller_ctxt.uc_mcontext.jmp_context.  gpr[7] ;\
		this->_libcall_ctxt.uc_stack.ss_size = this->_caller_ctxt.uc_stack.ss_size;\
             	this->_libcall_ctxt.uc_stack.ss_flags = 0;\
             	this->_libcall_ctxt.uc_link = &this->_current_ctxt;\
		makecontext (&this->_libcall_ctxt, (func) x10lib::AsyncSpawnInline, 4, place, handler, args, size);\
                swapcontext (&this->_current_ctxt, &this->_libcall_ctxt);

#endif
