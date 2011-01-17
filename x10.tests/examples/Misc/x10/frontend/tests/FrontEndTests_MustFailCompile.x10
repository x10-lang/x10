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

package x10.frontend.tests;
// TODO: We should put ALL our tests in different packages according to the directory structure

import harness.x10Test;

import x10.compiler.*; // @Uncounted @NonEscaping @NoThisAccess
import x10.compiler.tests.*; // err markers
import x10.util.*;

import x10.io.CustomSerialization;
import x10.io.SerialData;

/**
 * A group of tests for various frontend-related compilation issues.
 */
public class FrontEndTests_MustFailCompile extends x10Test {
	public def run():Boolean {
		return true;
	}
	public static def main(Array[String]) {
		new FrontEndTests_MustFailCompile().execute();
	}
}


// test object initialization (and more)

class TestFinalField42 {
	val q:Int;
	def this() { // ERR		
		finish {
			if (true) {
				async { 
					q=7;
					f(q);
				}
				f(q); // ERR
			} else {
				f(q); // ERR
			}
			f(q); // ERR
		}
		f(q); // ERR
	}
	private def f(i:Int):Int=i+1;
}
class TestFinalField43 {
	val q:Int;
	def this() { // ERR		
		finish {
			if (true) {
				async { 
					q=7;
				}
			} else {
			}
		}
		f(q); // ERR
	}
	private def f(i:Int):Int=i+1;
}
class TestFinalField {
	static val y:Int; // ERR
	static val s:Int = 3;
	static def test() {
		s = 4;  // ERR
	}

	val f:Int;
	var i:Int;
	native def this(); // a native ctor is assumed to initialize all fields
	def foo(x:TestFinalField) {
		s = 4;  // ERR
		f=2; // ERR
		x.f = 2; // ERR
		i=2;
		x.i = 2;
	}
	def this(x:TestFinalField) {
		s = 4;  // ERR
		finish async f=2; 
		x.f = 2; // ERR
		i=2;
		x.i = 2;
	}
	def this(Int) { 
		async f=2; 
		f=2; // ERR
	}
}

final class ClosuresDuringConstruction {
	var k:Int;
	def f() = k=3;
	def f2() = 3;
	val c1 = f.(); // ERR
	val c2 = ()=>f(); // ERR
	val c3 = f2.(); // ERR
	val c4 = ()=>f2(); // ERR
}
class CaptureThisInAtStmtExpr {
	val x = 3;
	def this() {
		val local = 3;
		val y =
			at (here) 
				x*2; // ERR
		at (here) { 
			val z = 
				x*2; // ERR
		}		
        finish ateach (p in Dist.makeUnique()) { 
			val z = 
				x*2; // ERR
			val z2 = local*2;
		}
		async {
			val z = x*2;
		}

		val y2 =
			at (here) local*2;
		at (here) {
			val z2 = local*2;
		}
	}
}


class WithManyProperties(i:Int,j:Int) {
	def this() {} // ERR: property(...) might not have been called
}
class NoPropertiesButWithPropertyCall {
	def this() {
		property(1); // ERR: Semantic Error: The property initializer must have the same number of arguments as properties for the class.
	}
	def this(Int) {
		super();
	}
	def this(Double) {
		super();
		property(1); // ERR
	}
}
class FinalFieldWrittenExactlyOnce {
	val f:Int;
	var flag:Boolean;
	var i:Int;
	def this() {} // ERR: not written
	def this(Int) { f=1; }
	def this(Short) { if (flag) f=1; else f=2; }
	def this(Double) { // ERR
		async f=1; 
	}
	def this(String) { 
		finish async f=1; 
	}
	def this(Float) { 
		async f=1; 
		f=2; // ERR
	}
	def this(Byte) { 
		if (flag) f=1; 
		f=2; // ERR
	}
	def this(Long) { // ERR
		while (flag) f=1; // ERR
	}
	def this(Long,Long) {
		do {
			f=1; // ERR
		} while (flag);
	}
	def this(Any) { 
		while (flag);
		{ f=1; }
	}
	def this(UByte) { 
		if (flag) f=1;
		else if (flag) f=2; 
		else f=3;
	}
	def this(UShort) { 
		switch (i) {
		case 1:
			f=1;
			break;
		case 3:
			f=2;
			break;
		default:
			f=3;
		}
	}
	def this(UInt) { // ERR
		switch (i) {
		case 1:
			f=1;
			break;
		case 3:
			f=2;
			break;
		}
	}
	def this(ULong) {
		switch (i) {
		case 1:
			f=1;
			break;
		case 3:
			f=2;
			break;
		}
		f=3;  // ERR
	}
	def this(Char) {  
		switch (i) {
		case 1:
			f=1;
		default:
			f=3; // ERR
		}
	}
	def this(Int,Int) { // ERR
		if (flag) f=1;
		else if (flag) f=2;
	}
	def this(Int,Byte) { // ERR
		val b:Int = (b=5); // ShouldBeErr
		var k:Int = (k=5);
		while (true) { val i:Int = 4;}
		f=f+1; // ERR
	}
	def this(Int,Short) { // ERR: Field 'f' was not definitely assigned.
		m();
	}
	private def m() {
		f=1; // ERR
	}
}

class InfiniteInit234 {
	var i:Int{self!=0};
	def this() {
		foo();
	}
	@NonEscaping private def foo() = foo();
}

class AllowCallsIfNoReadNorWrite {
	class Inner(i:Int) {
		def this() {
			val w = this.foo1();
			property(4);
		}
		@NonEscaping private def foo1() = 3 + foo2();
		@NonEscaping private def foo2() = 3;
	}
}


class DisallowCallsUnlessNoThisAccess {
	class Inner(i:Int) {
		static y=5;
		var x:Int=2;
		val z:Int=3;
		def this() {
			val w = this.foo1(); // ERR: You can use 'this' before 'property(...)' to call only @NoThisAccess methods or NonEscaping methods that do not read nor write any fields.
			property(4);
		}
		def this(i:Int) {
			val w = this.bar1(); // ERR: You can use 'this' before 'property(...)' to call only @NoThisAccess methods or NonEscaping methods that do not read nor write any fields.
			property(4);
		}
		def this(i:Boolean) {
			val w = this.ok1(); // ok 
			property(4);
		}
		@NoThisAccess def noThisAccess() { // subclasses cannot read nor write as well
			return y;
		}
		def this(i:String) {
			val w = this.noThisAccess(); // ok 
			property(4);
		}
		@NonEscaping private def ok1() = ok2()+2;
		@NonEscaping private def ok2() = ok3()+3;
		@NonEscaping private def ok3() {
			return y;
		}
		@NonEscaping private def foo1() = foo2()+2;
		@NonEscaping private def foo2() = foo3()+3;
		@NonEscaping private def foo3() {
			x=2; // There is a write to a field in this method!
			return y;
		}
		@NonEscaping private def bar1() = bar2()+2;
		@NonEscaping final def bar2() {
			return z; // There is a read from a field in this method!
		}
	}
}

class IllegalForwardRef234 {
	var i1:Int{self!=0} = i2; // ERR: Cannot read from field 'i2' before it is definitely assigned.
	var i2:Int{self!=0} = i1;
}

class TestUncountedAsync1 {
	//@Uncounted async S
	//is treated like this:
	//async if (flag) S
	//so the statement in S might or might not get executed.
	//Therefore even after a "finish" we still can't use anything assigned in S.                
	def test1() {
		val q:Int,q2:Int;
		finish {
			q2=2;
			use(q2);
			use(q); // ERR
			@Uncounted async {
				use(q2);
				q=1;
				use(q);
			}
			use(q2);
			use(q); // ERR
		}
		use(q2);
		use(q); // ERR
	}	
	def test2() {
		var q:Int;
		finish {
			@Uncounted async {
				q=1;
				use(q);
			}
			use(q); // ERR
		}
		use(q); // ERR
	}
	def test3() {
		var q:Int;
		finish {
			@Uncounted async {
				async { // it is implicitly @Uncounted
					q=1;
					use(q);
				}
			}
			use(q); // ERR
		}
		use(q); // ERR
	}
	def use(a:Int) {}

	var x:Int{self!=0};
	var x2:Int{self!=0};
	def this() { // ERR: Field 'x' was not definitely assigned in this constructor.
		finish {
			async x2=1;
			@Uncounted async {
				x=1;
			}
		}
	}

	static class Box[T] {
		var t:T;
		def this(t:T) { this.t = t; }
	}
	def mainExample() {
		val box = new Box[Boolean](false);
		@Uncounted async {
			@Uncounted async {
				atomic box.t = true;
			}
		}
		when (!box.t) {}
	}
}


class SquareMatrixTest123(rows:Int, cols:Int, matDist:Dist, mat:DistArray[Int]){
	var z:Int = 2;
	val q:Int;
	def this(r:Int, c:Int{self == r}) 	{
		val mShape:Region = null;
		val mDist = Dist.makeBlock(mShape);
		z++; // ERR: Can use 'this' only after 'property(...)'
		val closure = () =>
			z++; // ERR
		val closure2 = () => 
			q; // ERR
		property(r, c, mDist, DistArray.make[Int](mDist, 
			initMat // ERR: Can use 'this' only after 'property(...)'
		));
		q=3;
	}
	val initMat : (Point) => int = ([x,y]:Point) => x+y;
} 

class TwoErrorsInOneLineTest(o:Int) {
	var k:Int;
	def this() {
		k=  // ERR
			o; // ERR
		property(2);
	}
}

class SomeSuper87 {
	def this(i:Int) {}
}   
class TestSuperThisAndPropertyCalls(p:Int) extends SomeSuper87 {
	var i:Int;
	def this() {
		super(i); // ERR: Can use 'this' only after 'property(...)'
		property(i); // ERR: Can use 'this' only after 'property(...)'
	}
	def this(i:Int) {
		super(i);
		property(i);
	}
	def this(b:Boolean) {
		this(i); // ERR: Can use 'this' only after 'property(...)'
		property(1); // ERR: You cannot call 'property(...)' after 'this(...)'
	}
	def this(x:Double) { super(1); } // ERR: property(...) might not have been called
	def this(x:Float) { property(1); } 
	def this(x:Char) { 
		// val x = 3; // I can't check this error, because it is a parsing error: the call to "super(...)" must be the first statement.
		super(1); 
		property(1); 
	}
	def this(x:String) { 
		super(1); 
		val y = 3; 
		property(1);
	}
}


interface BarI34 {
	static x = 3;
	val y = Math.sqrt(2.0);
}
class TestPropAndConstants(p:Int) implements BarI34 {
	val q:Int = 3 as Int; // this will be moved (it might access properties)
	val q3:Int = p+4;
	val q2:Int = 42; // this is constant, so it won't be moved to the __fieldInitializers()
	var x:Int;
	def this() {
		val w=4;
		property(45);
		x=2;
	}
	def this(i:Int) {
		this();
		x=4;
	}
	def this(i:Boolean) {
		super();
		val w=4;
		{
		property(42);
		}
		x=2;
	}

}


class PropertySuperTests {
static class WithouProperties {}
static class WithProperties(x:Int) {
	def this() {
		property(1);
	}
	def this(i:Int) {
		property(i);
	}
}
static class SubWithProperties(y:Int) extends WithProperties {
	static S=1;
	val k=3;
	var z:Int = 4;
	def this() {
		super(1);
		property(2);
	}
	def this(i:Boolean) {
		this();
	}
	def this(i:Int) { 
		this();
		property(1); // ERR: You cannot call 'property(...)' after 'this(...)'
	}
	def this(i:Double) {
		super(
			super.x // ERR: You can use 'super' only after 'super(...)'
		);
		val w = z; // ERR: Can use 'this' only after 'property(...)'
		val w2 = this.S; // ERR: Can use 'this' only after 'property(...)'
		val w3 = S;
		val w4 = super.x;
		val w5 = this.x; // ERR: Can use 'this' only after 'property(...)'
		val w6 = this.y; // ERR: Can use 'this' only after 'property(...)'
		property(
			this.y // ERR: Can use 'this' only after 'property(...)'
		);
	}
	def this(i:Float) {
		property(1);
	}
	def this(i:String) { // ERR: property(...) might not have been called
	}
	def this(i:Any) {
		property(1);
		property(1); // ERR: You can call 'property(...)' at most once	
	}
}
static class SubWithoutProperties extends WithProperties {
	def this() {
		super(1);
	}
	def this(i:Float) {
		property(1); // ERR: The property initializer must have the same number of arguments as properties for the class.
	}
	def this(i:Double) {
		super(1);
		property(1); // ERR: The property initializer must have the same number of arguments as properties for the class.
	}
}
}



class TestPropertyCalls(p:Int, p2:Int) {
	def this() {} // ERR: property(...) might not have been called
	def this(Char) {
		property(1); // ERR: The property initializer must have the same number of arguments as properties for the class.
	}
	def this(i:Int) {
		val x:Int;
		if (i==1)
			x=2;
		else
			x=3;
		property(x,2);
	}
	def this(String) { 
		finish async property(1,2);
	}
	def this(Float) { // ERR: property(...) might not have been called
		async property(1,2); 
	}
	def this(Boolean) { 
		property(1,2);
		property(1,2); // ERR: You can call 'property(...)' at most once
	}
	def this(b:Double) { // ERR: property(...) might not have been called
		if (b==1) 
			property(1,2);
	}

	def m() {
		property(1,2); // ERR: A property statement may only occur in the body of a constructor.
	}
	static def q() {
		property(1,2); // ERR: A property statement may only occur in the body of a constructor.
	}
}



class ClosureExample {
  def this() {
    val closure1 = () =>i; // ERR
	val k = i;
    val closure2 = () =>k; 
  }
  val closure3 = () =>i; // ERR
  val i = 3;
  val closure4 = () =>i; // ERR
}
class ClosureIsNotAWrite {
	var i:Int{self != 0}; // ERR: Semantic Error: Field 'i' was not definitely assigned.
	val closure = () =>  { i=2; } ; // ERR
}

class TestPropertiesAndFields(i:Int, j:Int) {
	def this() {
		val x = 3;
		property(x,x);
		val k = i;
		val closure = () => k+4;
		j2 = j;
	}		
	
	val i2 :Int{self==i} = i;
	val j2 :Int{self==j};
}
class CheckCtorContextIsNotStatic[T](p:T) {
    public def this(o:Any) {
        property(o as T);
    }
} 


class TransientTest(p:Int) { // The transient field '...' must have a type with a default value.
	transient val x1 = 2; // ERR (because the type is infered to be Int{self==2}
	transient val x2:Int = 2;
	transient var y:Int;
	transient var y2:Int{self==3} = 3; // ERR
	transient var y3:Int{self!=0}; // ERR
	transient var y4:Int{self==0}; 
	transient var y5:Int{self!=3}; 
	transient var y6:Int{self==p}; // ERR
	def this(k:Int) {
		property(k);
		y3 = 4;
		y6 = p;
	}
}
		
class XTENLANG_1643 {
  var i:Int{self!=0};
  def this(j:Int{self!=0}) { i = j; }
}


final class ClosureTest57 {
	val z = 1;
	val c1 = () => z+1; // ERR
	var x:Int{self!=0} = 1;
	val c2 = () => { 
		x=3; // ERR
		return x+1; // ERR
	};
	var y:Int{self!=0}; // ERR: Field 'y' was not definitely assigned.
	val c3 = () => { 
		y=3; // ERR
		return y+1; // ERR
	};
	
	val c4 = () => w; // ERR
	val w = 42;

    def a() = q+2;
	val q = 4;
	
    final class C {
        def a() = q*3;
		val q = 4;
        final class D {
            @NonEscaping def a() = q+4;
            val sum = (()=>(ClosureTest57.this.a()
            		+C.this.a()
            		+D.this.a() // ERR
            		+a()  // ERR
					))();
			val z = q+2; // ERR: Cannot read from field 'q' before it is definitely assigned.
			val q = 5;
        }
    }
}
final class ClosureTest58 {
  def f() = x*3;
  val bar: ()=>Int = this.f.(); // ERR: The method call reads from field 'x' before it is definitely assigned.
  val z = bar();
  val x=2;

  var w:Int{self!=0}; // ERR: Field 'w' was not definitely assigned.
  def setW() = w=2;
  val q = this.setW.(); // ERR
  
  var w2:Int{self!=0}; 
  def setW2() = w2=2;
  val q2 = this.setW2(); // ok
}

class MultipleCtorsAndFieldInits {
	
	var b:Int{self!=0};
	var i:Int{self!=0};
	var z:Int{self!=0} = (3*b*(b=1)) as Int{self!=0}; // ERR: Cannot read from field 'b' before it is definitely assigned.
	var j:Boolean{self!=false}; 
	var k:Any{self!=null}; 

	
	def this(a:Int) {
		this();
	}
	def this(a:Boolean) { // ERR: Field 'k' was not definitely assigned in this conprivate structor.
		i=4;
		j = true;
	}
	def this() { // ERR: Field 'j' was not definitely assigned in this conprivate structor.
		finish {
			async {
				i = 2;
				val r1 = i;
			}			
			val r2 = i; // ERR
		}
		val r3 = i;
		k= new Object();
	}
	def this(k:MultipleCtorsAndFieldInits) { // ERR: Field 'k' was not definitely assigned in this conprivate structor.
		finish {k.i = 2; j=true;
		async i=3;
		}
	}
}

class UsingSuperFields {
	static class Super {
		val s1=1;
		val s2:Int;
		def this() {
			s2 = 2;
		}
	}
	static class Sub extends Super {
		val a = s1+s2;
		val b:Int;
		def this() {
			super();
			b = s1*s2;
		}
	}
}



