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



import java.util.concurrent.locks.ReentrantLock;
import java.util.List;
import java.util.ArrayList;


/**
 * A closure corresponds to a slow invocation of a task. This 
 * class is intended to be subclassed by client code. Subclasses
 * may specify extra state (e.g. fields such as result),
 * and will supply implementations of the abstract methods compute
 * and executeAsInlet. The code for a slow invocation of a task
 * should live in compute. The code for executeAsInlet should
 * specify where the result of the closure is to be deposited
 * in the parent closure.
 * 
 * Each worker contains a closure at the top level. When the
 * topmost closure of a victim is stolen, a new child closure is
 * created from the topmost frame in the closure  and left
 * behind on the victim. The old closure is now a parent
 * and is moved to the thief, which starts executing it.
 * 
 * @author vj 04/18/07
 *
 */
public class Closure  implements Executable {

	protected Cache cache;
	public  Frame frame;
	protected Closure parent;
	protected  Status status;
	protected ReentrantLock lock;
	protected Worker lockOwner;
	public int joinCount;
	
	public Frame parentFrame() { return parent.frame;}
	public Closure parent() { return parent;}
	/**
	 * Inlets are not represented explicitly as separate pieces of code --
	 * they will be once we figure out if we want to support them. (We 
	 * probably do.) Rather we just use a child closure directly as an inlet.
	 */
	protected List<Closure> completeInlets;
	protected List<Closure> incompleteInlets;
	
	protected Worker ownerReadyQueue;
	/**
	 * The ready deque is maintained through a pair of references.
	 */
	protected Closure nextReady, prevReady;
	public enum Status {

		RUNNING,
		SUSPENDED,
		RETURNING,
		READY,
		ABORTING
	}

	public interface Outlet {
		void run();
	}

	protected Status status() { return status;}
	
	protected Closure(Frame frame) {
		super();
		this.frame = frame;
		this.lock = new ReentrantLock();
		initialize();
	}
	
	/**
	 * Returns true if the closure has children
	 * that have not yet joined.
	 * @return
	 */
	protected boolean hasChildren() {
		return joinCount > 0;
	}
	
	
	void lock(Worker agent) { 
		lock.lock();
		lockOwner =agent;
	}
	
	void unlock() { 
		lockOwner=null;
		lock.unlock();
	}

	void addCompletedInlet(Closure child) {
		if (completeInlets == null)
			completeInlets = new ArrayList<Closure>();
		completeInlets.add(child);
	}
	
	void removeChild(Closure child) {
		if (incompleteInlets != null) 
		 incompleteInlets.remove(child);
		// for (Inlet i : incompleteInlets) {
		//	if (i.target == child) return i;
		//}
		//return null;
	}
	
	/**
	 * This code is executed by the thief on the parent while holding 
	 * the lock on the parent and on the victim.
	 * @param thief  -- The worker performing the steal.
	 * @param victim -- The worker from who work has been stolen.
	 * @return the child closure
	 */
	Closure promoteChild(Worker thief, Worker victim) {
		
		assert lockOwner == thief;
		assert status == Status.RUNNING;
		assert ownerReadyQueue == victim;
		assert victim.lockOwner == thief;
		assert nextReady==null;
		assert cache.exceptionOutstanding();

		Frame childFrame = cache.childFrame();
		Frame parentFrame = cache.topFrame();
		Closure child = childFrame.makeClosure();
		parentFrame.setOutletOn(child);
		
		// Leave the parent link in there.
		// It will not be used by globally quiescent computations.
		child.parent = this;
		child.joinCount = 0;
		child.cache = cache;
		child.status = Status.RUNNING;
		child.ownerReadyQueue=null;
		cache.incHead();
		
		victim.addBottom(thief, child);
		return child;
	}
	/**
	 * This code is executed by the thief on the parent while holding 
	 * the lock on the parent. The lock on the victim has been given up.
	 * Therefore other thiefs may be hitting upon the victim simultaneously.
	 * 
	 * @param thief  -- The worker performing the steal.
	 * @param child -- The child closure being promoted.
	 */
	void finishPromote(Worker thief, Closure child) {
		
		assert lockOwner == thief;
		assert child == null || child.lockOwner != thief;
		
		/* Add the child to the parent. */
		if (! child.requiresGlobalQuiescence())
			++joinCount;
		// No need to add child to parent's incomplete inlets
		// unless aborts are being propagated.
		//if (incompleteInlets == null)
		//	incompleteInlets = new ArrayList<Closure>();
		//incompleteInlets.add(child);
		
		/* Set the parent's cache to null and its status to READY */
		status=Status.READY;
		cache=null;
	}
	
