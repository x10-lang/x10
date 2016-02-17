import x10.util.*;

class MyMultipleExceptions {

    private static def flatten(me: MultipleExceptions, acc: GrowableRail[CheckedThrowable]) {
        for (e in me.exceptions) {
            if (e instanceof MultipleExceptions) {
                flatten(e as MultipleExceptions, acc);
            } else {
                acc.add(e);
            }
        }
    }

    /**
     * Remove the nesting of MultipleExceptions. For example, in the
     * following program, the MultipleExceptions me contains the
     * exceptions E1, E2 and E3:
     *
     *   MultipleExceptions.try {
     *       finish {
     *           async { throw new Exception("Exn 1"); }
     *           async finish {
     *               async { throw new Exception("Exn 2"); }
     *               async { throw new Exception("Exn 3"); }
     *           }
     *       }
     *   } catch (me: MultipleExceptions) {
     *       Console.OUT.println("List of all raised exceptions: "+me.exceptions);
     *   }
     *
     */
    public static operator try (body: () => void,
                                handler: (MultipleExceptions) => void) {
        try { body(); }
        catch (me: MultipleExceptions) {
            val exns = new GrowableRail[CheckedThrowable]();
            flatten(me, exns);
            handler (new MultipleExceptions(exns));
        }
    }

    /** Execute the handler on each exception of type E.
	All exceptions that are not of type E are re-thrown. */
    public static operator try[E] (body: () => void,
				   handler: (E) => void){E <: CheckedThrowable} {
        try { body(); }
        catch (me: MultipleExceptions) {
	    for (e in me.getExceptionsOfType[E]()) {
	    	handler(e);
	    }
	    val others = me.filterExceptionsOfType[E]();
	    if (others != null) { throw others; }
        }
    }

    /** Execute the first handler on each exception of type E1
	and the second handler on each exception of type E2.
	All exceptions that are not of type E1 or E2 are re-thrown. */
    public static operator try[E1, E2] (body: () => void,
					handler1: (E1) => void,
					handler2: (E2) => void){E1 <: CheckedThrowable,
				                                E2 <: CheckedThrowable} {
        try { body(); }
        catch (me: MultipleExceptions) {
	    for (e1 in me.getExceptionsOfType[E1]()) {
	    	handler1(e1);
	    }
	    val others = me.filterExceptionsOfType[E1]();
	    for (e2 in others.getExceptionsOfType[E2]()) {
	    	handler2(e2);
	    }
	    val remaining = me.filterExceptionsOfType[E2]();
	    if (remaining != null) { throw remaining; }
        }
    }

    /** Execute the first handler on each exception of type E1
	the second handler on each exception of type E2,
	and the third handler on each exception of type E3.
	All exceptions that are not of type E1 or E2 or E3 are re-thrown. */
    public static operator try[E1, E2, E3] (body: () => void,
					handler1: (E1) => void,
					handler2: (E2) => void,
					handler3: (E3) => void){E1 <: CheckedThrowable,
				                                E2 <: CheckedThrowable,
                                                                E3 <: CheckedThrowable} {
        try { body(); }
        catch (me: MultipleExceptions) {
	    for (e1 in me.getExceptionsOfType[E1]()) {
	    	handler1(e1);
	    }
	    val notE1 = me.filterExceptionsOfType[E1]();
	    for (e2 in notE1.getExceptionsOfType[E2]()) {
	    	handler2(e2);
	    }
	    val notE2 = notE1.filterExceptionsOfType[E2]();
	    for (e3 in notE2.getExceptionsOfType[E3]()) {
	    	handler3(e3);
	    }
	    val remaining = notE2.filterExceptionsOfType[E3]();
	    if (remaining != null) { throw remaining; }
        }
    }


    public static class TMP {
    /** Execute the handler on all exceptions of type E.
	All exceptions that are not of type E are re-thrown. */
    public static operator try[E] (body: () => void,
				   handler: (Rail[E]) => void){E <: CheckedThrowable} {
        try { body(); }
        catch (me: MultipleExceptions) {
	    val exns = me.getExceptionsOfType[E]();
	    if (exns.size != 0) {
	    	handler(exns);
	    }
	    val others = me.filterExceptionsOfType[E]();
	    if (others != null) { throw others; }
        }
    }
    }

