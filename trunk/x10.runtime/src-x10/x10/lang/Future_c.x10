/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.lang;

import x10.util.GrowableRail;

/**
 * The representation of an X10 future expression.
 * @author tardieu
 */
public class Future_c[T] extends Future[T] {
    /**
     * Latch for signaling and wait
     */
    private global val latch = new Latch();

    /**
     * Set if the activity terminated with an exception.
     * Can only be of type Error or RuntimeException
     */
    private global val exception = new GrowableRail[Throwable]();

    private global val result:GrowableRail[T];

    private global val eval:()=>T;

    public def this(eval:()=>T) {
        this.eval = eval;
        result = new GrowableRail[T]();
    }

    private global def result()= result as GrowableRail[T]!;
    public global def forced():boolean = at (latch) latch();

    public global def apply():T = force();

    public global def force():T {
        return at (latch) {
            latch.await();
            if (exception.length() > 0) {
                val e = exception(0);
                if (e instanceof Error)
                    throw e as Error;
                if (e instanceof RuntimeException)
                    throw e as RuntimeException;
                assert false;
            }
            result()(0)
        };
    }

    def run():Void {
        try {
            finish result().add(eval());
            latch.release();
        } catch (t:Throwable) {
            exception.add(t);
            latch.release();
        }
    }

    // [DC] The correct thing to do here is pull the name from the closure
    //public global def toString():String = name;
}

// vim:shiftwidth=4:tabstop=4:expandtab
