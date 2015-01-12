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

package x10.spec.examples;

//OPTIONS: -STATIC_CHECKS

import x10.compiler.*; // @Uncounted @NonEscaping @NoThisAccess
import x10.util.*;

class UseMacroInNewExpr(i:Int) {	
    public static type Bar = UseMacroInNewExpr{i==3};
	static def test() {
		val y = new Bar(2); // ERR: Constructor return type UseMacroInNewExpr is not a subtype of UseMacroInNewExpr{self.i==2}. todo: should be a better error message
	}
}
class TestClosureAlphaRenaming {
	def test() {
	 var x:(a:Int, b:Rail[String]{b.size==a}) => Boolean = null;
	 var y:(b:Int, a:Rail[String]{a.size==b}) => Boolean = null;
	 x=y;
	 y=x;
	}
}

class TypeInferenceBugs {
	def choose[T](a: T, b: T): T = a;
	def m(intSet: Set[Int], intList: List[Int]) {
		val y = choose(intSet, intList);
		val y1:Set[Int] = choose(intSet, intList); // ERR
		val y2:Collection[Int] = choose(intSet, intList);
		val y3:Collection[Int] = choose[Collection[Int]](intSet, intList);
	}
}
abstract class Mat(rows:Int, cols:Int) {
	static type Mat(r:Int, c:Int) = Mat{self.rows==r&&self.cols==c};
	static def makeMat(r:Int,c:Int) : Mat(r,c) = null;
	abstract operator this + (y:Mat(this.rows,this.cols))
	:Mat(this.rows, this.cols);
	abstract operator this * (y:Mat) {this.cols == y.rows}
	:Mat(this.rows, y.cols);

	static def example(a:Int, b:Int, c:Int) {
		val axb1 : Mat(a,b) = makeMat(a,b);
		val axb2 : Mat(a,b) = makeMat(a,b);
		val bxc : Mat(b,c) = makeMat(b,c);
		val axc : Mat(a,c) = (axb1 +axb2) * bxc;

		val err1 = axb1 + bxc; // ERR: Method operator+(y: x10.manual.examples.Mat{self.cols==x10.manual.examples.Mat#this.cols, self.rows==x10.manual.examples.Mat#this.rows}): x10.manual.examples.Mat{self.cols==x10.manual.examples.Mat#this.cols, self.rows==x10.manual.examples.Mat#this.rows} in x10.manual.examples.Mat{self==axb1, axb1.cols==b, axb1.rows==a} cannot be called with arguments (x10.manual.examples.Mat{self==bxc, bxc.cols==c, bxc.rows==b});    Invalid Parameter.	 Expected type: x10.manual.examples.Mat{self.cols==axb1.cols, self.rows==axb1.rows}	 Found type: x10.manual.examples.Mat{self==bxc, bxc.cols==c, bxc.rows==b}
		val err2 =  bxc * axb1; // ERR: Method operator*(y: x10.manual.examples.Mat){x10.manual.examples.Mat#this.cols==y.rows}[]: x10.manual.examples.Mat{self.cols==y.cols, self.rows==x10.manual.examples.Mat#this.rows} in x10.manual.examples.Mat{self==bxc, bxc.cols==c, bxc.rows==b} cannot be called with arguments (x10.manual.examples.Mat{self==axb1, axb1.cols==b, axb1.rows==a}); Call invalid; calling environment does not entail the method guard.
	}
}
class DestructurePointTest {
	static def destructurePoint() {
		val [i] : Point = Point.make(11);
		val p[j,k] = Point.make(22,33);
		val q[l,m] = [44,55]; // coerces an array to a point.
	}
	static def iterateExample() {	
		var sum : Int = 0;
		for ([i] in 1..100) sum += i;
	}
}
class FormalParameters {
	static def inc(var i:Int) { i += 1; }
	static def test() {
		var j:Int = 1;
		inc(j); // "j" is not changed
		assert j==1;
	}
}
class Locals {
	static def main(r: Rail[String]):void {
		val a : Int;
		a = r.size;
		val b : String;
		if (a == 5) b = "five?"; else b = "" + a + " args";
	}
}
/*
When ref is included in the language:

class RefExample {
	static def inc(ref i:Int) { i += 1; }
	static def test() {
		var j:Int{self==1} = 1;
        inc(j); // "j" IS changed! ShouldBeErr: subtyping for "ref" must be exact! With DYNAMIC_CHECKS there should be a check after the method returns.
		assert j==2;
	}

    // another example
    static def foo(ref i:Int) {
      bar(i); // ShouldBeErr: subtyping for "ref" must be exact!
    }
    static def bar(ref i:Any) {
      i = "as";
    }
}
*/

class InterfacesExamples {
	interface KnowsPi {
	PI = 3.14159265358;
	}


	interface E1 {static val a = 1;}
	interface E2 {static val a = 2;}
	interface E3 extends E1, E2{}
	class Example711A implements E3 {
	def example1() = a; // ERR ERR: Could not find field or local variable "a".
	def example2() = E1.a + E2.a;
	}

	interface I1 { static val a = 1;}
	interface I2 extends I1 {}
	interface I3 extends I1 {}
	interface I4 extends I2,I3 {}
	class Example711B implements I4 {
	def example() = a;
	}
}

class Crash {
	val f : (Int) => String = (Int)=>"";
	def f(Int) = false;
	def test() {
		val x = f(3); // can be disambiguated like this: (f)(3)
	}
}

class SuperTest {
	class A {
		val f = 3;
	}
	class B extends A {
		val f = 4;
		class C extends B {
			// C is both a subclass and inner class of B
			val f = 5;
			val t1:Int{self==4} = super.f;
			val t2:Int{self==4} = B.this.f;
			val t3:Int{self==3} = B.super.f;
		}
	}
}

class PropertyMethodExample(x:Int, y:Int) {
	def this(x:Int, y:Int) { property(x,y); }
	property eq() = (x==y);
	property is(z:Int) = x==z && y==z;
	def example( a : PropertyMethodExample{eq()}, b : PropertyMethodExample{is(3)} ) {}
}
class Oddvec { // ShouldBeErr?  no corresponding operator() defined.
public operator this(i:Int)=(newval:Int) = {}
}
