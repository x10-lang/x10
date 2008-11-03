/*
 * (c) Copyright IBM Corporation 2008
 *
 * $Id$
 * This file is part of XRX Kernel implementation in C++.
 */

/** Low level lock interface. **/

#ifndef __XRX_KERNEL_LOCK_H
#define __XRX_KERNEL_LOCK_H

#include "Types.h"

namespace xrx_kernel {

class Lock {
public:

	// constructors
	Lock(void);

	// destructor
	~Lock(void);

	// acquire lock blockingly
	void lock(void);

	// release lock
	void unlock(void);

	// acquire lock (non-blocking)
	boolean tryLock(void);

protected:
	pthread_mutex_t __lock_mutex;
	// do we really need a private copy??
	// (perhaps to change lock behavior dynamically)
	pthread_mutexattr_t __lock_mutexattr;
};

} /* closing brace for namespace xrx_kernel */

#endif /* __XRX_KERNEL_LOCK_H */
