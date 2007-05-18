/*
 *
 * (C) Copyright IBM Corporation 2007
 *
 *  This file is part of X10 runtime. It is 
 *  governed by the licence under which 
 *  X10 is released.
 *
 */
package x10.runtime.cws;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static x10.runtime.cws.ClosureStatus.*;

/**
 * The worker for Cilk-style work stealing.
 * @author vj 04/18/07
 *
 */
public class Worker extends Thread {
	/**
	 * The pool. This class could alternatively be an inner class,
	 * but the reference is left explicit to make it easier to see
	 * and check which calls are local to threads vs global to
	 * pool.
	 */
	
	final Pool pool;
	
	private Closure top, bottom;

	protected Lock lock; // dequeue_lock
	protected Worker lockOwner; // the worker holding the lock.
	protected int randNext;
	protected int index;
	
	public static Worker[] workers; // index is bounded above by this.
	public void lock(Worker agent) {
		lock.lock();
		this.lockOwner=agent;
	}
	public void unlock() {
		this.lockOwner=null;
		lock.unlock();
	}
	
	
	Closure.Cache cache;
	// an array of frames.
	Frame[] frames; 
	
	public void pushFrame(Frame frame) {
		int t = cache.tail;
		// may generate an  out of bounds exception.
		frames[t] =frame;
		cache.tail++;
		
	}
	public void popFrame() {
		--cache.tail;
	}
	public boolean popCheck() {
		int t = cache.tail;
		// need a store load fence.
		return cache.exception >= t;
	}
	public Closure popFrameCheck() {
		return popCheck()? bottom : null;
		
	}
	
	private void setRandSeed(int seed) {
		randNext = seed;
	}
	private int rand() {
		randNext = randNext*1103515245  + 12345;
		int result = randNext >> 16;
		if (result < 0) result = -result;
		return result;
	}
	
	/**
	 * Creates new Worker.
	 */
	public Worker(Pool pool, int index) {
		this.pool = pool;
		this.index = index;
		this.lock = new ReentrantLock();
		setDaemon(true);
		// Further initialization postponed to init()
	}
	
	volatile Closure currentTask = null, prevTask=null;
	
	/**
	 * Do the thief part of Dekker's protocol.  Return true upon success,
	 * false otherwise.  The protocol fails when the victim already popped
	 * T so that E=T.
	 */
	public boolean dekker(Closure cl) {
		// Closure_assert_ownership(ws, cl);
		cl.incrementExceptionPointer();
		if ((cl.cache.head + 1) >= cl.cache.tail) {
			cl.decrementExceptionPointer();
			return false;
		}
		return true;
	}

	
	public Closure steal() {
		// Cilk_event(ws, EVENT_STEAL_ATTEMPT);
		lock.lock();
		Closure cl = peekTop();
		if (cl == null) {
			// Cilk_event(ws, EVENT_STEAL_EMPTY_DEQUE);
			lock.unlock();
			return null;
		}
		cl.lock(this);
		ClosureStatus status = cl.status();
		assert (status == ABORTING || status == READY || status == RUNNING || status == RETURNING);
		if (status == READY) {
			Closure res = extractTop(this);
			assert (res == cl);
			cl.unlock();
			lock.unlock();
			return res;
		}
		if (status == RUNNING) {
			if (dekker(cl)) {
				Worker thief = (Worker) Thread.currentThread();
				Closure child = cl.promoteChild(thief);
				Closure res = extractTop(this);
				assert cl==res;
				lock.unlock();
				child.finishPromote(cl);
				cl.unlock();
				// Cilk_event(ws, EVENT_STEAL);
				return res;
			} 
		}
		if (status == RETURNING) {
//			Cilk_event(ws, EVENT_STEAL_RETURNING);
		}
		lock.unlock();
		cl.unlock();
		return null;
		
	}

