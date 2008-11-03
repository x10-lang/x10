/*
 * (c) Copyright IBM Corporation 2008
 *
 * $Id$
 * This file is part of XRX Kernel implementation in C++.
 */

/** Low level worker interface based on pthreads. **/

#ifndef __XRX_KERNEL_THREAD_H
#define __XRX_KERNEL_THREAD_H

#include "Types.h"
#include "Runnable.h"
#include "InterruptedException.h"

namespace xrx_kernel {

class Thread {
public:
	
	// constructors
	Thread(void);
	Thread(Runnable task);
	Thread(Runnable task, String name);

	// destructor
	~Thread(void);

	// return the currently executing thread
	static Thread currentThread(void);

	// begin thread execution
	void start(void);

	// put the current thread to sleep for the specified number
	// of milli seconds
	void sleep(Long millis) throw (InterruptedException);

	// put the current thread to sleep for the specified number
	// of milli seconds plus the specified number of nano seconds
	void sleep(Long millis, Int nanos) throw (InterruptedException);

	// disable the current thread for thread scheduling purposes
	void park(void);

	// disable the current thread for thread scheduling purposes
	// for up to the specified waiting time
	void parkNanos(Long nanos);

	// make available the current thread for thread scheduling
	void unpark(Thread thread);

protected:
	pthread_attr_t __thread_attr;
	Runnable __thread_runobj;
	static Thread &__current_thread;
	// ?? more state as per x10 thread pool executor

};

} /* closing brace for namespace xrx_kernel */

#endif /* __XRX_KERNEL_THREAD_H */
