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

namespace xrx_runtime {

// Allocates a new Thread object.
// local method to initialize a new thread object
static void
thread_init(const Runnable& task, String name)
{
	// to do
}
// constructor (1)
Thread::Thread()
{
	// to do
}

// constructor (2)
Thread::Thread(const Runnable& task)
{
	// to do
}

// constructor (3)
Thread::Thread(const Runnable& task, const String& name)
{
	// to do
}

// destructor
Thread::~Thread()
{
	// to do
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
	// to do
}

// Invoke the Runnable object's run method, if possible.
void
Thread::run(void)
{
	// to do
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
Object&
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
Object&
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

// Returns this thread's name.
String&
Thread::getName(void)
{
	return (*__thread_name);
}

// Set the name of this thread.
void
Thread::setName(const String& name)
{
	*__thread_name = name;
}

} /* closing brace for namespace xrx_runtime */
