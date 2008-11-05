/*
 * (c) Copyright IBM Corporation 2008
 *
 * $Id$
 * This file is part of XRX/C++ native layer implementation.
 */

/************************************************************
 * A thread is a unit of execution in a place.
 * In a given place, we can have multiple threads of execution.
 *
 * A union of a subset of the functionality of java.lang.Thread
 * augmented with the park/unpark API of java.util.concurrent.
 * locks.LockSupport.
 ************************************************************/

#ifndef __XRX_THREAD_H
#define __XRX_THREAD_H

#include "Types.h"
#include "Object.h"
#include "Runnable.h"
#include "InterruptedException.h"

namespace xrx_runtime {

class Thread : public Runnable {
public:
	
	// [constructors] Allocates a new Thread object.
	Thread();
	Thread(const Runnable& task);
	Thread(const Runnable& task, const String& name);

	// destructor
	~Thread();

	// Returns a reference to the currently executing thread object.
	static Thread& currentThread(void);

	/**
	 * Causes this thread to begin execution; the XRX runtime
	 * calls the run method of this thread.
	 * The result is that two threads are running concurrently:
	 * the current thread (which returns from the call to the
	 * start method) and the other thread (which executes its
	 * run method).
	 * Throws IllegalThreadStateException, if the thread was
	 * already started.
	 */
	void start(void);

	/**
	 * If this thread was constructed using a separate Runnable
	 * run object, then the Runnable objects' run method is called;
	 * otherwise, this method does nothing and returns.
	 */
	void run(void);

	/**
	 * Causes the currently executing thread to sleep (cease
	 * execution) for the specified number of milliseconds.
	 * The thread does not lose ownership of any monitors.
	 * Throws InterruptedException, if any thread has interrupted
	 * the current thread.
	 */
	static void sleep(const Long& millis) throw (InterruptedException);

	/**
	 * Causes the currently executing thread to sleep (cease
	 * execution) for the specified number of milliseconds plus
	 * the specified number of nanoseconds.
	 * The thread does not lose ownership of any monitors.
	 * Throws InterruptedException, if any thread has interrupted
	 * the current thread.
	 */
	static void sleep(const Long& millis, const Int& nanos) throw (InterruptedException);
	/**
	 * Disables the current thread for thread scheduling purposes
	 * unless the permit is available.
	 * If the permit is available then it is consumed and the call
	 * returns immediately; otherwise the current thread becomes
	 * disabled for thread scheduling purposes and lies dormant
	 * until one of three things happen:
	 * 1) Some other thread invokes unpark with the current thread
	 * as the target; or
	 * 2) Some other thread interrupts the current thread; or
	 * 3) The call spuriously (ie, for no reason) returns.
	 * This method does not report which of these caused the method
	 * to return.  Callers should re-check the conditions which
	 * caused the thread to park in the first place.
	 */
	static void park(void);

	/**
	 * Disables the current thread for thread scheduling purposes,
	 * for up to the specified waiting time, unless the permit is
	 * available.
	 * If the permit is available then it is consumed and the call
	 * returns immediately; otherwise the current thread becomes
	 * disabled for thread scheduling purposes and lies dormant
	 * until one of four things happen:
	 * 1) Some other thread invokes unpark with the current thread
	 * as the target; or
	 * 2) Some other thread interrupts the current thread; or
	 * 3) The specified waiting time elapses; or
	 * 4) The call spuriously (ie, for no reason) returns.
	 * This method does not report which of these caused the method
	 * to return.  Callers should re-check the conditions which
	 * caused the thread to park in the first place.
	 */
	static void parkNanos(const Long& nanos);

	/**
	 * Makes available the permit for the given thread, if it was
	 * not already available.  If the thread was blocked on park
	 * then it will unblock.  Otherwise, its next call to park is
	 * guaranteed not to block.  This operation is not guaranteed
	 * to have any effect at all if the given thread has not been
	 * started.
	 */
	static void unpark(Thread& thread);

	// Returns the current activity.
	Object& activity(void);

	// Set the current activity.
	void activity(const Object& activity);

	// Returns the current place.
	Object& place(void);

	// Set the current place.
	void place(const Object& place);

	// Returns this thread's name.
	String& getName(void);

	// Changes the name of this thread to be equal to the argument name.
	void setName(const String& name);

private:
	// the current place
	static Object *__current_place;
	// the current activity
	static Object *__current_activity;
	// the current thread
	static Thread *__current_thread;
	// thread id
	pthread_t __threadid;
	// thread name
	String *__thread_name;
	// thread attributes
	pthread_attr_t __thread_attr;
	// thread runnable object
	Runnable *__thread_runobj;
};

} /* closing brace for namespace xrx_runtime */

#endif /* __XRX_THREAD_H */
