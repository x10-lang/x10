/*
 * (c) Copyright IBM Corporation 2008
 *
 * $Id$
 * This file is part of XRX/C++ native layer implementation.
 */

/**
 * Implementation file for the low level reentrant lock
 * interface.
 */

#include <x10aux/config.h>
#include <x10aux/throw.h>

#include <x10/runtime/Lock__ReentrantLock.h>

#include <x10/runtime/IllegalMonitorStateException.h>

#include <errno.h>
#ifdef XRX_DEBUG
#include <iostream>
#endif /* XRX_DEBUG */

using namespace x10::lang;
using namespace x10::runtime;
using namespace x10aux;


x10aux::ref<Lock__ReentrantLock>
Lock__ReentrantLock::_make() {
    x10aux::ref<Lock__ReentrantLock> this_ = new (x10aux::alloc<Lock__ReentrantLock>()) Lock__ReentrantLock();
    this_->x10::lang::Ref::_constructor();
    this_->initialize();
    return this_;
}

void Lock__ReentrantLock::initialize()
{
    // create lock attributes object
    // ??check the return code for ENOMEM and throw OutOfMemoryError??
    (void)pthread_mutexattr_init(&__lock_attr);

    // set lock type to reentrant
    int type = PTHREAD_MUTEX_RECURSIVE;
    pthread_mutexattr_settype(&__lock_attr, type);

    // whether this lock is shared by threads across places
    // if so, set this to PTHREAD_PROCESS_SHARED
    int pshared = PTHREAD_PROCESS_PRIVATE;
    pthread_mutexattr_setpshared(&__lock_attr, pshared);

#ifdef PTHREAD_PRIO_NONE
    // we are not currently implementing any fairness policy
    int protocol = PTHREAD_PRIO_NONE;
    pthread_mutexattr_setprotocol(&__lock_attr, protocol);
#endif

    // create lock object
    // ??check the return code for ENOMEM and throw OutOfMemoryError??
    (void)pthread_mutex_init(&__lock, &__lock_attr);
}

// destructor
void Lock__ReentrantLock::teardown()
{
    // free lock object
    pthread_mutex_destroy(&__lock);

    // free lock attributes
    pthread_mutexattr_destroy(&__lock_attr);
}

// acquire lock (blocking)
void
Lock__ReentrantLock::lock()
{
    // blocks until the lock becomes available
    pthread_mutex_lock(&__lock);
}

// release lock
void
Lock__ReentrantLock::unlock()
{
    // calling thread doesn't own the lock
    if (pthread_mutex_unlock(&__lock) == EPERM) {
        throwException<IllegalMonitorStateException>();
    }
}

// acquire lock (non-blocking)
x10_boolean
Lock__ReentrantLock::tryLock()
{
    // acquire the lock only if it is not held by another thread
    if (pthread_mutex_trylock(&__lock) == 0) {
        return true;
    }
    return false;
}

// get lock count
x10_int
Lock__ReentrantLock::getHoldCount()
{
    // hack, until we find someway to do this reliably
    #ifdef _AIX
    #ifdef __64BIT__
        /**
         * typdef struct { long __mt_word[8]; } pthread_mutext_t;
         * 3rd element [index 2] contains basic lock status
         * 8th element [index 7] contains multiple of (UINT_MAX+1)
         * times number of additional recursive locks
         */
        #ifdef XRX_DEBUG
            std::cerr << __func__;
            for (int i = 0; i < 8; i++) {
                std::cerr << " " << i << "," << __lock.__mt_word[i];
            }
            std::cerr << std::endl;
        #endif /* XRX_DEBUG */
        if (__lock.__mt_word[2]) {
            long multiple = UINT_MAX;
            multiple += 1;
            int rlocks = (__lock.__mt_word[7])/multiple;
            return (rlocks + 1);
        }
        return 0;
    #else /* !__64BIT__ */
        /**
         * typedef struct { int __mt_word[13]; } pthread_mutex_t;
         * 5th element [index 4] contains basic lock status
         * 9th element [index 8] contains additional recursive locks
         */
        #ifdef XRX_DEBUG
            std::cerr << __func__;
            for (int i = 0; i < 13; i++) {
                std::cerr << " " << i << "," << __lock.__mt_word[i];
            }
            std::cerr << std::endl;
        #endif /* XRX_DEBUG */
        if (__lock.__mt_word[4]) {
            return (__lock.__mt_word[8] + 1);
        }
        return 0;
    #endif /* __64BIT__ */
    #else /* !_AIX */
        return -1;
    #endif /* _AIX */
    /*
    if (Xrx::__xrx_already_inited && Xrx::__xrx_session_valid) {
        int count, rc1, rc2;
        pthdb_mutex_t mutex = 0;

        pthread_suspend_others_np();
        pthdb_session_update(Xrx::__xrx_session);
        rc1 = pthdb_mutex(Xrx::__xrx_session, &mutex, PTHDB_LIST_FIRST);
        if (rc1 == PTHDB_SUCCESS) {
            rc2 = pthdb_mutex_lock_count(Xrx::__xrx_session,
                            mutex, &count);
        }
        pthread_continue_others_np();
        if (rc2 == PTHDB_SUCCESS) {
            return count;
        }
    }
    return -1;
    */
}

RTT_CC_DECLS1(Lock__ReentrantLock, "x10.runtime.Lock__ReentrantLock", Ref)

// vim:tabstop=4:shiftwidth=4:expandtab
