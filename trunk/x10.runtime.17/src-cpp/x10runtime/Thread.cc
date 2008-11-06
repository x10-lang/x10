/*
 * (c) Copyright IBM Corporation 2008
 *
 * $Id$
 * This file is part of XRX/C++ native layer implementation.
 */

/**
 * Implementation file for the low level thread interface.
 */

#include <Thread.h>
#include <sstream>

namespace xrx_runtime {

// initialize static data members
Object *Thread::__current_place = NULL;
Object *Thread::__current_activity = NULL;
Thread *Thread::__current_thread = NULL;
long Thread::__thread_cnt = 0;

// thread name prefix -- used to construct thread names
const String __thread_name_prefix = "xrxThread-";

// Helper method to initialize a Thread object.
void
Thread::thread_init(const Runnable *task, const String *name)
{
	// increment the overall thread count
	__thread_cnt += 1;

	// set thread's external id
	__thread_id = __thread_cnt;

	// set this thread's runnble object
	// null check will be done only during the actual invocation
	__thread_runobj = (Runnable *)task;

	// clear this thread's run flag
	__thread_already_started = false;

	// we maintain the internal copy of a thread name
	if (!name) {
		// construct a new name for this thread
		ostringstream ost;
		ost << __thread_name_prefix << __thread_id;
		__thread_name = new String(ost.str());
	} else {
		__thread_name = new String(*name);
	}
}

// Allocates a new Thread object.
// constructor (1)
Thread::Thread()
{
	thread_init(NULL, NULL);
}

// constructor (2)
Thread::Thread(const Runnable& task)
{
	thread_init(&task, NULL);
}

// constructor (3)
Thread::Thread(const Runnable& task, const String& name)
{
	thread_init(&task, &name);
}

// destructor
Thread::~Thread()
{
	// free internal thread name object
	delete __thread_name;
}

// Returns a reference to the currently executing thread object.
Thread&
Thread::currentThread(void)
{
	return (*__current_thread);
}

// Begin thread execution.
void
Thread::start(void)
{
	IllegalThreadStateException itse;

	if (__thread_already_started) {
		throw itse;
	}
	__thread_already_started = true;
	// call the run method of this thread
	this->run();
}

// Invoke the Runnable object's run method, if possible.
void
Thread::run(void)
{
	// check whether a separate Runnable run object is available
	// if so, invoke it's run method
	if (__thread_runobj != NULL) {
		(*__thread_runobj).run();
	} else {
		// do nothing
	}
}

/**
 * Put the current thread to sleep for the specified number of
 * milliseconds.
 */
void
Thread::sleep(const Long& millis) throw (InterruptedException)
{
	// to do
}

/**
 * Put the current thread to sleep for the specified number of
 * milliseconds plus the specified number of nano seconds.
 */
void
Thread::sleep(const Long& millis, const Int& nanos) throw (InterruptedException)
{
	// to do
}

/**
 * Disables the current thread for thread scheduling purposes
 * unless the permit is available.
 */
void
Thread::park(void)
{
	// to do
}

/**
 * Disables the current thread for thread scheduling purposes,
 * for up to the specified waiting time, unless the permit is
 * available.
 */
void
Thread::parkNanos(const Long& nanos)
{
	// to do
}

/**
 * Makes available the permit for the given thread, if it was
 * not already available.
 */
void
Thread::unpark(Thread& thread)
{
	// to do
}

// Returns the current activity.
const Object&
Thread::activity(void)
{
	return (*__current_activity);
}

// Set the current activity.
void
Thread::activity(const Object& activity)
{
	*__current_activity = activity;
}

// Returns the current place.
const Object&
Thread::place(void)
{
	return (*__current_place);
}

// Set the current place.
void
Thread::place(const Object& place)
{
	*__current_place = place;
}

// Returns the identifier of this thread.
long
Thread::getId()
{
	return __thread_id;
}

// Returns this thread's name.
const String&
Thread::getName(void)
{
	return (*__thread_name);
}

// Set the name of this thread.
void
Thread::setName(const String& name)
{
	// free the old thread name before new one is set
	delete __thread_name;
	__thread_name = new String(name);
}

} /* closing brace for namespace xrx_runtime */
