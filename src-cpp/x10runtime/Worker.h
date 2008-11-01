/*
 * (c) Copyright IBM Corporation 2008
 *
 * $Id$
 * This file is part of XRX Kernel implementation in C++.
 */

/** Low level worker interface based on pthreads. **/

#ifndef __XRX_KERNEL_WORKER_H
#define __XRX_KERNEL_WORKER_H

#include <pthread.h>
#include <iostream>
//#include <xrx/kernel/Runnable.h>
#include "Runnable.h"

using namespace std;
namespace xrx_kernel {

class Worker : public Runnable {
public:
	
	// constructors
	Worker(void);
	Worker(Runnable task);
	// string <-- String ??
	Worker(Runnable task, string name);

	// destructor
	~Worker(void);

	// invoke separate Runnable object run method, if available
	void run(void);

	// put the current thread to sleep for the specified number
	// of milli seconds
	// long <-- Long; once primitive classes are available
	void sleep(long millis);

	// put the current thread to sleep for the specified number
	// of milli seconds plus the specified number of nano seconds
	// long <-- Long && int <-- Int; once primitive classes are available
	void sleep(long millis, int nanos);

	// disable the current thread for thread scheduling purposes
	void park(void);

	// disable the current thread for thread scheduling purposes
	// for up to the specified waiting time
	// long <-- Long; once primitive classes are available
	void parkNanos(long nanos);

	// make available the current thread for thread scheduling
	void unpark(Worker worker);

protected:
	pthread_attr_t __worker_attr;
	Runnable __worker_runobj;
	pthread_t __worker;
	// ?? more state as per x10 thread pool executor

};

} /* closing brace for namespace xrx_kernel */

#endif /* __XRX_KERNEL_WORKER_H */
