/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

import harness.x10Test;
import x10.util.concurrent.AtomicBoolean;

/**
 * Simple test cases for AtomicBoolean API functions.
 */
public class AtomicBooleanTest extends x10Test {

    public def run():Boolean  {
        val ab = new AtomicBoolean(true);

        var success:Boolean = ab.compareAndSet(false, false);
	chk(!success && ab.get() == true); // compareAndSet should have failed, value still true

        success = ab.compareAndSet(true, false);
	chk(success && ab.get() == false); // compareAndSet should have succeeded; value is now false.

	// Now check that serialization works
	val ab2 = new AtomicBoolean();
	ab2.set(true);	
        chk(ab2.get());
	at (Place.places().next(here)) {
	    chk(ab2.get());
	    ab2.set(false);
	    chk(!ab2.get());
        }
        chk(ab2.get()); // original should not have been changed by update of copy inside of at

        return true;		
    }

    public static def main(Rail[String]) {
        new AtomicBooleanTest().execute();
    }
}