    /** Catch a MultipleExceptions that contains exactly one exception
        of type E1 and one of type E2. */
    public static operator try[E1, E2] (body: () => void,
					handler: (E1, E2) => void){E1 <: CheckedThrowable, E2 <: CheckedThrowable} {
        try { body(); }
        catch (me: MultipleExceptions) {
	    val exns = new GrowableRail[CheckedThrowable]();
            flatten(me, exns);
	    if (exns.size() != 2 ) {
		throw me;
	    }
	    val e1 = exns(0);
	    val e2 = exns(1);
	    if (e1 instanceof E1 && e2 instanceof E2) {
		handler(e1 as E1, e2 as E2);
	    } else {
		if (e2 instanceof E1 && e1 instanceof E2) {
		    handler(e2 as E1, e1 as E2);
		} else {
		    throw me;
		}
	    }
        }
    }

    public static def main(Rail[String]) {
	Console.OUT.println("** Flatten **");
	MyMultipleExceptions.try {
	    finish {
		async { throw new Exception("Exn 1"); }
		async finish {
		    async { throw new Exception("Exn 2"); }
		    async { throw new Exception("Exn 3"); }
		}
	    }
	} catch (me: MultipleExceptions) {
	    Console.OUT.println("List of all raised exceptions: "+me.exceptions);
	}

	Console.OUT.println("** Filter **");
	MyMultipleExceptions.try {
	    MyMultipleExceptions.try {
		finish {
		    async { throw new Exn1("Exn 1.1"); }
		    async finish {
			async { throw new Exn1("Exn 1.2"); }
			async { throw new Exn2("Exn 2"); }
			async { throw new Exception("Exn 3"); }
		    }
		}
	    } catch (e: Exn1) {
		Console.OUT.println("Exception of type Exn1: "+e);
	    }
	} catch (me: MultipleExceptions) {
	    Console.OUT.println("List of remaing exceptions: "+me.exceptions);
	}

	Console.OUT.println("** Filter array **");
	MyMultipleExceptions.try {
	    MyMultipleExceptions.TMP.try {
		finish {
		    async { throw new Exn1("Exn 1.1"); }
		    async finish {
			async { throw new Exn1("Exn 1.2"); }
			async { throw new Exn2("Exn 2"); }
			async { throw new Exception("Exn 3"); }
		    }
		}
	    } catch (exns: Rail[Exn1]) {
		Console.OUT.println("Exceptions of type Exn1: "+exns);
	    }
	} catch (me: MultipleExceptions) {
	    Console.OUT.println("List of remaing exceptions: "+me.exceptions);
	}

	Console.OUT.println("** Filter2 **");
	MyMultipleExceptions.try {
	    MyMultipleExceptions.try {
		finish {
		    async { throw new Exn1("Exn 1.1"); }
		    async finish {
			async { throw new Exn1("Exn 1.2"); }
			async { throw new Exn2("Exn 2"); }
			async { throw new Exception("Exn 3"); }
		    }
		}
	    } catch (e: Exn2) {
		Console.OUT.println("Exception of type Exn2: "+e);
	    } catch (e: Exn1) {
		Console.OUT.println("Exception of type Exn1: "+e);
	    }
	} catch (me: MultipleExceptions) {
	    Console.OUT.println("List of remaing exceptions: "+me.exceptions);
	}

	Console.OUT.println("** And **");
	MyMultipleExceptions.try {
	    MyMultipleExceptions.try {
		finish {
		    async { throw new Exn1("Exn 1.1"); }
		    async finish {
			// async { throw new Exn1("Exn 1.2"); }
			async { throw new Exn2("Exn 2"); }
			// async { throw new Exception("Exn 3"); }
		    }
		}
	    } catch (e1: Exn1, e2: Exn2) {
		Console.OUT.println("Exceptions of type Exn1 and Exn2: ("+e1+", "+e2+")");
	    }
	} catch (me: MultipleExceptions) {
	    Console.OUT.println("Exceptions (not Exn1 and Exn2): "+me.exceptions);
	}
    }
}


class Exn1 extends Exception {
    public def this(s: String) {
	super(s);
    }
}
class Exn2 extends Exn1 {
    public def this(s: String) {
	super(s);
    }
}
