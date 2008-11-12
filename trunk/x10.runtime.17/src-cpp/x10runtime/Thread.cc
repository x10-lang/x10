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
#include <errno.h>
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
	while (tp->__thread_already_started == false) {
		pthread_cond_wait(&(tp->__thread_start_cond), &(tp->__thread_start_lock));
	}
	pthread_mutex_unlock(&(tp->__thread_start_lock));
	// this thread is now running
	tp->__thread_running = true;
	tp->run();
	// finished running
	tp->__thread_running = false;
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

	// clear this thread's run flags
	__thread_already_started = false;
	__thread_running = false;

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

// Waits forever for this thread to die.
void
Thread::join(void)
{
	__xrxDPrStart();
	int status;
	pthread_join(__xthread, (void **)&status);
	__xrxDPrEnd();
}

// Tests if this thread is alive.
boolean
Thread::isAlive()
{
	__xrxDPrStart();
	if (__thread_already_started && __thread_running) {
		return true;
	}
	return false;
	__xrxDPrEnd();
}

// Interrupts this thread.
void
Thread::interrupt()
{
	__xrxDPrStart();
	if (isAlive()) {
		pthread_kill(__xthread, SIGINT);
	};
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

// Clean-up routine for sleep method call.
void
Thread::thread_sleep_cleanup(void *arg)
{
	cond_mutex_t *cmp = (cond_mutex_t *)arg;

	__xrxDPrStart();
	pthread_mutex_unlock(&(cmp->mutex));
	pthread_mutex_destroy(&(cmp->mutex));
	pthread_cond_destroy(&(cmp->cond));
	delete cmp;
	signal(SIGINT, SIG_DFL);
	__xrxDPrEnd();
}

/**
 * Put the current thread to sleep for the specified number of
 * milliseconds.
 */
void
Thread::sleep(const Long& millis) throw (InterruptedException)
{
	Int nanos = 0;

	sleep(millis, nanos);
}

// Dummy interrupt handler.
void
Thread::intr_hndlr(int signo)
{
	__xrxDPr();
}

/**
 * Put the current thread to sleep for the specified number of
 * milliseconds plus the specified number of nano seconds.
 */
void
Thread::sleep(const Long& millis, const Int& nanos) throw (InterruptedException)
{
	cond_mutex_t *cmp;
	boolean done = false;
	struct timeval tval, cval;
	struct timespec tout;
	long sleep_usec, cur_usec;
	int rc;
	InterruptedException ie;

	__xrxDPrStart();
	signal(SIGINT, intr_hndlr);
	cmp = new (cond_mutex_t);
	pthread_mutex_init(&(cmp->mutex), NULL);
	pthread_cond_init(&(cmp->cond), NULL);
	pthread_mutex_lock(&(cmp->mutex));
	pthread_cleanup_push(thread_sleep_cleanup, (void *)cmp);
	gettimeofday(&tval, NULL);
	tout.tv_sec = tval.tv_sec + (millis/1000);
	tout.tv_nsec = (tval.tv_usec * 1000) + nanos;
	sleep_usec = (tout.tv_sec * 1000 * 1000) +
				(tout.tv_nsec / 1000);
	while (!done) {
		rc = pthread_cond_timedwait(&(cmp->cond), &(cmp->mutex), &tout);
		if (rc == ETIMEDOUT) {
			// specified timeout has passed
			done = true;
		} else {
			// might be a spurious wakeup
			throw ie;
			break;
			/*
			gettimeofday(&cval, NULL);
			cur_usec = (cval.tv_sec * 1000 * 1000) + cval.tv_usec;
			if (cur_usec < sleep_usec) {
				throw ie;
				done = false;
				continue;
			}
			*/
		}
	}
	pthread_cleanup_pop(1);
	__xrxDPrEnd();
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
