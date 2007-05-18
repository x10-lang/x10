package x10.runtime.cws;

/**
 * A Frame holds the PC and dirty live variables in a procedure call
 * that contains an async spawn.
 * @author vj
 *
 */
public abstract class Frame {

	public Frame() {
		super();
	}
	
	/**
	 * Returns a closure that can contain a pointer to this frame.
	 * e.g. a FibFrame will return a FibClosure.
	 * @return
	 */
	public abstract Closure makeClosure();

}
