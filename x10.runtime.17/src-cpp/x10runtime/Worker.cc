/*
 * (c) Copyright IBM Corporation 2008
 *
 * $Id$
 * This file is part of XRX Kernel implementation in C++.
 */

/** Implementation file for low level worker interface. **/

//#include <xrx/kernel/Worker.h>
#include "Worker.h"
//#include <xrx/kernel/Runnbale.h>
#include "Runnable.h"

namespace xrx_kernel {

// constructor (1)
Worker::Worker(void)
{
	// to do
}

// constructor (2)
Worker::Worker(Runnable task)
{
	// let compiler take care of object type mismatches
	__worker_runobj = task;
	// to do

}

// constructor (3)
Worker::Worker(Runnable task, string name)
{
	__worker_runobj = task;
	// to do

}

// destructor
Worker::~Worker(void)
{
	// to do
}

// if available, invoke separate Runnable object run method
// otherwise, do nothing
void
Worker::run(void)
{
	// to do
}

// put the current thread to sleep for the specified number of
// milliseconds
void
Worker::sleep(long millis)
{
	// to do
}

// put the current thread to sleep for the specified number of
// milliseconds plus the specified number of nano seconds
void
Worker::sleep(long millis, int nanos)
{
	// to do
}

// disable the current thread for thread scheduling purposes
void
Worker::park(void)
{
	// to do
}

// disable the current thread for thread scheduling purposes
void
Worker::parkNanos(long nanos)
{
	// to do
}

// make available the current thread for thread scheduling
void
Worker::unpark(Worker worker)
{
	// to do
}

} /* closing brace for namespace xrx_kernel */