class DynamicDispatchingInCtorTest {
	abstract class Super {
		val x:Int;
		val size:Int;
		def this() {
			this.x = 42;
			size = calcSize(x);
		}
		@NoThisAccess abstract def calcSize(x:Int):Int;
		@NonEscaping def useError(i:Int):void {} // ERR: A @NonEscaping method must be private or final.	
		@NonEscaping final def use(i:Int):void {} 
		@NonEscaping private def useOk2(i:Int):void {} 
	}
	class Sub1 extends Super {
		@NoThisAccess def calcSize(x:Int):Int { return x*2; }
	}
	class Sub2 extends Super {
		def calcSize(x:Int):Int { // ERR: You must annotate x10.lang.Int calcSize(...) with @NoThisAccess because it overrides a method annotated with that. 
			return x*4; 
		}
	}
	class Sub3(p:Int) extends Super {
		val w = 3;
		var k:Int{self==p};
		def this() {
			property(4);
			k = p;
		}
		@NoThisAccess def calcSize(x:Int):Int { 
			use( // ERR: You cannot use 'this' or 'super' in a method annotated with @NoThisAccess
				w); // ERR: You cannot use 'this' or 'super' in a method annotated with @NoThisAccess
			return x+2; 
		}
	}
}

class TestAsync {	
	var i:Int{self!=0};
	def this() {
		finish {
			async {
				i = 2;
				val r1 = i;
			}			
			val r2 = i; // ERR: Cannot read from field 'i' before it is definitely assigned.
		}
		val r3 = i;
	}
	
	static def use(a:Int) {}
	
	public static def main(Array[String]) {
	     var i:Int, j:Int, k:Int, x:Int, y:Int;
         val m:Int, n:Int, q:Int;

         x=1;
		 finish async { use(x); x=4; use(x); }
		 use(x);
         finish async { y=4; use(x); use(y); }
		 use(y);

		 i=1;
		 use(i);
         // i:[1,1,1,1]
         finish {
             m=2;
			 use(m);
             // m:[1,1,1,1]
             if (true) {
                 async {
                     n=3; i=4; j=5; k=6; q=7;
					 use(n); use(i); use(j); use(k); use(q); use(m);
                     // n:[1,1,1,1] i:[2,2,2,2] j:[1,1,1,1] k:[1,1,1,1] q:[1,1,1,1]
                 }
				 use(n); // ERR: "n" may not have been initialized 
				 use(i); 
				 use(j); // ERR: "j" may not have been initialized
				 use(k); // ERR: "k" may not have been initialized
				 use(q); // ERR: "q" may not have been initialized
				 use(m);
                 // n:[0,0,1,1] i:[1,1,2,2] j:[0,0,1,1] k:[0,0,1,1] q:[0,0,1,1]
                 k=8;
				 use(k); 
                 // k:[1,1,2,2]
             } else {
                 // n:[0,0,0,0] m:[1,1,1,1] i:[1,1,1,1] j:[0,0,0,0] k:[0,0,0,0] q:[0,0,0,0]
                 n=9; 
				 use(n); 
				 m=10; // ERR: Final variable "m" might already have been initialized				 
				 use(m); 
                 // n:[1,1,1,1] m:[2,2,2,2]
             }
             // k:[0,1,0,2] n:[0,1,1,1] m:[1,2,1,2] i:[1,1,1,2] j:[0,0,0,1] q:[0,0,0,1]
             k=11;
			 use(k); 
             // k:[1,2,1,3]
         }
         // k:[1,3,1,3] n:[1,1,1,1] m:[1,2,1,2] i:[1,2,1,2] j:[0,1,0,1] q:[0,1,0,1]
         j=12;
		 use(q); // ERR: "q" may not have been initialized
		 use(n); use(i); use(j); use(k); use(m);
         // j:[1,2,1,2]
         // all (except q) are definitely-assigned now. 

	}
	static def use2(loc:Int,expected:Int,a:Int) { if (expected!=a) throw new RuntimeException("ERROR! loc="+loc+" expected="+expected+" a="+a); }
	
	public static def main2(Array[String]) {
	     var i:Int, j:Int, k:Int, x:Int, y:Int;
         val m:Int, n:Int, q:Int;

         x=1;
		 finish async { use2(101,1,x); x=4; use2(102,4,x); }
		 use2(103,4,x);
         finish async { y=5; use2(104,4,x); use2(105,5,y); }

		 i=1;
		 use2(106,1,i);
         // i:[1,1,1,1]
         finish {
             m=2;
			 use2(107,2,m);
             // m:[1,1,1,1]
             if (true) {
                 async {
                     n=3; i=4; j=5; k=6; q=7;
					 use2(108,3,n); use2(109,4,i); use2(110,5,j); use2(111,6,k); use2(112,7,q); use2(113,2,m);
                     // n:[1,1,1,1] i:[2,2,2,2] j:[1,1,1,1] k:[1,1,1,1] q:[1,1,1,1]
                 }
				 use2(115,2,m);
                 // n:[0,0,1,1] i:[1,1,2,2] j:[0,0,1,1] k:[0,0,1,1] q:[0,0,1,1]
                 k=8;
				 use2(116,8,k); 
                 // k:[1,1,2,2]
             } else {
                 // n:[0,0,0,0] m:[1,1,1,1] i:[1,1,1,1] j:[0,0,0,0] k:[0,0,0,0] q:[0,0,0,0]
                 n=9; 
				 use2(117,9,n); 	 
				 use2(118,2,m); 
                 // n:[1,1,1,1] m:[2,2,2,2]
             }
             // k:[0,1,0,2] n:[0,1,1,1] m:[1,2,1,2] i:[1,1,1,2] j:[0,0,0,1] q:[0,0,0,1]
             k=11;
			 use2(119,11,k); 
             // k:[1,2,1,3]
         }
         // k:[1,3,1,3] n:[1,1,1,1] m:[1,2,1,2] i:[1,2,1,2] j:[0,1,0,1] q:[0,1,0,1]
         j=12;
		 k=99;
		 use2(120,3,n); use2(121,4,i); use2(122,12,j); use2(123,99,k); use2(124,2,m);
         // j:[1,2,1,2]
         // all (except q) are definitely-assigned now. 

  }
}


class PropertyTest(p:Int) {
	static val i = 3;
	def this() {
		property(1);
		val w = p();
		val q = p;
	}
}

class TestNonEscapingWarning {
	final class FinalBar {
		def this() {
			f1();
			f2();
		}
		private def f1() {}
		final def f2() {}
	}
	class NonFinalBar {
		def this() {
			f1();
			f2();
		}
		private def f1() {}
		final def f2() {} // ERR: (warning)
	}
}
abstract class SuperClassTest {
	val a=1;
	val b=2;
	val c:Int;
	val d:Int;
	var x:Int{self!=0}; 
	var y:Int{self!=0};
	var z:Int{self!=0};

	def this(i:Int) { this(); x = y; }
	def this() { 
		super();
		q();
		f0(); // ERR: Cannot read from field 'c' before it is definitely assigned.
		c = 2;
		f0();
		setX();
		setY(); 
		setY(); 
		setZ();
		f1(); 
		d=4;
	}

	@NonEscaping def setX() { // ERR: A @NonEscaping method must be private or final.
		x = 42;
	}
	@NonEscaping final def setZ() {
		z = 42;
	}
	final def setY() { // ERR: (warning) Methods 'setY()' is called during construction and therefore should be marked as @NonEscaping.
		y = 42;
	}

	def g():Int = 1;
	abstract @NonEscaping def q():Int; // ERR: A @NonEscaping method must be private or final.
	@NonEscaping final def ba():Int = a+b;
	@NonEscaping private def f0():Int = a+b+c;
	@NonEscaping protected def f1():Int = a+c; // ERR: A @NonEscaping method must be private or final.
	@NonEscaping final native def e1():Int; 
	@NonEscaping native def e2():Int; // ERR: A @NonEscaping method must be private or final.
}

class Sub1Test extends SuperClassTest {
	val w = 1;
	var q:Int{self!=0} = 1;
	def this(i:Int) { this(); x = y; }
	def this() {
		super();
		readD(); 
		g(); // ERR: The call Sub1Test.this.g() is illegal because you can only call a superclass method during construction only if it is annotated with @NonEscaping.
		setX();
		setZ();
		f2(); 
	}
	final def readD() { // ERR: (warning) 
		val q = d;
	}
	@NonEscaping private def f2():Int = 1;
	def q():Int = 2;
}


class TypeNameTest {
	val n = typeName();
}
struct TypeNameTest2 {
	val n = typeName();
}



class TestNonEscaping {
	val x = foo();

	@NonEscaping private def f1() {} 

	@NonEscaping final def f5() {
		bar(); // ERR: The call TestNonEscaping.this.bar() is illegal because you can only call private/final @NonEscaping methods or @NoThisAccess methods during construction.
	}
	def bar() {} // ERR: (warning)



	@NonEscaping final def foo() {
		this.foo2(); 
		return 3;
	}
	final def foo2() { // ERR: (warning)
	}
}



interface BlaInterface {
	def bla():Int{self!=0};
}
class TestAnonymousClass {
	static val anonymous1 = new Object() {};
	val anonymous2 = new Object() {}; // ERR: 'this' cannot escape via an anonymous class during construction
	val anonymous3 = new TestAnonymousClass() {}; // ERR: 'this' cannot escape via an anonymous class during construction
	def foo() {
		val x = new Object() {};
	}
	@NonEscaping final def foo2() {
		val x = new Object() {}; // ERR: 'this' cannot escape via an anonymous class during construction
	}

	val anonymous = new BlaInterface() { // ERR: 'this' cannot escape via an anonymous class during construction
		public def bla():Int{self!=0} {
			return k;
		}
	};
	val inner = new Inner(); // ERR: 'this' and 'super' cannot escape from a constructor or from methods called from a constructor
	
	val qqqq = at (here.next()) this; // ERR: Semantic Error: 'this' and 'super' cannot escape from a constructor or from methods called from a constructor

	val w:Int{self!=0} = anonymous.bla();
	val k:Int{self!=0};
	def this() {
		assert w!=0;
		k = 3;
	}

	class Inner implements BlaInterface {
		public def bla():Int{self!=0} {
			return k;
		}
	}
}


class C57 {
 var m: Int{self!=0}, n:Int{self!=0};
 @NonEscaping private final def ctorLike() {
  n = m; 
 }
 def this() {
  ctorLike(); // ERR: Cannot read from field 'm' before it is definitely assigned.
  m = 7;
 }
}


class TestGlobalRefInheritance {
    private var k:GlobalRef[TestGlobalRefInheritance] = GlobalRef[TestGlobalRefInheritance](this);
	final def getK() = k;
	@NonEscaping def getK2() = // ERR: A @NonEscaping method must be private or final.
		k; // ERR: Cannot use 'k' because a GlobalRef[...](this) cannot be used in a field initializer, constructor, or methods called from a constructor.
}
class TestGlobalRefInheritanceSub extends TestGlobalRefInheritance {
	def this() {
		val escaped1 = getK(); // ERR: The call TestGlobalRefInheritanceSub.this.getK() is illegal because you can only call @NonEscaping methods of a superclass from a constructor or from methods called from a constructor
		val escaped2 = getK2();
	}
}


struct TestStructCtor[T] { T <: Object } {
	def this() {}
	def test1(
		TestStructCtor[TestStructCtor[String]]) {// ERR (Semantic Error: Type TestStructCtor[TestStructCtor[x10.lang.String]] is inconsistent.)
	}
	def use(Any) {}
	def test2() {
		use( TestStructCtor[TestStructCtor[String]]() ); // ERR (Semantic Error: Inconsistent constructor return type)// ERR (Semantic Error: Method or static constructor not found for given call.		 Call: TestStructCtor[TestStructCtor[x10.lang.String]]())
	}
}
class MyBOX[T] {
	def this(t:T) {}
}
class TestGlobalRefRestriction {
    private var k:GlobalRef[TestGlobalRefRestriction] = GlobalRef[TestGlobalRefRestriction](this);
    private val z = GlobalRef[TestGlobalRefRestriction](this); 
	val z2 = GlobalRef[TestGlobalRefRestriction](this); // ERR (must be private)
	var f:GlobalRef[TestGlobalRefRestriction];
	val q1 = k; // ERR (can't use GlobalRef(this) )
	val w1 = new MyBOX[GlobalRef[TestGlobalRefRestriction]](this.k); // ERR (can't use "k" cause it is a GlobalRef(this) )
	var other:TestGlobalRefRestriction = null;
	val q2 = other.k;
	val w2 = new MyBOX[GlobalRef[TestGlobalRefRestriction]](other.k);

	def this() {
		foo1();
		k = GlobalRef[TestGlobalRefRestriction](null); // ok, can assign to globalRef fields (but you can't read from them)
		k =
			f = k; // ERR
		f = k; // ERR
		k = f;
		f = other.k;
		k = other.k;
		f = GlobalRef[TestGlobalRefRestriction](this); // ERR (this escaped)
	}
	@NonEscaping private def foo1() {
		val z = (k as GlobalRef[TestGlobalRefRestriction]{home==here}); // ERR (because it is called from a ctor)
		z();
	}
	private def foo2() {
		val z = (k as GlobalRef[TestGlobalRefRestriction]{home==here});
		z();
	}

	class Inner {
		private var k1:GlobalRef[Inner] = GlobalRef[Inner](this);
		private val k2:GlobalRef[Inner] = GlobalRef[Inner](Inner.this);
		val z1 = k1; // ERR
		val z2 = k2; // ERR
		var w:GlobalRef[TestGlobalRefRestriction] = GlobalRef[TestGlobalRefRestriction](TestGlobalRefRestriction.this); 
		val z3 = w; // ok, because the outer class is already cooked.
		def this() {
			k1 = GlobalRef[Inner](this); // ERR
			w = GlobalRef[TestGlobalRefRestriction](TestGlobalRefRestriction.this);
		}
	}
}


class TestFieldInitializer {
	var flag:Boolean = true;
	var z:Int = flag ? 3 : z+1; // ERR: Cannot read from field 'z' before it is definitely assigned.
	val j = flag ? 3 : foo(); // ERR: reads from j before it is assigned.
	val k = foo();	
	var i:Int{self!=0};
	@NonEscaping final def foo():Int {
		val z = j;
		i = 1;
		return 2;
	}
}

class Test2 {
    val layout:Int{self!=0};
	def this() {
		bla(); // ERR: bla() reads from layout before it is written to!
		layout = 1;
	}
	@NonEscaping private def bla() {
		Console.OUT.println(layout); // previously printed 0
	}
}

class Person {
  var name:String{name!=null};
  def this(name:String{name != null}) {
    setName(name);
  }
  @NonEscaping public final def setName(name:String{name != null}) {
    this.name = name;
  }
}
class Example1 {
  var flag:Boolean;
  var i1:Int{self!=0};
  def this() {
    setI(); 
    // i1 is definitely-assigned now
  }
  @NonEscaping final def setI() {
    if (flag) {
      i1 = 2;
    } else {
      setI();
    }
  }
}
class Example2 {
  var flag:Boolean;
  var i1:Int{self!=0};
  var i2:Int{self!=0};
  def this() {
    finish m2();
  }
  //Read=[i1] SeqWrite=[i2] Write=[i1,i2] 
  @NonEscaping final def m1() {
    val z1 = i1;
    if (flag) {
      async { i1 = 1; }
      i2 = 2;
      val z2 = i2; // doesn't need to be in Read set, because we assigned to it beforehand.
    } else {
      m2();
      i2 = 3;
    }
  }
  //Read=[] SeqWrite=[i1] Write=[i1,i2]
  @NonEscaping private def m2() {
    if (flag) {
      finish async { i1 = 1; val z = i1; }
      async { i2 = 2; }
    } else {
      i1 = 3;
      m1();
    }
  }
}
class Example3 {
  var flag:Boolean;
  var i1:Int{self!=0};
  var i2:Int{self!=0};
  var i3:Int{self!=0};

  def this() {
    m1();
  }
  def this(i:Int) {
    m2();
  }
  //Read=[] SeqWrite=[i1,i2,i3] Write=[i1,i2,i3]
  @NonEscaping final def m1() { 
    i1 = 1;
    m2();
  }
  //Read=[] SeqWrite=[i1,i2,i3] Write=[i1,i2,i3]
  @NonEscaping private def m2() {
    i2 = 2;
    m3();
  }
  //Read=[] SeqWrite=[i1,i2,i3] Write=[i1,i2,i3]
  @NonEscaping private def m3() {
    i3 = 3;
    if (flag) {
        i1=1; i2=2; // stop the recursion.
    } else {
        m1();
    }
  }
}


class LegalExample {
  val f1:Int = 4*2; 
  val f2:Int; // must be initialized in every ctor
  var v1:Int; // has a default value, does not have to be assigned
  var v2:Int{self!=0};  // must be assigned because there is no default value
  def this() {
    this(4);
  }
  def this(i:Int{self!=0}) {
    super();
    f2 = m1();
    setV2(i);
  }
  @NonEscaping private def m1():Int = v1++;
  @NonEscaping public final def setV2(i:Int{self!=0}) { v2 = i; }
}
class IllegalExample {
  var f2:Int{self!=0}; 
  var v2:Int{self!=0};  
  def this() { // ERR field is not initialized in this()
    f2 = 3;
    if (3==4) setV2();
  }
  def this(i:Int) { // ERR field is not initialized in this(Int)
    setV2();
  }
  @NonEscaping final def setV2() { v2 = 3; }
}
class IllegalExample2[T] {
  var t:T; // ERR (not initialized)
}

