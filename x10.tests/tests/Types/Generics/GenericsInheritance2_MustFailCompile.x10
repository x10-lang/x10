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
import x10.compiler.*; // @Uncounted @NonEscaping @NoThisAccess
import x10.compiler.tests.*; // err markers

/**
 * Test inheritance and generics.
 *
 * @author nystrom 8/2008
 */
public class GenericsInheritance2_MustFailCompile extends x10Test {
        interface I[T] { def m(): T; }
        @ERR interface II extends I[Long], I[Float] {}
        @ERR @ERR @ERR @ERR @ERR @ERR abstract class Q implements I[Long], I[Float] {} // todo: too many duplicated messages! [Semantic Error: m(): x10.lang.Int in GenericsInheritance2_MustFailCompile.I[x10.lang.Int] cannot override m(): x10.lang.Float in GenericsInheritance2_MustFailCompile.I[x10.lang.Float]; attempting to use incompatible return type.
        @ERR @ERR class C implements I[Long], I[Float] { // todo: message can be better: GenericsInheritance2_MustFailCompile.C should be declared abstract; it does not define m(): x10.lang.Long, which is declared in GenericsInheritance2_MustFailCompile.I
                /* conflict, also can't implement m anyway */
        }
        class C2 implements I[Long], I[Float] {
            @ERR public def m():long = 1; //  Semantic Error: m(): x10.lang.Int in GenericsInheritance2_MustFailCompile.C2 cannot override m(): x10.lang.Float in GenericsInheritance2_MustFailCompile.I[x10.lang.Float]; attempting to use incompatible return type.
        }
        class C3 implements I[Long], I[Float] { 
            @ERR @ERR public def m():long = 1; // Semantic Error: m(): x10.lang.Int in GenericsInheritance2_MustFailCompile.C3 cannot override m(): x10.lang.Float in GenericsInheritance2_MustFailCompile.C3; attempting to use incompatible return type.
            @ERR @ERR @ERR public def m():Float = 1; // Semantic Error: m(): x10.lang.Float in GenericsInheritance2_MustFailCompile.C3 cannot override m(): x10.lang.Int in GenericsInheritance2_MustFailCompile.C3; attempting to use incompatible return type.
        }
        @ERR @ERR class C4 implements I[Long], I[Float] { }

        interface J[T] { def m(t:T):void; }
        class JImpl implements J[Long], J[Float] {
            public def m(Int):void {}
            public def m(Float):void {}
        }
        interface K[T] { def m():void; }
        class KImpl implements K[Long], K[Float] {
            public def m():void {}
        }

	public def run(): boolean {
		return true;
	}

	public static def main(var args: Rail[String]): void {
		new GenericsInheritance2_MustFailCompile().execute();
	}
}

