/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: lock.cc,v 1.1 2007-06-25 16:07:36 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

/** Implementation file for Shared lock interface. **/

#include <x10/lock.h>
#include <sys/errno.h>
#include <lapi.h>

namespace x10lib {

/* Blockingly acquire the shared lock. */
void Lock(void)
{
	extern lapi_handle_t __x10_hndl;
	extern lapi_thread_func_t __x10_tf;
	extern int __x10_inited;

	if (!__x10_inited) return;
	(void)__x10_tf.mutex_lock(__x10_hndl);
}

/* Release the acquired shared lock. */
void Unlock(void)
{
	extern lapi_handle_t __x10_hndl;
	extern lapi_thread_func_t __x10_tf;
	extern int __x10_inited;

	if (!__x10_inited) return;

	(void)__x10_tf.mutex_unlock(__x10_hndl);
}

/* Try and acquire the shared lock. */
int TryLock(void)
{
	extern lapi_handle_t __x10_hndl;
	extern lapi_thread_func_t __x10_tf;
	extern int __x10_inited;

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
