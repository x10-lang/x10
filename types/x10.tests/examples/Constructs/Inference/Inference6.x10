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

import harness.x10Test;

/**
 * Inference of method type parameters and method return type
 * with generic types.
 *
 * @author nystrom 8/2008
 */
public class Inference6 extends x10Test {
        def m[T](x: Array[T](1)) = x(0);

	public def run(): boolean = {
                val x = m([1]);  // ShouldNotBeERR:  Method m[T](x: x10.array.Array[T]{self.rank==1}): T{x.rank==1} in Inference6{self==Inference6#this} cannot be called with arguments (x10.array.Array[x10.lang.Int]{self.rank==1, self.rect==true, self.zeroBased==true, self.rail==true, self.size==1}); Cannot infer type for type parameter T.
                val y: int = x;
		return y == 1;
	}

	public static def main(var args: Array[String](1)): void = {
		new Inference6().execute();
	}
}