class SuperTest22 {
	def this() {
		foo();
	}
	final def foo() { // ERR (warning)
	}
}
class SuperCallTest extends SuperTest22 {
	def this() {
		super();
		foo(); // ERR (cannot call super methods in a private constructor unless annotated with @NonEscaping)
	}
}

class TestOverLoadingWithLiterals {
	static def f(Byte)=1;
	static def f(Int)=2;
	public static def main(Array[String]) {
		val i1:Int{self==1} = f(1y);
		val i2:Int{self==2} = f(1);
		if (i1!=1 || i2!=2) throw new Exception("Failed");
	}
}
class TestFieldInitForwardRef {
	val Y:Int = this.X;
	static val X:Int = 2;

	class Inner {
		var g:Int = this.f+2; // ERR
		var h:Int = this.h*3; // ERR
		var f:Int = q+2;
	}
	static class StaticInner {
		var f:Int;
		var g:Int = this.f+2; 
		var h:Int = this.h*3; // ERR
	}

	var z1:Int = foo(null); 
	var z2:Int = this.foo(null); 
	var z3:TestFieldInitForwardRef = this; // ERR
	var z4:Int = z3.foo(this); // ERR
	var z5:Int = z3.foo(null);
	var w1:Int = z3.a;
	var w2:Int = this.a; // ERR
	
	var a:Int = q+2; // ERR
	var b:Int = this.q+2; // ERR
	var c:Int = a+b+this.q+2; // ERR
	var w:Int = a*b+this.c;
	var p:Int = a*b+this.c+q; // ERR
	var q:Int = 1;

	var e:Inner = new Inner(); // ERR
	var e2:StaticInner = new StaticInner();

	@NonEscaping private def foo(arg:TestFieldInitForwardRef):Int = 3;
}


// The following types haszero:
//    * a type that can be null  (e.g., Any, closures, but not a struct or Any{self!=null} )
//	  * Primitive structs (Short,UShort,Byte,UByte, Int, Long, ULong, UInt, Float, Double, Boolean, Char)
//    * user defined structs without a constraint and without a class invariant where all fields haszero.
class SimpleUserDefinedStructTest {	
	static struct S(x:Int, y:Int) {}
	static struct S2 {
		val x:Int = 1;
	}
	static struct S3 {x==1} {
		val x:Int = 1;
	}
	static struct S4 {
		val x = 1;
	}
	static struct S5[T] {T haszero} {
	  val s:S2 = Zero.get[S2](); 
	  val t:T = Zero.get[T](); 
	}
	static struct S6 {
		val s:Int{self==1} = 1;
	}
	static struct S7 {
		val s:Int{self==0} = 0;
	}

	static class C {
	  var s:S; 
	  var x:S2; 
	  var y:S3; // ShouldBeErr (because class invariant are not treated correctly: X10ClassDecl_c.classInvariant is fine, but  X10ClassDef_c.classInvariant is wrong)
	  var z1:S4; // ERR
	  var z2:S5[Int];
	  var z3:S6; // ERR
	  var z4:S7;

	  var s6:S{self.y==0};  // ERR (any constrained user-defined struct, doesn't haszero. because of a bug in ConstrainedType_c.fields())
	}
	def main(Array[String]) {
		Console.OUT.println( Zero.get[S5[S5[Int]]]().t.t );
	}
}
class ConstraintPropagationToFields {
	static struct S(x:Int,y:Int) {
		def this(a:Int,b:Int):S{self.x==a,self.y==b} {
			property(a,b);
		}
	}
	static class C {
	  val s1:S{y!=0} = S(0,1); 
	  val s2:S{y!=0} = S(1,0); // ERR: The type of the field initializer is not a subtype of the field type.
	  val z1:Int{self!=0} = s1.y;
	  @ShouldBeErr val z2:Int{self==0} = s1.y;
	}
}
struct UserDefinedStruct {}
class TestFieldsWithoutDefaults[T] {
	// generic parameter test
	var f2:T; // ERR

	// includes: Short,UShort,Byte,UByte, Int, Long, ULong, UInt, Float, Double, Boolean, Char
	// primitive private struct tests
	var i1:Int;
	var i2:Int{self==0};
	var i3:Int{self!=1};
	var i4:Int{self!=0}; // ERR

	var y1:Byte;
	var y2:Byte{self==0y};
	var y3:Byte{self!=1y};
	var y4:Byte{self!=0Y}; // ERR

	var s1:Short;
	var s2:Short{self==0s};
	var s3:Short{self!=1S};
	var s4:Short{self!=0s}; // ERR

	var ub1:UByte;
	var ub2:UByte{self==0yu};
	var ub3:UByte{self!=1uY};
	var ub4:UByte{self!=0yu}; // ERR

	var us1:UShort;
	var us2:UShort{self==0su};
	var us3:UShort{self!=1us};
	var us4:UShort{self!=0Su}; // ERR

	var l1:Long;
	var l2:Long{self==0l};
	var l3:Long{self!=0l}; // ERR
	var ul1:ULong;
	var ul2:ULong{self==0ul};
	var ul3:ULong{self!=0lu}; // ERR
	var ui1:UInt;
	var ui2:UInt{self==0u};
	var ui3:UInt{self!=0u}; // ERR
	var ff1:Float;
	var ff2:Float{self==0.0f};
	var ff3:Float{self!=0.0f}; // ERR
	var d1:Double;
	var d2:Double{self==0.0};
	var d3:Double{self!=0.0}; // ERR
	@x10.compiler.Uninitialized var b0:Boolean;
	var b1:Boolean;
	var b2:Boolean{self==true}; // ERR
	var b3:Boolean{self!=false}; // ERR
	var b4:Boolean{self==false};
	var b5:Boolean{self}; // ERR
	var b6:Boolean{!self};
	var ch1:Char;
	var ch2:Char{self=='\0'};
	var ch3:Char{self!='\0'}; // ERR

	// references (with or without null)
	var r0:Array[Int{self!=0}];
	var r1:Array[T];
	var r2:Any;
	var r3:Array[T]{self!=null}; // ERR
	var r4:Any{self!=null}; // ERR

	// closures can be null
	var c1:() => Int = null;
	var c2:() => void; 
	var c3:(t:T) => T;


	// user-defined private struct examples
	var definedS1:UserDefinedStruct;

}








class EscapingCtorTest(p:EscapingCtorTest) {
	var tt:EscapingCtorTest;
	val w:Int;
	val v1:Int = 1;
	var x1:Int;
	static def foo(t:EscapingCtorTest)=2;	
	static def bar(t:Inner)=2;	
	def this() {
		this(null);
	}
	def this(i:Int) {
		this(i,null);
	}
	def this(a:EscapingCtorTest) {
		property(a);
		val q:EscapingCtorTest = null;
		w = 2;
		val alias = q; 
		val callToString = ""+q;
		val callOp1 = q+q; 
		q.tt = q;
		this.tt = q;
		foo(q);
		q.m();
		q.z(q);
		val inner = q.new Inner();
	}
	def this(i:Int,a:EscapingCtorTest) {
		property(a);
		val q:EscapingCtorTest = null;
		w = 4;
		val alias = this; // ERR
		val callToString = ""+this; // ERR
		val callOp1 = q+this; // ERR
		val callOp2 = this+q; // ERR
		val callOp3 = q*this; // ERR
		val callOp4 = this*q; // ERR
		val callApply1 = this(null);
		val callApply2 = this(this); // ERR
		this(null);
		q.tt = this; // ERR
		this.tt = q;
		q.tt = this.tt;
		foo(this); // ERR
		q.z(this);  // ERR
		val inner1 = new Inner(); // ERR
		val inner2 = this.new Inner(); // ERR
		this.m(); 

	}
	final operator this+(that:EscapingCtorTest):EscapingCtorTest = null;
	final operator (that:EscapingCtorTest)*this:EscapingCtorTest = null;
	@NonEscaping final operator this(that:EscapingCtorTest):EscapingCtorTest = null;

	@NonEscaping final def m() {
		g();
	}
	@NonEscaping private def g() {
		z(null);
	}
	@NonEscaping final def z(q:EscapingCtorTest) {
		q.g();
		g();
		val inner1 = new Inner(); // ERR
		val callOp1 = q+this; // ERR
	}

	// inner class - this of the inner class cannot escape, but the outer can escape (because you access it's fields via methods, e.g., Outer.this.getHeader())
	class Inner {
		val f:Int;
		val v2:Int = 4;
		var x2:Int;
		def this() {
			f = 3;
			x2 = v2;
			x2 = v1;
			// Outer "this" can escape
			g(); 
			EscapingCtorTest.this.g();
			z(EscapingCtorTest.this);
			// Inner "this" can NOT escape
			f(null); 
			this.f(null); 
			val z:Inner = null;
			z.f(z);
			z.f(this); // ERR
			bar(this); // ERR
			bar(z);
		}
		@NonEscaping private def f(inner:Inner) {}
	}
}


class Example4 {
  var i1:Int{self!=0};
  var i2:Int{self!=0};
  var i3:Int{self!=0};

  def this() {
    m1(); 
  }
  def this(i:Int) {
    m2(); 
  }
  @NonEscaping final def m1() {
    i1 = 1;
    m2();
  }
  @NonEscaping private def m2() {
    i2 = 2;
    m3();
  }
  @NonEscaping private def m3() {
    i3 = 3;
	if (i3==4) {
		i1=1; i2=2; // stop the recursion.
	} else {
	    m1();
	}
  }
}


struct Bla2Struct {
	val x = 4;
	def equals(i:Int)=false;
}

struct Outer[U] {
	static struct Test[U] {
		def blabla()=2;
	}
	def bla() {
		Console.OUT.println(Test[U]().blabla());
		return 42;
	}
}
	
interface InterfaceForStruct {}
struct BlaStruct(p:Int) implements InterfaceForStruct {
	val i:Int = 4;
	val jj:String =  new String("as");
	val j:UInt = 4;
	val h:Object = new Object();
	def this(p:Int) {		property(p);	}
}
struct Test3[U,B] {
	val u:U;
	val b:B;
	def this(u:U,b:B) {
		this.u = u;
		this.b = b;
	}
}

class TestSwitchOnFinalVal {
    val i=2+1;
	def this() {
		switch(3+0) {
            case i:
            case 4:
        }
    }   
}







class TestBreaksInAsyncAt {
	class Shyk_Flup  { // XTENLANG-823
	  public def test() {
		 
		 lesp_frobi: for (var i : Int = 0; i < 10; i++) {
			 if (i<3) break lesp_frobi;
		   finish async{
			 break lesp_frobi; // ERR: Cannot break in an async
		   }
		   x10.io.Console.OUT.println("This must not happen!");
		 }
	  }
	}

	var flag:Boolean;
	
	def test1() {
		while (flag) {
			if (flag) continue;
			if (flag) break;
		}
	}
	def test2() {
		while (flag) {
			at (here) {
				if (flag) continue;
				if (flag) break;
			}
		}
	}
	
	def test3() {
		while (flag) {
			async {
				while (flag) {
					if (flag) break;
				}
			}
		}
	}

	def failTestLabel() {
		while (flag) {
			if (flag) continue non_existing_label; // ERR: Target of branch statement not found.
		}
	}
	def failTest1() {
		while (flag) {
			async {
				if (flag) continue; // ERR: Cannot continue in an async
			}
		}
	}
	def failTest2() {
		while (flag) {
			async {
				if (flag) break; // ERR: Cannot break in an async
			}
		}
	}
	def failTest3() {
		while (flag) {
			async {
				if (flag) return; // ERR: Cannot return from an async.
			}
		}
	}
}



class Possel811 { //XTENLANG-811
  interface I {
	def i():void; 
  }
  def test() {
    new I(){}; // ERR: <anonymous class> should be declared abstract; it does not define i(): void, which is declared in Possel811.I
  }
}

class LocalVarInAsyncTests {
	def simple() {
		var i:Int = 2;
		finish async i++;
	}
	def atAndAsync() {
		var i:Int = 2;
		val p = here;
		finish at (here.next()) async {
		  finish async at (p) 
			  i++; // ok
		}
	}
	def test1() {		
		var i:Int = 2;
		finish async {
		  finish async i++; // ok
		}
	}
	def test2() {
		var i:Int = 2;
		finish {
			val x= ()=> { async 
				i++; // ERR: cannot capture local var in a closure
			};
		}
	}
	var flag:Boolean;
	def test3() {
		var i:Int = 2;
		if (flag) {
		  finish async i++; // ok
		}
	}
	def test4() {
		var i:Int = 2;
		async 
			i++;  // ERR: Local var 'i' cannot be captured in an async if there is no enclosing finish in the same scoping-level as 'i'; consider changing 'i' from var to val.
		async async
			i++; // ERR
		async finish async finish async finish async async
			i++; // ERR
		async {
		  finish async i++; // ERR
		}
	}
}
class AccessOfVarIllegalFromClosure { // XTENLANG-1888
	val x:Int = 1;
	var y:Int = 1;

    public def run() {
        
        val a:Int = 1;
        var b:Int = 1;

        val closure = 
			() => 
				x+
				y+
				a+
				b; // ERR: Local variable "b" is accessed from an inner class or a closure, and must be declared final.
    }

	static def use(i:Any) {}
	def test2() {
		var q:Int = 1;
		finish async q=2;
		use(() => {
			use(q); // ERR: Local variable "q" is accessed from an inner class or a closure, and must be declared final.
		});
		use(() => { async
			use(q); // ERR: Local variable "q" is accessed from an inner class or a closure, and must be declared final.
		});
		use(() => { finish async async
			use(q); // ERR: Local variable "q" is accessed from an inner class or a closure, and must be declared final.
		});
		
		use(() => { var q:Int = 1;
			use(q);
		});
		use(() => { var q:Int = 1; async
			use(q); // ERR
		});
		use(() => { var q:Int = 1; finish async { async
			use(q);
		}});
		use(() => { finish async { var q:Int = 1; async
			use(q); // ERR
		}});
		use(() => { finish { var q:Int = 1; async { async
			use(q); // ERR
		}}});
		use(() => { finish { async { async {var q:Int = 1; 
			use(q);
		}}}});
		use(() => { finish { async { async {
			use(q); // ERR: Local variable "q" is accessed from an inner class or a closure, and must be declared final.
			var q:Int = 1; 
		}}}});
		val w = q;
	}
}

class TestCaptureVarInClosureAsyncInnerAnon {
	def test() {
		var x:Int = 2;
		class Inner {
			val y = 
				x+2; // ERR: Local variable "x" is accessed from an inner class or a closure, and must be declared final.
		}
		val inner = new Inner();
		val anonymous = new Object() {
			val y =
				x+2; // ERR: Local variable "x" is accessed from an inner class or a closure, and must be declared final.
		};
		val closure = () =>
			x+2; // ERR: Local variable "x" is accessed from an inner class or a closure, and must be declared final.
		finish async { 
			val y =
				x+2;
		}
	}
}
class TestVarAccessInClosures {
   val a1:int = 1;
   var a2:int = 1;
    public def run() {
        
        val b1:int = 1;
        var b2:int = 1;

        class C {
            val c1:int = 1;
            var c2:int = 1;
            def foo() = {
                val fun = () => {
                    val d1:int = 1;
                    var d2:int = 1;
                    (() => a1+b1+c1+d1+
						a2+
						b2+ // ERR: Local variable "b2" is accessed from an inner class or a closure, and must be declared final.
						c2+
						d2  // ERR: Local variable "d2" is accessed from an inner class or a closure, and must be declared final.
						)()
                };
                fun()
            }
        }
	}
}


class TestOnlyLocalVarAccess {
	// for some reason, the origin of "frame" is null. I can reproduce it using type inference: see XTENLANG-1902
	var i:Int;
	static def testInferReturnType()=test0(null);
	static def test0(var b:TestOnlyLocalVarAccess) { // we type-checking test0 twice: once to infer its return type (then "b"'s placeTerm is null), then a second time to really type-check it (then the placeTerm is fine)
		b.i++;
	}

	static def use(x:Any) {}
	static def testUse() {
		var x:Int = 0;
		at (here) use(x);
		use(x);
		at (here.next()) 
			use(x); // ERR: Local variable "x" is accessed at a different place, and must be declared final.
		val place1 = here;
		val place2 = place1;
		at (here.next()) {
			use(x); // ERR: Local variable "x" is accessed at a different place, and must be declared final.
			at (place1) 
				use(x);
			at (place2) 
				use(x);
			at (place2) at (here) 
				use(x);
			at (place2) at (here.next()) 
				use(x); // ERR: Local variable "x" is accessed at a different place, and must be declared final.
			at (place1) at (place2) 
				use(x);
		}
		use(x);
	}
	static def test0() {
		var x:Int = 0;
		at (here) x=20;
		x=10;
		at (here.next()) 
			x=1; // ERR: Local variable "x" is accessed at a different place, and must be declared final.
		val place1 = here;
		val place2 = place1;
		at (here.next()) {
			x=2; // ERR: Local variable "x" is accessed at a different place, and must be declared final.
			at (place1) 
				x=3;
			at (place2) 
				x=4;
			at (place1) at (place2) 
				x=5;
		}
		Console.OUT.println(x);
	}
	def test1() {
		var x:Int = 0;
		at (here) x=20;
		x=10;
		at (here.next()) 
			x=1; // ERR: Local variable "x" is accessed at a different place, and must be declared final.
		val place1 = here;
		val place2 = place1;
		at (here.next()) {
			x=2; // ERR: Local variable "x" is accessed at a different place, and must be declared final.
			at (place1) 
				x=3;
			at (place2) 
				x=4;
			at (place1) at (place2) 
				x=5;
		}
		Console.OUT.println(x);
	}
	def test2(var x:Int) {
		at (here) x=20;
		x=10;
		at (here.next()) 
			x=1; // ERR: Local variable "x" is accessed at a different place, and must be declared final.
		val place1 = here;
		val place2 = place1;
		at (here.next()) {
			x=2; // ERR: Local variable "x" is accessed at a different place, and must be declared final.
			at (place1) 
				x=3;
			at (place2) 
				x=4;
			at (place1) at (place2) 
				x=5;
		}
		Console.OUT.println(x);
	}

