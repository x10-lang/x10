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

/** A representation of the UTS Geometric tree benchmark.
 * 
 */

import x10.util.Stack;
public class Geometric(b0:UInt, a:UInt, d:UInt) extends TaskFrame[TreeNode, UInt]{
	static type Constants = UTS.Constants;
	public static def usageLine(b0:UInt, r:UInt, a:UInt, d:UInt, seq:UInt, w:UInt, nu:UInt, l:UInt, z:UInt) {
		Console.OUT.println("b0=" + b0 +
				"   r=" + r +
				"   a=" + a +
				"   d=" + d +
				"   s=" + seq +
				"   w=" + w +
				"   n=" + nu +
				"   l=" + l + 
				"   z=" + z +
				(l==3U ?" base=" + NetworkGenerator.findW(Place.MAX_PLACES, z) : ""));
	}
	
	public def runRootTask(s:TreeNode, stack:Stack[TreeNode]!):Void offers UInt {
		runTask(s, stack);
	}
	public def runTask  (
			node:TreeNode, 
			stack:Stack[TreeNode]!) offers UInt { 
		/* compute branching factor at this node */
		var curNodeBranchingFactor:double;

	if (0U == node.d) { /* root node */
		curNodeBranchingFactor = b0;
	} else { /* calculate the branching factor for this node */
		if (Constants.EXPDEC == a) { /* Exponential decrease */
			val tmpLogOne = -1.0 * Math.log (b0 as double);
			val tmpLogTwo = Math.log (d as double);
			curNodeBranchingFactor = b0  * 
			Math.pow (node.d as double, 
					tmpLogOne/tmpLogTwo);
		} else if (Constants.CYCLIC == a) { /* Cyclic */
			if (node.d > (5U*d)) {
				curNodeBranchingFactor = 0.0;
			} else {
				val TWO = 2.0;
				val PI = 3.141592653589793;
				val exponent = Math.sin (TWO*PI*(node.d as double)/
						(d as double));

				curNodeBranchingFactor = Math.pow (b0, 
						exponent);
			}
		} else if (Constants.FIXED == a) { /* Fixed */
			curNodeBranchingFactor = (node.d < d) ? 
					b0 : /* true */
						0U; /* false */
		} else if (Constants.LINEAR == a) { /* Linear --- default */
			curNodeBranchingFactor = b0 *
			(1.0 - (node.d as double)/
					(d as double));
		} else {
			curNodeBranchingFactor = 0;
			Console.OUT.println ("Unknown shape function for geometric UTS");
		}
	}

	/* Now, calculate the number of children */
	val probForCurNodeBranchingFactor = 1.0 / (1.0 + curNodeBranchingFactor);
	val randomNumber = (node.r)() as double;
	val normalizedRandomNumber = randomNumber / UTS.NORMALIZER;
	val numChildren:UInt = Math.floor ((Math.log (1-normalizedRandomNumber)) /
			(Math.log 
					(1-probForCurNodeBranchingFactor))) as UInt;

	/* Push all the children onto the stack */
	for (var i:UInt=0; i<numChildren; ++i) 
		stack.push(TreeNode (node.d+1, node.r));
	offer numChildren;
	}
}