    boolean handleException(Worker w) {
    	cache.resetExceptionPointer(w);
    	Status s = status;
    	assert s == Status.RUNNING || s == Status.RETURNING;
    	if (cache.empty()) {
    		assert joinCount==0;
    		status = Status.RETURNING;
        	return true;
    	}
    	return false;
    	
    }
  
    private void signalImmediateException(Worker ws) {
    	assert lockOwner == ws;
    	assert status == Status.RUNNING;
    	cache.signalImmediateException();
    }
   
    /**
     * Return protocol. The closure has completed its computation. Its return value
     * is now propagated to the parent. The closure to be executed next, possibly null,
     * is returned.  
     * This closure must not be locked (by this worker??) and must not be in any deque.
     * Required that ws==Thread.currentThread().
     * @return the parent, if this is the last child joining.
     */
     private Closure closureReturn(Worker w) {
    	
    	// Short circuit for globally quiescent computations.
    	if (requiresGlobalQuiescence() && parent != null) {
    		w.currentJob().accumulateResultInt(resultInt());
    		return null;
    	}
    	assert (joinCount==0);
    	assert (ownerReadyQueue==null);
    	assert (lockOwner != w);
    	
    	if (! requiresGlobalQuiescence())
    		completed();
    	Closure parent = this.parent;
    	if (parent == null) {
    		// Must be a top level closure.
    		return null;
    	}
    	return parent.acceptChild(w, this);
     }
     
     private Closure acceptChild(Worker ws, Closure child) {
    	lock(ws);
    	try {
    		assert status != Status.RETURNING;
    		assert frame != null;
    		//removeChild(child);
    		--joinCount;
    		child.lock(ws);
    		assert (lockOwner == ws);
    		addCompletedInlet(child);
    		try {
    			return provablyGoodStealMaybe(ws, child);
    		} finally {
    			child.unlock();
    		}
    	} finally {
    		unlock();
    	}
    	// The child should be garbage at this point.
    }
    
    /**
     * Suspend this closure. Called during execution of slow sync
     * when there is at least one outstanding child.
     * ws must be the worker executing suspend.
     * Assume: ws=Thread.currentThread();
     */
   void suspend(Worker ws) {
    	assert lockOwner == ws;
    	assert status == Status.RUNNING;
    	
    	status = Status.SUSPENDED;
    	
    	// throw away the bottommost closure on the worker.
    	// the only references left to this closure are from
    	// its children.
    	Closure cl = ws.extractBottom(ws);
    	assert cl==this;
    	
//    	Setting ownedReadyQueue to null even though Cilk does not do it.
    	cl.ownerReadyQueue=null;
    }
    
    /**
     * Return the parent provided that its joinCount is zero 
     * and it is suspended. The parent should now be considered
     * stolen by the worker that just finished returning the
     * value of a child
     * @return parent or null
     */
    private Closure provablyGoodStealMaybe(Worker w, Closure child) {
    	assert child.lockOwner==w;
    	Closure result = null;
    	
    	if (joinCount==0 && status == Status.SUSPENDED) {
    		result = this;
    		pollInlets(w);
    		ownerReadyQueue =null;
    		status=Status.READY;
    		cache=null;
    		if (Worker.reporting) {
    			System.out.println(w + " awakens " + this);
    		}
    	} 
    	return result;
    }
	
   
	/**
	 * Run all completed inlets.
	 * TODO: Figure out why pollInlets are being run incrementally
	 * and not just once, after joinCount==0. Perhaps because
	 * this method is supposed to perform abort processing.
	 */
	void pollInlets(Worker w) {
		assert lockOwner==w;
		//System.out.println(w + " polling " + this);
		if (status==Status.RUNNING && ! w.jobRequiresGlobalQuiescence && ! cache.atTopOfStack()) {
			assert w.lockOwner == w;
		}
		if (completeInlets != null)
			for (Closure i : completeInlets) {
				i.executeAsInlet();
			}
		completeInlets = null;
		//System.out.println(w + " .. done polling, yielding " + this);
	}
	
