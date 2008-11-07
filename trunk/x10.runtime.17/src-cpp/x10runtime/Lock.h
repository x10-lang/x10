/*
 * (c) Copyright IBM Corporation 2008
 *
 * $Id$
 * This file is part of XRX/C++ native layer implementation.
 */

/************************************************************
 * A low-level lock that provides a subset of the functionality
 * of java.util.concurrent.locks.ReentrantLock.
 *
 * A reentrant Lock is owned by the thread last successfully
 * locking, but not yet unlocking it.  A thread invoking lock()
 * will return, successfully acquiring the lock, when the lock
 * is not owned by another thread.  The method will return
 * immediately if the calling thread already owns the lock.
 ************************************************************/

#ifndef __XRX_LOCK_H
#define __XRX_LOCK_H

#include <Types.h>
#include <IllegalMonitorStateException.h>

namespace xrx_runtime {

class Lock {
public:

	// [constructor] Create an instance of reentrant Lock.
	Lock();

	// destructor
	~Lock();

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
	 * lock count is decremented.  When the lock count reaches zero,
	 * the lock becomes available for other threads to acquire.
	 * If the calling thread is not the owner of this lock then
	 * IllegalMonitorStateException is thrown.
	 */
	void unlock() throw (IllegalMonitorStateException);

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
	boolean tryLock();

#ifdef XRX_DEBUG
	/**
	 * Queries the number of holds on this lock by the calling thread.
	 * A thread has a hold on a lock for each lock action that is
	 * not matched by an unlock action.
	 * Returns zero if this lock is not held by the calling thread.
	 */
	int getHoldCount();
#endif /* XRX_DEBUG */

private:
	// lock id
	pthread_mutex_t __lock;
	// lock attributes
	pthread_mutexattr_t __lock_attr;
};

} /* closing brace for namespace xrx_runtime */

#endif /* __XRX_LOCK_H */
