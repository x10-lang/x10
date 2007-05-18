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


import static x10.runtime.cws.ClosureStatus.*;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;
import java.util.List;
import java.util.ArrayList;


/**
 * A closure corresponds to a slow invocation of a task. Each
 * worker contains a closure at the top level. When the
 * topmost closure of a victim is stolen, a new child closure is
 * created from the topmost frame in the closure  and left
 * behind on the victim. The old closure is now a parent
 * and is moved to the thief, which starts executing it.
 * 
 * @author vj 04/18/07
 *
 */
public abstract class Closure  {
	public static final int MAXIMUM_CAPACITY = 1 << 30;
	public static final int INITIAL_CAPACITY = 1 << 13; // too high??
	public static final int EXCEPTION_INFINITY = Integer.MAX_VALUE;
	public  class Cache {
		// these are indices into a table of Frames.
		volatile int head;
		volatile int tail;
		volatile int exception;
		Frame[] stack;
		 /**
	     * Pushes a task. Called only by current thread.
	     * @param x the task
	     */
	    final void pushFrame(Frame x) {
	    	Frame[] array = stack;
	    	if (array != null && tail < array.length - 1) {
	    		array[tail]=x;
	    		++tail;
	    		return;
	    	}
	    	
	    	growAndPushFrame(x);
	    }
		  /*
	     * Handles resizing and reinitialization cases for pushFrame
	     * @param x the task
	     */
	    private void growAndPushFrame(Frame x) {
	        int oldSize = 0;
	        int newSize = 0;
	        Frame[] oldArray = stack;
	        if (oldArray != null) {
	            oldSize = oldArray.length;
	            newSize = oldSize << 1;
	        }
	        if (newSize < INITIAL_CAPACITY)
	            newSize = INITIAL_CAPACITY;
	        if (newSize > MAXIMUM_CAPACITY)
	            throw new Error("Frame stack size exceeded");
	        Frame[] newArray = new Frame[newSize];
	        
	        newArray[tail] = x;
	        stack = newArray;
	        ++tail;
	    }
	    public void resetExceptionPointer() {
	    	exception = head;
	    }
	    /**
	     * TODO: Ensure that a fence is not needed after the write to exception.
	     *
	     */
	    public void incrementExceptionPointer() {
	    	Thread ws = Thread.currentThread();
	    	assert lockOwner == ws;
	    	assert status == RUNNING;
	    	if (exception != EXCEPTION_INFINITY)
	    		++exception;
	    	
	    }
	    public void decrementExceptionPointer() {
	    	Thread ws = Thread.currentThread();
	    	assert lockOwner == ws;
	    	assert status == RUNNING;
	    	if (exception != EXCEPTION_INFINITY)
	    		--exception;
	    	
	    }
	    /**
	     * TODO: Check that the write to the volatile variable
	     * is visible to every other thread.
	     *
	     */
	    public void signalImmediateException() {
	    	assert lockOwner == Thread.currentThread();
	    	assert status == RUNNING;
	    	exception = EXCEPTION_INFINITY;
	    }
	    public boolean atTopOfStack() {
	    	return head+1 == tail;
	    }
	    public Frame childFrame() {
	    	return stack[head+1];
	    }
	}
	Cache cache;
	Frame frame;
	Closure parent;
	ClosureStatus status;
	Lock lock;
	Worker lockOwner;
	int joinCount;
	Object result;
	
	/**
	 * Inlets are not represented explicitly as separate pieces of code --
	 * they will be once we figure out if we want to support them. (We 
	 * probably do.) Rather we just use a child closure directly as an inlet.
	 */
	List<Closure> completeInlets;
	List<Closure> incompleteInlets;
	
	Worker ownerReadyQueue;
	/**
	 * The ready deque is maintained through a pair of references.
	 */
	Closure nextReady, prevReady;
	
	public ClosureStatus status() { return status;}
	
