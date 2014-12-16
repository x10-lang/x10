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

#ifndef X10_AUX_LOCL_H
#define X10_AUX_LOCL_H

#include <pthread.h>

namespace x10aux {

    /**
     * A class that encapsulates the platform-specific details
     * of creating and using a re-entrant mutex.
     */
    class reentrant_lock {
    public:
        reentrant_lock() { initialize(); }
        ~reentrant_lock() { teardown(); }
        
    private:
        void initialize();
        void teardown();

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
        void lock();

        /**
         * Attempts to release this lock.
         * If the calling thread is the owner of this lock then the
         * lock count is decremented and the method returns true.
         * If the calling thread is not the owner of this lock then
         * this method returns false.
         */
        bool unlock();

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
        bool tryLock();

        /**
         * Queries the number of holds on this lock by the calling thread.
         * A thread has a hold on a lock for each lock action that is
         * not matched by an unlock action.
         * Returns zero if this lock is not held by the calling thread.
         * Returns -1 if this operation is not supported on this platform.
         */
        int getHoldCount();

    private:
        // lock id
        pthread_mutex_t __lock;
        // lock attributes
        pthread_mutexattr_t __lock_attr;
    };
}

#endif /* X10_AUX_LOCL_H */

// vim:tabstop=4:shiftwidth=4:expandtab
