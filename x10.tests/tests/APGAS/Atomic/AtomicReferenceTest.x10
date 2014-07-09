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
import x10.util.concurrent.AtomicReference;

/**
 * Simple test cases for AtomicReference API functions.
 */
public class AtomicReferenceTest extends x10Test {

    public def run():Boolean  {
	val obj1 = "abc";
	val obj2 = "def";
	val obj3 = "ghi";
	//val obj3 = "ghi".toString(); // workaround for javac bug. see comment in XTENLANG-2006.
        val ref = AtomicReference.newAtomicReference[String](obj1);

        var success:Boolean = ref.compareAndSet(obj2, obj3);
	chk(!success && ref.get() == obj1); // compareAndSet should have failed, value still true

        success = ref.compareAndSet(obj1, obj3);
	chk(success && ref.get() == obj3); // compareAndSet should have succeeded; value is now false.

	// Now check that serialization works
	at (Place.places().next(here)) {
	    chk(ref.get().equals(obj3));
	    chk(ref.get() == obj3);
	    ref.set("xyz");
	    chk(ref.get().equals("xyz"));
        }
        chk(ref.get() == obj3); // original should not have been changed by update of copy inside of at

        return true;		
    }

    public static def main(Rail[String]) {
        new AtomicReferenceTest().execute();
    }
}