	def test3(a: DistArray[double](1)) {		
        var n: int = 1;
		at (here) n=2;
		finish ateach ([i] in a.dist | 
			(0..n)) { // checks XTENLANG-1902
			n++; // ERR: Local variable "n" is accessed at a different place, and must be declared final.
		}
	}
}
class TestValInitUsingAt { // see XTENLANG-1942
	static def test() {
		val x:Int;
		at (here.next()) 
			x = 2;
		val y = x; // ShouldNotBeERR: Semantic Error: "x" may not have been initialized
	}
    static def test2() {
        var x_tmp:Int = 0; // we have to initialize it (otherwise, the dataflow
        val p = here;
        at (p.next())
          at (p)
            x_tmp = 2;
        val x = x_tmp; // if we hadn't initialized x_tmp, then the dataflow would complain that "x_tmp" may not have been initialized
    }
}


class TestGlobalRefHomeAt { // see http://jira.codehaus.org/browse/XTENLANG-1905
	def test() {
		val p = here;
		val r:GlobalRef[TestGlobalRefHomeAt]{home==p} = GlobalRef[TestGlobalRefHomeAt](this);
		val r2:GlobalRef[TestGlobalRefHomeAt]{home==p} = r;
		val r3:GlobalRef[TestGlobalRefHomeAt]{home==p} = GlobalRef[TestGlobalRefHomeAt](this);

		use(r());
		use(r2());
		use(r3());

		at (r.home()) {
			use(r()); 
			use(r2()); // ShouldNotBeERR
			use(r3()); // ShouldNotBeERR
		}
		at (r2.home()) {
			use(r()); // ShouldNotBeERR
			use(r2()); 
			use(r3());// ShouldNotBeERR
		}
		at (r3.home()) {
			use(r()); // ShouldNotBeERR
			use(r2()); // ShouldNotBeERR
			use(r3());
		}
		at (here.next()) {
			use(r()); // ERR
			use(r2()); // ERR
			use(r3()); // ERR			
			
			at (r.home()) {
				use(r()); 
				use(r2()); // ShouldNotBeERR
				use(r3()); // ShouldNotBeERR
			}
			at (r2.home()) {
				use(r()); // ShouldNotBeERR
				use(r2()); 
				use(r3());// ShouldNotBeERR
			}
			at (r3.home()) {
				use(r()); // ShouldNotBeERR
				use(r2()); // ShouldNotBeERR
				use(r3());
			}
		}
	}
	def use(x:Any) {}
}
class TestGlobalRefHomeAt2 {
	 private val root = GlobalRef(this); 
	 def test1() {
		 val x = (new TestGlobalRefHomeAt2()).root; 
		 return @ShouldNotBeERR x();
	 }
	 def test2() {
		 val x = (at (here.next()) new TestGlobalRefHomeAt2()).root; 
		 return @ERR x();
	 }
	 def test3() {
		 val x = at (here.next()) (new TestGlobalRefHomeAt2().root); 
		 return @ERR x();
	 }
	 def test4() {
		 val x = this.root; 
		 return @ShouldBeErr x();
	 }
	 def test5(y:TestGlobalRefHomeAt2) {
		 val x = y.root; 
		 return @ERR x();
	 }
}

class A564[T,U] {
    def foo(x:T,y:U, z:String):void {}
}
class B564 extends A564[String,String] {
    def foo(x:String,y:String, z:String):Int { return 1; } // ERR: foo(x: x10.lang.String, y: x10.lang.String, z: x10.lang.String): x10.lang.Int in B cannot override foo(x: x10.lang.String, y: x10.lang.String, z: x10.lang.String): void in A[x10.lang.String, x10.lang.String]; attempting to use incompatible return type.
	// B.foo(String,String,String) cannot override A.foo(T,U,String); attempting to use incompatible return type.  found: int required: void
}


class ReturnStatementTest {
	
	class A {
	  def m() {
		at (here.next()) return here;// ShouldNotBeERR (Semantic Error: Cannot return value from void method or closure.)// ShouldNotBeERR (Semantic Error: Cannot return a value from method public x10.lang.Runtime.$dummyAsync(): void.)
	  }
	  def test() {
			val x1 = m();// ShouldNotBeERR (Semantic Error: Local variable cannot have type void.)
			val x2 = m();// ShouldNotBeERR (Semantic Error: Local variable cannot have type void.)
			testSameType(x1,x2,[x1,x2]);
	  }
	  def testSameType[T](x:T,y:T,arr:Array[T]) {}
	}
	
	static def ok(b:Boolean):Int {
		if (b) 
			return 1;
		else {
			async {
				val closure = () => {
					return 3;
				};
			}
		}
		at (here.next())
			return 2; // ShouldNotBeERR ERR todo: we get 2 errors: Cannot return value from void method or closure.		Cannot return a value from method public static x10.lang.Runtime.$dummyAsync(): void
		finish {
			async { val y=1; }
			return 3;
		}
	}
	static def err1(b:Boolean):Int {
		if (b) return 1;
		async 
			return 2; // ERR: Cannot return from an async.
		return 3;
	}
	static def err2(b:Boolean):Int {
		async at (here) 
			return 1; // ERR: Cannot return from an async.
		return 2;
	}
	static def err3(b:Boolean):Int {
		at (here) async
			return 1; // ERR: Cannot return from an async.
		return 2;
	}
	static def err4(b:Boolean):Int {
		finish async 
			return 1; // ERR: Cannot return from an async.
		return 2;
	}
}


// Test method resolution

class TestMethodResolution { // see XTENLANG-1915
  def m(Int)="";
  def m(Long)=true;
  def m(Any)=3;
  def test(flag:Boolean) {
	val arr = [1,2l]; // infers Array[Any]
	val x = flag ? 1 : 2l; // infers Long
    val i1:Boolean = m(x); 
    val i2:Int = m(arr(0)); // ShouldBeErr
  }

  
	def check[T](t:T)=1;
	def check(t:Any)="";
	def testGenerics() {
		val r1:Int = check(1); // ShouldBeErr: should resolve to the non-generic method
		val r2:Int = (check.(Any))(1); // ShouldBeErr: should DEFNITELY resolve to the non-generic method
        val r3:Int = check[Int](1); // be explicit
		val r4:Int = (check.(Any))(1); // ShouldBeErr: ahhh?  be explicit
		// todo: What is the syntax for generic method selection?
		// neither "(m.[String](String))" nor "(m[String].(String))" parses.
	}
}


class TestHereInGenericTypes { // see also XTENLANG-1922
	static class R {
	  val x:Place{self==here} = here;
	}
	static def foo(y:Place{self==here}) {
		assert y==here; // will fail at runtime! but according to the static type it should succeed!
	}
	static def bar(y:Place{self==here}) {
		at (here.next()) foo(y); // ERR: todo: how can we report an error message that won't contain _place6 ?
		// Today's error: Method foo(y: x10.lang.Place{self==here}): void in TestHereInGenericTypes{self==TestHereInGenericTypes#this} cannot be called with arguments (x10.lang.Place{self==y, _place6==y});    Invalid Parameter.
//	 Expected type: x10.lang.Place{self==here}
//	 Found type: x10.lang.Place{self==y, _place6==y}
	}
  static def testR() {
	val r = new R();
	at (here.next()) {
	  val r2:R = r; // This is the only "hole" in my proof: you can say that the type of "r" changed when it crossed the "at" boundary. But that will puzzle programmers...
	  foo(r2.x); // we didn't cross any "at", so from claim 3, the type of "r2.x" didn't change.
	}
  }



  private static class Box[T](t:T) {}
  def test() {
    val b:Box[Place{self==here}] = null;
	val p1:Place{self==here} = b.t;
	val HERE = here;
	at (here.next()) {
		val b2:Box[Place{self==here}] = b; // ERR
		val p2:Place{self==here} = b.t; // ERR
		val b3:Box[Place{self==HERE}] = b;
		val p3:Place{self==HERE} = b.t;
	}
  }
  
	static def m(p:Place{self==here}) {}
	static def test1(p:Place{self==here}) {
		m(p); // ok
		at (here.next()) {
			m(p); // ERR
		}
	}
	static def test2(p:Place) {p==here} {
		m(p); // OK
		at (here.next()) {
			m(p); // ShouldBeErr (XTENLANG-1929)
		}
	}
}

class TestInterfaceInvariants { // see XTENLANG-1930
	interface I(p:Int) {p==1} {}
	class C(p:Int) implements I {
		def this() { 
			property(0); // ShouldBeErr
		}
	}
	interface I2 extends I{p==2} {} // ShouldBeErr
	interface I3 {p==3} extends I2 {} // ShouldBeErr
	static def test(i:I) {
		@ShouldBeErr var i1:I{p==5} = i;
		var i2:I{p==1} = i;
	}
}

class OuterThisConstraint(i:Int) { // see XTENLANG-1932
	def m1():OuterThisConstraint{self.i==this.i} = this;
	class Inner {
		def m2():OuterThisConstraint{self.i==OuterThisConstraint.this.i} = OuterThisConstraint.this;
	}
	static def test(a:OuterThisConstraint{i==3}) {
		val inner:OuterThisConstraint{self.i==3}.Inner = a.new Inner();
		val x1:OuterThisConstraint{i==3} = a.m1();
		val x2:OuterThisConstraint{i==3} = inner.m2();
		@ShouldBeErr val x3:OuterThisConstraint{i==4} = inner.m2();
	}
}

class NullaryPropertyMethod {
	static class E(x:Int) {
		property y() = x==2;
		property z = x==2;
		
		public static def test() {		
			val e = new E(2);
			var e1:E{y()} = null; // ShouldNotBeERR: Method or static constructor not found
			e1 = e as E{y()}; // ShouldNotBeERR: Method or static constructor not found
			var e2:E{z} = null;
			e2 = e as E{z};
			var e3:E{y} = null;
			e3 = e as E{y};
			var e4:E{z()} = null; // ShouldNotBeERR: Method or static constructor not found
			e4 = e as E{z()}; // ShouldNotBeERR: Method or static constructor not found
		}
		public static def main(Array[String]) {
			val e = new E(2);
			var e1:E{self.y()} = null;
			e1 = e as E{self.y()};
			var e2:E{self.z} = null;
			e2 = e as E{self.z};
			var e3:E{self.y} = null;
			e3 = e as E{self.y};
			var e4:E{self.z()} = null;
			e4 = e as E{self.z()};
		}
	}
}
class TestXtenLang1938 { // see XTENLANG-1938
	public static def main(Array[String]) {
		val h = new TestXtenLang1938();
		val x = h+h; // ShouldNotBeERR: Local variable cannot have type void
		// h+h; // doesn't parse! maybe it should?

	}
	static operator (p:TestXtenLang1938) + (q:TestXtenLang1938) { // ShouldBeErr: operators results are parsed as expressions (so now they can't return void)
		Console.OUT.println("overloaded +");
	}
}
/**
see: 
http://jira.codehaus.org/browse/XTENLANG-1445
http://jira.codehaus.org/browse/XTENLANG-865
http://jira.codehaus.org/browse/XTENLANG-638
http://jira.codehaus.org/browse/XTENLANG-1470
http://jira.codehaus.org/browse/XTENLANG-1519
*/
class TestCoAndContraVarianceInInterfaces {
	
	 class CtorTest[+T] {
	  val t1:T;
	  def this(t:T) { t1 = t; } // ok
	  class Inner {
		val t2:T;
		def this(t:T) { t2 = t; } // ok
	  }
	}
	class Bar[T] {
		def bar[U]() {}
		//def bar2[+U]() {} // parsing error, which is good :)
		def foo(z:T, y:Bar[T],x:Box[Bar[Bar[T]{self!=null}]]{x!=null}) {}
	}

	interface Covariant[+T] {
		def get():T;
		def set(t:T):void; // ERR
	}
	interface Contravariant[-T] {
		def get():T; // ERR
		def set(t:T):void; 
	}
	interface Invariant[T] {
		def get():T;
		def set(t:T):void;
	}

	// check extends
	interface E1[+T] extends Covariant[T] {}
	interface E2[-T] extends Covariant[T] {} // ERR
	interface E3[+T] extends Contravariant[T] {} // ERR
	interface E4[-T] extends Contravariant[T] {} 
	interface E5[+T] extends 
		Contravariant[T], // ERR
		Covariant[T] {} 
	interface E6[-T] extends 
		Contravariant[T],
		Covariant[T] {} // ERR
	interface E7[T] extends Contravariant[T],Covariant[T] {}
	interface E8[+T] extends Contravariant[Contravariant[T]] {}
	interface E9[-T] extends 
		Contravariant[Contravariant[T]] {}  // ERR (todo: error should be on the use of T, and not on the entire TypeNode, see XTENLANG-1439): "Cannot use contravariant type parameter T in a covariant position"
	interface E10[-T] extends Invariant[T] {} // ERR: "Cannot use contravariant type parameter T in an invariant position"

	interface GenericsAndVariance[+CO,-CR,IN] {
		def ok1(CR,IN):CO;
		def ok2(CR,IN):IN;
		def ok3(CR,IN):void;
		def ok4():Contravariant[CR];
		def ok5():Contravariant[IN];
		def ok6():Contravariant[Contravariant[Contravariant[CR]]];
		def ok7():Covariant[CO];
		def ok8():Covariant[IN];
		def ok9(GenericsAndVariance[CR,CO,IN]):void;
		def ok10():GenericsAndVariance[CO,CR,IN];
		def ok11(GenericsAndVariance[IN,IN,IN]):void;
		def ok12():GenericsAndVariance[IN,IN,IN];
		def ok13( (CO)=>void, ()=>CR, ()=>IN, (IN)=>IN ): ((CR)=>CO);

		def err1():CR; // ERR
		def err2(CO):void; // ERR
		def err3():Contravariant[CO]; // ERR
		def err4():Covariant[CR]; // ERR
		def err5(GenericsAndVariance[CO,IN,IN]):void; // ERR
		def err6(GenericsAndVariance[IN,CR,IN]):void; // ERR
		def err7(GenericsAndVariance[IN,IN,CO]):void; // ERR
		def err8(GenericsAndVariance[IN,IN,CR]):void; // ERR
		def err9():GenericsAndVariance[CR,IN,IN]; // ERR
		def err10(): ( (CO)=>void ); // ERR
		def err11(): ( ()=>CR ); // ERR
		def err12((CR)=>void):void; // ERR
		def err13(()=>CO):void; // ERR
	}

	// todo: what about constraints? and properties fields and methods?
	interface Constraints[+CO,-CR,IN](p1:CO,p2:IN,p3:(CR)=>void) 
		// todo: can we variance in the constraint?
		{ CO <: CR ,
		  CO <: IN ,
  		  CR <: CO ,
  		  CR <: IN ,
  		  IN <: CO ,
  		  IN <: CR ,
		  IN <: String,
		  CO <: String,
		  CR <: String,
		  String <: CO,
		  String <: CR,
		  String <: IN,
  		  CO <: Contravariant[CO] ,
  		  CR <: Covariant[CR]
		}
	{
		// todo: can we use variance in method guards?
		def m() { CO <: CR } :void;
		// todo: property methods?
		property pm():CO;
	}

	interface Comparable[+T] {}
	static class Foo implements Comparable[Foo] {
		def test() {
			val x:Comparable[Foo] = this;
			val y:Comparable[Comparable[Foo]] = this;
			val z:Comparable[Comparable[Comparable[Foo]]] = this;
		}
	}
}


class ConstraintsBugs {
	class A(p:Int) {
		def this(p:Int):A{self.p==p} {
			property(p);
		}
	}
	class B extends A{p==1} {
		def this():B{self.p==1} {
			super(2); // ShouldBeErr
		}
	}
}

class SuperQualifier { // see XTENLANG-1948
	class Parent {
	public val f = 2;
	}
	class Ego extends Parent {
		// todo: this error blocks the entire next dataflow phase 
	//val x = Parent.super.f;  // ShouldNotBeErr: The nested class "Ego" does not have an enclosing instance of type "Parent".
	}
}
class TestArrayLiteralInference {	
	var z: Array[int](1){rect, zeroBased, size==4} = [ 1, 2,3,4 ];
}

class TestInstanceOperators {
static class IntA {
	operator this <  (that:IntA) = true;
}
static class LongB {
	public static operator (r:IntA):LongB = null;
	operator this <  (that:LongB) = false;

	static def test(a:IntA, b:LongB) {
		val res1:Boolean{self==true} = a<a;
		val res2:Boolean{self==false} = b<b;
		val res3:Boolean{self==true} = b<b; // ERR: Cannot assign expression to target.	 Expression: b < b	 Expected type: x10.lang.Boolean{self==true}	 Found type: x10.lang.Boolean{self==false}
		val res5:Boolean{self==false} = b<a; 
		val res4:Boolean{self==false} = a<b; // ok (converts "a" to type LongB)
	}
}
}


class ExplodingPointTest {	
    def f1([i,j]:Point,x:Int)= i+x+j;	
    def f2(p[i,j]:Point(2),x:Int)= p(0)+i+x+j;

