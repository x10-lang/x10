/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

import harness.x10Test;

/**
 * Testing that doing an at (here) still results in the
 * copying required by the X10 2.1 onbect model
 * It creates a 10 element linked list with the data values from 1..10.
 * It then copies this list via an at, checks to see it is well-formed,
 * mutates it, checks to see the mutation works, then checks to see that
 * the original list was not mutated.
 */
public class AtCopy extends x10Test {

    public def run():Boolean {
	chk(Place.numPlaces() > 1, "Test must be run with at least 2 places");
        var passed:Boolean = true;

        var h:Link = null;
        for (i in 1n..10n) {
             h = new Link(11n-i, h);
        }
	val head = h;
	passed &= validate(head, 1n);

	passed &= at (here) mutate(head);
	passed &= validate(head, 1n);
	passed &= at (Place.places().next(here)) mutate(head);
	passed &= validate(head, 1n);

        return passed;
    }

    public def mutate(head:Link):Boolean {
	var cur:Link = head;
        var passed:boolean = true;
        passed &= validate(head, 1n);
        var count:int = 0n;
        while (cur != null) {
            cur.data += 100n;
            cur = cur.ptr;
            count++;
        }
        passed &= validate(head, 101n);
	chk(count == 10n);
	return passed;
    }

    public def validate(var cur:Link, var c:int):Boolean {
	var passed:boolean = true;
        var count:int = 0n;
	while (cur != null) {
            // Console.OUT.println("Checking "+here+". Expected "+c+" found "+cur.data+" count is "+count);
            if (cur.data != c) {
                Console.OUT.println("Validation error "+here+". Expected "+c+" but found "+cur.data +" at count = "+count);
                passed = false;
            }
            cur = cur.ptr;
            c++;
            count++;
        }
	chk(count == 10n);
        return passed;
    }

    public static def main(Rail[String]) {
        new AtCopy().execute();
    }

    static class Link {
	var ptr:Link;
	var data:int;
	def this(d:int, p:Link) { data = d; ptr = p; }
    }
}
