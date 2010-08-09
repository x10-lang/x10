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


/**
 * A representation of the UTS Binomial tree benchmark.
 */
import x10.util.Stack;
public final class Binomial(b0:UInt, q:Double, m:UInt) extends TaskFrame[UTS.SHA1Rand, UInt]{
	static type SHA1Rand=UTS.SHA1Rand;
	public static def usageLine(b0:UInt, r:UInt, mf:UInt, seq:UInt, w:UInt, nu:UInt, q:Double, l:UInt, z:UInt) {
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
	public def runTask(s:SHA1Rand, stack:Stack[SHA1Rand]!) offers UInt {
		pushN(s, s() < q ? m : 0U, stack);
	}
	public def runRootTask(s:SHA1Rand, stack:Stack[SHA1Rand]!) offers UInt {
		pushN(s, b0, stack);
	}
	private def pushN(s:SHA1Rand, N:UInt, stack:Stack[SHA1Rand]!) offers UInt {
		if (N == 0U ) return;
		for (var i:UInt=0; i<N; ++i) 
			stack.push(SHA1Rand(s, i));
				offer N;
	}
	
}
