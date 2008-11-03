/*
 * (c) Copyright IBM Corporation 2008
 *
 * $Id$
 * This file is part of XRX Kernel implementation in C++.
 */

/** Implementation file for low level lock interface. **/

#include "Lock.h"

namespace xrx_kernel {

// constructors implementation
Lock::Lock(void)
{
	// remove cast once we are ready to catch no memory exceptions
	(void)pthread_mutexattr_init(&__lock_mutexattr);

	// java reentrantlock behavior
	int type = PTHREAD_MUTEX_RECURSIVE;
	(void)pthread_mutexattr_settype(&__lock_mutexattr, type);

	// change this to 'SHARED' for inter place accesses
	int pshared = PTHREAD_PROCESS_PRIVATE;
	(void)pthread_mutexattr_setpshared(&__lock_mutexattr, pshared);

	// until we need lock priorities
	{
		int protocol = PTHREAD_PRIO_DEFAULT;
		(void)pthread_mutexattr_setprotocol(&__lock_mutexattr, protocol);
	}

	// remove cast once we are ready to catch no memory exceptions
	(void)pthread_mutex_init(&__lock_mutex, &__lock_mutexattr);

}

// destructor implementation
Lock::~Lock(void)
{
	(void)pthread_mutex_destroy(&__lock_mutex);
	(void)pthread_mutexattr_destroy(&__lock_mutexattr);

}

// blocking lock (reentrant)
void
Lock::lock(void)
{
	(void)pthread_mutex_lock(&__lock_mutex);
}

// release lock (reentrant)
void
Lock::unlock(void)
{
	// remove the underlying cast once we are ready to
	// handle properly EPERM (not the owner) through exceptions
	(void)pthread_mutex_unlock(&__lock_mutex);
}

// try and acquire lock
boolean
Lock::tryLock(void)
{
	// no need to handle EBUSY return value for the simple
	// trylock semantics [java trylock() behavior]
	if (pthread_mutex_trylock(&__lock_mutex) == 0) {
		return true;
	}
	return false;
}

} /* closing brace for namespace xrx_kernel */
