/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: lock.cc,v 1.2 2007-12-10 12:12:05 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

/** Implementation file for Shared lock interface. **/

#include <x10/lock.h>
#include <sys/errno.h>
#include <lapi.h>
#include "__x10lib.h__"

namespace x10lib {

/* Blockingly acquire the shared lock. */
void Lock(void)
{
	if (!__x10_inited) return;
	(void)__x10_tf.mutex_lock(__x10_hndl);
}

/* Release the acquired shared lock. */
void Unlock(void)
{
	if (!__x10_inited) return;

	(void)__x10_tf.mutex_unlock(__x10_hndl);
}

/* Try and acquire the shared lock. */
int TryLock(void)
{
	if (!__x10_inited) return EINVAL;

	return __x10_tf.mutex_trylock(__x10_hndl);
}

} /* closing brace for namespace x10lib */

/* Blockingly acquire the shared lock. */
extern "C"
void x10_lock(void)
{
	return x10lib::Lock();
}

/* Release the acquired shared lock. */
extern "C"
void x10_unlock(void)
{
	return x10lib::Unlock();
}

/* Try and acquire the shared lock. */
extern "C"
int x10_try_lock(void)
{
	return x10lib::TryLock();
}