    def f3(p[i,j]:Point(1))=1; // ShouldBeErr 

	def test() {
		val [i,j] = [1,2]; 
		return i+j;
	}
	def test2() {
		val p[i,j] = [1,2]; 
		return p(0)+i+j;
	}
	def test3() {
		for(p[i,j] in (1..2)*(3..4)) 
			return p(0)+i+j;
		for(p[i] in (3..4)) 
			return p(0)+i;
		for(p[i]:Point(2) in (3..4)*(3..4))  {} // ShouldBeErr (the type doesn't match the number of exploded ints
		return 3;
	}
	def test4() {
		for(p[i,j] in (3..4)) // ERR: Loop domain is not of expected type.  
			return p(0)+i+j;
		return 3;
	}
	def test5() {
		var r:Region = null;
		for (p in r) {}
		@ShouldBeErr for (p[i] in r) {} //  Loop domain is not of expected type.
	}
	// val p[i,j] = [1,2]; // doesn't parse for fields :)
}


class TestOverloadingAndInterface {
interface XXX {
	def m(Object):Int;
}
interface YYY {
	def m(String):String;
}
class C3 implements XXX,YYY {
	public def m(Object):Int = 1;
	public def m(String):String="";
	def test(c:C3) {
		val f1:XXX = c;
		val f2:YYY = c;
	}
}
class C implements (Any)=>Int {
	public operator this(Any):Int = 1;
	public operator this(String):String="";

	def test(c:C) {
		val x:Int = c(1);
		val y:String = c("");
		val f:(String)=>Int = c;
		val i:Int = f("str");
	}	
}
class D[T] implements (T)=>Int {
	public operator this(T):Int = 1;
	public operator this(String):String="";

	def test(c:D[Any]) {
		val x:Int = c(1);
		val y:String = c("");
		val f:(String)=>Int = c;
		val i:Int = f("str");
	}	
}
class C2 implements (Any)=>Int, (String)=>String {
	public operator this(Any):Int = 1;
	public operator this(String):String="";

	def test(c:C2) {
		val x:Int = c(1);
		val y:String = c("");
		val f1:(String)=>Int = c;
		val f2:(String)=>String = c; 
	}	
}
}

class TestPropertyAssignment(x:Int, y:Int{self==3}) {
    def this(a:Int, b:Int) {
		property(a,b); // ShouldBeErr
    }
}

final class ConstraintsInClosures {
  def f(x:Int) {x!=0} = 1/x;
  def f2(x:Int{self!=0}) = 1/x;
  def test() {
	  val bar: (Int)=>Int = this.f.(Int);  // ERR: should we dynamically generate a new closure that checks the guard?
	  val bar2: (Int)=>Int = this.f2.(Int{self!=0}); // ERR
  }
}
class TestCasting[T] {
	def testCasting(arr:Array[T], func: (Point)=>T) {
	  val arr3 = arr as Array[T]{rank==3}; // ok, constant time check of arr.rank
	  val func3 = func as (Point{rank==3})=>T; // ShouldBeErr: because we can't check it in constant time (we have to create a closure that checks this for every invocation)
	}
}
class FieldNotFound { 
	val q= this.f;  // ERR: Field f not found in type "FieldNotFound{self==FieldNotFound#this}".
}
final class TestCasts { // TestInitInCasts
	val b:Int{b==3} = 3 as Int{b==3}; // ERR: Cannot read from field 'b' before it is definitely assigned.
	val c:Int{c==3} = 3 as Int{self==3};

	def test() {
		val a3:Int = a3*3; // ERR: "a3" may not have been initialized
		val a:Int{a!=5} = 
			3 as Int{a!=5}; // ERR: "a" may not have been initialized
		val a2:Int{a2!=5} = 
			3 as Int{self!=5};
	}
	
	def use(x:Int) {}
	val x:Int;
	val w:TestCasts;
	def this(a:TestCasts) {
		use(3 as Int{a.x==self});
		use(3 as Int{a.w.w.x==self});
		use(3 as Int{x==self}); // ERR: Cannot read from field 'x' before it is definitely assigned.
		val y:Int;
		use(3 as Int{y==self}); // ERR: "y" may not have been initialized
		val b:TestCasts;
		use(3 as Int{b.x==self}); // ERR: "b" may not have been initialized
		use(3 as Int{b.w.w.x==self}); // ERR: "b" may not have been initialized
		use(3 as Int{b.w.x==b.x}); // ERR: "b" may not have been initialized

		val z:Int;
		async use(3 as Int{z==self}); // ERR: "z" may not have been initialized
		z=2;
		async use(3 as Int{z==self});

		beforeX(); // ERR: Cannot read from field 'x' before it is definitely assigned.
		async use(3 as Int{x==self}); // ERR: Cannot read from field 'x' before it is definitely assigned.
		x=2;
		async use(3 as Int{x==self});
		afterX();

		use(3 as Int{w.x==self}); // ERR: Cannot read from field 'w' before it is definitely assigned.
		w=null;
		use(3 as Int{w.x==self});
	}
	def beforeX() {
		use(3 as Int{x==self});
	}
	def afterX() {
		use(3 as Int{x==self});
	}
	def foo(a:TestCasts) {
		use(3 as Int{a.x==self});
		use(3 as Int{a.w.w.x==self});
		use(3 as Int{x==self});
		val y:Int;
		use(3 as Int{y==self}); // ERR: "y" may not have been initialized
		val b:TestCasts;
		use(3 as Int{b.x==self}); // ERR: "b" may not have been initialized
		use(3 as Int{b.w.w.x==self}); // ERR: "b" may not have been initialized
		use(3 as Int{b.w.x==b.x}); // ERR: "b" may not have been initialized
	}

	class Inner {
		val l:Inner;
		def this(i:TestCasts) {
			use(3 as Int{x==self});			
			use(3 as Int{w.x==self});
			use(3 as Int{i.x==self});			
			use(3 as Int{i.w.x==self});
			use(3 as Int{l==null});	// ERR: Cannot read from field 'l' before it is definitely assigned.
			l=null;
			use(3 as Int{l==null});
		}
		def foo() {
			use(3 as Int{l==null});
		}
	}
}


class CyclicInference {
	def f1() {
		if (true) return 2;
		val x = f2(); // err: (only if f1 is before f2): Local variable cannot have type void.
		return 3;
	}
	def f2() {
		val y = f1(); // ShouldBeErr
		return 4;
	}
}

class ScopingRules { // see XTENLANG-2056
	def test1() {
		var i:Int = 1;
		while (true) {
			var i:Int = 1; // ERR: Local variable "i" multiply defined. Previous definition at ...
		}
	}
	def test2() {
		val i:Int = 1;
		at (here) {
			val i:Int = 1; // ShouldBeErr, XTENLANG-2056
		}
	}
	def test3() {
		val i:Int = 1;
		val c = () => {
			val i:Int = 1; // ok
		};
	}
}


	/*
	You can copy&paste this code in Java:
	
	void useInt(int x) {}
	void useLong(long x) {}
	void useFloat(float x) {}
	void useDouble(double x) {}
	void test() {
		...
	}
	*/
class TestOverflows { // see XTENLANG-1774
	def useByte(x:Byte) {}
	def useShort(x:Short) {}
	def useInt(x:Int) {}
	def useLong(x:Long) {}
	def useFloat(x:Float) {}
	def useDouble(x:Double) {}
	
	def useUByte(x:UByte) {}
	def useUShort(x:UShort) {}
	def useUInt(x:UInt) {}
	def useULong(x:ULong) {}
	def test() {
		// todo: more octal tests

		// equivalence of 0x , 0 and decimal representations
		val lit1:Byte{self==0xffY} = -1y;
		val lit2:Byte{self==0377Y} = -1y;
		val lit3:Byte{self==0xfeY} = -2y;
		val lit4:Byte{self==0376Y} = -2y;
		val lit5:UByte{self==0xffUY} = 255uy;
		val lit6:UByte{self==0xfeYU} = 254yu;
		val lit7:UByte{self==0377YU} = 255uy;
		val lit8:UByte{self==0376UY} = 254yu;

		val err1:Byte{self==0xfeY} = -3y; // ERR
		val err2:UByte{self==0xfeYU} = 253yu; // ERR

		// in HEX
		useByte(0x80y); 
		useByte(0x7fy); 
		useShort(0x8000s); 
		useShort(0x7fffs); 
		// in decimal
		useByte(-129y);  // ERR ERR
		useByte(-128y); 
		useByte(127y); 
		useByte(128y);  // ShouldBeErr
		useByte(129y);  // ERR
		useShort(-32769s);  // ERR ERR
		useShort(-32768s); 
		useShort(32767s); 
		useShort(32768s);  // ShouldBeErr
		useShort(32769s);  // ERR
		
		// in HEX
		useUByte(0x00yU); 
		useUByte(0xffUy); 
		useUByte(0x111yU); // ERR
		useUShort(0x0000Us); 
		useUShort(0xffffsU); 
		useUShort(0x11111SU); // ERR
		// in decimal
		useUByte(0yu); 
		useUByte(255uy); 
		useUByte(256uy); // ERR 
		useUShort(0su); 
		useUShort(65535us); 
		useUShort(65536us); // ERR 
		
		// Int & Long
		// in HEX
		useInt(0x80000000);
		useInt(0x7fffffff);
		useInt(0xffffffff); // is negative
		useLong(0x8000000000000000L);
		useLong(0x7fffffffffffffffL);
		useLong(0xffffffffffffffffL);// is negative
		useInt(0x800000000); // ERR: Integer literal 34359738368 is out of range. (todo: better err message)
		useLong(0x80000000000000000L); // ShouldBeErr (currently in generates 0L !)
		// in decimal
		useInt(-2147483648);
		useInt(2147483647);
		useLong(-9223372036854775808l);
		useLong(9223372036854775807l);		
		useInt(-2147483649); // ERR ERR: Integer literal -2147483649 is out of range. (todo: better err message)
		useInt(2147483648); // ShouldBeErr
		useLong(-9223372036854775809l); // ShouldBeErr
		useLong(9223372036854775808l); // ShouldBeErr

		// in octal
		useInt(020000000000);
		useInt(017777777777);
		useLong(01000000000000000000000L);
		useLong(0777777777777777777777L);
		useInt(0200000000000); // ERR: Integer literal 17179869184 is out of range. (todo: better err message)
		useLong(010000000000000000000000L); // ShouldBeErr
		

		// Double&Float	
		// in decimal 		
		useDouble(-1.7976931348623157E308);
		useDouble(-4.9E-324);
		useDouble(4.9E-324);
		useDouble(1.7976931348623157E308);
		
		useDouble(4.9E-325); // ShouldBeErr: floating point number too small
		useDouble(2E-324); // ShouldBeErr: floating point number too small
		useDouble(1.7976931348623157E309); // ShouldBeErr: floating point number too large
		useDouble(1.8E308); // ShouldBeErr: floating point number too large

		// in HEX (doesn't parse in X10, but it does parse in Java)
//		useDouble(-0x1.fffffffffffffP+1023);
//		useDouble(-0x0.0000000000001P-1022);
//		useDouble(0x0.0000000000001P-1022);
//		useDouble(0x1.fffffffffffffP+1023);
//
//		useDouble(-0x0.00000000000001P-1022); // ShouldBeErr: floating point number too small
//		useDouble(0x0.00000000000001P-1022); // ShouldBeErr: floating point number too small
//		useDouble(0x0.0000000000001P-1023); // ShouldBeErr: floating point number too small
//		useDouble(-0x0.0000000000001P-1023); // ShouldBeErr: floating point number too small
//		useDouble(-0x2.fffffffffffffP+1023); // ShouldBeErr: floating point number too large
//		useDouble(0x2.fffffffffffffP+1023); // ShouldBeErr: floating point number too large
//		useDouble(0x1.fffffffffffffP+1024); // ShouldBeErr: floating point number too large
//		useDouble(-0x1.fffffffffffffP+1024); // ShouldBeErr: floating point number too large


		// todo: test constant propagation (+1, *1, *2)

		// todo: unary minus (it's a special case)


		// unsigned test cases		
		// in HEX
		useUInt(0x0u);
		useUInt(0xffffffffu);
		useULong(0x00ul);
		useULong(0xffffffffffffffffLu);
		// in decimal
		useUInt(0U);
		useUInt(4294967295U);		
		useULong(0ul);
		useULong(18446744073709551616uL);
	}
}

class PrivateWithinInnerClass {	
	class Parent78 {
		private def priv() {}
	}
	class Child78 extends Parent78 {
		private def priv() { // IS overriding 
			super.priv(); 
		}
	}
}
class Parent78 {
	private def priv() {}
	public def pub1() {}
	public def pub2() {}
	public def pub3() {}
	public def pub4() {}
	protected def prot1() {}
	protected def prot2() {}
	protected def prot3() {}
	protected def prot4() {}
	def pack1() {}
	def pack2() {}
	def pack3() {}
	def pack4() {}
}
class Child78 extends Parent78 {
	private def priv() { // not overriding 
		super.priv(); // ERR
	}
	public def pub1() {}
	private def pub2() {} // ERR
	protected def pub3() {}// ERR
	def pub4() {}// ERR

	public def prot1() {}
	private def prot2() {}// ERR
	protected def prot3() {}
	def prot4() {}// ERR

	public def pack1() {}
	private def pack2() {}// ERR
	protected def pack3() {}
	def pack4() {}
}

class XTENLANG_2052 {
	val s1 = new Array[Double][3.14,1];
	val s2 = new Array[Double][3.14,
				"1"]; // ERR: Semantic Error: The literal is not of the given type	 expr:"1"	 type: x10.lang.String{self=="1"}	 desired type: x10.lang.Double
	val x = ULong.MAX_VALUE; //XTENLANG-2054
}
class XTENLANG_2070 {
	var i:Int=0;
	var j1:Int = ++(i); // ShouldNotBeERR
}
class TestLoopLocalIsVal {
	def testLoopLocalIsVal(r:Region) {
		for (p in r) {
			async { 
				val q = p; // p is final so it can be used in an async
			}
		}
		for (val p in r) {
			async { 
				val q = p; // p is final so it can be used in an async
			}
		}
		for (var p:Point in r) { // ERR: Enhanced for loop may not have var loop index. var p: Point{amb}
		}
		ateach (val p in Dist.makeUnique()) {}
		ateach (var p in Dist.makeUnique()) {} // ERR: Syntax Error: Enhanced ateach loop may not have var loop indexvar p: (#161514210)_	
	}
}


class LegalCoercionsBetweenJavaNumerics {
	// Checked and it is consistent with java
//	float f=0;
//	double d=0;
//
//	byte b=0;
//	int i=0;
//	long l=0;
//	short s=0;
	var f:Float=0;
	var d:Double=0;

	var b:Byte=0;
	var i:Int=0;
	var l:Long=0;
	var s:Short=0;

	def test() {
		f = d; // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenJavaNumerics.this.d		 Expected type: x10.lang.Float		 Found type: x10.lang.Double)
		f = b;
		f = i;
		f = l;
		f = s;
		d = f;
		d = b;
		d = i;
		d = l;
		d = s;
		b = f; // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenJavaNumerics.this.f		 Expected type: x10.lang.Byte		 Found type: x10.lang.Float)
		b = d; // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenJavaNumerics.this.d		 Expected type: x10.lang.Byte		 Found type: x10.lang.Double)
		b = i; // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenJavaNumerics.this.i		 Expected type: x10.lang.Byte		 Found type: x10.lang.Int)
		b = l; // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenJavaNumerics.this.l		 Expected type: x10.lang.Byte		 Found type: x10.lang.Long)
		b = s; // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenJavaNumerics.this.s		 Expected type: x10.lang.Byte		 Found type: x10.lang.Short)
		i = f; // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenJavaNumerics.this.f		 Expected type: x10.lang.Int		 Found type: x10.lang.Float)
		i = d; // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenJavaNumerics.this.d		 Expected type: x10.lang.Int		 Found type: x10.lang.Double)
		i = b;
		i = l; // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenJavaNumerics.this.l		 Expected type: x10.lang.Int		 Found type: x10.lang.Long)
		i = s;
		l = f; // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenJavaNumerics.this.f		 Expected type: x10.lang.Long		 Found type: x10.lang.Float)
		l = d; // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenJavaNumerics.this.d		 Expected type: x10.lang.Long		 Found type: x10.lang.Double)
		l = b;
		l = i;
		l = s;
		s = f; // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenJavaNumerics.this.f		 Expected type: x10.lang.Short		 Found type: x10.lang.Float)
		s = d; // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenJavaNumerics.this.d		 Expected type: x10.lang.Short		 Found type: x10.lang.Double)
		s = b;
		s = i; // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenJavaNumerics.this.i		 Expected type: x10.lang.Short		 Found type: x10.lang.Int)
		s = l; // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenJavaNumerics.this.l		 Expected type: x10.lang.Short		 Found type: x10.lang.Long)
	}
}
class LegalCoercionsBetweenAllNumerics {	
	var f:Float=0;
	var d:Double=0;

	var b:Byte=0;
	var i:Int=0;
	var l:Long=0;
	var s:Short=0;

	var ub:UByte=0;
	var ui:UInt=0;
	var ul:ULong=0;
	var us:UShort=0;