	/* 
	 * Extract the topmost closure in the ready deque of this
	 * worker. May return null.
	 * Assumes that the executing thread already holds the lock on the deque
	 * for this worker.
	 */
	public Closure extractTop(Worker agent) {
		assert lockOwner==agent;
		Closure cl = top;
		if (cl == null) {
			bottom = null;
			return cl;
		}
		top = cl.nextReady;
		if (cl == bottom) {
			cl.nextReady = null;
			bottom = null;
		} else {
			assert cl.nextReady !=null;
			cl.nextReady.prevReady = null;
		}
		return cl;
	}
	/**
	 * Return the top of the closure deque, without removing it.
	 * @return top of the closure deque
	 */
	public Closure peekTop() {
		assert lockOwner==this;
		Closure cl = top;
		return cl;
	}
	/**
	 * Return the closure at the bottom fo the deque.
	 * @return
	 */
	public Closure extractBottom() {
		Thread agent = Thread.currentThread();
		assert lockOwner==agent;
		Closure cl=bottom;
		if (cl == null) {
			top=null;
			return null;
		}
		cl.ownerReadyQueue =this;
		bottom = cl.prevReady;
		if (cl == top) {
			assert cl.prevReady==null;
			top=null;
		} else {
			assert cl.prevReady !=null;
			cl.prevReady.nextReady = null;
		}
		return cl;
	}
	public Closure peekBottom() {
		Thread agent = Thread.currentThread();
		assert lockOwner==agent;
		Closure cl = bottom;
		if (cl==null) {
			top=null;
			return cl;
		}
		cl.ownerReadyQueue = this;
		return cl;
	}
	public void addBottom(Closure cl) {
		Thread agent = Thread.currentThread();
		assert lockOwner==agent;
		assert cl !=null;
		assert cl.ownerReadyQueue == null;
		cl.prevReady = bottom;
		cl.nextReady = null;
		bottom = cl;
		cl.ownerReadyQueue = this;
		if (top == null) {
			top = cl;
		} else {
			cl.prevReady.nextReady = cl;
		}
		
	}
	public void assertIsBottom(Closure cl) {
		assert cl == bottom;
	}
	
	public void suspend(Closure cl) {
		// worker has to have a lock on its deque
		// worker has to own cl
		// 
		assert (cl.status == RUNNING);
		cl.status = SUSPENDED;
		Closure cl1 = extractBottom(); // throw away the bottom most guy
		assert (cl==cl1);
	}
	/**
	 * A slow sync.
	 * @return true if the closure has to be suspended, false o.w.
	 */
	public boolean sync() {
		lock(this);
		try { 
			Closure t = peekBottom();
			assert t.status==RUNNING;
			t.lock(this);
			try { 
				assert t.status==RUNNING;
				
				// In slow sync. Therefore must be the case
				// that there is no frame on the stack.
				// i.e. this worker has finished executing any children asyncs. 
				// Other workers may still be working on children.
				assert t.cache.atTopOfStack();
				// Execute all completed inlets.
				// Note these are being executed in a single-threaded
				// context since the lock is held.
				t.pollInlets();
				// pollInlets may change bottom of queue, due to abort processing.
				assertIsBottom(t);
				if (t.hasChildren()) {
					// Suspend. Now any child that is joining
					// will get to check inlets and if they 
					// are all done, then execute this task
					// in place.
					suspend(t);
					return true;
				}
			} finally {
				t.unlock();
			}
			
		} finally {
			unlock();
		}
		return false;
		
	}
	
	volatile boolean done;
	Closure closure;
	public void run() {
		assert index >= 0;
		setRandSeed(index*162347);
		Closure cl = closure;
		while (!done) {
			if (closure == null) {
				lock(this);
				try {
					cl = extractBottom();
				} finally {
					unlock();
				}
			}
			while (cl == null && !done) {
				int victim = rand() % workers.length;
				if (victim != index) {
					cl = workers[victim].steal();
					// TOOD: Add support for lowering priority.
					
				}
			}
			if (! done) {
				cl = cl.execute();
			}
		}
	}
	
	
}


