/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2016.
 */

package x10.lang;

import x10.compiler.Inline;
import x10.util.GrowableRail;

/**
 * MultipleExceptions is used to to summarize all uncaught exceptions
 * raised during the execution of a <code>finish</code> and rethrow
 * them as a single exception when all activities controlled by the 
 * <code>finish</code> have terminated.
 */
public class MultipleExceptions(exceptions:Rail[CheckedThrowable]) extends Exception {
    public property exceptions():Rail[CheckedThrowable] = exceptions;

    public def this(es:GrowableRail[CheckedThrowable]) {
        property(es.toRail());
    }

    public def this() {
        property(null);
    }

    public def this(t:CheckedThrowable) {
        property(new Rail[CheckedThrowable](1, t));
    }

    public def printStackTrace(): void {
        for (t in exceptions) {
            t.printStackTrace();
        }
    }

    public static def make(es:GrowableRail[CheckedThrowable]):MultipleExceptions {
        if (null == es || es.isEmpty()) return null;
        return new MultipleExceptions(es);
    }

    public static def make(t:CheckedThrowable):MultipleExceptions {
        if (null == t) return null;
        return new MultipleExceptions(t);
    }

    /** 
     * Gets exceptions of the given type that are nested within this
     * instance of MultipleExceptions.
     * @param deep perform a deep traversal of the tree of MultipleExceptions
     *   associated with nested finish constructs
     * @return a rail containing only the exceptions of the given type 
     */
    public final def getExceptionsOfType[T](deep:Boolean):Rail[T] {
        val es = new GrowableRail[T]();
        for (e in exceptions) {
            if (e instanceof T) {
                es.add(e as T);
            } else if (deep && e instanceof MultipleExceptions) {
                val es2 = (e as MultipleExceptions).getExceptionsOfType[T]();
                for (e2 in es2) es.add(e2);
            }
        }

        return es.toRail();
    }

    public final def getExceptionsOfType[T]() = getExceptionsOfType[T](true);

    /** 
     * Gets a copy of this MultipleExceptions instance, with all nested
     * exceptions of the given type removed.
     * This method may be used for example is to filter all DeadPlaceExceptions
     * so that exceptions of other types can be handled separately.
     * @param deep perform a deep traversal of the tree of MultipleExceptions
     *   associated with nested finish constructs
     * @return a new MultipleExceptions, filtering out all exceptions of the given type 
     */
    public final def filterExceptionsOfType[T](deep:Boolean):MultipleExceptions {
        val es = new GrowableRail[CheckedThrowable]();
        for (e in exceptions) {
            if (deep && e instanceof MultipleExceptions) {
                val me = (e as MultipleExceptions).filterExceptionsOfType[T]();
                if (me != null) es.add(me);
            } else if (! (e instanceof T)) {
                es.add(e);
            }
        }

        return MultipleExceptions.make(es);
    }

    public final def filterExceptionsOfType[T]() = filterExceptionsOfType[T](true);


    /**
     * Gets a copy of this MultipleExceptions instance where the
     * nesting of MultipleExceptions is flatten. It means that
     * exceptions that was included inside a nested MultipleExceptions
     * are now in the toplevel MultipleExceptions dans that the
     * toplevel MultipleExceptions do not contains other
     * MultipleExceptions.
     * @param me the MultipleExceptions to flatten
     * @return a new MultipleExceptions without neted MultipleExceptions
     */
    public final def flatten():MultipleExceptions {
        val exns = new GrowableRail[CheckedThrowable]();
        flattenAux(this, exns);
        return MultipleExceptions.make(exns);
    }

    private static def flattenAux(me: MultipleExceptions, acc: GrowableRail[CheckedThrowable]):void {
        for (e in me.exceptions) {
            if (e instanceof MultipleExceptions) {
                flattenAux(e as MultipleExceptions, acc);
            } else {
                acc.add(e);
            }
        }
    }


    private final def splitExceptionsOfType[T](deep:Boolean,
                                               accT: GrowableRail[T], accNotT: GrowableRail[CheckedThrowable]) {
        for (e in exceptions) {
            if (deep && e instanceof MultipleExceptions) {
                (e as MultipleExceptions).splitExceptionsOfType[T](deep, accT, accNotT);
            } else if (e instanceof T) {
                accT.add(e as T);
            } else {
                accNotT.add(e);
            }
        }
    }