	def test() {
		f = d;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.d		 Expected type: x10.lang.Float		 Found type: x10.lang.Double)
		f = b;
		f = i;
		f = l;
		f = s;
		f = ub;
		f = ui;
		f = ul;
		f = us;
		d = f;
		d = b;
		d = i;
		d = l;
		d = s;
		d = ub;
		d = ui;
		d = ul;
		d = us;
		b = f;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.f		 Expected type: x10.lang.Byte		 Found type: x10.lang.Float)
		b = d;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.d		 Expected type: x10.lang.Byte		 Found type: x10.lang.Double)
		b = i;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.i		 Expected type: x10.lang.Byte		 Found type: x10.lang.Int)
		b = l;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.l		 Expected type: x10.lang.Byte		 Found type: x10.lang.Long)
		b = s;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.s		 Expected type: x10.lang.Byte		 Found type: x10.lang.Short)
		b = ub;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.ui		 Expected type: x10.lang.Byte		 Found type: x10.lang.UByte)
		b = ui;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.ui		 Expected type: x10.lang.Byte		 Found type: x10.lang.UInt)
		b = ul;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.ul		 Expected type: x10.lang.Byte		 Found type: x10.lang.ULong)
		b = us;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.us		 Expected type: x10.lang.Byte		 Found type: x10.lang.UShort)
		i = f;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.f		 Expected type: x10.lang.Int		 Found type: x10.lang.Float)
		i = d;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.d		 Expected type: x10.lang.Int		 Found type: x10.lang.Double)
		i = b;
		i = l;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.l		 Expected type: x10.lang.Int		 Found type: x10.lang.Long)
		i = s;
		i = ub;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.ub		 Expected type: x10.lang.Int		 Found type: x10.lang.UByte)
		i = ui;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.ul		 Expected type: x10.lang.Int		 Found type: x10.lang.UInt)
		i = ul;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.ul		 Expected type: x10.lang.Int		 Found type: x10.lang.ULong)
		i = us;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.us		 Expected type: x10.lang.Int		 Found type: x10.lang.UShort)
		l = f;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.f		 Expected type: x10.lang.Long		 Found type: x10.lang.Float)
		l = d;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.d		 Expected type: x10.lang.Long		 Found type: x10.lang.Double)
		l = b;
		l = i;
		l = s;
		l = ub;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.ub		 Expected type: x10.lang.Long		 Found type: x10.lang.UByte)
		l = ui;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.ui		 Expected type: x10.lang.Long		 Found type: x10.lang.UInt)
		l = ul;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.us		 Expected type: x10.lang.Long		 Found type: x10.lang.ULong)
		l = us;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.us		 Expected type: x10.lang.Long		 Found type: x10.lang.UShort)
		s = f;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.f		 Expected type: x10.lang.Short		 Found type: x10.lang.Float)
		s = d;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.d		 Expected type: x10.lang.Short		 Found type: x10.lang.Double)
		s = b;
		s = i;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.i		 Expected type: x10.lang.Short		 Found type: x10.lang.Int)
		s = l;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.l		 Expected type: x10.lang.Short		 Found type: x10.lang.Long)
		s = ub;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.ub		 Expected type: x10.lang.Short		 Found type: x10.lang.UByte)
		s = ui;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.ui		 Expected type: x10.lang.Short		 Found type: x10.lang.UInt)
		s = ul;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.ul		 Expected type: x10.lang.Short		 Found type: x10.lang.ULong)
		s = us;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.ul		 Expected type: x10.lang.Short		 Found type: x10.lang.UShort)
		ub = f;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.f		 Expected type: x10.lang.UByte		 Found type: x10.lang.Float)
		ub = d;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.d		 Expected type: x10.lang.UByte		 Found type: x10.lang.Double)
		ub = b;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.i		 Expected type: x10.lang.UByte		 Found type: x10.lang.Byte)
		ub = i;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.i		 Expected type: x10.lang.UByte		 Found type: x10.lang.Int)
		ub = l;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.l		 Expected type: x10.lang.UByte		 Found type: x10.lang.Long)
		ub = s;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.s		 Expected type: x10.lang.UByte		 Found type: x10.lang.Short)
		ub = ui;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.ui		 Expected type: x10.lang.UByte		 Found type: x10.lang.UInt)
		ub = ul;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.ul		 Expected type: x10.lang.UByte		 Found type: x10.lang.ULong)
		ub = us;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.us		 Expected type: x10.lang.UByte		 Found type: x10.lang.UShort)
		ui = f;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.f		 Expected type: x10.lang.UInt		 Found type: x10.lang.Float)
		ui = d;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.d		 Expected type: x10.lang.UInt		 Found type: x10.lang.Double)
		ui = b;
		ui = i;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.l		 Expected type: x10.lang.UInt		 Found type: x10.lang.Int)
		ui = l;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.l		 Expected type: x10.lang.UInt		 Found type: x10.lang.Long)
		ui = s;
		ui = ub;
		ui = ul;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.ul		 Expected type: x10.lang.UInt		 Found type: x10.lang.ULong)
		ui = us;
		ul = f;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.f		 Expected type: x10.lang.ULong		 Found type: x10.lang.Float)
		ul = d;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.d		 Expected type: x10.lang.ULong		 Found type: x10.lang.Double)
		ul = b;
		ul = i;
		ul = l;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.d		 Expected type: x10.lang.ULong		 Found type: x10.lang.Long)
		ul = s;
		ul = ub;
		ul = ui;
		ul = us;
		us = f;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.f		 Expected type: x10.lang.UShort		 Found type: x10.lang.Float)
		us = d;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.d		 Expected type: x10.lang.UShort		 Found type: x10.lang.Double)
		us = b;
		us = i;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.i		 Expected type: x10.lang.UShort		 Found type: x10.lang.Int)
		us = l;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.l		 Expected type: x10.lang.UShort		 Found type: x10.lang.Long)
		us = s;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.l		 Expected type: x10.lang.UShort		 Found type: x10.lang.Short)
		us = ub;
		us = ui;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.ui		 Expected type: x10.lang.UShort		 Found type: x10.lang.UInt)
		us = ul;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.ul		 Expected type: x10.lang.UShort		 Found type: x10.lang.ULong)
	}
}
class OperatorTestCases { // XTENLANG-2084
	static class InstanceBinary1 {
		operator this*(that:Int):Any = 1;
		operator this*(that:Int):Any = 1; //  ERR (Semantic Error: Duplicate method "method OperatorTestCases.InstanceBinary1.operator*(that:x10.lang.Int): x10.lang.Any"; previous declaration at C:\cygwin\home\Yoav\test\Hello.x10:114,9-41.) 

		operator this-(that:Int):Any = 1;
		operator (that:Int)-this:Any = 1;

		operator this+(that:InstanceBinary1):Any = 1; 
		operator (that:InstanceBinary1)+this:Any = 1; // ShouldBeErr 
	}
	static class InstanceAndStatic {
		operator this*(that:Int):Any = 1;
		static operator (x:InstanceAndStatic)*(that:Int):Any = 1; // ShouldBeErr

		operator (that:Int)+this:Any = 1;
		static operator (x:Int)+(that:InstanceAndStatic):Any = 1; // ShouldBeErr
	}

	// test inheritance
	static class Parent {
		operator this*(that:Int):Any = 1;
		operator this+(that:Int):Any = 1;
		static operator (x:Parent)+(that:Int):Any = 1;
	}
	static class Child extends Parent {		
		operator this*(that:Int):Any = 1; // overriding
		operator (that:Int)+this:Any = 1; // ShouldBeErr
		static operator (x:Parent)+(that:Int):Any = 1; // hiding
	}

	// test inheritance, static, instance
	static class BinaryAndUnary {
		operator this+(that:Int):Any = 1; // ShouldNotBeERR (Semantic Error: operator+(that: x10.lang.Int): x10.lang.Any in OperatorTestCases.BinaryAndUnary cannot override operator+(that: x10.lang.Int): x10.lang.Any in OperatorTestCases.BinaryAndUnary; overridden method is static)
		static operator +(that:Int):Any = 1; // ShouldNotBeERR (Semantic Error: operator+(that: x10.lang.Int): x10.lang.Any in OperatorTestCases.BinaryAndUnary cannot override operator+(that: x10.lang.Int): x10.lang.Any in OperatorTestCases.BinaryAndUnary; overridden method is notstatic) // ERR (Semantic Error: Duplicate method "method static OperatorTestCases.BinaryAndUnary.operator+(that:x10.lang.Int): x10.lang.Any"; previous declaration at C:\cygwin\home\Yoav\test\Hello.x10:39,9-41.)
	}

}

class TestOperatorResolutionWithoutCoercions { // XTENLANG-1692
	// An example of operator resolution (without coercions)
	// If we remove the inheritance, and instead define a coercion from C to B and from B to A, then we'll get the same operator resolutions.
	static class R {}
	static class A {
		// class A will define op+(A,A) and op+(B,A) and op+(A,C)
		operator this+(a:A)=1; 
		operator (b:B)+this=2; 
		static operator (a:A)+(c:C)=3;
	}
	static class B extends A {
		operator (b:B)+this { Console.OUT.println("B"); return 2; }
	}
	static class C extends B {
		operator this+(a:A) { Console.OUT.println("C"); return 1; }
	}

	static class Example {	
		static operator (b:B)+(c:C)=4;

		def example(a:A,b:B,c:C) { Console.OUT.println("Example:");
			val x1:Int{self==1} = a+a; // resolves to A::op+(A,A) and dynamically dispatches on the first argument (so it might execute C::op+(C,A) at runtime)
			val x2:Int{self==1} = a+b; // resolves to A::op+(A,A) and dynamically dispatches on the first argument (so it might execute C::op+(C,A) at runtime)
			val x3:Int{self==3} = a+c; // resolves to A::op+(A,C) so it does a static call
			val x4:Int{self==2} = b+a; // resolves to A::op+(B,A) and dynamically dispatches on the second argument (so it might execute B::op+(B,B) at runtime)
			val x5:Int{self==2} = b+b; // resolves to B::op+(B,B) and dynamically dispatches on the second argument
			val x6:Int{self==2} = b+c; // ShouldBeErr:  should resolve to Example::op+(B,C) so it does a static call (so the return type should be 4!)
			val x7:Int{self==1} = c+a; // resolves to C::op+(C,A) and dynamically dispatches on the first argument
			val x8:Int{self==1} = c+b; // ShouldBeErr: Ambiguity! C::op+(C,A) or B::op+(B,B) ?
			val x9:Int{self==1} = c+c;  // ShouldBeErr: Ambiguity! C::op+(C,A) or Example::op+(B,C) ?
		}
	}
	static  class Main {
	  public static def main(args: Array[String]) {
		  new Example().example( new A(), new B(), new C() );
		  Console.OUT.println("Now let's test dynamic dispatching!"); 
		  new Example().example( new C(), new C(), new C() );
	  }
	}
}


class TestStructEqualsClass {
	def this(Int) {}
	def this(Any) {}
}

class SubtypeConstraints {
class A[T] { T <: Object } {
	var a1:A[T];
	var a2:A[Int]; // ERR: Semantic Error: Type A[x10.lang.Int] is inconsistent.
	var a4:A[Int{self==3}]; // ERR
}
class Test[T]  {
	def m1(GlobalRef[T]{self.home==here}) {} // ERR
	def m2(GlobalRef[T]{self.home==here}) {T<:Object} {}
}
static class TestStatic[T]{T<:Object} {
	public static def m1[T]():TestStatic[T]  = null; // ERR
	public static def m2[T]() {T<:Object} :TestStatic[T] = null;
    public static type TestStatic[T](p:Int) {T<:Object} = TestStatic[T]{1==p};
}

}
class TransitivityOfEquality {
// check for transitivity of equality
class Tran[X,Y,W,U,Z] {X==Z, Z==Y,Int==Y, W==Z} {
	var a0:Tran[X,Y,W,U,Z];
	var a9:Tran[Int,Int,Int,Int,Int];
	var a1:Tran[Y,W,W,U,Z];
	var a2:Tran[Y,W,Int,U,Int];
	var a3:Tran[Y,Int,W,U,Z];
	var a4:Tran[Z,W,Z,U,Int];
	
	var e1:Tran[U,Y,W,U,Z]; // ERR: Type Tran[U, Y, W, U, Z] is inconsistent.
}
}

class HaszeroConstraints {
class M[T] {
	def q() { T haszero } {}
}
class A[T] { T haszero } {}
class B[U] {
	def f1() { U == Int } {
		var x:A[U] = null;
	}
	def f2() { U == Any } {
		var x:A[U] = null;
	}
	def f3() { U == Any{self!=null} } {
		var x:A[U] = null; // ERR
	}
}

class C[V] { V == Int } {
	def f1() { 
		var x:A[V] = null;
	}
}
class D[V] { V == Int{self!=0} } {
	def f1() { 
		var x:A[V] = null; // ERR
	}
}

class Test2[W] { W haszero } {	
	var a1:A[W];
	class Inner {	
		var i1:A[W];
	} 
    def test1() {
        var x:A[W];
    }
    def test2() { W haszero } {
        var x:A[W];
    }
}
class Test[W](p:Int) {
	def test() {
		m1.q(); // ERR
		m2.q();
	}
	var m1:M[Int{self!=0}];
	var m2:M[Int];

	var a0:A[W]; // ERR
    def test2() {
        var x:A[W]; // ERR
    }
    def test3() { W haszero } {
        var x:A[W];
    }
	var a1:A[Int];
	var a2:A[Int{self!=3}];
	var a3:A[Int{self==0}];
	var a4:A[Int{self==3}]; // ERR
	var a5:A[Int{self!=0}]; // ERR
	var a6:A[Int{self!=p}]; // ERR
	var a7:A[Int{self==p}]; // ERR
}
} // end HaszeroConstraints



class XTENLANG_967  {
    def test() {        
        class C[T] {
			val f1 = (){T<:Object} => "hi"; // method guard on closures still doesn't work
			def f2(){T<:Object} = "hi";
		}
        val res1 =  new C[Int]().f1(); // ShouldBeErr
        val res2 =  new C[Int]().f2(); // ERR: Type guard {} cannot be established; inconsistent in calling context.
    }	
}
class XTENLANG_1574(v:Int) {v==1} {
	static def m(a:XTENLANG_1574) {
		val b:XTENLANG_1574{self.v==1} = a; 
		@ShouldBeErr val b2:XTENLANG_1574{self.v==2} = a;
	}
}
class TestMethodGuards[T](a:Int, p:Place) {
	def f() {a==1} {}
	def q() {a==1} { f(); }
	def m() {
		f(); // ERR
	}
	
	def f2() {p==Place.FIRST_PLACE} {}
	def q2() {p==Place.FIRST_PLACE} { f2(); }
	def m2() {
		f2(); // ERR
	}

	def f1() {T haszero} {}
	def q1() {T haszero} { f1(); }
	def m1() {
		f1(); // ERR
	}
}
class ProblemsWithFieldsInConstraints {	// these errors are both with STATIC_CALLS and with DYNAMIC_CALLS
	val f1:Int;
	val f2:Int{self==f1};
	def this() {
		f2 = 2; // ERR
		f1 = 2;
	}
	def test() {
		val local1:Int;
		val local2:Int{self==local1};
		local2 = 1; // ERR
		local1 = 1;
	}
}
class InconsistentPropertyVsField(p:Int) {
	val f:Int = 2;
	def test() {
		val a0:InconsistentPropertyVsField{self.p==1} = null;
		val a1:InconsistentPropertyVsField{self.f==1} = null; // ERR: Only properties may be prefixed with self in a constraint.
		val a2:InconsistentPropertyVsField{a2.p==1} = null;
		val a3:InconsistentPropertyVsField{a3.f==1} = null; // ERR: Only properties may be prefixed with self in a constraint.
		val a4:InconsistentPropertyVsField{a0.p==1} = null;
		val a5:InconsistentPropertyVsField{a0.f==1} = null; // ShouldBeErr? why don't I have an error here?
		val a6:InconsistentPropertyVsField{this.p==1} = null;
		val a7:InconsistentPropertyVsField{this.f==1} = null;
	}
}

class FieldInInvariant1 {a==1} { // ShouldBeErr
	val a:Int;
	def this() { a=2; }
}
class FieldInInvariant2 {this.a==1} {  // ShouldBeErr
	val a:Int = 2;
}
class FieldInInvariant3 {self.a==1} { // ERR: Semantic Error: self may only be used within a dependent type
	val a:Int = 1;
}
class XTENLANG_688(a:Int) {
	val f1:Int{self==a} = a;
	val f2:Int{self==f1} = a;
}
class XTENLANG_688_2(a:Int) { // fine even with DYNAMIC_CALLS (cause we do not generate a cast)
	val f2:Int{self==f1} = a;
	val f1:Int{self==a} = a;
}

class LegalForwardRef { 
    val f1:LegalForwardRef{this.f2==this.f1} = null; 
    val f2:LegalForwardRef = null; 

    val f3:LegalForwardRef{this.f4==this.f3} = f4; // ERR: Cannot read from field 'f4' before it is definitely assigned.
    val f4:LegalForwardRef = null; 
}
class LegalForwardRef2 { 
	val x:Int{self==y} = 1; // legal forward reference to y (we don't really use it's value)
	val y:Int{self==1} = 1;
}
class IllegalForwardRef2 { 
	val x:Int{self==y} = 1; // ERR with STATIC_CALLS (The type of the field initializer is not a subtype of the field type.) with DYNAMIC_CALLS (Cannot read from field 'y' before it is definitely assigned.)
	val y:Int{self==2} = 2;
}
class XTENLANG_686_2(a:Int) {
	def this() : XTENLANG_686_2{1==this.a}{property(1);} // ok to use this on the return type
	def this(a:Int{self==this.a}) {property(a);} // ERR: Semantic Error: This or super cannot be used (implicitly or explicitly) in a constructor formal type.	 Formals: [val a: x10.lang.Int{self==FordesemiFoo#this.a}]
}
class XTENLANG_686(a:Int) {
	val f1:Int{self==a} = a;
	val f2:Int{self==f1} = a;

