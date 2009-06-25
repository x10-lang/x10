/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: lock.h,v 1.1 2007-08-02 11:22:43 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

/** Shared lock interface. **/

#ifndef __X10_LOCK_H
#define __X10_LOCK_H

/* C++ Lang Interface */
#ifdef __cplusplus
namespace x10lib {

	/* Blockingly acquire the shared lock. */
	void Lock(void);

	/* Release the acquired shared lock. */
	void Unlock(void);

	/* Try and acquire the shared lock. */
	int TryLock(void);

} /* closing brace for namespace x10lib */
#endif

/* C Lang Interface */
#ifdef __cplusplus
extern "C" {
#endif

/* Blockingly acquire the shared lock. */
void x10_lock(void);

/* Release the acquired shared lock. */
void x10_unlock(void);

/* Try and acquire the shared lock. */
int x10_try_lock(void);

#ifdef __cplusplus
} /* closing brace for extern "C" */
#endif

#endif /* __X10_LOCK_H */
