/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

#ifndef X10_LANG_THREAD_H
#define X10_LANG_THREAD_H

#include <x10aux/config.h>
#include <x10aux/serialization.h>

#include <x10/lang/X10Class.h>
#include <x10/lang/String.h>
#include <x10/lang/VoidFun_0_0.h>

#include <pthread.h>

namespace x10 {
    namespace lang {
        class Place;
        class String;
        class Reference;
        class Runtime__Worker;

        // execution thread condition & associated lock pair
        typedef struct {
            pthread_cond_t cond;
            pthread_mutex_t mutex;
        } cond_mutex_t;

        // thread permit type
        typedef struct {
            pthread_cond_t cond;
            pthread_mutex_t mutex;
            x10_boolean permit;
        } permit_t;

       /************************************************************
        * A thread is a unit of execution in a place.
        * In a given place, we can have multiple threads of execution.
        *
        * A union of a subset of the functionality of java.lang.Thread
        * augmented with the park/unpark API of java.util.concurrent.
        * locks.LockSupport.
        */
        class Thread : public ::x10::lang::X10Class {
        public:
            RTT_H_DECLS_CLASS;

            // [constructors] Allocates a new Thread object.
            static Thread* _make(::x10::lang::String* name);

            Thread* _constructor(::x10::lang::String* name) {
                thread_init(name);
                return this;
            }

            static Thread* _make();

            Thread* _constructor() {
                return NULL;
            }

            virtual ::x10aux::serialization_id_t _get_serialization_id();

            virtual void _serialize_body(::x10aux::serialization_buffer &buf);
            
            // destructor
            ~Thread();

            // Returns a reference to the currently executing thread object.
            static Thread* currentThread(void);

            /**
             * Causes this thread to begin execution; the XRX runtime
             * calls the run method of this thread.
             * The result is that two threads are running concurrently:
             * the current thread (which returns from the call to the
             * start method) and the other thread (which executes its
             * run method).
             * Throws IllegalThreadStateException, if the thread was
             * already started.
             */
            void start(void);

            /**
             * Waits forever for this thread to die.
             */
            void join(void);

            /**
             * Tests if this thread is alive.
             * A thread is alive if it has been started and has not yet died.
             */
            x10_boolean isAlive();

            /**
             * Interrupts this thread.
             */
            void interrupt();

            /**
             * Causes the currently executing thread to sleep (cease
             * execution) for the specified number of milliseconds.
             * The thread does not lose ownership of any monitors.
             * Throws InterruptedException, if any thread has interrupted
             * the current thread.
             */
            static void sleep(x10_long millis);

            /**
             * Causes the currently executing thread to sleep (cease
             * execution) for the specified number of milliseconds plus
             * the specified number of nanoseconds.
             * The thread does not lose ownership of any monitors.
             * Throws InterruptedException, if any thread has interrupted
             * the current thread.
             */
            static void sleep(x10_long millis, x10_int nanos);
            /**
             * Disables the current thread for thread scheduling purposes
             * unless the permit is available.
             * If the permit is available then it is consumed and the call
             * returns immediately; otherwise the current thread becomes
             * disabled for thread scheduling purposes and lies dormant
             * until one of three things happen:
             * 1) Some other thread invokes unpark with the current thread
             * as the target; or
             * 2) Some other thread interrupts the current thread; or
             * 3) The call spuriously (ie, for no reason) returns.
             * This method does not report which of these caused the method
             * to return.  Callers should re-check the conditions which
             * caused the thread to park in the first place.
             */
            static void park(void);

            /**
             * Disables the current thread for thread scheduling purposes,
             * for up to the specified waiting time, unless the permit is
             * available.
             * If the permit is available then it is consumed and the call
             * returns immediately; otherwise the current thread becomes
             * disabled for thread scheduling purposes and lies dormant
             * until one of four things happen:
             * 1) Some other thread invokes unpark with the current thread
             * as the target; or
             * 2) Some other thread interrupts the current thread; or
             * 3) The specified waiting time elapses; or
             * 4) The call spuriously (ie, for no reason) returns.
             * This method does not report which of these caused the method
             * to return.  Callers should re-check the conditions which
             * caused the thread to park in the first place.
             */
            static void parkNanos(x10_long nanos);

            /**
             * Makes available the permit for the given thread, if it was
             * not already available.  If the thread was blocked on park
             * then it will unblock.  Otherwise, its next call to park is
             * guaranteed not to block.  This operation is not guaranteed
             * to have any effect at all if the given thread has not been
             * started.
             */
            void unpark();

            // Returns the current worker.
            ::x10::lang::Runtime__Worker* worker(void);

            // API matching for Java runtime.  Not actually needed for C++ runtime.
            ::x10::lang::Place home(void);
            
            // Set the current worker.
            void worker(::x10::lang::Runtime__Worker* worker);

            // Returns this thread's name.
            ::x10::lang::String* name(void);

            /**
             * Returns the identifier of this thread. The thread ID is
             * a positive long number generated when this thread was created.
             * The thread ID is unique and remains unchanged during its lifetime.
             */
            long getId();

			// Thread context is not used in Native X10, only in Managed X10
            void removeWorkerContext();

            /**
             * Returns the system thread id.
             */
            static x10_long getTid();

            // Changes the name of this thread to be equal to the argument name.
            void name(::x10::lang::String* name);

            /**
             * This method does nothing and returns.
             * Subclasses of Thread should override this method.
             * This method is invoked when the thread is started.
             */
            virtual void __apply();

            // pthread -> Thread mapping is maintained as thread specific data
            static pthread_key_t __thread_mapper;
            static x10_boolean __thread_mapper_inited;

            static void initAttributes(pthread_attr_t*);
            
        protected:
            // Helper method to initialize a Thread object.
            void thread_init(::x10::lang::String* name);
            // Thread start routine.
            static void *thread_start_routine(void *arg);
            // Clean-up routine for sleep method call.
            static void thread_sleep_cleanup(void *arg);
            // Dummy interrupt handler.
            static void intr_hndlr(int signo);
            // Thread permit initialization.
            static void thread_permit_init(permit_t *perm);
            // Thread cond_mutex initialization.
            static void thread_cmp_init(cond_mutex_t *cmp);
            // Thread permit finalization.
            static void thread_permit_destroy(permit_t *perm);
            // Thread cond_mutex finalization.
            static void thread_cmp_destroy(cond_mutex_t *cmp);
            // Thread permit cleanup handler.
            static void thread_permit_cleanup(void *arg);
            // Thread cond_mutex cleanup handler.
            static void thread_mapper_cleanup(void *arg);
            // Thread binding to processor capabilities
            void thread_bind_cpu();

        private:
            // the current worker
            ::x10::lang::Runtime__Worker* __current_worker;
            // internal thread id counter (monotonically increasing only)
            static long __thread_cnt;
            // thread id
            long __thread_id;
            // thread name
            ::x10::lang::String* __thread_name;
            // thread's pthread id
            // ??using __thread clashes with already existing identifier??
            pthread_t __xthread;
            // thread attributes
            pthread_attr_t __xthread_attr;
            // thread run flags
            x10_boolean __thread_already_started;
            x10_boolean __thread_running;
            // thread start condition & associated lock
            pthread_cond_t __thread_start_cond;
            pthread_mutex_t __thread_start_lock;
            // thread specific permit object
            permit_t __thread_permit;
            cond_mutex_t __thread_cmp;
        };
    }
}

#endif /* X10_LANG_THREAD_H */

// vim:tabstop=4:shiftwidth=4:expandtab
