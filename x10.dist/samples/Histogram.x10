/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

import x10.util.Random;
import x10.io.Console;

public class Histogram {

  /**
    * Compute the histogram of the array a in the rail b.
    */
    public static def run(a:Array[int](1), b:Array[int](1)) {
	finish 
	    for ([i] in a) async {
	       val bin = a(i)% b.size();
	       atomic b(bin)++;
	    }
    }

    public static def compute(N:int, S:int):boolean {
	val a = new Array[int](N, (i:int)=> i);
	val b = new Array[int](S);
	run(a, b);
	val v = b(0);
        var ok:boolean = true;
	for ([x] in b) ok &= (b(x)==v);
        return ok;
    }

    public static def main(args:Array[String](1)) {
	if (args.size != 2) {
	    Console.OUT.println("Usage: Histogram SizeOfArray Buckets");
	    return;
        }
	val N = int.parse(args(0));
	val S = int.parse(args(1));
        val ok = compute(N,S);
	if (ok) {
	    Console.OUT.println("Test ok.");
	} else {
	    Console.OUT.println("Test failed.");
	}
    }
}
