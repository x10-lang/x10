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
        def n[A1,B1,C1](x:A1, y:B1, z:C1){A1<: B1, B1 <:C1} =
            m(x as A1,y as B1,z as C1); 
        // Need the casts, since the type of x is A1{self==x}, of z is C1{self==z}.
        // Cannot establish A1{self==x} <: C1{self==z}.
        
	public def run(): boolean = {
                val x = n(new AA(), new BB(), new CC());   
               
		return x instanceof AA;  
	}

	public static def main(var args: Rail[String]): void = {
		new Inference7a().execute();
	}
}