	val f3:XTENLANG_686{self.a==this.f4} = null; // ok
	val f4:Int = 2;

	val f5:Int{self==3};


	def this(b:XTENLANG_686{self.a==1}) {
		val q1: XTENLANG_686{self.a==this.a} = null; // ok
		val q2: XTENLANG_686{self.a==this.f1} = null; // ok
		property(1);
		// we put field initializers here
		val q3: XTENLANG_686{self.a==this.a} = null; // ok
		val q4: XTENLANG_686{self.a==this.f1} = null; // ok
		val q5: XTENLANG_686{self.a==this.f5} = null; // ok
		
		val i1:Int{self==f5} = 3; // ok
		val i2:Int{self==f5} = 4; // ERR in both STATIC_CALLS (Cannot assign expression to target.) and DYNAMIC_CALLS (Cannot read from field 'f5' before it is definitely assigned.)

		val i3:Int{3==f5} = 3; // ok
		f5 = 3;
		val i4:Int{3==f5} = 4; // ok
	}
}

class CastToTypeParam[T] { 
	val f1:T = 0 as T;
	val f2:T = 1 as T;
	def test(a:CastToTypeParam[String]) {
		val f:String = a.f1;
		val s = 0 as String; // ERR: Cannot cast expression to type
	}
	public static def main(Array[String]) {
		Console.OUT.println(new CastToTypeParam[String]().f1); // throws x10.lang.ClassCastException
	}
}

class XTENLANG_685(a : Int, b : Int{this.a == 1}) {
	def this() {
		property(1,1);
	}  
	def this(Boolean) {
		property(1,2);
	}  
	def this(String):XTENLANG_685{self.a == 1} {// ShouldNotBeERR (Semantic Error: Invalid type; the real clause of XTENLANG_685{self.a==2, self.b==1} is inconsistent.)
		property(2,1); // ERR (Semantic Error: Cannot bind literal 2 to 1) todo: better error message
	}  
	def this(Float):XTENLANG_685{this.a == 1} {// ShouldNotBeERR (Semantic Error: Invalid type; the real clause of XTENLANG_685{self.a==2, self.b==1} is inconsistent.)
		property(2,1); // ShouldBeErr
	}  
	def this(Double) {// ShouldNotBeERR (Semantic Error: Invalid type; the real clause of XTENLANG_685{self.a==2, self.b==1} is inconsistent.)
		property(2,1); // ShouldBeErr
	}  
}

//class Tree(left:Tree, right:Tree{this.left==self.left}) {} // ShouldNotBeErr, see XTENLANG-2117
class NonStaticTypedef(p:Int) { 
	type T = NonStaticTypedef{self.p==1}; // ERR: Illegal type def NonStaticTypedef.T: type-defs must be static.
}


class hasZeroTests {
	class Q00[T] {
		var t:T; // ERR
	}
	class Q0[T] {T haszero} {
	  var t:T;
	}
	class Q1[T] {T haszero} {
	  val t:T; // ERR
	}
	class Q2[T] {
	  val t:T;
	  def this() {T haszero} { // ERR
	  }
	  def this(t:T) {
		this.t = t;
	  }
	}
	class Q3[T] {
	  var t:T;
	  def this() {T haszero} { // ERR
	  }
	  def this(t:T) {
		this.t = t;
	  }
	}

	static class Zero {
	  public static native def get[T]() {T haszero} :T;
	}
	class Q4[T] {
	  val t:T;
	  def this() {T haszero} { 
		 this( Zero.get[T]() );
	  }
	  def this(Boolean) {
		 this( Zero.get[T]() ); // ERR
	  }
	  def this(t:T) {
		this.t = t;
	  }
	}
	class haszeroExamples0[T] {T haszero} {
		var t:T;

	  def m0() {
		  m1(); // ok
	  }
	  def m1() {T haszero} {}
	}
	class haszeroExamples2[T] {
		val t:T;	
	  def this() {T haszero} { 
		 this( Zero.get[T]() );
	  }
	  def this(t:T) {
		this.t = t;
	  }

	  def m0() {}
	  def m1() {T haszero} {}
	  def m2() {T haszero} {
		  m0();
		  m1();
	  }
	  def m3() {
		  m0();
		  m1(); // ERR 
	  }
	}

	class haszeroExamples[T] {
	  var t:T;
	  def this() {T haszero} {
		this(Zero.get[T]());
	  }
	  def this(t:T) {
		setT(t);
	  }
	  private def setT(t:T) { this.t = t; }
	}

	class haszeroUsages {
		var x1:haszeroExamples2[Int{self!=0}]; // ok
		var x2:haszeroExamples0[Int{self!=0}]; // ERR 
		val x3 = 
			new haszeroExamples2[Int{self!=0}](5); // ok
		val x4 = 
			new haszeroExamples2[Int{self!=0}]();  // ERR
		val x5 =  new haszeroExamples2[Int]();
		def test() {
			x3.m0();
			x3.m1(); // ERR 
			x5.m0();
			x5.m1(); 
		}
	}

}
class RuntimeTestsOfHaszero {
	public static def main(Array[String]) {
		new RuntimeTestsOfHaszero().m();
	}

	var i:Int;
	var k:Int{self!=3};
	var l:Long;
	var s:String;
	val a1 = new A[String]();
	val a2 = new A[Int]();
	val a3 = new A[Long]();

	def m() {
		assert(1==++i);
		assert(0==k);
		assert(0==l++);
		assert(null==a1.t);
		assert(1==++a2.t);
		assert(1==++a3.t);
		assert(4==foo(Zero.get[Double]()));
	}

	def foo(Int)=3;
	def foo(Double)=4;

	static class A[T] {T haszero} {
		var t:T;
	}
}


class StaticOverriding { // see XTENLANG-2121
  static class C {
    static def m() = 0;
  }
  static class D extends C {
    static def m() = 1; // ShouldNotBeERR: Semantic Error: m(): x10.lang.Int{self==1} in StaticOverriding.D cannot override m(): x10.lang.Int{self==0} in StaticOverriding.C; attempting to use incompatible return type.
  }
}

class TreeUsingFieldNotProperty { this.left==null } { // ShouldBeErr
  val left:TreeUsingFieldNotProperty = null;
}
class XTENLANG_1149 {
    def m(b:Boolean, x:Object{self!=null}, y:Object{self!=null}):Object{self!=null} {
        val z:Object{self!=null} = b ? x : y;
        @ShouldBeErr val z2:Object{self==null} = b ? x : y;
        return z;
    }
}
class XTENLANG_1149_2 {
	class B {}
	var f:Boolean;
	def test() {
		val b1 = new B();
		val b2 = new B();
		@ShouldBeErr val c0:B{self==null} = f ? b1 : b2;
		val c1:B{self!=null} = f ? b1 : b2;
		val c2:B = f ? b1 : b2;
		val c3:B{self!=null} = f ? (b1 as B{self!=null}) : b2;
		val c4:B{self!=null} = f ? (b1 as B{self!=null}) : (b2 as B{self!=null});
		val c5:B{self!=null} = f ? b1 : b1;
		@ERR val c6:B{self==null} = f ? b1 : b1;

		val arr1 = new Array[B{self!=null}][b1,b2];
		@ERR val arr2:Array[B{self!=null}] = [b1,b2]; //  we do not infer constraints, because then [1] will be Array[Int{self==1}]
		val arr3:Array[B] = [b1,b2]; 
		@ShouldBeErr val arr4 = new Array[B{self==b2}][b1,b2];
	}
}

class TestClassInvariant78 {
class AA(v:Int) {v==1} {} // ShouldBeErr
class BB(v:Int) {v==1} {
	def this(q:Int) { property(q); } // ShouldBeErr
}
static class A(v:Int) {v==1} {
	static def m(a:A) {
		val b:A{self.v==1} = a;
		@ShouldBeErr val b2:A{self.v==2} = a;
	}
	def m2(a:A) {
		val b1:A{self.v==1} = this;
		val b2:A{this.v==1} = this;
		val b3:A{self.v==1} = a;
		@ShouldBeErr val b33:A{self.v==2} = a; 
		val b4:A{this.v==1} = a;
	}
}
}

class TestDuplicateClass { // XTENLANG-2132
	class A(v:Int) {} 
	// static class A(v:Int) {}  // ShouldBeErr (causes a crash: AssertionError: TestDuplicateClass.A->TestDuplicateClass.A x10.types.X10ParsedClassType_c is already in the cache; cannot replace with TestDuplicateClass.A x10.types.X10ParsedClassType_c)
}

class TestSerialization {
class TestAt {
	var i:Int{self!=0};
	def this() { // ERR: Semantic Error: Field 'i' was not definitely assigned.
		at (here.next()) 
			i=2; // ERR: 'this' or 'super' cannot escape via an 'at' statement during construction.
	}
}
class TestSerialize {
	var i:Int{self!=0};
	def this() {
		at (here.next()) 
			this.set(3); // ERR: 'this' or 'super' cannot escape via an 'at' statement during construction.
	    this.set(2);
	}
	private def set(x:Int{self!=0}) { i=x; }
	public def serialize():SerialData {
		assert i!=0; // will fail, because we haven't written anything to "i" yet!
		return new SerialData(i,null); // here we read here the value of "i" though it wasn't set yet
	}
}

class ClosureAndSerialize {
    val x = 2;    
	val BigD = Dist.makeBlock((0..10)*(0..10), 0);
    val A = DistArray.make[Double](BigD,(p:Point)=>
		1.0*this.x); // ERR: 'this' or 'super' cannot escape via a closure during construction.
    val k:Int{self!=0} = 3;

    public def serialize():SerialData {
		Console.OUT.println(k);
		assert k==3; // will fail when used on multiple places
		return new SerialData(1,null);
	}
}
class HashMapSerialize {
	def testHashmapSerialize() {
		val map = new HashMap[String,String]();
		map.put("a","A");
		map.put("b","B");
		at (here) {
			map.put("c","C");
			assert(map.size()==3);
		}		
		assert(map.size()==2);
	}
}
}

class XTENLANG_2142 implements CustomSerialization { // ShouldBeErr: missing ctor "def this(SerialData)"
	public def serialize():SerialData= null;
}


class TestComparableAndArithmetic {
  def compare[T](x:T,y:T) { T <: Comparable[T] } = x.compareTo(y);
  def add[T](x:T,y:T) { T <: Arithmetic[T] } = x+y;
  def test() {
	  {
		  var x:Int=2, y:Int=3;
		  use(compare(x,y));
		  use(compare(x,x));
		  use(compare(y,y));
		  use(add(x,y)); // ShouldNotBeERR
		  use(add(x,x)); // ShouldNotBeERR
		  use(add(y,y)); // ShouldNotBeERR
	  }
	  {
		  var x:Double=2.0, y:Double=3.0;
		  use(compare(x,y));
		  use(compare(x,x));
		  use(compare(y,y));
		  use(add(x,y)); // ShouldNotBeERR
		  use(add(x,x)); // ShouldNotBeERR
		  use(add(y,y)); // ShouldNotBeERR
	  }
  }
  def use(i:Int) {}
}

interface Ann42 //extends MethodAnnotation, ClassAnnotation, FieldAnnotation, ImportAnnotation, PackageAnnotation, TypeAnnotation, ExpressionAnnotation, StatementAnnotation 
	{ }
@ERR @Ann42 class TestAnnotationChecker( // Annotations on class declarations must implement x10.lang.annotations.ClassAnnotation
	@ERR @Ann42 p:Int) { // Annotations on field declarations must implement x10.lang.annotations.FieldAnnotation
	@ERR def use(@Ann42 x:Any)=x;  // Annotations must implement x10.lang.annotations.Annotation
	@ERR @Ann42 def m0() {} // Annotations on method declarations must implement x10.lang.annotations.MethodAnnotation
	def test() {
		use(@ERR @Ann42 "A"); // Annotations on expressions must implement x10.lang.annotations.ExpressionAnnotation
		@ERR @Ann42 {} // Annotations on statements must implement x10.lang.annotations.StatementAnnotation
	}
	var i:Int @ERR @Ann42; // Annotations on types must implement x10.lang.annotations.TypeAnnotation
	@ERR @Ann42 var j:Int; // Annotations on field declarations must implement x10.lang.annotations.FieldAnnotation

	def m1() {
		@ERR @Ann42 {}
		@ERR @Ann42 while (true) {
			@ERR @Ann42 if (true) 
				@ERR @Ann42 continue;
			@ERR @Ann42 break;
		}
		val c1 = ()=>
			@ERR @Ann42 { 5 };

		val c2 = ()=>
			@ERR @Ann42 { return 5; };

		@ERR @Ann42 ;
		@ERR @Ann42 assert false;
		@ERR @Ann42 if (true);
		var y:Int = @ERR @Ann42 5;
		var x:Int = @ERR @Ann42 + 5;
		@ERR @Ann42 val z = 
			@ERR @Ann42 + 5;
		use(@ERR @Ann42 ++x);
		use(@ERR @Ann42 4+5);
		use(@ERR @Ann42 5);
		use(@ERR @Ann42 use(5));
		//@Ann42 use(5); // todo: it should parse!
	}
	@ERR @Ann42 def m2() = @ERR @Ann42 4;
	@ERR @Ann42 def m3() = @ERR @Ann42 { 5 };
}

class SubtypeCheckForUserDefinedConversion { // see also SubtypeCheckForUserDefinedConversion_MustFailCompile
	static class Foo {}
	static class A {
		// implicit_as
		@ERR public static operator (p:Int):Foo = null;
		public static operator (p:Long):A = null;
		public static operator (x:A):String = null;

		// explicit_as
		@ERR public static operator (x:Double) as Foo = null;
		public static operator (x:Float) as A = null;

		// far todo: trying 2+ or 0 formals causes a syntax error, and I think it should be a semantic error
		// public static operator (p:Long, x:Int) as A = null;
		// public static operator () as A = null;
	}
	static struct St {
		// implicit_as
		@ERR public static operator (p:Int):Foo = null;
		public static operator (p:Long):St = St();
		public static operator (x:St):String = null;

		// explicit_as
		@ERR public static operator (x:Double) as Foo = null;
		public static operator (x:Float) as St = St();
	}
	static class C[V] {}
	static class B[U] {	
		def test(var l:Long, var s:String, var a:A) {
			a = l;
			s = a;

			l = @ERR a;
			a = @ERR s;
			l = @ERR s;
			s = @ERR l;
		}

		// implicit_as
	    public static operator[T](x:Double):B[T] = null;
	    public static operator[T](x:Int):B[A] = null;
	    @ERR public static operator[T](x:T):T = x;
	    @ERR public static operator[T](x:String):C[T] = null;
		
		// explicit_as
		@ERR public static operator (x:Double) as Foo = null;
		public static operator[T] (x:Float) as B[T] = null;
		@ERR public static operator[T] (x:String) as B = null; // Type is missing parameters.
	}

	// what happens if we have two possible implicit/explicit coercions?
	// We give priority to coercions found in the target type (over the single one that can be found in the source type).
	static class Y(j:Int) {
		public static operator (p:Y):X{i==2} = null;
		public static operator (p:Y) as ? :X{i==3} = null;
	}
	static class X(i:Int) {
		public static operator (p:Y):X{i==1} = null;
		public static operator (p:Y) as ? :X{i==4} = null;
	}
	static class Z(i:Int) {
		@ShouldNotBeERR public static operator (p:Int) as Z{i==4} = null; // see XTENLANG-2202
	}
	static class W(i:Int) {
		public static operator (p:Int) as ? :W{i==4} = null;
	}
	static class TestAmbiguity {
		def test1(y:Y):X{i==1} = y;
		@ERR def test2(y:Y):X{i==2} = y; // Cannot return expression of given type.
		@ERR def test3(y:Y):X{i==3} = y; // Cannot return expression of given type.
		@ERR def test4(y:Y):X{i==4} = y; // Cannot return expression of given type.
		
		@ERR def test5(y:Y):X{i==1} = y as X; // Cannot return expression of given type.
		@ERR def test6(y:Y):X{i==2} = y as X; // Cannot return expression of given type.
		@ERR def test7(y:Y):X{i==3} = y as X; // Cannot return expression of given type.
		def test8(y:Y):X{i==4} = y as X;
	}
}


class Void_Is_Not_A_Type_Tests { // see also XTENLANG-2220
	static class B[T] {}
	val i:Int=1;
	@ERR def test2(void) {}
	def test3() {
		@ERR val closure:Any = (void)=>{};
	}
	// parsing error: def test4():void{i==i} {}

	@ERR def test(arg:void) {
		@ERR val z:void; //Local variable cannot have type void.
		// parsing error: var k:void{i==1};
		@ERR var b:B[void]; // Cannot instantiate invariant parameter T of A.B with type void.
		// parsing error: var b2:B[void{i==1}];
	}	
}


class MethodCollisionTests { // see also \x10.tests\examples\Constructs\Interface\InterfaceMethodCollision_MustFailCompile.x10
	static class NoVariantTests {
		static interface Q {
			@ERR def hashCode():void;
		}
		static interface A {
			def m():void;
			def m(i:Int):void;
		}
		static interface A2 extends A {}
		static interface D extends A {
			@ERR def m():Any;
			def m(i:Double):void;
		}
		static interface B {
			def m():Any;
			def m(i:Double):void;
		}
		static interface B2 extends B {}
		@ERR static interface C extends A,B {}
		@ERR static interface C2 extends A2,B2 {}

