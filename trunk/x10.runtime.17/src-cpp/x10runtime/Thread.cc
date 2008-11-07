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
#include <Debug.h>
#include <sstream>

namespace xrx_runtime {

// initialize static data members
Object *Thread::__current_place = NULL;
Object *Thread::__current_activity = NULL;
Thread *Thread::__current_thread = NULL;
long Thread::__thread_cnt = 0;

// thread name prefix -- used to construct thread names
const String __thread_name_prefix = "xrxThread-";

// Thread start routine.
void *
Thread::thread_start_routine(void *arg)
{
	// simply call the run method of the invoking thread object
	__xrxDPrStart();
	Thread *tp = (Thread *)arg;
	pthread_mutex_lock(&(tp->__thread_start_lock));
	pthread_cond_wait(&(tp->__thread_start_cond), &(tp->__thread_start_lock));
	pthread_mutex_unlock(&(tp->__thread_start_lock));
	tp->run();
	__xrxDPrEnd();
	pthread_exit(NULL);
}

// Helper method to initialize a Thread object.
void
Thread::thread_init(const Runnable *task, const String *name)
{
	__xrxDPrStart();

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
		std::ostringstream ost;
		ost << __thread_name_prefix << __thread_id;
		__thread_name = new String(ost.str());
	} else {
		__thread_name = new String(*name);
	}

	// create start condition object with default attributes
	// ??check the return code for ENOMEM/EAGAIN??
	(void)pthread_cond_init(&__thread_start_cond, NULL);

	// create the associated lock object with default attributes
	// ??check the return code for ENOMEM??
	(void)pthread_mutex_init(&__thread_start_lock, NULL);

	// create thread attributes object
	// ??check the return code for ENOMEM??
	(void)pthread_attr_init(&__xthread_attr);

	// set this thread's attributes
	// guardsize
	size_t guardsize = PAGESIZE;
	pthread_attr_setguardsize(&__xthread_attr, guardsize);
	// inheritsched
	int inheritsched = PTHREAD_INHERIT_SCHED;
	pthread_attr_setinheritsched(&__xthread_attr, inheritsched);
	// schedpolicy
	int policy = SCHED_OTHER;
	pthread_attr_setschedpolicy(&__xthread_attr, policy);
	// detachstate
	int detachstate = PTHREAD_CREATE_JOINABLE;
	pthread_attr_setdetachstate(&__xthread_attr, detachstate);
	// contentionscope
	int contentionscope = PTHREAD_SCOPE_PROCESS;
	pthread_attr_setscope(&__xthread_attr, contentionscope);
	// stacksize
	size_t stacksize = PTHREAD_STACK_MIN;
	pthread_attr_setstacksize(&__xthread_attr, stacksize);
	// suspendstate
	//int suspendstate = PTHREAD_CREATE_SUSPENDED_NP;
	//pthread_attr_setsuspendstate_np(&__xthread_attr, suspendstate);

	// create a new execution thread ??in suspended state??
	(void)pthread_create(&__xthread, &__xthread_attr,
				Thread::thread_start_routine, (void *)this);

	__xrxDPrEnd();
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
	__xrxDPrStart();
	// free internal thread name object
	delete __thread_name;
	// free start condition object & its lock
	pthread_mutex_destroy(&__thread_start_lock);
	pthread_cond_destroy(&__thread_start_cond);
	// free thread attributes
	pthread_attr_destroy(&__xthread_attr);
	__xrxDPrEnd();
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

	__xrxDPrStart();

	if (__thread_already_started) {
		throw itse;
	}
	pthread_mutex_lock(&__thread_start_lock);
	__thread_already_started = true;
	pthread_cond_signal(&__thread_start_cond);
	pthread_mutex_unlock(&__thread_start_lock);

	__xrxDPrEnd();
}

// Invoke the Runnable object's run method, if possible.
void
Thread::run(void)
{
	// check whether a separate Runnable run object is available
	// if so, invoke it's run method
	__xrxDPrStart();
	if (__thread_runobj != NULL) {
		(*__thread_runobj).run();
	} else {
		// do nothing
	}
	__xrxDPrEnd();
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
