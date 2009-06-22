/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Oct 13, 2004
 */
package x10.compilergenerated;


import x10.lang.Object;
import x10.runtime.Clock;


/**
 * Abstract parent class of all subclasses that implement clocked
 * final variables.  Note that this class is intentionally package
 * scoped since it is not supposed to be visible to the application
 * programmer. 
 *   
 * The plan is to have the X10 compiler synthesizes public (!) 
 * subclasses of this Delegate class for each clocked final 
 * variable (in practice, there only needs to
 * be one subclass for each type of clocked final variable).  
 * Alternatively, it would be possible to use Java Generics instead
 * (see below).  For now, a small set of implementations is provided
 * by hand.
 *   
 * All subclasses contain two public (!) fields (that's why the
 * class needs to be public), the 'current' value and the 'future' value. 
 * Each time the program reads the current value of a clocked final 
 * field the X10 compiler rewrites the access to 'this.current'.  
 * Each time the program  writes the next value of a clocked 
 * final field the X10 compiler
 * rewrites the access to 'this.next'.  Both fields are declared
 * to have the appropriate type of the specific clocked final
 * variable.  The compiler generates an 'advance()' method which 
 * merely assigns 'current = next;'.  Note that the X10 run-time is
 * expected to instrument the advance() method further with appropriate
 * barrier code to allow for clocked-final related optimizations.
 *  
 * Example (for a synthetic subclass for 'int' clocked finals):
 * <code>
 * public final ClockedFinalInt extends ClockedFinal {
 *   public int current;
 *   public int next;
 *   public ClockedFinalInt(Clock c, int start) { 
 *     super(c); 
 *     current = next = start; 
 *   }
 *   void advance() { current = next; }
 * }
 * </code>
 * 
 * With generics (option for the future), the code would look like this:
 * <code>
 * public final ClockedFinal<T> {
 *   public T current;
 *   public T next;
 *   public ClockedFinal(Clock c, T start) { 
 *     c.register(...);
 *     current = next = start; 
 *   }
 *   void advance() { current = next; }
 * }
 * </code>
 * 
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
 * Or, with generics (which would be more expensive due to boxing
 * and unboxing for primitives and the addition of casts for objects):
 * 
 * <code>
 * Clock c = Runtime._.makeClock();
 * ClockedFinal<int> foo = new ClockedFinal<int>(c, 42);
 * foo.next = 1;
 * c.next();
 * print(foo.current);
 * </code>
 * 
 * 
 * @author Christian Grothoff, Christoph von Praun
 */
abstract class ClockedFinal extends Object {

    ClockedFinal(Clock c) {
    	/* vj: Needs to be implemented completely.
        c.registerAdvanceListener(new AdvanceListener() {
                public void notifyAdvance() {
                    advance();
                }
               });
               */
    }

    /**
     * Notification method called when we're advancing
     * into the next phase.  Compiler generated subclasses
     * are expected to override advance in order to implement
     * the switch operation for clocked final variables.
     */
    abstract void advance();
    
} // end of ClockedFinal


