package x10.lang;

/**
 * The representation of clocks in X10.
 *
 * In addition to the methods supported below, clocks are also used in
 * the following statements:
 *     next;
 *     clocked (c1,..., cn) Stm
 * @author vj
 */

public value clock {
    protected this() {}
    
    public static def make(): clock = Runtime.makeClock();
    /**
     * An activity calls this method to tell the clock that is done
     * with whatever it intended to do during this phase of the clock.
     * The activity will not post any other statement for execution
     * in this phase of the clock.
     */
    abstract public def resume(): void;

    /**
     * An activity calls this method to tell the clock that it is no
     * longer interested in interacting with the clock. It will no
     * longer call now or resume on this clock. The activity is
     * considered de-registered from the clock after this method
     * returns.
     */
    abstract public def drop(): void;

    /**
     * An activity may call this method to determine whether it is
     * registered with this clock or not.
     */
    abstract public def registered(): boolean;
    
    /** Returns true iff this activity was registered on this clock
     * and has since dropped it.
     */
    abstract public def dropped(): boolean;

    /**
     * Warning: Unsafe with a possibility of deadlock!
     * Block until all clocks that this activity is registered with
     * have called continue (or next since next implies continue on
     * all registered clocks).
     * @see doNext
     */
    public abstract def next(): void; 
    
}