		static abstract class A3 implements A2 {}
		static abstract class B3 implements B2 {}
		// we have two errors because the method was inherited from two different paths
		@ERR @ERR static abstract class C3_1 extends B3 implements A2 {}
		@ERR @ERR static abstract class C3_2 extends A3 implements B2 {}
	}
	// covariant return tests
	/*
	This compiles fine in Java:
    interface A {
        Object m();
    }
    interface B {
        String m();
    }
    interface C extends A,B{}
    class D implements C {
        public String m() { return "a"; }        
    }
	*/
	
	static class CovariantReturnTests {
		static interface A {
			def m():Any;
		}
		static interface B {
			def m():Int; // gives java-backend errors: XTENLANG-2221
		}
		static interface C extends A,B {}
		static class D implements C {
			public def m():Int = 2;
		}
	}
	static class CovariantReturnTests2 {
		static interface A {
			def m():Any;
		}
		static interface B {
			def m():String;
		}
		static interface C extends A,B {}
		static class D implements C {
			public def m():String = "";
		}
	}
	static class CloneTest {
		static interface A {
			def clone():A;
		}
		static interface B extends A {
			def clone():B;
		}
		static class C1 implements B,A {
			public def clone():C1=null;
		}
		static class C2 implements B,A {
			public def clone():B=null;
		}
		static class C3 implements B,A {
			@ERR public def clone():A=null;
		}
	}
	static class CloneTest2 {	
		static interface A[T] {
			def clone():A[T];
		}
		static interface B[T] extends A[T] {
			def clone():B[T];
		}
		static class C1[T] implements B[T],A[T] {
			public def clone():C1[T]=null;
		}
		static class C2[T] implements B[T],A[T] {
			public def clone():B[T]=null;
		}
		static class C3[T] implements B[T],A[T] {
			@ERR public def clone():A[T]=null;
		}
	}
}

class CachingResolverAssertionFailed { // see XTENLANG-2254
	static class Z {}
	// todo: @ShouldBeErr static struct Z {}
}



class TestCircularStructs { // see XTENLANG-2187 
	@ERR static struct Z(u:Z) {} 
	static struct W {
		@ERR val u:W; 
		def this(u:W) { this.u = u; }
	}
	
	@ERR static struct Cycle1(u:Cycle2) {} 
	@ERR static struct Cycle2(u:Cycle1) {} 

	// see XTENLANG-2144 that was closed
	//TestStructStaticConstant
    static struct S {
        static val ONE = S();
    }
	@ERR static struct U(u:U) {} 


    public static def main(Array[String]{rail}) {
        val x1 = new Array[S](2);
    }
}
class CircularityTestsWithInheritanceInterfacesAndStructs { // see XTENLANG-2187
	@ERR static struct Z(u:Z) {}
	static struct Z2 {
		static val z2:Z2 = Z2();
	}

	static struct Complex(i:Int,j:Int) {}

	static struct Generic[T] {
		val t:T;
		def this(t:T) { this.t = t; }
	}
	static class GenClassUsage {
		var x:Generic[GenClassUsage];
	}
	static struct GenStructUsage {
		@ERR val x:Generic[GenStructUsage];
		def this(x:Generic[GenStructUsage]) { this.x = x; }
	}
	static struct GenStructUsage2[T] {
		val x:Generic[T];
		def this(x:Generic[T]) { this.x = x; }
	}
	@ERR static struct GenStructUsage3 implements GenStructUsage2[GenStructUsage3] {}
	static struct GenStructUsage4 {
		@ERR val x:GenStructUsage2[GenStructUsage2[GenStructUsage4]];
		def this(x:GenStructUsage2[GenStructUsage2[GenStructUsage4]]) { this.x = x; }
	}
	static struct IgnoreGeneric[T] {}
	@ERR static struct GenStructUsage44(x:IgnoreGeneric[IgnoreGeneric[GenStructUsage44]]) {} // far todo: even though the generic is "ignored" (there is no field of that type), I still report an error. We could do another analysis that checks which type parameters are actually used by fields and only handle such type parameters.
	static struct GenStructUsage5 {
		val x:GenStructUsage2[GenStructUsage2[GenStructUsage2[Generic[Int]]]];
		def this(x:GenStructUsage2[GenStructUsage2[GenStructUsage2[Generic[Int]]]]) { this.x = x; }
	}

	
	static struct Box[T](b:T) {}
	@ERR static struct GA[T](a:Box[GB[T]]) {}
	@ERR static struct GB[T](a:Box[GA[T]]) {}

	static struct Infinite[T] {
		@ERR val t:Infinite[Infinite[T]];
		def this(t:Infinite[Infinite[T]]) {
			this.t = t;
		}
	}
	@ERR static struct Infinite2[T](t:Infinite2[T]) {}
	static struct GenStructUsage6 {
		val x:Infinite[Int];
		def this(x:Infinite[Int]) { this.x = x; }
	}

	static struct A1 {
		val b:B1;
		def this(b:B1) { this.b = b; }
	}
	static class B1 {
		val a:A1;
		def this(a:A1) { this.a = a; }
	}
	static struct A2 {
		@ERR val b:B2;
		def this(b:B2) { this.b = b; }
	}
	static struct B2 {
		@ERR val a:A2;
		def this(a:A2) { this.a = a; }
	}

	static struct X {	
		val inner:Inner;
		def this(a:Inner) { this.inner = a; }
		class Inner {}
	}
	static class X2 {	
		class Inner extends X2 {}
	}


	property i() = 5;
	@ERR class R extends R {i()==5} {}
	@ERR class R1 {i()==3} {}
	@ERR class R2 {@ERR i()==3} extends R2 {}
	class R3 {}
	@ERR @ERR @ERR class R4 extends R3 {@ERR i()==3} {}
	
	@ERR static class W extends W {}
	static val i=3;
	@ERR static class P extends Q{i==3} {}
	static class Q extends P{i==3} {}

	@ERR static class S[T] extends S[S[T]] {}
	
	@ERR static class A[T] extends B[A[T]] {}
	static class B[T] extends A[B[T]] {}

	@ERR static class E[T] extends T {} // Cannot extend type T; not a class.


	// circularity in interfaces
	@ERR interface I1 extends I2 {}
	interface I2 extends I1 {}

	@ERR interface I3 extends I3 {}
	@ERR interface I4[T] extends I4[I4[T]] {}
	@ERR interface I5(i:Int) extends I5{self.i==1} {}
	interface I6(i:I6) {}

	interface Comparable[T](i:T) {}
	class Foo(i:Foo) implements Comparable[Foo] {}
	@ERR class Foo2(i:Comparable[Foo2]) implements Comparable[Foo2] {}
}

class ConformanceChecks { // see XTENLANG-989
	interface A {
	   def m(i:Int){i==3}:void;
	}
	class IntDataPoint implements A {
	   @ShouldBeErr public def m(i:Int){i==4}:void {}
	}
}

class TestThisAndInheritanceInConstraints {
	class A {
		def m(b1:A, var b2:A{A.this==b1}) {}
	}
	class B extends A {
		def m(b1:A, var b2:A{B.this==b1}) {}
	}
	class C extends A {
		@ShouldBeErr def m(b1:A, var b2:A{C.this==b2}) {}
	}
}

class TestConstraintsAndProperties(i:Int, j:Int) {
	def test1(var x1:TestConstraintsAndProperties{self.i==1}, var x2:TestConstraintsAndProperties{self.i==2}) {
		x1 = @ERR x2;
	}
	def test2(var x1:TestConstraintsAndProperties{this.i==1}, var x2:TestConstraintsAndProperties{this.i==2}) {
		x1 = @ERR x2;
	}
	def test3(var x1:TestConstraintsAndProperties{this.i==1}, var x2:TestConstraintsAndProperties{this.j==2}) {
		x1 = @ShouldBeErr x2;
	}
}

class TestFieldsInConstraints { // see XTENLANG-989
	class A {
	 public val i:Int = 2;
	 public val k = 2;
	}
	class B extends A {
	 public val k = 3;
	 class C {
	  public val k = 4;
	  public property j() = B.this.i;
	  // all these refer to the same field A.i
	  def m1(var x1:C{C.this.j()==1}, var x2:C{B.this.i==1}, var x3:C{B.super.i==1}) {
		x1 = x2;
		x1 = x3;
		x2 = x1;
		x2 = x3;
		x1 = x2;
		x1 = x3;
	  }
	  // check field resolution
	  def testResolution() {
		  val Ak:Int{self==2} = B.super.k;
		  val Bk:Int{self==3} = B.this.k;
		  val Ck:Int{self==4} = C.this.k;
		  
		  @ERR val Ak2:Int{self==5} = B.super.k; // todo: Found type: x10.lang.Int{self==2, B#this.k==2}  ???
		  @ERR val Bk2:Int{self==5} = B.this.k; // ok: Found type: x10.lang.Int{self==3, B#this.k==3}
		  @ERR val Ck2:Int{self==5} = C.this.k; // ok: Found type: x10.lang.Int{self==4, B.C#this.k==4}
	  }
	  // all these refer to different fields "k"
	  def m2(var x1:C{C.this.k==1}, var x2:C{B.this.k==1}, var x3:C{B.super.k==1}) {
		x1 = @ShouldBeErr x2;
		x1 = @ShouldBeErr x3;
		x2 = @ShouldBeErr x1;
		x2 = @ShouldBeErr x3;
		x1 = @ShouldBeErr x2;
		x1 = @ShouldBeErr x3;
	  }
	  // in fact, even if we use different literals (1,2,3), we still don't get an error!
	  def m3(var x1:C{C.this.k==1}, var x2:C{B.this.k==2}, var x3:C{B.super.k==3}) {
		x1 = @ShouldBeErr x2;
		x1 = @ShouldBeErr x3;
		x2 = @ShouldBeErr x1;
		x2 = @ShouldBeErr x3;
		x1 = @ShouldBeErr x2;
		x1 = @ShouldBeErr x3;
	  }
	 }
	}
}

class PropertyFieldResolution {
	interface A(i:Int) {}
	class B(i:Int{self==0})  implements A {
		val k1:Int{self==0} = this.i;
		@ERR val k2:Int{self==1} = this.i;
	}
}
class SettableAssignWithoutApply {
    operator this(i:Int)=(v: Int): void {}
	def m() {
        this(1) = 2;
        this(1) += @ERR @ERR 2;
	}
	def testSettableAssign(b:DistArray[Int], p:Point(b.rank)) {
		b(p)+=1;
		b(p)++;
	}
}





class TestTypeParamShaddowing {
class E[F] {F <: String } { //1
 def test(f:F):String = f; // making sure "F" refers to the type parameter

 @ERR val f1 = new F(1); // in Java: unexpected type. found: type parameter F.  required: class
 class F { //2
    @ShouldBeErr val ff = new F(1); // in Java: unexpected type. found: type parameter F.  required: class
    def this(i:Int) {}
 }
 @ERR val f2 = new F(1); // in Java: unexpected type. found: type parameter F.  required: class
 // you can choose the class over the type parameter by using the outer class as a qualifier
 def disambiguate() {
	 val f3 = this.new F(1);
	 val f4 = new E.F(1);
 }
}
}
class ShaddowingTypeParametersTests[T, T2,T3] {
	@ShouldBeErr def m1[T](t:T) {}
	static def m2[T](t:T) {}
	@ShouldBeErr static struct T {}
	@ShouldBeErr static class T2 {}
    @ShouldBeErr public static type T3 = Int;

	class A {}
	class B[A] {}

	class C {
		class D[C] {}
	}

	class E[F] {
		@ShouldBeErr class F {}
	}

	class G[U] {
		@ShouldBeErr class I[U] {}
	}

	class J {
		@ShouldBeErr class J2[T] {}
		@ShouldBeErr def m[T](t:T) {}
	}

	def q[U]() {
		{
			@ShouldBeErr class U {}
		}
		{
			@ShouldBeErr class A[U] {}
		}
		{
			class K[U2] {
				@ShouldBeErr def m1[U]() {}
				@ShouldBeErr def m2[U2]() {}
			}
		}
	}
}

class YetAnotherConstraintBugWithFieldPropogation {
	def test1() {
		val region = (1..1)*(1..1);
		val i: Iterator[Point{self.rank==2}] = region.iterator();
	}
	def test2() {
		val i: Iterator[Point{self.rank==2}] = ((1..1)*(1..1)).iterator(); // was XTENLANG-2275
	}
}
class CyclicTypeDefs {	
	static type B = B; // ERR
	static type X = Y; // 
	static type Y = Z; // ERR
	static type Z = X; // ERR ERR
}
class XTENLANG_2277 {	
	def m(a:Rail[Int]) {
		val x:Int{self==2} = a(0)+=2; // ShouldBeErr
	}
}

class TestSetAndApplyOperators {
	static class OnlySet {
		operator this(i:Int) = 0;
	}
	static class OnlyApply {
		operator this(i:Int)=(v:Int) = 1;
	}
	static class BothSetApply {
		operator this(i:Int) = 2;
		operator this(i:Int)=(v:Int):Int{self==v} = v;
	}
	def test(s:OnlySet, a:OnlyApply, sa:BothSetApply) {
		s(2);
		a(2); // ERR
		sa(2);
		s(2) = 3; // ERR
		a(2) = 3; 
		sa(2) = 3; 
		s(2) += 3; // ERR
		a(2) += 3; // ERR ERR
		sa(2) += 5; 
		val i1:Int{self==5} = sa(2) = 5; 
		val i2:Int{self==5} = sa(2) += 5; // ShouldBeErr (XTENLANG_2277)
		val i3:Int{self==4} = sa(2) += 5; // ERR
	}
}

class ArrayAndRegionTests {
	def test(a1:Array[Int](0..10), r:Region{zeroBased, rect, rank==1}, a2:Array[Int](r), a3:Array[Int]{zeroBased, rect, rank==1}) {
		val reg:Region{zeroBased, rect, rank==1} = 0..10;
		val arr1:Array[Int]{zeroBased, rect, rank==1} = new Array[Int](0..10,0);
		val arr2:Array[Int]{zeroBased, rect, rank==1} = new Array[Int](reg,0);
		val arr3:Array[Int]{region.zeroBased, region.rect, region.rank==1} = new Array[Int](reg,0);
		val arr4:Array[Int](reg) = null;
		m1(a1);  // ShouldNotBeERR
		m1(a2);
		m1(a3);
		m1(arr3);
		m1(arr4);

		m2(a1); // ShouldNotBeERR
		m2(a2);
		m2(a3);
		m2(arr3);
		m2(arr4);
	}
	def m1(Array[Int]{zeroBased, rect, rank==1}) {}
	def m2(Array[Int]{region.zeroBased, region.rect, region.rank==1}) {}
}

class PropertyFieldTest42 { // XTENLANG-945
	interface I(a:Int) {}
	class B {
		def m(i:I) = i.a;
	}
}

class MethodGuardEntailsOverriden {
	class A(i:Int) {
	  def m() {i==1} {}
	  def m2() {i!=0} {}
	}
	class B extends A {
	  def this() { super(3); }
	  def m() {i!=0} {}
	  @ShouldBeErr def m2() {i==1} {} // guard can only be made stronger. see XTENLANG-2325
	} 
}


class TestOperatorsWithoutGuards {
	public operator - this:Int = 1;
	public operator this * (g:TestOperatorsWithoutGuards) = 2;
	public operator this(i:Int) = 3;
	public operator this(i:Int) = (j:Int) = 4;

	def test(g1:TestOperatorsWithoutGuards, g2:TestOperatorsWithoutGuards) {
		val a = -g1;
		val b = g1*g2;
		val c = g1(42);
		val d = g1(42)=43;
	}
}
class XTENLANG_2329(x:Int) { // see XTENLANG_2329, but here we check with VERBOSE_CALLS (unlike in XTENLANG_2329.x10 where we check with STATIC_CALLS)
	public operator this * (g:XTENLANG_2329) {x==0} = 2;
	public operator this(i:Int) {x==0} = 3;
	public operator this(i:Int) = (j:Int) {x==0}  = 4;

	def test(g1:XTENLANG_2329, g2:XTENLANG_2329) {
		@ERR val b = g1*g2;
		@ERR val c = g1(42);
		@ERR val d = g1(42)=43;
	}
	
	def closureTest(c: (i:Int) {i==0} => Int , k:Int ) {
		@ShouldBeErr val a = c(k);
	}
}

class DynamicGuardCheck {
	class A {
		def this(q:Int) {q==0} {}
	}
	class B extends A {
		def m1(i:Int{self==0}) {}
		def m2(i:Int) {i==0} {}
		def this(i:Int, j:Int) {i==0} {
			super(j); // ERR: with VERBOSE:	Warning: Generated a dynamic check for the method guard.
		}
		def this(i1:Int) {
			this(i1,4); // ERR: with VERBOSE:	Warning: Generated a dynamic check for the method guard.
		}

		def test(q:Int) {
			m1(q); // ERR: Warning: Expression 'q' was cast to type x10.lang.Int{self==0}.
			m2(q); // ERR: with VERBOSE:	Warning: Generated a dynamic check for the method guard.		with STATIC_CALLS: Method m2(i: x10.lang.Int){i==0}[]: void in Hello{self==Hello#this} cannot be called with arguments (x10.lang.Int{self==q}); Call invalid; calling environment does not entail the method guard.
			new B(q,q); // ERR: with VERBOSE:	Warning: Generated a dynamic check for the method guard.
		}

		def closureTest(c: (i:Int) {i==0} => Int , k:Int ) {
			@ShouldBeErr { c(k); }
		}
	}
}