// -*- mode: java; c-file-style: "stroustrup" -*-

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

package examples.UTS;

/**
 * A representation of the UTS Binomial tree benchmark.
 */
import x10.util.Stack;
import global.lb.*;
public final class Binomial(b0:Int, q:Double, m:Int) extends TaskFrame[UTS.SHA1Rand, Int]{
	static type SHA1Rand=UTS.SHA1Rand;
	public static def usageLine(b0:Int, r:Int, mf:Int, seq:Int, w:Int, nu:Int, q:Double, l:Int, z:Int) {
		Console.OUT.println("b0=" + b0 +
				"   r=" + r +
				"   m=" + mf +
				"   s=" + seq +
				"   w=" + w +
				"   n=" + nu +
				"   q=" + q +
                "   l=" + l + 
                "   z=" + z +
                (l==3U ?" base=" + NetworkGenerator.findW(Place.MAX_PLACES, z) : ""));
	}
	public def runTask(s:SHA1Rand, stack:Stack[SHA1Rand]) offers Int {
		pushN(s, s() < q ? m : 0U, stack);
	}
	public def runRootTask(s:SHA1Rand, stack:Stack[SHA1Rand]) offers Int {
		pushN(s, b0, stack);
	}
	private def pushN(s:SHA1Rand, N:Int, stack:Stack[SHA1Rand]) offers Int {
		if (N == 0U ) return;
		for (var i:Int=0; i<N; ++i) 
			stack.push(SHA1Rand(s, i as Int));
				offer N;
	}
	
}
