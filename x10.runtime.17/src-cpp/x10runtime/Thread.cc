/*
 * (c) Copyright IBM Corporation 2008
 *
 * $Id$
 * This file is part of XRX Kernel implementation in C++.
 */

/** Implementation file for low level worker interface. **/

#include "Thread.h"

namespace xrx_kernel {

// constructor (1)
Thread::Thread(void)
{
	// to do
}

// constructor (2)
Thread::Thread(Runnable task)
{
	// let compiler take care of object type mismatches
	__thread_runobj = task;
	// to do

}

// constructor (3)
Thread::Thread(Runnable task, string name)
{
	__thread_runobj = task;
	// to do

}

// destructor
Thread::~Thread(void)
{
	// to do
}

// return the currently executing thread
Thread
Thread::currentThread(void)
{
	return (__current_thread);
	// to do
}

// begin thread execution
void
Thread::start(void)
{
	// to do
}

// put the current thread to sleep for the specified number of
// milliseconds
void
Thread::sleep(Long millis) throw (InterruptedException)
{
	// to do
}

// put the current thread to sleep for the specified number of
// milliseconds plus the specified number of nano seconds
void
Thread::sleep(Long millis, Int nanos) throw (InterruptedException)
{
	// to do
}

// disable the current thread for thread scheduling purposes
void
Thread::park(void)
{
	// to do
}

// disable the current thread for thread scheduling purposes
void
Thread::parkNanos(Long nanos)
{
	// to do
}

// make available the current thread for thread scheduling
void
Thread::unpark(Thread worker)
{
	// to do
}

} /* closing brace for namespace xrx_kernel */
