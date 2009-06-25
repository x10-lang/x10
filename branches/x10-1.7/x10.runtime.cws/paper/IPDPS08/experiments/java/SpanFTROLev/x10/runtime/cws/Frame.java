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


/**
 * A Frame holds the PC and dirty live variables in a procedure call
 * that contains an async spawn. 
 * API note: Code written by users of the work-stealing API will typically
 * extend this class for every procedure containing async spawns and finishes.
 * 
 * The design of this library is based on the Cilk runtime, developed by the Cilk
 * group at MIT.
 * 
 * @author vj 04/18/07
 *
 */
public class Frame implements Cloneable, Executable {
	
	public Frame() {
		super();
	}
	
	/**
	 * Returns a closure that can contain a pointer to this frame.
	 * e.g. a FibFrame will return a FibClosure. The executeInlet
	 * method of this closure must know where in the frame to 
	 * place its result.
	 * @return -- a closure for executing the code in this method
	 * instance from the position specified by this frame.
	 * Should be overridden by subclasses.
	 */
	public  Closure makeClosure() { return null;}
	
	 /**
	  * Set the Outlet object on c so that it can supply
	  * its result into the right field of the given frame.
	  * Should be overridden by subclasses.
	  * @param c -- The closure whose outlet must be set.
	  */
	public  void setOutletOn(Closure c) {}
	
	/**
	 * To be implemented in subclasses to support in place update of frames
	 * on the frame stack.
	 * @param x -- the new value for the distinguished slot in the frame.
	 */
	public void setInt(int x) { throw new UnsupportedOperationException();}
	public void setObject(Object x) { throw new UnsupportedOperationException();}
	
	public Frame copy() {
		try {
		return (Frame) clone();
		} catch (CloneNotSupportedException z) {
			// but it is?
			assert false;
			return null;
		}
	}

	/**
	 * Should be overridden by subclasses. Specifies the work associated
	 * with this frame for globally quiescent computations.
	 * @param w
	 */
	public void compute(Worker w) throws StealAbort {
		
	}
	/**
	 * If the job is globally quiescent, closures are not needed.
	 * In this case the frame will directly specify the work to be done.
	 */
	public final Executable execute(Worker w) {
		Cache c = w.cache;
		c.pushFrame(this);
		c.resetExceptionPointer(w);
		try {
			compute(w);
			//c.popFrame();
		} catch (StealAbort z) {
			// do nothing. the exception has done its work
			// unwinding the call stack.
		}
		return null;
	}
}