	public Closure() {
		super();
		initialize();
	}
	/**
	 * Subclasses should override this as appropriate. 
	 * But they should alwas first call super.initialize();
	 *
	 */
	public void initialize() {
		// need to handle abort processing.
	}

	public boolean hasChildren() {
		return joinCount > 0;
	}
	
	public void lock(Worker agent) { 
		lock.lock();
		lockOwner =agent;
	}
	public void unlock() { 
		
		lockOwner=null;
		lock.unlock();
	}
	public void addCompletedInlet(Closure child) {
		if (completeInlets == null)
			completeInlets = new ArrayList<Closure>();
		completeInlets.add(child);
	}
	
	public void makeReady() {
		status=READY;
		cache=null;
	}
	
	public void completeAndEnque(Closure child) {
		Worker ws = (Worker) Thread.currentThread();
		assert (lockOwner == ws);
		addCompletedInlet(child);
		
	}
	public void removeChild(Closure child) {
		assert this.ownerReadyQueue == Thread.currentThread();
		if (incompleteInlets != null) 
		 incompleteInlets.remove(child);
		// for (Inlet i : incompleteInlets) {
		//	if (i.target == child) return i;
		//}
		//return null;
	}
	
	/**
	 * This code is executed by the thief.
	 * @param victim
	 * @return
	 */
	Closure promoteChild(Worker victim) {
		Worker ws = (Worker) Thread.currentThread();
		Frame f = cache.childFrame();
		Closure child = f.makeClosure();
		assert lockOwner == ws;
		assert status == RUNNING;
		assert ownerReadyQueue == victim;
		assert ws.lockOwner == victim;
		assert nextReady==null;
		assert cache.head <= cache.exception;
		child.parent = this;
		child.joinCount = 0;
		child.cache = cache;
		child.status = RUNNING;
		++child.cache.head;
		child.frame = cache.stack[cache.head];
		victim.addBottom(child);
		return child;
	}
	public void finishPromote(Closure child) {
		Thread ws = Thread.currentThread();
		assert lockOwner == ws;
		assert child.lockOwner != ws;
		++joinCount;
		addChild(child);
		makeReady();
		
	}
	public void addChild(Closure child) {
		Thread ws = Thread.currentThread();
		assert lockOwner==ws;
		assert child.lockOwner != ws;
		if (incompleteInlets == null)
			incompleteInlets = new ArrayList<Closure>();
		incompleteInlets.add(child);
		
	}

    public Closure setupForExecution() {
    	Worker ws = (Worker) Thread.currentThread();
    	status = RUNNING;
    	// load the cache from the worker's state.
    	cache = ws.cache;
    	cache.pushFrame(frame);
    	cache.resetExceptionPointer();
    	return this;
    }
    public void decrementExceptionPointer() {
    	cache.decrementExceptionPointer();
    }
    public void incrementExceptionPointer() {
    	cache.incrementExceptionPointer();
    }
    public void resetExceptionPointer() {
    	cache.resetExceptionPointer();
    }
    /**
     * This closure has completed its computation. Return its value
     * to the parent. If this is the last child joining the parent,
     * and the parent is suspended, return the parent (this is a
     * provably good steal).
     * @return
     */
   