    // /**
    //  * try control structure that catches MultipleExceptions and
    //  * executes the handler on the flattened version of the catched
    //  * MultipleExceptions. For example, in the following program, the
    //  * MultipleExceptions me contains the exceptions E1, E2 and E3
    //  * (not E1 and a MultipleExceptions):
    //  *
    //  *   MultipleExceptions.try {
    //  *       finish {
    //  *           async { throw new Exception("Exn 1"); }
    //  *           async finish {
    //  *               async { throw new Exception("Exn 2"); }
    //  *               async { throw new Exception("Exn 3"); }
    //  *           }
    //  *       }
    //  *   } catch (me: MultipleExceptions) {
    //  *       Console.OUT.println(me.exceptions);
    //  *   }
    //  *
    //  * @param body the body of the try block
    //  * @param handler the body of the exception handler
    //  *
    //  */
    // public static @Inline operator try (body: () => void,
    //                             handler: (MultipleExceptions) => void) {
    //     try { body(); }
    //     catch (me: MultipleExceptions) {
    //         handler (me.flatten());
    //     }
    // }

    // /**
    //  * try control structure with a finally handler that catches
    //  * MultipleExceptions and executes the handler on the flattened
    //  * version of the catched MultipleExceptions.
    //  *
    //  * @param body the body of the try block
    //  * @param handler the body of the exception handler
    //  * @param finallyBlock the body of finally block
    //  *
    //  */
    // public static @Inline operator try (body: () => void,
    //                             handler: (MultipleExceptions) => void,
    //                             finallyBlock: () => void) {
    //     try { body(); }
    //     catch (me: MultipleExceptions) { handler (me.flatten()); }
    //     finally { finallyBlock(); }
    // }


    /**
     * try control structure that catches MultipleExceptions and
     * executes the handler on the rail containing all the exceptions
     * of type E that was in the MultipleExceptions. The remaining
     * exceptions are re-thrown in a MultipleExceptions. For example,
     * the following code prints the number of exceptions of type
     * UnsupportedOperationException. Here, the value 2 is printed and
     * the exception of type IllegalOperationException is re-thrown in
     * a MultipleExceptions.
     *
     *    MultipleExceptions.try(true) {
     *      finish {
     *        async { throw new UnsupportedOperationException(); }
     *        finish {
     *          async { throw new UnsupportedOperationException(); }
     *          async { throw new IllegalOperationException(); }
     *        }
     *      }
     *    } catch (t: Rail[UnsupportedOperationException]) {
     *        Console.OUT.println(t.size);
     *    }
     *
     * @param deep perform a deep traversal of the tree of MultipleExceptions
     * @param body the body of the try block
     * @param handler the body of the exception handler
     *
     */
    public static @Inline operator try[E] (deep: Boolean,
                                   body: () => void,
                                   handler: (Rail[E]) => void) {
        try { body(); }
        catch (me: MultipleExceptions) {
            val exns = new GrowableRail[E]();
            val others = new GrowableRail[CheckedThrowable]();
            me.splitExceptionsOfType[E](deep, exns, others);
            if (exns.size() > 0) { handler(exns.toRail()); }
            if (others.size() > 0) { throw new MultipleExceptions(others); }
        }
    }

    /**
     * try control structure with a finally block that catches
     * MultipleExceptions and executes the handler on the rail
     * containing all the exceptions of type E that was in the
     * MultipleExceptions. The remaining exceptions are re-thrown
     * in a MultipleExceptions.
     *
     * @param deep perform a deep traversal of the tree of MultipleExceptions
     * @param body the body of the try block
     * @param handler the body of the exception handler
     * @param finallyBlock the body of finally block
     *
     */
    public static @Inline operator try[E] (deep: Boolean,
                                   body: () => void,
                                   handler: (Rail[E]) => void,
                                   finallyBlock: () => void) {
        try { body(); }
        catch (me: MultipleExceptions) {
            val exns = new GrowableRail[E]();
            val others = new GrowableRail[CheckedThrowable]();
            me.splitExceptionsOfType[E](deep, exns, others);
            if (exns.size() > 0) { handler(exns.toRail()); }
            if (others.size() > 0) { throw new MultipleExceptions(others); }
        }
        finally { finallyBlock(); }
    }

