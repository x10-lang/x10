package x10.lang;

/**
 * Implementation of X10 Clocks.  There are some differences between
 * this implementation and what the spec says.  Most notably, clocks
 * are not a final class but an interface.  This is to allow the compiler
 * to generate subclasses that use the delegate pattern in order to
 * implement clocked final variables.  In essence, the compiler creates
 * a subclass of Clock.Delegate that contains the clocked final variables,
 * accessor methods and an advance method.  Then, instead of the original
 * clock the subclass of Clock.Delegate can be passed around to give
 * access to the clocked final variables.  Note that the compiler needs
 * to mangle a bit with the field accesses to ensure that reads read from
 * one set of variables (current) whereas writes go to another set (next).
 * The Clock.Delegate.advance() implementations merely copy the values from
 * the next set to the current set.
 * 
 * In the Clock interface methods that map to reserved words in the 
 * X10 source language are prefixed with a 'do' since some of them 
 * conflict with reserved words in Java.
 * 
 * @author Christian Grothoff
 */
public interface Clock {

    /**
     * Register the current activity with this clock.
     */
    public void register();
    
    /**
     * Execute the given activity.  The clock will not advance
     * into the next phase until the given activity and all
     * activities transitively started by a have completed.
     * 
     * @param a an activity to run; the activity is started here
     *  (Runtime.here()!) and the clock will not advance until
     *  the activity completes.
     */
    public void doNow(Activity a);
    
    /**
     * Notify the clock that this activity has completed its
     * clocked activities in this cycle, that is the current
     * activity will not issue any further calls to 'now' until
     * it calls 'next'.
     *
     */
    public void doContinue();
    
    /**
     * Drop this activity from the clock.  Afterwards the
     * activity may no longer use continue or now on this clock.
     * Other activities will no longer be blocked waiting for 
     * the current activity to complete the phase.  
     * 
     * @return true if the activity has already dropped this
     *   clock (or if it never was registered).
     */
    public boolean drop();
    
    /**
     * Block until all clocks that this activity is registered with
     * have called continue (or next since next implies continue on
     * all registered clocks). 
     * 
     * Note that the semantics of this implementation are different
     * than what is stated in the X10 Report in that the programmer
     * does not specify the clocks but next applies to all clocks
     * that the activity is registered with.
     */
    public void doNext();

    /**
     * Register a callback that is to be called whenever the clock
     * advances into the next phase.
     * 
     * @param al the listener to notify
     */
    public void registerAdvanceListener(AdvanceListener al);
    
    /**
     * Callback method used by the Clock to notify all listeners
     * that the Clock is advancing into the next phase.
     * 
     * @author Christian Grothoff
     */
    public interface AdvanceListener {
        public void notifyAdvance();
    }
    
    /**
     * Abstract parent class of all subclasses that implement clocked
     * final variables.  Note that this class is intentionally package
     * scoped since it is not supposed to be visible to the application
     * programmer. 
     *   
     * The compiler synthesizes public (!) subclasses of this Delegate 
     * class for
     * each clocked final variable (in practice, there only needs to
     * be one subclass for each type of clocked final variable).  
     * These subclasses contain two public (!) fields (that's why the
     * class needs to be public), the 'current' value and the 'future' value.  Each time the program
     * reads the current value of a clocked final field the X10 compiler
     * rewrites the access to 'this.current'.  Each time the program 
     * writes the next value of a clocked final field the X10 compiler
     * rewrites the access to 'this.next'.  Both fields are declared
     * to have the appropriate type of the specific clocked final
     * variable.  The compiler generates an 'advance()' method which 
     * merely assigns 'current = next;'.  Note that the X10 run-time is
     * expected to instrument the advance() method further with appropriate
     * barrier code to allow for clocked-final related optimizations.
     *  
     * Example (for a synthetic subclass for 'int' clocked finals):
     * <code>
     * public final IntFinal extends Clock.Final_ {
     *   public int current;
     *   public int next;
     *   public IntFinal(Clock c, int start) { super(c); current = next = start; }
     *   void advance() { current = next; }
     * }
     * </code>
     * 
     * Suppose the compiler encounters the following X10 code:
     * <code>
     * clock c = new clock();
     * clocked(c) final int foo = 42;
     * next(foo) = 1;
     * next c;
     * print(foo);
     * </code>
     * 
     * With the IntFinal class from before, the code would be translated
     * to the following Java code:
     * 
     * <code>
     * Clock c = Runtime._.makeClock();
     * IntFinal foo = new IntFinal(c, 42);
     * foo.next = 1;
     * c.next();
     * print(foo.current);
     * </code>
     * 
     * 
     * Note: this is an inner class of the Clock interface for two 
     * reasons.  First, the class integrates tightly with the Clock
     * interface.  Second, it must live in x10.lang because the
     * compiler directly accesses it (it is NOT interchangable since
     * the compiler refers to the methods and class name directly,
     * just like Runtime/Future, etc.).  But it is also not a public
     * API (see: package scoped), so I wanted to reduce its visibility
     * by not putting it into its own top-level source file.  
     * 
     * @author Christian Grothoff
     */
    static abstract class Final_ {

        Final_(Clock c) {
            c.registerAdvanceListener(new AdvanceListener() {
                    public void notifyAdvance() {
                        advance();
                    }
                   });
        }

        /**
         * Notification method called when we're advancing
         * into the next phase.  Compiler generated subclasses
         * are expected to override advance in order to implement
         * the switch operation for clocked final variables.
         */
        abstract void advance();
        
    } // end of Clock.Delegate
    
} // end of Clock