	 /**
     * Must be called by every slow procedure after it sets the 
     * return value but before it returns.
     * This method ensures that the closure's value is returned
     * to the parent. If this is the last child joining the parent,
     * and the parent is suspended, return the parent (this is a
     * provably good steal).
     * @return
     */
   
    Closure returnValue(Worker w) {
    	assert status==Status.RETURNING;
    	return closureReturn(w);
    }
	
	/**
	 * Execute this closure. Performed after the closure has been
	 * stolen from a victim. Will eventually invoke compute(frame),
	 * after setting things up.
	 * @param w -- the current thread, must be equal to Thread.currentThread()
	 */
	public Executable execute(Worker w) {
		assert lockOwner != w;
		
		lock(w);
		Status s = status;
		Frame f = frame;
		if (s == Status.READY) {
			try {
				status = Status.RUNNING;
		    	// load the cache from the worker's state.
		    	cache = w.cache;
		    	cache.pushFrame(frame);
		    	cache.resetExceptionPointer(w);
				assert f != null;
				pollInlets(w);
			} finally {
				unlock();
			}
			w.lock(w);
			try { 
				w.addBottom(w, this);
			} finally {
				w.unlock();
			}
			try {
				compute(w, f);
			} catch (StealAbort z) {
				// do nothing. the exception has done its work
				// unwinding the call stack.
			}
			return null;
		}
		if (s == Status.RETURNING) {
			unlock();
			return returnValue(w);
		}
		throw new Error(w + "executes " + status + " " + this + ": error!");
	}

	/**
	 * Return your value to the parent closure. Typically the
	 * closure will be created with information about where
	 * to deposit its result.
	 */
	void executeAsInlet() {
		if (outlet !=null) {
			outlet.run();
		}
	}
//	=============== The methods intended to be called by client code =======
//	=============== that subclasses Closure.========
	
	/**
	 * Slow execution entry point for the scheduler. Invoked by the thief
	 * running in the scheduler after it has stolen the closure from a victim.
	 * @param w -- The thread invoking the compute, i.e. w == Thread.currentThread()
	 * @param frame -- frame within which to execute
	 */
	protected void compute(Worker w, Frame frame) throws StealAbort {}
    
	/**
	 * Subclasses should override this as appropriate. 
	 * But they should alwas first call super.initialize();
	 *
	 */
	protected void initialize() {
		// need to handle abort processing.
	}

    /* Public method intended to be invoked from within 
     * slow methods of client code.
     * @return true -- iff the closure must suspend because not 
     *                 all children have returned
     */
	final protected boolean sync(Worker ws) {
		return ws.sync();
	}
	
	
	/**
	 * Invoked by client code before a return from slow code. It will
	 * mark the current closure as RETURNING so it wont be stolen. It will
	 * pop the current frame. 
	 * Before invoking this call, client code is responsible for setting the appropriate state
	 * on the closure so that the value to be returned is known.
	 * 
	 */
	final protected void setupReturn() {
		// Do not trust client code to pass this parameter in.
		Worker w = (Worker) Thread.currentThread();
		done = true;
		w.lock(w);
		try {
			if (requiresGlobalQuiescence()) {
				w.extractBottom(w);
				// speed the result on its way.
				if (parent != null)
					w.currentJob().accumulateResultInt(resultInt());
				return;
			}
			assert w.peekBottom(w)==this;
			lock(w);
			try {
				assert status==Status.RUNNING;
				status=Status.RETURNING;
				//frame=null;
				w.popFrame();
			} finally {
				unlock();
			}
		} finally {
			w.unlock();
		}
	}
	final protected void setupGQReturnNoArg() {
		// Do not trust client code to pass this parameter in.
		Worker w = (Worker) Thread.currentThread();
		w.lock(w);
		try {
			w.extractBottom(w);
			w.popFrame();
			return;
		} finally {
			w.unlock();
		}
	}
	final protected void setupGQReturnNoArgNoPop() {
		// Do not trust client code to pass this parameter in.
		Worker w = (Worker) Thread.currentThread();
		w.lock(w);
		try {
			w.extractBottom(w);
			return;
		} finally {
			w.unlock();
		}
	}
	