    /**
     * try control structure that catches MultipleExceptions and
     * executes the handler on the rail containing all the exceptions
     * of type E that was in the MultipleExceptions and the nested
     * nested ones. The remaining exceptions are re-thrown in a
     * MultipleExceptions.
     *
     * @param body the body of the try block
     * @param handler the body of the exception handler
     *
     */
    public static @Inline operator try[E] (body: () => void,
                                   handler: (Rail[E]) => void) {
        MultipleExceptions.operator try[E](true, body, handler);
    }

    /**
     * try control structure with a finally block that catches
     * MultipleExceptions and executes the handler on the rail
     * containing all the exceptions of type E that was in the
     * MultipleExceptions and the nested nested ones. The remaining
     * exceptions are re-thrown in a MultipleExceptions.
     *
     * @param body the body of the try block
     * @param handler the body of the exception handler
     * @param finallyBlock the body of finally block
     *
     */
    public static @Inline operator try[E] (body: () => void,
                                   handler: (Rail[E]) => void,
                                   finallyBlock: () => void) {
        MultipleExceptions.operator try[E](true, body, handler, finallyBlock);
    }


    /**
     * try control structure that catches MultipleExceptions and
     * executes the first handler on the rail containing all the
     * exceptions of type E1 and the second handler on the rail
     * containing all the exceptions of type E2 that was in the
     * MultipleExceptions. The remaining exceptions are re-thrown in a
     * MultipleExceptions.
     *
     * @param deep perform a deep traversal of the tree of MultipleExceptions
     * @param body the body of the try block
     * @param handler1 the body of the exception handler
     * @param handler2 the body of the exception handler
     *
     */
    public static @Inline operator try[E1,E2] (deep: Boolean,
                                       body: () => void,
                                       handler1: (Rail[E1]) => void,
                                       handler2: (Rail[E2]) => void) {
        try { body(); }
        catch (me: MultipleExceptions) {
            val exns1 = new GrowableRail[E1]();
            val others1 = new GrowableRail[CheckedThrowable]();
            me.splitExceptionsOfType[E1](deep, exns1, others1);
            if (exns1.size() > 0) { handler1(exns1.toRail()); }
            val exns2 = new GrowableRail[E2]();
            val others2 = new GrowableRail[CheckedThrowable]();
            (new MultipleExceptions(others1)).splitExceptionsOfType[E2](deep, exns2, others2);
            if (exns2.size() > 0) { handler2(exns2.toRail()); }
            if (others2.size() > 0) { throw new MultipleExceptions(others2); }
        }
    }


    /**
     * try control structure with a finally block that catches
     * MultipleExceptions and executes the first handler on the rail
     * containing all the exceptions of type E1 and the second handler
     * on the rail containing all the exceptions of type E2 that was
     * in the MultipleExceptions. The remaining exceptions are
     * re-thrown in a MultipleExceptions.
     *
     * @param deep perform a deep traversal of the tree of MultipleExceptions
     * @param body the body of the try block
     * @param handler1 the body of the exception handler
     * @param handler2 the body of the exception handler
     *
     */
    public static @Inline operator try[E1,E2] (deep: Boolean,
                                       body: () => void,
                                       handler1: (Rail[E1]) => void,
                                       handler2: (Rail[E2]) => void,
                                       finallyBlock: () => void) {
        try { body(); }
        catch (me: MultipleExceptions) {
            val exns1 = new GrowableRail[E1]();
            val others1 = new GrowableRail[CheckedThrowable]();
            me.splitExceptionsOfType[E1](deep, exns1, others1);
            if (exns1.size() > 0) { handler1(exns1.toRail()); }
            val exns2 = new GrowableRail[E2]();
            val others2 = new GrowableRail[CheckedThrowable]();
            (new MultipleExceptions(others1)).splitExceptionsOfType[E2](deep, exns2, others2);
            if (exns2.size() > 0) { handler2(exns2.toRail()); }
            if (others2.size() > 0) { throw new MultipleExceptions(others2); }
        } finally { finallyBlock(); }
    }

    /**
     * try control structure that catches MultipleExceptions and
     * executes the first handler on the rail containing all the
     * exceptions of type E1 and the second handler on the rail
     * containing all the exceptions of type E2 that was in the
     * MultipleExceptions and the nested nested ones. The remaining
     * exceptions are re-thrown in a MultipleExceptions.
     *
     * @param body the body of the try block
     * @param handler1 the body of the exception handler
     * @param handler2 the body of the exception handler
     *
     */
    public static @Inline operator try[E1,E2] (body: () => void,
                                       handler1: (Rail[E1]) => void,
                                       handler2: (Rail[E2]) => void) {
        MultipleExceptions.operator try[E1,E2](true, body, handler1, handler2);
    }

