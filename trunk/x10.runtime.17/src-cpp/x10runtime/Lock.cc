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

#include <Lock.h>
#include <errno.h>

namespace xrx_runtime {

// [constructor] Create an instance of reentrant Lock.
Lock::Lock()
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

	// we are not currently implementing any fairness policy
	int protocol = PTHREAD_PRIO_NONE;
	pthread_mutexattr_setprotocol(&__lock_attr, protocol);

	// create lock object
	// ??check the return code for ENOMEM and throw OutOfMemoryError??
	(void)pthread_mutex_init(&__lock, &__lock_attr);
}

// destructor
Lock::~Lock()
{
	// free lock object
	pthread_mutex_destroy(&__lock);

	// free lock attributes
	pthread_mutexattr_destroy(&__lock_attr);
}

// acquire lock (blocking)
void
Lock::lock()
{
	// blocks until the lock becomes available
	pthread_mutex_lock(&__lock);
}

// release lock
void
Lock::unlock() throw (IllegalMonitorStateException)
{
	IllegalMonitorStateException imse;

	// calling thread doesn't own the lock
	if (pthread_mutex_unlock(&__lock) == EPERM) {
		throw imse;
	}
}

// acquire lock (non-blocking)
boolean
Lock::tryLock()
{
	// acquire the lock only if it is not held by another thread
	if (pthread_mutex_trylock(&__lock) == 0) {
		return true;
	}
	return false;
}

// get lock count
int
Lock::getHoldCount()
{
	// don't we need to suspend the corresponding thread??
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
}

} /* closing brace for namespace xrx_runtime */