	final protected void setupGQReturn() {
		// Do not trust client code to pass this parameter in.
		Worker w = (Worker) Thread.currentThread();
		// do not set done to true. This will be 
		// done when global quiescence is recognized.
		w.lock(w);
		try {
			assert this==w.peekBottom(w);
			lock(w);
			try {
				assert status==Status.RUNNING;
				status=Status.RETURNING;
				//frame=null;
				w.popFrame();
			} finally {
				unlock();
			}
		} finally {
			w.unlock();
		}
	}
	
	protected Outlet outlet;
	public void setOutlet(Outlet o) { outlet=o;}
	
	/** Replace the frame by a copy. 
	 * 
	 * <p> Called during
	 * a steal to ensure that frames are not shared between the caches
	 * of two different workers. Must be called with the current thread,
	 * w, holding the lock on the victim. This ensures that the 
	 * victim is not running freely, modifying the frame being
	 * copied. Why? Because the Theft Protocol ensures that the
	 * victim cannot return to processing the frame until it has checked
	 * whether this frame has been stolen. If the frame has been stolen,
	 * the victim must grab the lock on itself. So therefore we must
	 * ensure that this method is called by the thief before it releases 
	 * the lock on the victim. 
	 * 
	 * Note that the victim is by definition the owner of this closure.
	 * ownerReadyQueue must not be null.
	 *
	 */
	final void copyFrame(Worker w) {
		assert ownerReadyQueue.lockOwner==w;
		frame = frame.copy();
	}
	
	protected boolean done = false;
	
	public boolean isDone() { return done;}
	
	public RuntimeException getException() { return null;}
	
	/**
	 * Invoked on completion of the computation associated with this closure. 
	 * May be overridden by client code. Note: This method may be invoked more than once
	 * by the scheduler.
	 *
	 */
	public void completed() {
		done = true;
	}
	
	// At most one of the following pairs of methods should be overridden by subclassing
	// closures. No pair may be overridden if the closure does not have an associated
	// return value. These methods are not abstract so that Closure can be used directly
	// when there is no reason to subclass it.
	public void setResultInt(int x) { throw new UnsupportedOperationException(); }
	public void accumulateResultInt(int x) { throw new UnsupportedOperationException();}
	public int resultInt() { assert false; throw new UnsupportedOperationException();}
	
	public void setResultFloat(float x) {throw new UnsupportedOperationException();}
	public void accumulateResultFloat(float x) { throw new UnsupportedOperationException();}
	public float resultFloat() { throw new UnsupportedOperationException();}
	
	public void setResultLong(long x) {throw new UnsupportedOperationException();}
	public void accumulateResultLong(long x) { throw new UnsupportedOperationException();}
	public long resultLong() { throw new UnsupportedOperationException();}
	
	public void setResultDouble(double x) {throw new UnsupportedOperationException();}
	public void accumulateResultDouble(double x) { throw new UnsupportedOperationException();}
	public double resultDouble() { throw new UnsupportedOperationException();}
	
	public void setResultObject(Object x) {throw new UnsupportedOperationException();}
	public Object resultObject() { throw new UnsupportedOperationException();}
	public boolean requiresGlobalQuiescence() { return false; }
	

}