    /**
     * try control structure with a finally block that catches
     * MultipleExceptions and executes the first handler on the rail
     * containing all the exceptions of type E1 and the second handler
     * on the rail containing all the exceptions of type E2 that was
     * in the MultipleExceptions and the nested nested ones. The
     * remaining exceptions are re-thrown in a MultipleExceptions.
     *
     * @param body the body of the try block
     * @param handler1 the body of the exception handler
     * @param handler2 the body of the exception handler
     *
     */
    public static @Inline operator try[E1,E2] (body: () => void,
                                       handler1: (Rail[E1]) => void,
                                       handler2: (Rail[E2]) => void,
                                       finallyBlock: () => void) {
        MultipleExceptions.operator try[E1,E2](true, body, handler1, handler2, finallyBlock);
    }


    /**
     * try control structure that catches MultipleExceptions and
     * executes the handler on each exception of type E that was in
     * the MultipleExceptions. The remaining exceptions are re-thrown
     * in a MultipleExceptions. For example, the following code prints
     * twice the message "UnsupportedOperationException catched" and
     * the exception of type IllegalOperationException is re-thrown in
     * a MultipleExceptions.
     *
     *    MultipleExceptions.try(true) {
     *      finish {
     *        async { throw new UnsupportedOperationException(); }
     *        finish {
     *          async { throw new UnsupportedOperationException(); }
     *          async { throw new IllegalOperationException(); }
     *        }
     *      }
     *    } catch (UnsupportedOperationException) {
     *        Console.OUT.println("UnsupportedOperationException catched");
     *    }
     *
     * @param deep perform a deep traversal of the tree of MultipleExceptions
     * @param body the body of the try block
     * @param handler the body of the exception handler
     *
     */
    public static @Inline operator try[E] (deep: Boolean,
                                   body: () => void,
                                   handler: (E) => void) {
        try { body(); }
        catch (me: MultipleExceptions) {
            val exns = new GrowableRail[E]();
            val others = new GrowableRail[CheckedThrowable]();
            me.splitExceptionsOfType[E](deep, exns, others);
            for (e:E in exns.toRail()) {
                handler(e);
            }
            if (others.size() > 0) { throw new MultipleExceptions(others); }
        }
    }

    /**
     * try control structure with a finally block that catches
     * MultipleExceptions and executes the handler on each exception
     * of type E that was in the MultipleExceptions. The remaining
     * exceptions are re-thrown in a MultipleExceptions.
     *
     * @param deep perform a deep traversal of the tree of MultipleExceptions
     * @param body the body of the try block
     * @param handler the body of the exception handler
     * @param finallyBlock the body of finally block
     *
     */
    public static @Inline operator try[E] (deep: Boolean,
                                   body: () => void,
                                   handler: (E) => void,
                                   finallyBlock: () => void) {
        try { body(); }
        catch (me: MultipleExceptions) {
            val exns = new GrowableRail[E]();
            val others = new GrowableRail[CheckedThrowable]();
            me.splitExceptionsOfType[E](deep, exns, others);
            for (e:E in exns.toRail()) {
                handler(e);
            }
            if (others.size() > 0) { throw new MultipleExceptions(others); }
        }
        finally { finallyBlock(); }
    }


    /**
     * try control structure that catches MultipleExceptions and
     * executes the handler on each exception of type E that was in
     * the MultipleExceptions and the nested nested ones. The
     * remaining exceptions are re-thrown in a MultipleExceptions.
     *
     * @param body the body of the try block
     * @param handler the body of the exception handler
     *
     */
    public static @Inline operator try[E] (body: () => void,
                                   handler: (E) => void) {
        MultipleExceptions.operator try[E](true, body, handler);
    }

    /**
     * try control structure with a finally block that catches
     * MultipleExceptions and executes the handler on each exception
     * of type E that was in the MultipleExceptions and the nested
     * nested ones. The remaining exceptions are re-thrown in a
     * MultipleExceptions.
     *
     * @param body the body of the try block
     * @param handler the body of the exception handler
     * @param finallyBlock the body of finally block
     *
     */
    public static @Inline operator try[E] (body: () => void,
                                   handler: (E) => void,
                                   finallyBlock: () => void) {
        MultipleExceptions.operator try[E](true, body, handler, finallyBlock);
    }


