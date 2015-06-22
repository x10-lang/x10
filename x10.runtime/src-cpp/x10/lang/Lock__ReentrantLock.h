/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

#ifndef X10_LANG_LOCK__REENTRANT_LOCK_H
#define X10_LANG_LOCK__REENTRANT_LOCK_H

#include <x10/lang/X10Class.h>

#include <x10aux/lock.h>

namespace x10 {
    namespace lang {

       /**
        * A low-level lock that provides a subset of the functionality
        * of java.util.concurrent.locks.Lock__ReentrantLock.
        *
        * A reentrant Lock is owned by the thread last successfully
        * locking, but not yet unlocking it.  A thread invoking lock()
        * will return, successfully acquiring the lock, when the lock
        * is not owned by another thread.  The method will return
        * immediately if the calling thread already owns the lock.
        */
        class Lock__ReentrantLock : public ::x10::lang::X10Class {
        public:
            RTT_H_DECLS_CLASS;
    
            virtual ::x10aux::serialization_id_t _get_serialization_id() {
                fprintf(stderr, "Lock cannot be serialized.  (Lock__ReentrantLock.h)\n");
                abort();
            }
            virtual void _serialize_body(::x10aux::serialization_buffer&) {
                fprintf(stderr, "Lock cannot be serialized.  (Lock__ReentrantLock.h)\n");
                abort();
            }

            virtual void _constructor (void) { }

            static Lock__ReentrantLock* _make();
            ~Lock__ReentrantLock() { }

        public:
           /**
            * Acquires the lock [blocking call].
            * Acquires the lock if it is not held by another thread and
            * returns immediately, setting the lock hold count to one.
            * If the calling thread already holds the lock then the hold
            * count is incremented by one and the method returns immediately.
            * If the lock is held by another thread then the calling thread
            * blocks until the lock is available.
            */
            void lock() { _lock.lock(); }


            /**
             * Attempts to release this lock.
             * If the calling thread is the owner of this lock then the
             * lock count is decremented.  When the lock count reaches zero,
             * the lock becomes available for other threads to acquire.
             * If the calling thread is not the owner of this lock then
             * IllegalMonitorStateException is thrown.
             */
            void unlock() { if (!_lock.unlock()) raiseException(); }
                

            /**
             * Acquires the lock [non-blocking call].
             * Acquires the lock only if it is not held by another thread
             * at the time of invocation.
             * Acquires the lock if it is not held by another thread and
             * returns immediately with the value true, setting the lock
             * count to one.
             * If the calling thread already owns this lock then the lock
             * count is incremented by one and the method returns true.
             * If the lock is owned by another thread the this method will
             * return immediately with the value false.
             */
            x10_boolean tryLock() { return _lock.tryLock(); }

            /**
             * Queries the number of holds on this lock by the calling thread.
             * A thread has a hold on a lock for each lock action that is
             * not matched by an unlock action.
             * Returns zero if this lock is not held by the calling thread.
             */
            x10_int getHoldCount() { return _lock.getHoldCount(); }

        private:
            ::x10aux::reentrant_lock _lock;
            void raiseException();
        };
    }
}

#endif /* __X10_LANG_LOCK__REENTRANT_LOCK_H */

// vim:tabstop=4:shiftwidth=4:expandtab
