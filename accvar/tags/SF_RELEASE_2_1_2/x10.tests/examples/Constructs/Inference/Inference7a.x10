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
 * with generic types. Checking that the type inferencer understands 
 * A <: B, B <: C implies A <: C.
 *
 * @author vj 10/10/10
 */
public class Inference7a extends x10Test {
        def m[A,B,C](x: A, y:B, z:C) {A <:  C}:A=x;

        static class AA extends BB{}
        static class BB extends CC {}
        static class CC{}
        def n[A1,B1,C1](x:A1,y:B1,z:C1){A1<: B1, B1 <:C1} =
            m(x,y,z); // ShouldNotBeERR: Method m[A, B, C](x: A, y: B, z: C){}[A <: C]: A in Inference7a{self==Inference7a#this} cannot be called with arguments (A1{self==x}, B1{self==y}, C1{self==z}); Type guard [A1{self==x} <: C1{self==z}] cannot be established; inconsistent in calling context.
        
	public def run(): boolean = {
                val x = n(new AA(), new BB(), new CC());  // ShouldNotBeERR
               
		return x instanceof AA; // ShouldNotBeERR
	}

	public static def main(var args: Array[String](1)): void = {
		new Inference7a().execute();
	}
}