    /**
     * try control structure that catches MultipleExceptions and
     * executes the first handler on each exception of type E1 and the
     * second handler on on each exception of type E2 that was in the
     * MultipleExceptions. The remaining exceptions are re-thrown in a
     * MultipleExceptions.
     *
     * @param deep perform a deep traversal of the tree of MultipleExceptions
     * @param body the body of the try block
     * @param handler1 the body of the exception handler
     * @param handler2 the body of the exception handler
     *
     */
    public static @Inline operator try[E1,E2] (deep: Boolean,
                                       body: () => void,
                                       handler1: (E1) => void,
                                       handler2: (E2) => void) {
        try { body(); }
        catch (me: MultipleExceptions) {
            val exns1 = new GrowableRail[E1]();
            val others1 = new GrowableRail[CheckedThrowable]();
            me.splitExceptionsOfType[E1](deep, exns1, others1);
            for (e in exns1.toRail()) {
                handler1(e);
            }
            val exns2 = new GrowableRail[E2]();
            val others2 = new GrowableRail[CheckedThrowable]();
            (new MultipleExceptions(others1)).splitExceptionsOfType[E2](deep, exns2, others2);
            for (e in exns2.toRail()) {
                handler2(e);
            }
            if (others2.size() > 0) { throw new MultipleExceptions(others2); }
        }
    }


    /**
     * try control structure with a finally block that catches
     * MultipleExceptions and executes the first handler on each
     * exception of type E1 and the second handler on each exception
     * of type E2 that was in the MultipleExceptions. The remaining
     * exceptions are re-thrown in a MultipleExceptions.
     *
     * @param deep perform a deep traversal of the tree of MultipleExceptions
     * @param body the body of the try block
     * @param handler1 the body of the exception handler
     * @param handler2 the body of the exception handler
     *
     */
    public static @Inline operator try[E1,E2] (deep: Boolean,
                                       body: () => void,
                                       handler1: (E1) => void,
                                       handler2: (E2) => void,
                                       finallyBlock: () => void) {
        try { body(); }
        catch (me: MultipleExceptions) {
            val exns1 = new GrowableRail[E1]();
            val others1 = new GrowableRail[CheckedThrowable]();
            me.splitExceptionsOfType[E1](deep, exns1, others1);
            for (e in exns1.toRail()) {
                handler1(e);
            }
            val exns2 = new GrowableRail[E2]();
            val others2 = new GrowableRail[CheckedThrowable]();
            (new MultipleExceptions(others1)).splitExceptionsOfType[E2](deep, exns2, others2);
            for (e in exns2.toRail()) {
                handler2(e);
            }
            if (others2.size() > 0) { throw new MultipleExceptions(others2); }
        } finally { finallyBlock(); }
    }

    /**
     * try control structure that catches MultipleExceptions and
     * executes the first handler on each exception of type E1 and the
     * second handler on each exception of type E2 that was in the
     * MultipleExceptions and the nested nested ones. The remaining
     * exceptions are re-thrown in a MultipleExceptions.
     *
     * @param body the body of the try block
     * @param handler1 the body of the exception handler
     * @param handler2 the body of the exception handler
     *
     */
    public static @Inline operator try[E1,E2] (body: () => void,
                                       handler1: (E1) => void,
                                       handler2: (E2) => void) {
        MultipleExceptions.operator try[E1,E2](true, body, handler1, handler2);
    }

    /**
     * try control structure with a finally block that catches
     * MultipleExceptions and executes the first handler on each
     * exception of type E1 and the second handler on each exception
     * of type E2 that was in the MultipleExceptions and the nested
     * nested ones. The remaining exceptions are re-thrown in a
     * MultipleExceptions.
     *
     * @param body the body of the try block
     * @param handler1 the body of the exception handler
     * @param handler2 the body of the exception handler
     *
     */
    public static @Inline operator try[E1,E2] (body: () => void,
                                       handler1: (E1) => void,
                                       handler2: (E2) => void,
                                       finallyBlock: () => void) {
        MultipleExceptions.operator try[E1,E2](true, body, handler1, handler2, finallyBlock);
    }


}