    public Closure returnValue() {
    	assert status==RETURNING;
    	return closureReturn();
    }
    /**
     * Return protocol. The closure has completed its computation. Its return value
     * is now propagated to the parent. The closure to be executed next, possibly null,
     * is returned.  
     * This closure must not be locked (by this worker??) and must not be in any deque.
     * @return the parent, if this is the last child joining.
     */
    public Closure closureReturn() {
    	Worker ws = (Worker) Thread.currentThread();
    	assert (joinCount==0);
    	assert (status == RETURNING);
    	assert (ownerReadyQueue==null);
    	assert (lockOwner != ws);
    	Closure parent = this.parent;
    	assert (parent != null);
    	
    	parent.lock(ws);
    	try {
    		assert parent.status != RETURNING;
    		assert parent.frame != null;
    		parent.removeChild(this);
    		lock(ws);
    		parent.completeAndEnque( this);
    		try {
    			if (parent.status == RUNNING) {
    				parent.cache.signalImmediateException();
    			} else
    				parent.pollInlets();
    			// Is a fence() needed?
    			--parent.joinCount;
    			return parent.provablyGoodStealMaybe();
    		} finally {
    			unlock();
    		}
    		
    	} finally {
    		parent.unlock();
    	}
    	// The child should be garbage at this point.
    }
    /**
     * Suspend this closure. Called during execution of slow sync
     * when there is at least one outstanding child.
     *
     */
    public void suspend() {
    	Worker ws = (Worker) Thread.currentThread();
    	assert lockOwner == ws;
    	assert status == RUNNING;
    	status = SUSPENDED;
    	Closure cl = ws.extractBottom();
    	assert cl==this;
    	//TODO: Shouldnt its ownedReadyQueue be set to null?
    	//drop this closure. The only reference left to it is from the child
    	
    }
    /**
     * Return the parent provided that its joinCount is zero 
     * and it is suspended. The parent should now be considered
     * stolen by the worker that just finished returning the
     * value of a child
     * @return parent or null
     */
    private Closure provablyGoodStealMaybe() {
    	Thread ws = Thread.currentThread();
    	assert lockOwner==ws;
    	assert parent != null;
    	Closure result = null;
    	if (parent.joinCount==0 &&parent.status == SUSPENDED) {
    		result = parent;
    		parent.pollInlets();
    		parent.ownerReadyQueue =null;
    		parent.makeReady();
    	}
    	return result;
    }
	
	public boolean sync() {
		return ((Worker) Thread.currentThread()).sync();
	}
	/**
	 * Run all completed inlets.
	 * TODO: Figure out why pollInlets are being run incrementally
	 * and not just once, after joinCount==0. Perhaps because
	 * this method is supposed to perform abort processing.
	 */
	public void pollInlets() {
		Worker ws = (Worker) Thread.currentThread();
		assert lockOwner==ws;
		if (status==RUNNING && ! cache.atTopOfStack())
			assert ws.lockOwner == ws;
		for (Closure i : completeInlets) {
			i.executeAsInlet(this);
		}
		completeInlets = null;
	}
	/**
	 * Before a slow code return. The closure is marked as RETURNING
	 * so it wont be stolen.
	 * @param result
	 */
	public void setResult(Object result) {
		Worker ws = (Worker) Thread.currentThread();
		ws.lock(ws);
		try {
			
			Closure t = ws.peekBottom();
		
			lock(ws);
			try {
				assert t.status==RUNNING;
				t.status=RETURNING;
				t.frame=null;
				t.result=result;
			} finally {
				t.unlock();
			}
		} finally {
			ws.unlock();
		}
	}
	
	/**
	 * Execute this closure. Performed after the closure has been
	 * stolen from a victim. Will eventually invoke compute(frame),
	 * after setting things up.
	 */
	public Closure execute() {
		Worker ws = (Worker) Thread.currentThread();
		assert lockOwner != ws;
		lock(ws);
		 
		ClosureStatus s = status;
		Frame f = frame;
		if (s == READY) {
			try {
				setupForExecution();
				assert f != null;
				pollInlets();
			} finally {
				unlock();
			}
			ws.lock(ws);
			try { 
				ws.addBottom(this);
			} finally {
				ws.unlock();
			}
			compute(f);
			return null;
		}
		if (s == RETURNING) {
			unlock();
			return returnValue();
		}
		throw new Error("Worker executes closure with ");
	}
	
	/**
	 * Slow execution entry point.
	 * @param frame
	 */
	abstract protected void compute(Frame frame);
	
	/**
	 * Return your value to the parent closure. Record in the child
	 * some representation of where it was created in the body
	 * of the parent, and use that to determine where to 
	 * store the result in the parent frame.
	 * @param parent -- the closure into which the child returns its value
	 */
	abstract protected void executeAsInlet(Closure parent);

}
