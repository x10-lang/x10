/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.runtime.clock;

import x10.core.Value;

/**
 * The representation of clocks in X10.
 *
 * In addition to the methods supported below, clocks are also used in
 * the following statements:
 *     next;
 *     clocked (c1,..., cn) Stm
 *     now (c) Stm
 * Handtranslated from the X10 source in x10/lang/clock.x10 by vj
 * @author Christoph von Praun
 * @author vj
 */
abstract public class AbstractClock extends Value {

	protected AbstractClock() {}

	/**
	 * An activity calls this method to tell the clock that is done
	 * with whatever it intended to do during this phase of the clock.
	 * The activity will not post any other statement for execution
	 * in this phase of the clock.
	 */
	abstract public void resume();

	/**
	 * An activity calls this method to tell the clock that it is no
	 * longer interested in interacting with the clock. It will no
	 * longer call now or resume on this clock. The activity is
	 * considered de-registered from the clock after this method
	 * returns.
	 */
	abstract public void drop();


	/**
	 * An activity may call this method to determine whether it is
	 * registered with this clock or not.
	 */
	abstract public boolean registered();
	abstract public boolean dropped();

	/**
	 * Block until all clocks that this activity is registered with
	 * have called continue (or next since next implies continue on
	 * all registered clocks).
	 *
	 * This method is used internally by the Runtime and
	 * should not be visible for clients.  Clients should use
	 * Runtime.doNext().
	 */
	abstract public void doNext();

	/**
	 * Warning: Unsafe with a possibility of deadlock!
	 * Block until all clocks that this activity is registered with
	 * have called continue (or next since next implies continue on
	 * all registered clocks).
	 * @see doNext
	 */
	public void next() {
		doNext();
	}
	
}
