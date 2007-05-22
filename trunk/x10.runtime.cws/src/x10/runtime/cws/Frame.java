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
 * @author vj 04/18/07
 *
 */
public class Frame {

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

}
