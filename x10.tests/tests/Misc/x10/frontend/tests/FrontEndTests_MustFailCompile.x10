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

package x10.frontend.tests;

// TODO: We should put ALL our tests in different packages according to the directory structure

// todo: if you change it to VERBOSE_CHECKS, we're missing a lot of warnings (I should create a similar test case that checks VERBOSE_CHECKS) 
//OPTIONS: -STATIC_CHECKS 

import harness.x10Test;

import x10.compiler.*; // @Uncounted @NonEscaping @NoThisAccess
import x10.compiler.Synthetic;
import x10.compiler.tests.*; // err markers
import x10.regionarray.*;
import x10.util.*;
import x10.lang.annotations.*; // FieldAnnotation MethodAnnotation

import x10.io.CustomSerialization;
import x10.io.Deserializer;
import x10.io.Serializer;

/**
 * A group of tests for various frontend-related compilation issues.
 */
public class FrontEndTests_MustFailCompile extends x10Test {
	public def run():Boolean {
		return true;
	}
	public static def main(Rail[String]) {
		new FrontEndTests_MustFailCompile().execute();
	}
}


// test object initialization (and more)

class TestFinalField42 {
	val q:Long;
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
	private def f(i:Long):Long=i+1;
}
class TestFinalField43 {
	val q:Long;
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
	private def f(i:Long):Long=i+1;
}
class TestFinalField {
	static val y:Long; // ERR
	static val s:Long = 3;
	static def test() {
		s = 4;  // ERR
	}

	val f:Long;
	var i:Long;
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
	def this(Long) { 
		async f=2; 
		f=2; // ERR
	}
}

final class ClosuresDuringConstruction {
	var k:Long;
	def f() = k=3;
	def f2() = 3;
	val c2 = ()=>f(); // ERR
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


class WithManyProperties(i:Long,j:Long) {
	def this() {} // ERR: property(...) might not have been called
}
class NoPropertiesButWithPropertyCall {
	def this() {
		property(1); // ERR: Semantic Error: The property initializer must have the same number of arguments as properties for the class.
	}
	def this(Long) {
		super();
	}
	def this(Double) {
		super();
		property(1); // ERR
	}
}
class FinalFieldWrittenExactlyOnce {
	val f:Long;
	var flag:Boolean;
	var i:Int;
	def this() {} // ERR: not written
	def this(Long) { f=1; }
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
	def this(Int) { // ERR
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
		case 1n:
			f=1;
			break;
		case 3n:
			f=2;
			break;
		default:
			f=3;
		}
	}
	def this(UInt) { // ERR
		switch (i) {
		case 1n:
			f=1;
			break;
		case 3n:
			f=2;
			break;
		}
	}
	def this(ULong) {
		switch (i as int) {
		case 1n:
			f=1;
			break;
		case 3n:
			f=2;
			break;
		}
		f=3;  // ERR
	}
	def this(Char) {  
		switch (i) {
		case 1n:
			f=1;
		default:
			f=3; // ERR
		}
	}
	def this(Int,Int) { // ERR
		if (flag) f=1;
		else if (flag) f=2;
	}
	def this(Long,Byte) { // ERR
		val b:Long = (b=5); // ERR
		var k:Long = (k=5); // ERR
		while (true) { val i:Long = 4;}
		f=f+1; // ERR
	}
	def this(Long,Short) { // ERR: Field 'f' was not definitely assigned.
		m();
	}
	private def m() {
		f=1; // ERR
	}
}

class TestExceptionsDefAssignment {
	def test() {
		val x1:Long;
		val x2:Long;
		val x3:Long;
		try {
			x1 = 2;
		} catch (e:Exception) {
			x2 = 3;
		} finally {
			x3 = 4;
		}
		use(x1); // ERR
		use(x2); // ERR
		use(x3);
	}
	var b:Boolean;
	def testReturn() {
		val x1:Long;
		if (b) {
			x1 = 1;
			return;
		}
		x1 = 2;
		use(x1);
	}
	def use(a:Any) {}
}
class InfiniteInit234 {
	var i:Long{self!=0};
	def this() {
		foo();
	}
	@NonEscaping private def foo() { foo(); }
}

class AllowCallsIfNoReadNorWrite {
	class Inner(i:Long) {
		def this() {
			val w = this.foo1();
			property(4);
		}
		@NonEscaping private def foo1() = 3 + foo2();
		@NonEscaping private def foo2() = 3;
	}
}


class DisallowCallsUnlessNoThisAccess {
	class Inner(i:Long) {
		static y=5;
		var x:Long=2;
		val z:Long=3;
		def this() {
			val w = this.foo1(); // ERR: You can use 'this' before 'property(...)' to call only @NoThisAccess methods or NonEscaping methods that do not read nor write any fields.
			property(4);
		}
		def this(i:Long) {
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
	var i1:Long{self!=0} = i2; // ERR: Cannot read from field 'i2' before it is definitely assigned.
	var i2:Long{self!=0} = i1;
}

class TestUncountedAsync1 {
	//@Uncounted async S
	//is treated like this:
	//async if (flag) S
	//so the statement in S might or might not get executed.
	//Therefore even after a "finish" we still can't use anything assigned in S.                
	def test1() {
		val q:Long;
		val q2:Long;
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
		var q:Long;
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
		var q:Long;
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
	def use(a:Long) {}

	var x:Long{self!=0};
	var x2:Long{self!=0};
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


class SquareMatrixTest123(rows:Long, cols:Long, matDist:Dist, mat:DistArray[Long]){
	var z:Long = 2;
	val q:Long;
	def this(r:Long, c:Long{self == r}) 	{
		val mShape:Region(2) = null;
		val mDist = Dist.makeBlock(mShape);
		z++; // ERR: Can use 'this' only after 'property(...)'
		val closure = () =>
			z++; // ERR
		val closure2 = () => 
			q; // ERR
		property(r, c, mDist, DistArray.make[Long](mDist, 
			initMat // ERR: Can use 'this' only after 'property(...)'
		));
		q=3;
	}
	val initMat : (Point(2)) => long = ([x,y]:Point(2)) => x+y;
} 

class TwoErrorsInOneLineTest(o:Long) {
	var k:Long;
	def this() {
		k=  // ERR
			o; // ERR
		property(2);
	}
}

class SomeSuper87 {
	def this(i:Long) {}
}   
class TestSuperThisAndPropertyCalls(p:Long) extends SomeSuper87 {
	var i:Long;
	def this() {
		super(i); // ERR: Can use 'this' only after 'property(...)'
		property(i); // ERR: Can use 'this' only after 'property(...)'
	}
	def this(i:Long) {
		super(i);
		property(i);
	}
	def this(b:Boolean) {
		this(i); // ERR: Can use 'this' only after 'property(...)'
		property(1); // ERR: You cannot call 'property(...)' after 'this(...)'
	}
	def this(x:Double) { super(1); } // ERR: property(...) might not have been called
	def this(x:Float) { property(1); } // ERR: super() not found
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
class TestPropAndConstants(p:Long) implements BarI34 {
	val q:Long = 3 as Long; // this will be moved (it might access properties)
	val q3:Long = p+4;
	val q2:Long = 42; // this is constant, so it won't be moved to the __fieldInitializers()
	var x:Long;
	def this() {
		val w=4;
		property(45);
		x=2;
	}
	def this(i:Long) {
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
static class WithProperties(x:Long) {
	def this() {
		property(1);
	}
	def this(i:Long) {
		property(i);
	}
}
static class SubWithProperties(y:Long) extends WithProperties {
	static S=1;
	val k=3;
	var z:Long = 4;
	def this() {
		super(1);
		property(2);
	}
	def this(i:Boolean) {
		this();
	}
	def this(i:Long) { 
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



class TestPropertyCalls(p:Long, p2:Long) {
	def this() {} // ERR: property(...) might not have been called
	def this(Char) {
		property(1); // ERR: The property initializer must have the same number of arguments as properties for the class.
	}
	def this(i:Long) {
		val x:Long;
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
		if (b==1.0)
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
	var i:Long{self != 0}; // ERR: Semantic Error: Field 'i' was not definitely assigned.
	val closure = () =>  { i=2; } ; // ERR
}

class TestPropertiesAndFields(i:Long, j:Long) {
	def this() {
		val x = 3;
		property(x,x);
		val k = i;
		val closure = () => k+4;
		j2 = j;
	}		
	
	val i2 :Long{self==i} = i;
	val j2 :Long{self==j};
}
class CheckCtorContextIsNotStatic[T](p:Array[T]) {
    public def this(o:Any) {
        property(o as Array[T]); // ERR: Warning: This is an unsound cast because X10 currently does not perform constraint solving at runtime for generic parameters.
    }
} 


class TransientTest(p:Long) { // The transient field '...' must have a type with a default value.
	transient val x1 = 2; // ERR (because the type is infered to be Long{self==2}
	transient val x2:Long = 2;
	transient var y:Long;
	transient var y2:Long{self==3} = 3; // ERR
	transient var y3:Long{self!=0}; // ERR
	transient var y4:Long{self==0}; 
	transient var y5:Long{self!=3}; 
	transient var y6:Long{self==p}; // ERR
	def this(k:Long) {
		property(k);
		y3 = 4;
		y6 = p;
	}
}
		
class XTENLANG_1643 {
  var i:Long{self!=0};
  def this(j:Long{self!=0}) { i = j; }
}


final class ClosureTest57 {
	val z = 1;
	val c1 = () => z+1; // ERR
	var x:Long{self!=0} = 1;
	val c2 = () => { 
		x=3; // ERR
		return x+1; // ERR
	};
	var y:Long{self!=0}; // ERR: Field 'y' was not definitely assigned.
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
  val bar: ()=>Long = ()=>this.f(); // ERR: The method call reads from field 'x' before it is definitely assigned.
  val z = bar();
  val x=2;

  var w:Long{self!=0}; // ERR: Field 'w' was not definitely assigned.
  def setW() = w=2;
  val q = ()=>this.setW(); // ERR
  
  var w2:Long{self!=0}; 
  def setW2() = w2=2;
  val q2 = this.setW2(); // ok
}

class MultipleCtorsAndFieldInits {
	
	var b:Long{self!=0};
	var i:Long{self!=0};
	var z:Long{self!=0} = (3*b*(b=1)) as Long{self!=0}; // ERR: Cannot read from field 'b' before it is definitely assigned.
	var j:Boolean{self!=false}; 
	var k:Any{self!=null}; 

	
	def this(a:Long) {
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
		k= new Empty();
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
		val s2:Long;
		def this() {
			s2 = 2;
		}
	}
	static class Sub extends Super {
		val a = s1+s2;
		val b:Long;
		def this() {
			super();
			b = s1*s2;
		}
	}
}



class DynamicDispatchingInCtorTest {
	abstract class Super {
		val x:Long;
		val size:Long;
		def this() {
			this.x = 42;
			size = calcSize(x);
		}
		@NoThisAccess abstract def calcSize(x:Long):Long;
		@NonEscaping def useError(i:Long):void {} // ERR: A @NonEscaping method must be private or final.	
		@NonEscaping final def use(i:Long):void {} 
		@NonEscaping private def useOk2(i:Long):void {} 
	}
	class Sub1 extends Super {
		@NoThisAccess def calcSize(x:Long):Long { return x*2; }
	}
	class Sub2 extends Super {
		def calcSize(x:Long):Long { // ERR: You must annotate x10.lang.Long calcSize(...) with @NoThisAccess because it overrides a method annotated with that. 
			return x*4; 
		}
	}
	class Sub3(p:Long) extends Super {
		val w = 3;
		var k:Long{self==p};
		def this() {
			property(4);
			k = p;
		}
		@NoThisAccess def calcSize(x:Long):Long { 
			use( // ERR: You cannot use 'this' or 'super' in a method annotated with @NoThisAccess
				w); // ERR: You cannot use 'this' or 'super' in a method annotated with @NoThisAccess
			return x+2; 
		}
	}
}

class TestAsync {	
	var i:Long{self!=0};
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
	
	static def use(a:Long) {}
	
	public static def main(Rail[String]) {
	     var i:Long;
	     var j:Long;
	     var k:Long;
	     var x:Long;
	     var y:Long;
         val m:Long;
         val n:Long;
         val q:Long;

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
	static def use2(loc:Long,expected:Long,a:Long) { if (expected!=a) throw new Exception("ERROR! loc="+loc+" expected="+expected+" a="+a); }
	
	public static def main2(Rail[String]) {
	     var i:Long;
	     var j:Long;
	     var k:Long;
	     var x:Long;
	     var y:Long;
         val m:Long;
         val n:Long;
         val q:Long;

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


class PropertyTest(p:Long) {
    public property p():Long = p;
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
	val c:Long;
	val d:Long;
	var x:Long{self!=0}; 
	var y:Long{self!=0};
	var z:Long{self!=0};

	def this(i:Long) { this(); x = y; }
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

	def g():Long = 1;
	abstract @NonEscaping def q():Long; // ERR: A @NonEscaping method must be private or final.
	@NonEscaping final def ba():Long = a+b;
	@NonEscaping private def f0():Long = a+b+c;
	@NonEscaping protected def f1():Long = a+c; // ERR: A @NonEscaping method must be private or final.
	@NonEscaping final native def e1():Long; 
	@NonEscaping native def e2():Long; // ERR: A @NonEscaping method must be private or final.
}

class Sub1Test extends SuperClassTest {
	val w = 1;
	var q:Long{self!=0} = 1;
	def this(i:Long) { this(); x = y; }
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
	@NonEscaping private def f2():Long = 1;
	def q():Long = 2;
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
	def bla():Long{self!=0};
}
class TestAnonymousClass {
	static val anonymous1 = new Any() {};
	val anonymous2 = new Any() {}; // ERR: 'this' cannot escape via an anonymous class during construction
	val anonymous3 = new TestAnonymousClass() {}; // ERR: 'this' cannot escape via an anonymous class during construction
	def foo() {
		val x = new Any() {};
	}
	@NonEscaping final def foo2() {
		val x = new Any() {}; // ERR: 'this' cannot escape via an anonymous class during construction
	}

	val anonymous = new BlaInterface() { // ERR: 'this' cannot escape via an anonymous class during construction
		public def bla():Long{self!=0} {
			return k;
		}
	};
	val inner = new Inner(); // ERR: 'this' and 'super' cannot escape from a constructor or from methods called from a constructor
	
	val qqqq = at (Place.places().next(here)) this; // ERR: Semantic Error: 'this' and 'super' cannot escape from a constructor or from methods called from a constructor

	val w:Long{self!=0} = anonymous.bla();
	val k:Long{self!=0};
	def this() {
		assert w!=0;
		k = 3;
	}

	class Inner implements BlaInterface {
		public def bla():Long{self!=0} {
			return k;
		}
	}
}


class C57 {
 var m: Long{self!=0};
 var n: Long{self!=0};
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


struct TestStructCtor[T] { T isref } {
	def this() {}
	def test1(
		TestStructCtor[TestStructCtor[String]]) {// ERR (Semantic Error: Type TestStructCtor[TestStructCtor[x10.lang.String]] is inconsistent.)
	}
	def use(Any) {}
	def test2() {
		use( TestStructCtor[TestStructCtor[String]]() ); // ERR (Semantic Error: Inconsistent constructor return type)
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
			k1 = GlobalRef[Inner](this); // now ok
			w = GlobalRef[TestGlobalRefRestriction](TestGlobalRefRestriction.this);
		}
	}
}


class TestFieldInitializer {
	var flag:Boolean = true;
	var z:Long = flag ? 3 : z+1; // ERR: Cannot read from field 'z' before it is definitely assigned.
	val j = flag ? 3 : foo(); // ERR: reads from j before it is assigned.
	val k = foo();	
	var i:Long{self!=0};
	@NonEscaping final def foo():Long {
		val z = j;
		i = 1;
		return 2;
	}
}

class Test2 {
    val layout:Long{self!=0};
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
  var i1:Long{self!=0};
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
  var i1:Long{self!=0};
  var i2:Long{self!=0};
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
  var i1:Long{self!=0};
  var i2:Long{self!=0};
  var i3:Long{self!=0};

  def this() {
    m1();
  }
  def this(i:Long) {
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
  val f1:Long = 4*2; 
  val f2:Long; // must be initialized in every ctor
  var v1:Long; // has a default value, does not have to be assigned
  var v2:Long{self!=0};  // must be assigned because there is no default value
  def this() {
    this(4);
  }
  def this(i:Long{self!=0}) {
    super();
    f2 = m1();
    setV2(i);
  }
  @NonEscaping private def m1():Long = v1++;
  @NonEscaping public final def setV2(i:Long{self!=0}) { v2 = i; }
}
class IllegalExample {
  var f2:Long{self!=0}; 
  var v2:Long{self!=0};  
  def this() { // ERR field is not initialized in this()
    f2 = 3;
    if (3==4) setV2();
  }
  def this(i:Long) { // ERR field is not initialized in this(Long)
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
	static def f(Long)=2;
	public static def main(Rail[String]) {
		val i1:Long{self==1} = f(1y);
		val i2:Long{self==2} = f(1);
		if (i1!=1 || i2!=2) throw new Exception("Failed");
	}
}
class TestFieldInitForwardRef {
	val Y:Long = this.X;
	static val X:Long = 2;

	class Inner {
		var g:Long = this.f+2; // ERR
		var h:Long = this.h*3; // ERR
		var f:Long = q+2;
	}
	static class StaticInner {
		var f:Long;
		var g:Long = this.f+2; 
		var h:Long = this.h*3; // ERR
	}

	var z1:Long = foo(null); 
	var z2:Long = this.foo(null); 
	var z3:TestFieldInitForwardRef = this; // ERR
	var z4:Long = z3.foo(this); // ERR
	var z5:Long = z3.foo(null);
	var w1:Long = z3.a;
	var w2:Long = this.a; // ERR
	
	var a:Long = q+2; // ERR
	var b:Long = this.q+2; // ERR
	var c:Long = a+b+this.q+2; // ERR
	var w:Long = a*b+this.c;
	var p:Long = a*b+this.c+q; // ERR
	var q:Long = 1;

	var e:Inner = new Inner(); // ERR
	var e2:StaticInner = new StaticInner();

	@NonEscaping private def foo(arg:TestFieldInitForwardRef):Long = 3;
}


// The following types haszero:
//    * a type that can be null  (e.g., Any, closures, but not a struct or Any{self!=null} )
//	  * Primitive structs (Short,UShort,Byte,UByte, Int, Long, ULong, UInt, Float, Double, Boolean, Char)
//    * user defined structs without a constraint and without a class invariant where all fields haszero.
class SimpleUserDefinedStructTest {	
	static struct S(x:Long, y:Long) {}
	static struct S2 {
		val x:Long = 1;
	}
	static struct S3 {x==1} { // ERR
		val x:Long = 1;
	}
	static struct S4 {
		val x = 1;
	}
	static struct S5[T] {T haszero} {
	  val s:S2 = Zero.get[S2](); 
	  val t:T = Zero.get[T](); 
	}
	static struct S6 {
		val s:Long{self==1} = 1;
	}
	static struct S7 {
		val s:Long{self==0} = 0;
	}

	static class C {
	  var s:S; 
	  var x:S2; 
	  var y:S3; // ERR
	  var z1:S4; // ERR
	  var z2:S5[Long];
	  var z3:S6; // ERR
	  var z4:S7;

	  var s6:S{self.y==0};  // ERR (any constrained user-defined struct, doesn't haszero. because of a bug in ConstrainedType_c.fields())
	}
	def main(Rail[String]) {
		Console.OUT.println( Zero.get[S5[S5[Long]]]().t.t );
	}
}
class ConstraintPropagationToFields {
	static struct S(x:Long,y:Long) {
		def this(a:Long,b:Long):S{self.x==a,self.y==b} {
			property(a,b);
		}
	}
	static class C {
	  val s1:S{y!=0} = S(0,1); 
	  val s2:S{y!=0} = S(1,0); // ERR: The type of the field initializer is not a subtype of the field type.
	  val z1:Long{self!=0} = s1.y;
	  @ERR val z2:Long{self==0} = s1.y;
	}
}
struct UserDefinedStruct {}
class TestFieldsWithoutDefaults[T] {
	// generic parameter test
	var f2:T; // ERR

	// includes: Short,UShort,Byte,UByte, Int, Long, ULong, UInt, Float, Double, Boolean, Char
	// primitive private struct tests
	var i1:Int;
	var i2:Int{self==0n};
	var i3:Int{self!=1n};
	var i4:Int{self!=0n}; // ERR

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
	var l2:Long{self==0};
	var l3:Long{self!=0}; // ERR
	var ul1:ULong;
	var ul2:ULong{self==0ul};
	var ul3:ULong{self!=0lu}; // ERR
	var ui1:UInt;
	var ui2:UInt{self==0un};
	var ui3:UInt{self!=0un}; // ERR
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
	var b6:Boolean{!self}; // ERR
	var ch1:Char;
	var ch2:Char{self=='\0'};
	var ch3:Char{self!='\0'}; // ERR

	// references (with or without null)
	var r0:Array[Int{self!=0n}];
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








class EscapingCtorTest(p:String) {
	var tt:EscapingCtorTest;
	val w:Long;
	val v1:Long = 1;
	var x1:Long;
	static def foo(t:EscapingCtorTest)=2;	
	static def bar(t:Inner)=2;	
	def this() {
		this(null);
	}
	def this(i:Long) {
		this(i,null);
	}
	def this(a:EscapingCtorTest) {
		property(null);
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
	def this(i:Long,a:EscapingCtorTest) {
		property(null);
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
		val f:Long;
		val v2:Long = 4;
		var x2:Long;
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
  var i1:Long{self!=0};
  var i2:Long{self!=0};
  var i3:Long{self!=0};

  def this() {
    m1(); 
  }
  def this(i:Long) {
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
	def equals(i:Long)=false;
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
struct BlaStruct(p:Long) implements InterfaceForStruct {
	val i:Long = 4;
	val jj:String =  new String("as");
	val j:ULong = 4u;
	val h:Empty = new Empty();
	def this(p:Long) {		property(p);	}
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
    val i=2n+1n;
	def this() {
		switch(3n+0n) {
            case i:
            case 4n:
        }
    }   
	def testSwitchOnLocal(y:int) {
        switch(y) {
            case 1n: val x = 4;
            case 4n: Console.OUT.println(x); // ShouldBeErr (see XTENLANG-2267)
        }
    }
}




class XTENLANG_1196 {
	def test1(P:Array[Long]) {
		for (p in P) 
			async break; // ERR
	}
	def test2(vec:Dist) {
		// foreach was removed
		ateach(v in vec) {
			break; // ERR
		}
	}
}
class TestBreaksInAsyncAt {
	class Shyk_Flup  { // XTENLANG-823
	  public def test() {
		 
		 lesp_frobi: for (var i : Long = 0; i < 10; i++) {
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
		var i:Long = 2;
		finish async i++;
	}
	def atAndAsync() {
		var i:Long = 2;
		val p = here;
		finish at (Place.places().next(here)) async {
		  finish async at (p) 
			  i++; // ok
		}
	}
	def test1() {		
		var i:Long = 2;
		finish async {
		  finish async i++; // ok
		}
	}
	def test2() {
		var i:Long = 2;
		finish {
			val x= ()=> { async 
				i++; // ERR: cannot capture local var in a closure
			};
		}
	}
	var flag:Boolean;
	def test3() {
		var i:Long = 2;
		if (flag) {
		  finish async i++; // ok
		}
	}
	def test4() {
		var i:Long = 2;
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
	val x:Long = 1;
	var y:Long = 1;

    public def run() {
        
        val a:Long = 1;
        var b:Long = 1;

        val closure = 
			() => 
				x+
				y+
				a+
				b; // ERR: Local variable "b" is accessed from an inner class or a closure, and must be declared final.
    }

	static def use(i:Any) {}
	def test2() {
		var q:Long = 1;
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
		
		use(() => { var q:Long = 1;
			use(q);
		});
		use(() => { var q:Long = 1; async
			use(q); // ERR
		});
		use(() => { var q:Long = 1; finish async { async
			use(q);
		}});
		use(() => { finish async { var q:Long = 1; async
			use(q); // ERR
		}});
		use(() => { finish { var q:Long = 1; async { async
			use(q); // ERR
		}}});
		use(() => { finish { async { async {var q:Long = 1; 
			use(q);
		}}}});
		use(() => { finish { async { async {
			use(q); // ERR: Local variable "q" is accessed from an inner class or a closure, and must be declared final.
			var q:Long = 1; 
		}}}});
		val w = q;
	}
}

class TestCaptureVarInClosureAsyncInnerAnon {
	def test() {
		var x:Long = 2;
		class Inner {
			val y = 
				x+2; // ERR: Local variable "x" is accessed from an inner class or a closure, and must be declared final.
		}
		val inner = new Inner();
		val anonymous = new Any() {
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
   val a1:long = 1;
   var a2:long = 1;
    public def run() {
        
        val b1:long = 1;
        var b2:long = 1;

        class C {
            val c1:long = 1;
            var c2:long = 1;
            def foo() {
                val fun = () => {
                    val d1:long = 1;
                    var d2:long = 1;
                    (() => a1+b1+c1+d1+
						a2+
						b2+ // ERR: Local variable "b2" is accessed from an inner class or a closure, and must be declared final.
						c2+
						d2  // ERR: Local variable "d2" is accessed from an inner class or a closure, and must be declared final.
						)()
                };
                return fun();
            }
        }
	}
}


class TestOnlyLocalVarAccess {
	// for some reason, the origin of "frame" is null. I can reproduce it using type inference: see XTENLANG-1902
	var i:Long;
	static def testInferReturnType()=test0(null);
	static def test0(var b:TestOnlyLocalVarAccess) { // we type-checking test0 twice: once to infer its return type (then "b"'s placeTerm is null), then a second time to really type-check it (then the placeTerm is fine)
		return b.i++;
	}

	static def use(x:Any) {}
	static def testUse() {
		var x:Long = 0;
		at (here) use(x);
		use(x);
		at (Place.places().next(here)) 
			use(x); // ERR: Local variable "x" is accessed at a different place, and must be declared final.
		val place1 = here;
		val place2 = place1;
		at (Place.places().next(here)) {
			use(x); // ERR: Local variable "x" is accessed at a different place, and must be declared final.
			at (place1) 
				use(x);
			at (place2) 
				use(x);
			at (place2) at (here) 
				use(x);
			at (place2) at (Place.places().next(here)) 
				use(x); // ERR: Local variable "x" is accessed at a different place, and must be declared final.
			at (place1) at (place2) 
				use(x);
		}
		use(x);
	}
	static def test0() {
		var x:Long = 0;
		at (here) x=20; // ERR: Local variable "x" is accessed at a different place, and must be declared final.
		x=10;
		at (Place.places().next(here)) 
			x=1; // ERR: Local variable "x" is accessed at a different place, and must be declared final.
		val place1 = here;
		val place2 = place1;
		at (Place.places().next(here)) {
			x=2; // ERR: Local variable "x" is accessed at a different place, and must be declared final.
			at (place1) 
				x=3; // ERR: Local variable "x" is accessed at a different place, and must be declared final.
			at (place2) 
				x=4; // ERR: Local variable "x" is accessed at a different place, and must be declared final.
			at (place1) at (place2) 
				x=5; // ERR: Local variable "x" is accessed at a different place, and must be declared final.
		}
		Console.OUT.println(x);
	}
	def test1() {
		var x:Long = 0;
		at (here) x=20; // ERR: Local variable "x" is accessed at a different place, and must be declared final.
		x=10;
		at (Place.places().next(here)) 
			x=1; // ERR: Local variable "x" is accessed at a different place, and must be declared final.
		val place1 = here;
		val place2 = place1;
		at (Place.places().next(here)) {
			x=2; // ERR: Local variable "x" is accessed at a different place, and must be declared final.
			at (place1) 
				x=3; // ERR: Local variable "x" is accessed at a different place, and must be declared final.
			at (place2) 
				x=4; // ERR: Local variable "x" is accessed at a different place, and must be declared final.
			at (place1) at (place2) 
				x=5; // ERR: Local variable "x" is accessed at a different place, and must be declared final.
		}
		Console.OUT.println(x);
	}
	def test2(var x:Long) {
		at (here) x=20; // ERR: Local variable "x" is accessed at a different place, and must be declared final.
		x=10;
		at (Place.places().next(here)) 
			x=1; // ERR: Local variable "x" is accessed at a different place, and must be declared final.
		val place1 = here;
		val place2 = place1;
		at (Place.places().next(here)) {
			x=2; // ERR: Local variable "x" is accessed at a different place, and must be declared final.
			at (place1) 
				x=3; // ERR: Local variable "x" is accessed at a different place, and must be declared final.
			at (place2) 
				x=4; // ERR: Local variable "x" is accessed at a different place, and must be declared final.
			at (place1) at (place2) 
				x=5; // ERR: Local variable "x" is accessed at a different place, and must be declared final.
		}
		Console.OUT.println(x);
	}

	def test3(a: DistArray[double](1)) {		
        var n: long = 1;
		at (here) n=2; // ERR: Local variable "x" is accessed at a different place, and must be declared final.
		finish ateach ([i] in a.dist | 
			Region.make(0..n)) { // checks XTENLANG-1902
			n++; // ERR: Local variable "n" is accessed at a different place, and must be declared final.
		}
	}
}
class TestValInitUsingAt { // see XTENLANG-1942
    static def test2() {
        var x_tmp:Long = 0; // we have to initialize it (otherwise, the dataflow
        val p = here;
        at (Place.places().next(p))
          at (p)
            x_tmp = 2; // ERR: Local variable "x" is accessed at a different place, and must be declared final.
        val x = x_tmp; // if we hadn't initialized x_tmp, then the dataflow would complain that "x_tmp" may not have been initialized
    }
	static def testVal() {
		val x:Long;
		at (Place.places().next(here)) 
			x = 2; // ERR
	}
	static def testVar() {
		var x:Long;
		at (Place.places().next(here)) 
			x = 2; // ERR
	}
	static def testVarAtScope() {
		at (Place.places().next(here)) {
			var y:Long;
			y = 2;
		}
	}
	static def testOkVal() {
		val x:Long;
		val p = here;
		at (Place.places().next(p)) {
			at (p)
				x = 2; // ERR: Local variable "x" is accessed at a different place, and must be declared final.
		}
		at (Place.places().next(here)) {
			val z = x;
		}
		val y = x;
	}
	static def testOkVar() {
		var x:Long;
		val p = here;
		at (Place.places().next(p)) {
			at (p)
				x = 2; // ERR: Local variable "x" is accessed at a different place, and must be declared final.
		}
		at (Place.places().next(here))  {
			val z = x; // ERR
		}
		val y = x;
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

		at (r.home) {
			use(r()); 
			use(r2());
			use(r3());
		}
		at (r2.home) {
			use(r());
			use(r2()); 
			use(r3());
		}
		at (r3.home) {
			use(r());
			use(r2());
			use(r3());
		}
		at (Place.places().next(here)) {
			use(r()); // ERR
			use(r2()); // ERR
			use(r3()); // ERR			
			
			at (r.home) {
				use(r()); 
				use(r2());
				use(r3());
			}
			at (r2.home) {
				use(r());
				use(r2()); 
				use(r3());
			}
			at (r3.home) {
				use(r());
				use(r2());
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
		 return x();
	 }
	 def test2() {
		 val x = (at (Place.places().next(here)) new TestGlobalRefHomeAt2()).root; 
		 return @ERR x();
	 }
	 def test3() {
		 val x = at (Place.places().next(here)) (new TestGlobalRefHomeAt2().root); 
		 return @ERR x();
	 }
	 def test4() {
		 val x = this.root; 
		 return @ERR x();
	 }
	 def test5(y:TestGlobalRefHomeAt2) {
		 val x = y.root; 
		 return @ERR x();
	 }
}

class A564[T,U] {
    def foo(x:T,y:U, z:String):void {}
}
class B564 extends A564[String,String] { // ERR
    def foo(x:String,y:String, z:String):Long { return 1; } // ERR: foo(x: x10.lang.String, y: x10.lang.String, z: x10.lang.String): x10.lang.Long in B cannot override foo(x: x10.lang.String, y: x10.lang.String, z: x10.lang.String): void in A[x10.lang.String, x10.lang.String]; attempting to use incompatible return type.
	// B.foo(String,String,String) cannot override A.foo(T,U,String); attempting to use incompatible return type.  found: long required: void
}


class ReturnStatementTest {
	
	class A {
	  def m() {
		at (Place.places().next(here)) return here;// ShouldNotBeERR (Semantic Error: Cannot return value from void method or closure.)// ShouldNotBeERR (Semantic Error: Cannot return a value from method public x10.lang.Runtime.$dummyAsync(): void.)
		val x = 3; // ERR (unreachable statement)
	  }
	}
	
	static def ok(b:Boolean):Long {
		finish {
			async { val y=1; }
			return 3;
		}
	}
	static def ok2(b:Boolean):Long {
		if (b) 
			return 1;
		else {
			async {
				val closure = () => {
					return 3;
				};
			}
		}
		at (Place.places().next(here))
			return 2; // ShouldNotBeERR ERR todo: we get 2 errors: Cannot return value from void method or closure.		Cannot return a value from method public static x10.lang.Runtime.$dummyAsync(): void		
	}
	static def err1(b:Boolean):Long {
		if (b) return 1;
		async 
			return 2; // ERR: Cannot return from an async.
		return 3;
	}
	static def err2(b:Boolean):Long {
		async at (here) 
			return 1; // ERR: Cannot return from an async.
		return 2;
	}
	static def err3(b:Boolean):Long {
		at (here) async
			return 1; // ERR: Cannot return from an async.
		return 2;
	}
	static def err4(b:Boolean):Long {
		finish async 
			return 1; // ERR: Cannot return from an async.
		return 2;
	}
}


// Test method resolution

class TestMethodResolution { // see XTENLANG-1915
  def m(Int)="";
  def m(Long)=true;
  def m(Any)=3n;
  def rtype(c:Boolean) { if (c) return 1n; else return 2l; }
  def genm[T](a:T, b:T):T = a;
  def test(flag:Boolean) {
    val arr = [1n,2l]; // infers Array[Any]
    val x = flag ? 1n : 2l; // infers Any
    val r = rtype(flag); // infers Any
    val g = genm(1n,2l); // infers Any
    val i1:Int = m(arr(0n));
    val i2:Int = m(x); 
    val i3:Int = m(r);
    val i4:Int = m(g);
  }

  
	def check[T](t:T)=1n;
	def check(t:Any)="";
	def testGenerics() {
		@ERR val r1:Int = check(1n); // Err ambiguous Err
		@ERR val r2:Int = ((x:Any)=>check(x))(1n); // Err: should DEFNITELY resolve to the non-generic method
        val r3:Int = check[Int](1n); // be explicit
	}
}


class TestHereInGenericTypes { // see also XTENLANG-1922
	static class R {
	  val x:Place{self==here} = here; // ERR: Cannot use "here" in this context
	}
	static def foo(y:Place{self==here}) {
		assert y==here; // will fail at runtime! but according to the static type it should succeed!
	}
	static def bar(y:Place{self==here}) {
		at (Place.places().next(here)) foo(y); // ERR: todo: how can we report an error message that won't contain _place6 ?
		// Today's error: Method foo(y: x10.lang.Place{self==here}): void in TestHereInGenericTypes{self==TestHereInGenericTypes#this} cannot be called with arguments (x10.lang.Place{self==y, _place6==y});    Invalid Parameter.
//	 Expected type: x10.lang.Place{self==here}
//	 Found type: x10.lang.Place{self==y, _place6==y}
	}
  static def testR() {
	val r = new R();
	at (Place.places().next(here)) {
	  val r2:R = r; // This is the only "hole" in my proof: you can say that the type of "r" changed when it crossed the "at" boundary. But that will puzzle programmers...
	  foo(r2.x); // ERR: we didn't cross any "at", so from claim 3, the type of "r2.x" didn't change.
	}
  }



  private static class Box[T] {
    val t:T;
    def this(t:T) { this.t = t; }
  }
  def test() {
    val b:Box[Place{self==here}] = null;
	val p1:Place{self==here} = b.t;
	val HERE = here;
	at (Place.places().next(here)) {
		val b2:Box[Place{self==here}] = b; // ERR
		val p2:Place{self==here} = b.t; // ERR
		val b3:Box[Place{self==HERE}] = b;
		val p3:Place{self==HERE} = b.t;
	}
  }
  
	static def m(p:Place{self==here}) {}
	static def test1(p:Place{self==here}) {
		m(p); // ok
		at (Place.places().next(here)) {
			m(p); // ERR
		}
	}
	static def test2(p:Place) {p==here} {
		m(p); // OK
		at (Place.places().next(here)) {
			m(p); // ERR (XTENLANG-1929)
		}
	}
}

class TestInterfaceInvariants { // see XTENLANG-1930
	interface I {p()==1} {
        public property p():Long;
	}
	class C(p:Long) implements I {
        public property p():Long = p;
		def this() { 
			property(0); // ERR
		}
	}
	@ERR interface I2 extends I{self.p()==2} {}
	@ERR @ERR interface I3 {this.p()==3} extends I2 {}
	static def test(i:I) {
		@ERR @ERR var i1:I{self.p()==5} = i;
		var i2:I{self.p()==1} = i;
	}
}

class OuterThisConstraint(i:Long) { // see XTENLANG-1932
	def m1():OuterThisConstraint{self.i==this.i} = this;
	class Inner {
		def m2():OuterThisConstraint{self.i==OuterThisConstraint.this.i} = OuterThisConstraint.this;
	}
	static def test(a:OuterThisConstraint{i==3}) {
		val inner:OuterThisConstraint{self.i==3}.Inner = a.new Inner();
		val x1:OuterThisConstraint{i==3} = a.m1();
		val x2:OuterThisConstraint{i==3} = inner.m2();
		@ERR val x3:OuterThisConstraint{i==4} = inner.m2();
	}
}

class NullaryPropertyMethod {
	static class E(x:Long) {
		property y() = x==2;
		property z() = x==2;
		
		public static def test() {		
			val e = new E(2);
			var e1:E{y()} = null; // ShouldNotBeERR: Cannot access a non-static method from a static context
			e1 = e as E{y()}; // ShouldNotBeERR: Cannot access a non-static method from a static context
			var e2:E{z} = null;
			e2 = e as E{z};
			var e3:E{y} = null;
			e3 = e as E{y};
			var e4:E{z()} = null; // ShouldNotBeERR: Cannot access a non-static method from a static context
			e4 = e as E{z()}; // ShouldNotBeERR: Cannot access a non-static method from a static context
		}
		public static def main(Rail[String]) {
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
	public static def main(Rail[String]) {
		val h = new TestXtenLang1938();
		val x = h+h; // ShouldNotBeERR: Local variable cannot have type void
		// h+h; // doesn't parse! maybe it should?

	}
	static operator (p:TestXtenLang1938) + (q:TestXtenLang1938) { // ShouldBeErr: operators results are parsed as expressions (so now they can't return void)
		Console.OUT.println("overloaded +");
	}
}


class ConstraintsBugs {
	class A(p:Long) {
		def this(p:Long):A{self.p==p} {
			property(p);
		}
	}
	class B extends A{p==1} {
		def this():B{self.p==1} { // ERR ERR
			super(2); 
		}
	}
}

class SuperQualifier { // see XTENLANG-1948
	class Parent {
	public val f = 2;
	}
	class Ego extends Parent {
		// todo: this error blocks the entire next dataflow phase 
	//val x = Parent.super.f;  // ShouldNotBeERR: The nested class "Ego" does not have an enclosing instance of type "Parent".
	}
}
class TestRailLiteralInference {	
	var z: Rail[long]{size==4} = [1,2,3,4];
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

class XTENLANG_2399 {
    def m(p:Point(1)) {
		val [i,j] = p; // ERR
		val q[m]:Point(2) = [3,4]; // ERR
		for(x[n]:Point(2) in Region.make(3..4, 3..4))  {} // ERR (the type doesn't match the number of exploded ints
	}
}
class ExplodingPointTest[T] {	
	// you can explode either a Point or an Array[T] into its components, 
	// so the elements are either of type T or Long.
	// The rank of the Point/Array must be EQUAL to the number of components.
	// If you do not write in the type the rank of the Point/Array then it is assumed to be the number of components.
	// You can explode either in:
	// 1) a formal of a method where you must give the type
	// 2) a local where you either give the type or an intializer and then we infer the type
	// 3) a local in a loop (we look at the collection to infer the type of the index)
	def explodeArray(a[i,j,k]:Array[Array[T]]{size==3,rank==1}) {
		val z:Array[T] = i;
		val z2:Long = i; // ERR

		val w3:Array[Array[T]]{size==3,rank==1} = a; 
		val w4:Array[Array[T]]{size==2,rank==1} = a; // ERR
		val w5:Array[Array[T]]{size==3,rank==2} = a; // ERR
	}
	def explodePoint(a[i,j]:Point) {
		val z2:Long = i;
		val z:Array[T] = i;  // ERR
		val w2:Point(2) = a; 
		val w3:Point(3) = a; // ERR
	}
	def loopTest(a:ArrayList[Array[T]], b:Collection[Point], a2:ArrayList[Array[T](2)], b2:Collection[Point(2)]) {
		for (x[i] in a) {} // ERR
		for (x[i] in b) {} // ERR
		for (x[i] in a2) {} // ERR
		for (x[i] in b2) {} // ERR
		for (x[i,j] in a) {} // ERR
		for (x[i,j] in b) {} // ERR
		for (x[i,j] in a2) {} // ShouldNotBeERR
		for (x[i,j] in b2) {}
	}
	def testInconsistent(
		a1[i1,j1]:Point(1), // ERR
		a2[i2,j2]:Point(2),
		a3[i3,j3]:Point(3), // ERR
		a4[i4,j4]:Point{rank==2}, 
		a5[i5,j5]:Point{rank!=2}, // ERR
		a6[i6,j6]:Point{rank!=1}, // ok {rank!=1 && rank==2} is consistent
		a7[i7,j7]:Point{rank==a2.rank}, 
		a8[i8,j8]:Point{rank==a3.rank}, // ShouldBeErr
		a9[i9,j9]:Point,
		a10[i10,j10]:Point{rank==a9.rank},
		a11[i11,j11]:Point{rank!=a9.rank}, // ShouldBeErr
		a12:Point,
		a13[i13,j13]:Point{rank==a12.rank},
		a14[i14,j14]:Point{rank==a12.rank},
		a15[i15,j15]:Point{rank!=a12.rank}) // ShouldBeErr
		{}


    def f1([i,j]:Point(2),x:Long)= i+x+j;	
    def f2(p[i,j]:Point(2),x:Long)= p(0)+i+x+j;

    def f3(p[i,j]:Point(1))=1; // ERR 

	def test() {
		val [i,j] = [1,2]; // ShouldNotBeERR
		return i+j;
	}
	def test2() {
		val p[i,j] = [1,2]; // ShouldNotBeERR
		return p(0)+i+j;
	}
	def test3() {
		for(p[i,j] in Region.make(1..2, 3..4))
			return p(0)+i+j;
		for(p[i] in Region.make(3..4))
			return p(0)+i;
		for(p[i]:Point(2) in Region.make(3..4, 3..4))  {} // ERR XTENLANG-2399 (the type doesn't match the number of exploded ints)
		for(x[n]:Point in Region.make(3..4, 3..4))  {} // ERR (otherwise it causes the ForLoopOptimizer to crash.)
		for(x[n,m]:Point in Region.make(3..4, 3..4))  {}
		return 3;
	}
    def m(p:Point(1)) { // XTENLANG-2399
		val [i,j] = p; // ERR
		val q[m]:Point(2) = [3,4]; // ERR
	}
	def test4() {
		for(p[i,j] in Region.make(3..4)) // ERR: Loop domain is not of expected type.  
			return p(0)+i+j;
		return 3;
	}
	def test5() {
		var r:Region = null;
		for (p in r) {}
		@ERR for (p[i] in r) {} //  Loop domain is not of expected type.
	}
	def testWithoutInitVal() {
		var p[i,j,k]: Point; // ERR
	}
	def exactTest() {		
        { val [i,j] = [1,2,3]; } // ERR
        { val [i,j] = [1,2]; }  // ShouldNotBeERR
        { val [i,j] = [1 as long]; } // ERR
        { val [i,j]:Point = [1,2,3]; } // ShouldBeErr
        { val [i,j]:Point = [1,2]; } 
        { val [i,j]:Point = [1 as long]; } // ShouldBeErr
        { val [i,j]:Point(3) = [1,2,3]; } // ERR
        { val [i,j]:Point(2) = [1,2]; }
        { val [i,j]:Point(1) = [1 as long]; } // ERR
	}
	
	def checkVarPoint(var p[i,j] : Point(2)) {
		p = [3,4];
		i = 5; // ERR
	}
	def nonPointExploding(q[m]:Long) { // ERR
		val z = m+1; // exploded variables are always assumed to be Long.
	}
	def closureTest() {		
		val f1: (Point(2)) => Long  =   (p[i,j]:Point) => i+j;
		val f2: (Point) => Long  =   (p[i,j]:Point) => i+j; // ERR
	}
	def explodingLocal() {		
        { val p[i,j]: Array[Long] = new Array[Long](2); } // ERR ERR ERR: Semantic Error: You can exploded the Array only if its has the constraint {rank==1,size=2}
        { val p[i,j]: Array[Long]{rank==1, size==2} = new Array[Long](2); }
        { val p[i,j]: Array[Point] = new Array[Point](2); }  // ERR ERR ERR: Semantic Error: You can exploded the Array only if its has the constraint {rank==1,size=2}
        { val p[i,j]: Array[Point]{rank==1, size==2} = new Array[Point](2); } 
        { val p[i,j]: Array[Point]{rank==1, size==2} = new Array[Point](3); } // ERR
        { val p[i,j]: Array[Point]{rank==1, size==2} = new Array[Point](2); }
        { val p[i,j]: Array[Point]{rank==2, size==2} = new Array[Point](2); } // ERR ERR ERR ERR 
        { val p[i,j]: Array[Point]{rank==1, size==3} = new Array[Point](3); } // ERR 
        { val p[i,j]: Array[Point] = new Array[Point](Region.make(2..3, 4..5)); } // ERR ERR ERR 
	}
	def testArrayApplyMethod() {
        { 
			val p = new Array[Long](2); 
			val i = p(0);
		}
        { 
			val p:Array[Long]{self.rank==1} = new Array[Long](2); 
			val i = p(0);
		}
	}
	def testArrayLongCtor() {
        { val p: Array[Long] = new Array[Long](3); } 
        { val p: Array[Long]{rank==1} = new Array[Long](3); } 
        { val p: Array[Long]{rank==3} = new Array[Long](3); } // ERR 
        { val p: Array[Long]{size==3} = new Array[Long](3); } 
        { val p: Array[Long]{size==2} = new Array[Long](3); } // ERR 
        { val p: Array[Long]{region.rank==1} = new Array[Long](3); } 
        { val p: Array[Long]{region.rank==3} = new Array[Long](3); } // ERR 
        { val p: Array[Long]{region.rect} = new Array[Long](3); } 
        { val p: Array[Long]{region.rect==false} = new Array[Long](3); } // ERR 
        { val p: Array[Long]{region.rail} = new Array[Long](3); } 
        { val p: Array[Long]{region.rail==false} = new Array[Long](3); } // ERR 
        { val p: Array[Long]{rect} = new Array[Long](3); } 
        { val p: Array[Long]{rect==false} = new Array[Long](3); } // ERR 
        { val p: Array[Long]{rail} = new Array[Long](3); } 
        { val p: Array[Long]{rail==false} = new Array[Long](3); } // ERR 
	}

	// val p[i,j] = [1,2]; // doesn't parse for fields :)

}



class TestOverloadingAndInterface {
interface XXX {
	def m(Empty):Long;
}
interface YYY {
	def m(String):String;
}
class C3 implements XXX,YYY {
	public def m(Empty):Long = 1;
	public def m(String):String="";
	def test(c:C3) {
		val f1:XXX = c;
		val f2:YYY = c;
	}
}
class C implements (Any)=>Long {
	public operator this(Any):Long = 1;
	public operator this(String):String="";

	def test(c:C) {
		val x:Long = c(1);
		val y:String = c("");
		val f:(String)=>Long = c;
		val i:Long = f("str");
	}	
}
class D[T] implements (T)=>Long {
	public operator this(T):Long = 1;
	public operator this(String):String="";

	def test(c:D[Any]) {
		val x:Long = c(1);
		val y:String = c("");
		val f:(String)=>Long = c;
		val i:Long = f("str");
	}	
}
class C2 implements (Any)=>Long, (String)=>String {
	public operator this(Any):Long = 1;
	public operator this(String):String="";

	def test(c:C2) {
		val x:Long = c(1);
		val y:String = c("");
		val f1:(String)=>Long = c;
		val f2:(String)=>String = c; 
	}	
}
}

class TestPropertyAssignment(x:Long, y:Long{self==3}) {
    def this(a:Long, b:Long) {
		property(a,b); // ERR
    }
}

final class ConstraintsInClosures {
  def f(x:Long) {x!=0} = 1/x;
  def f2(x:Long{self!=0}) = 1/x;
  def test() {
	  val bar: (Long)=>Long = (x:Long)=>this.f(x);  // ERR ERR: should we dynamically generate a new closure that checks the guard?
	  val bar2: (Long)=>Long = (x:Long{self!=0})=>this.f2(x); // ERR
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
	val b:Long{d==3} = 3 as Long{d==3}; // ShouldBeErr: Cannot read from field 'b' before it is definitely assigned.
	val d:Long = 3;
	val c:Long{c==3} = 3 as Long{self==3};

	def test() {
		val a3:Long = a3*3; // ERR ERR: "a3" may not have been initialized
		val a:Long{a!=5} = 
			3 as Long{a!=5}; // ERR ERR ERR [Semantic Error: Could not find field or local variable "a"., Semantic Error: Local variable may not have been initialized     Local variable: a]
		val a2:Long{a2!=5} = 
			3 as Long{self!=5};
	}
	
	def use(x:Long) {}
	val x:Long;
	val w:TestCasts;
	def this(a:TestCasts) {
		use(3 as Long{a.x==self});
		use(3 as Long{a.w.w.x==self});
		use(3 as Long{x==self}); // ERR: Cannot read from field 'x' before it is definitely assigned.
		val y:Long;
		use(3 as Long{y==self}); // ERR: "y" may not have been initialized
		val b:TestCasts;
		use(3 as Long{b.x==self}); // ERR: "b" may not have been initialized
		use(3 as Long{b.w.w.x==self}); // ERR: "b" may not have been initialized
		use(3 as Long{b.w.x==b.x}); // ERR: "b" may not have been initialized

		val z:Long;
		async use(3 as Long{z==self}); // ERR: "z" may not have been initialized
		z=2;
		async use(3 as Long{z==self});

		beforeX(); // ERR: Cannot read from field 'x' before it is definitely assigned.
		async use(3 as Long{x==self}); // ERR: Cannot read from field 'x' before it is definitely assigned.
		x=2;
		async use(3 as Long{x==self});
		afterX();

		use(3 as Long{w.x==self}); // ERR: Cannot read from field 'w' before it is definitely assigned.
		w=null;
		use(3 as Long{w.x==self});
	}
	def beforeX() {
		use(3 as Long{x==self});
	}
	def afterX() {
		use(3 as Long{x==self});
	}
	def foo(a:TestCasts) {
		use(3 as Long{a.x==self});
		use(3 as Long{a.w.w.x==self});
		use(3 as Long{x==self});
		val y:Long;
		use(3 as Long{y==self}); // ERR: "y" may not have been initialized
		val b:TestCasts;
		use(3 as Long{b.x==self}); // ERR: "b" may not have been initialized
		use(3 as Long{b.w.w.x==self}); // ERR: "b" may not have been initialized
		use(3 as Long{b.w.x==b.x}); // ERR: "b" may not have been initialized
	}

	class Inner {
		val l:Inner;
		def this(i:TestCasts) {
			use(3 as Long{x==self});			
			use(3 as Long{w.x==self});
			use(3 as Long{i.x==self});			
			use(3 as Long{i.w.x==self});
			use(3 as Long{l==null});	// ERR: Cannot read from field 'l' before it is definitely assigned.
			l=null;
			use(3 as Long{l==null});
		}
		def foo() {
			use(3 as Long{l==null});
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
		var i:Long = 1;
		while (true) {
			var i:Long = 1; // ERR: Local variable "i" multiply defined. Previous definition at ...
		}
	}
	def test2() {
		val i:Long = 1;
		at (here) {
			val i:Long = 1; // ERR, Local variable "i" multiply defined. Previous definition at ...
		}
	}
	def test3() {
		val i:Long = 1;
		val c = () => {
			val i:Long = 1; // ok
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
		useInt(0x80000000n);
		useInt(0x7fffffffn);
		useInt(0xffffffffn); // is negative
		useLong(0x8000000000000000L);
		useLong(0x7fffffffffffffffL);
		useLong(0xffffffffffffffffL);// is negative
		useInt(0x800000000n); // ERR: Integer literal 34359738368 is out of range. (todo: better err message)
		useLong(0x80000000000000000L); // ShouldBeErr (currently in generates 0L !)
		// in decimal
		useInt(-2147483648n);
		useInt(2147483647n);
		useLong(-9223372036854775808l);
		useLong(9223372036854775807l);		
		useInt(-2147483649n); // ERR ERR: Integer literal -2147483649 is out of range. (todo: better err message)
		useInt(2147483648n); // ShouldBeErr
		useLong(-9223372036854775809l); // ShouldBeErr
		useLong(9223372036854775808l); // ShouldBeErr

		// in octal
		useInt(020000000000n);
		useInt(017777777777n);
		useLong(01000000000000000000000L);
		useLong(0777777777777777777777L);
		useInt(0200000000000n); // ERR: Integer literal 17179869184 is out of range. (todo: better err message)
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
		useUInt(0x0un);
		useUInt(0xffffffffun);
		useULong(0x00ul);
		useULong(0xffffffffffffffffLu);
		// in decimal
		useUInt(0UN);
		useUInt(4294967295UN);		
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

class XTENLANG_2054 {
	val x = ULong.MAX_VALUE; //XTENLANG-2054
}
class XTENLANG_2070 {
	var i:Long=0;
	var j1:Long = ++(i); // ShouldNotBeERR
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

	var b:Byte=0y;
	var i:Int=0n;
	var l:Long=0l;
	var s:Short=0s;

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
		b = i; // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenJavaNumerics.this.i		 Expected type: x10.lang.Byte		 Found type: x10.lang.Long)
		b = l; // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenJavaNumerics.this.l		 Expected type: x10.lang.Byte		 Found type: x10.lang.Long)
		b = s; // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenJavaNumerics.this.s		 Expected type: x10.lang.Byte		 Found type: x10.lang.Short)
		i = f; // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenJavaNumerics.this.f		 Expected type: x10.lang.Long		 Found type: x10.lang.Float)
		i = d; // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenJavaNumerics.this.d		 Expected type: x10.lang.Long		 Found type: x10.lang.Double)
		i = b;
		i = l; // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenJavaNumerics.this.l		 Expected type: x10.lang.Long		 Found type: x10.lang.Long)
		i = s;
		l = f; // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenJavaNumerics.this.f		 Expected type: x10.lang.Long		 Found type: x10.lang.Float)
		l = d; // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenJavaNumerics.this.d		 Expected type: x10.lang.Long		 Found type: x10.lang.Double)
		l = b;
		l = i;
		l = s;
		s = f; // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenJavaNumerics.this.f		 Expected type: x10.lang.Short		 Found type: x10.lang.Float)
		s = d; // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenJavaNumerics.this.d		 Expected type: x10.lang.Short		 Found type: x10.lang.Double)
		s = b;
		s = i; // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenJavaNumerics.this.i		 Expected type: x10.lang.Short		 Found type: x10.lang.Long)
		s = l; // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenJavaNumerics.this.l		 Expected type: x10.lang.Short		 Found type: x10.lang.Long)
	}
}
class LegalCoercionsBetweenAllNumerics {	
	var f:Float=0;
	var d:Double=0;

	var b:Byte=0y;
	var i:Int=0n; 
	var l:Long=0l;
	var s:Short=0s;

	var ub:UByte=0uy;
	var ui:UInt=0un;
	var ul:ULong=0ul;
	var us:UShort=0us;

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
		i = ub;  
		i = ui;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.ul		 Expected type: x10.lang.Int		 Found type: x10.lang.UInt)
		i = ul;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.ul		 Expected type: x10.lang.Int		 Found type: x10.lang.ULong)
		i = us;  
		l = f;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.f		 Expected type: x10.lang.Long		 Found type: x10.lang.Float)
		l = d;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.d		 Expected type: x10.lang.Long		 Found type: x10.lang.Double)
		l = b;
		l = i;
		l = s;
		l = ub;  
		l = ui;  
		l = ul;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.us		 Expected type: x10.lang.Long		 Found type: x10.lang.ULong)
		l = us;  
		s = f;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.f		 Expected type: x10.lang.Short		 Found type: x10.lang.Float)
		s = d;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.d		 Expected type: x10.lang.Short		 Found type: x10.lang.Double)
		s = b;
		s = i;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.i		 Expected type: x10.lang.Short		 Found type: x10.lang.Int)
		s = l;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.l		 Expected type: x10.lang.Short		 Found type: x10.lang.Long)
		s = ub;  
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
		operator this*(that:Long):Any = 1;
		operator this*(that:Long):Any = 1; //  ERR (Semantic Error: Duplicate method "method OperatorTestCases.InstanceBinary1.operator*(that:x10.lang.Long): x10.lang.Any"; previous declaration at C:\cygwin\home\Yoav\test\Hello.x10:114,9-41.) 

		operator this-(that:Long):Any = 1;
		operator (that:Long)-this:Any = 1;

		operator this+(that:InstanceBinary1):Any = 1; 
		operator (that:InstanceBinary1)+this:Any = 1; // ShouldBeErr 
	}
	static class InstanceAndStatic {
		operator this*(that:Long):Any = 1;
		static operator (x:InstanceAndStatic)*(that:Long):Any = 1; // ShouldBeErr

		operator (that:Long)+this:Any = 1;
		static operator (x:Long)+(that:InstanceAndStatic):Any = 1; // ShouldBeErr
	}

	// test inheritance
	static class Parent {
		operator this*(that:Long):Any = 1;
		operator this+(that:Long):Any = 1;
		static operator (x:Parent)+(that:Long):Any = 1;
	}
	static class Child extends Parent {		
		operator this*(that:Long):Any = 1; // overriding
		operator (that:Long)+this:Any = 1; // ShouldBeErr
		static operator (x:Parent)+(that:Long):Any = 1; // hiding
	}

	// test inheritance, static, instance
	static class BinaryAndUnary { // ShouldNotBeERR
		operator this+(that:Long):Any = 1; // ShouldNotBeERR (Semantic Error: operator+(that: x10.lang.Long): x10.lang.Any in OperatorTestCases.BinaryAndUnary cannot override operator+(that: x10.lang.Long): x10.lang.Any in OperatorTestCases.BinaryAndUnary; overridden method is static)
		static operator +(that:Long):Any = 1; // ShouldNotBeERR (Semantic Error: operator+(that: x10.lang.Long): x10.lang.Any in OperatorTestCases.BinaryAndUnary cannot override operator+(that: x10.lang.Long): x10.lang.Any in OperatorTestCases.BinaryAndUnary; overridden method is notstatic) // ERR (Semantic Error: Duplicate method "method static OperatorTestCases.BinaryAndUnary.operator+(that:x10.lang.Long): x10.lang.Any"; previous declaration at C:\cygwin\home\Yoav\test\Hello.x10:39,9-41.)
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
			val x1:Long{self==1} = a+a; // resolves to A::op+(A,A) and dynamically dispatches on the first argument (so it might execute C::op+(C,A) at runtime)
			val x2:Long{self==1} = a+b; // resolves to A::op+(A,A) and dynamically dispatches on the first argument (so it might execute C::op+(C,A) at runtime)
			val x3 = a+c; // ERR: Semantic Error: Ambiguous operator because it matches more than one operator definition.  Matches both A::op+(A,C) and A::this+A
			val x4 = b+a; // // ERR: Semantic Error: Ambiguous operator because it matches more than one operator definition.  Matches both A::this+A and A::B+this
			val x5:Long{self==2} = b+b; // resolves to B::op+(B,B) and dynamically dispatches on the second argument
			val x6:Long{self==2} = b+c; // ShouldBeErr:  should resolve to Example::op+(B,C) so it does a static call (so the return type should be 4!)
			val x7:Long{self==1} = c+a; // resolves to C::op+(C,A) and dynamically dispatches on the first argument
			val x8:Long{self==1} = c+b; // ShouldBeErr: Ambiguity! C::op+(C,A) or B::op+(B,B) ?
			val x9:Long{self==1} = c+c;  // ShouldBeErr: Ambiguity! C::op+(C,A) or Example::op+(B,C) ?
		}
	}
	static  class Main {
	  public static def main(args: Rail[String]) {
		  new Example().example( new A(), new B(), new C() );
		  Console.OUT.println("Now let's test dynamic dispatching!"); 
		  new Example().example( new C(), new C(), new C() );
	  }
	}
}


class TestStructEqualsClass {
	def this(Long) {}
	def this(Any) {}
}

class SubtypeConstraints {
class A[T] { T isref } {
	var a1:A[T];
	var a2:A[Long]; // ERR: Semantic Error: Type A[x10.lang.Long] is inconsistent.
	var a4:A[Long{self==3}]; // ERR
}
class Test[T]  {
	def m1(GlobalRef[T]{self.home==here}) {} // ERR: Semantic Error: Type is inconsistent.
	def m2(GlobalRef[T]{self.home==here}) {T isref} {}  
}
static class TestStatic[T]{T isref} {
	public static def m1[T]():TestStatic[T]  = null; // ERR
	public static def m2[T]() {T isref} :TestStatic[T] = null;
    public static type TestStatic[T](p:Long) {T isref} = TestStatic[T]{1==p};
}

}
class TransitivityOfEquality {
// check for transitivity of equality
class Tran[X,Y,W,U,Z] {X==Z, Z==Y,Long==Y, W==Z} {
	var a0:Tran[X,Y,W,U,Z];
	var a9:Tran[Long,Long,Long,Long,Long];
	var a1:Tran[Y,W,W,U,Z];
	var a2:Tran[Y,W,Long,U,Long];
	var a3:Tran[Y,Long,W,U,Z];
	var a4:Tran[Z,W,Z,U,Long];
	
	var e1:Tran[U,Y,W,U,Z]; // ERR: Type Tran[U, Y, W, U, Z] is inconsistent.
}

class TestTransitivityInGuards[A,B,C,D, E] {
	def m1() {A==B, B==C, C==D} {
		m2(); // ShouldNotBeERR
		m3(); // ERR
	} 
	def m2() {A==D} {}
	def m3() {A==E} {}
}
}

class XTENLANG_2397[K,V] { K <: Comparable[K] } {
	def this(start:K, map:XTENLANG_2397[K,V]) {}
	def this(map:XTENLANG_2397[K,V], end:K) {}
}

class HaszeroConstraints {
class M[T] {
	def q() { T haszero } {}
}
class A[T] { T haszero } {}
class B[U] {
	def f1() { U == Long } {
		var x:A[U] = null;
	}
	def f2() { U == Any } {
		var x:A[U] = null;
	}
	def f3() { U == Any{self!=null} } {
		var x:A[U] = null; // ERR
	}
}

class C[V] { V == Long } {
	def f1() { 
		var x:A[V] = null;
	}
}
class D[V] { V == Long{self!=0} } {
	def f1() { 
		var x:A[V] = null; // ERR
	}
}

class Test2[W] { W haszero } {	
	var a1:A[W];
	class Inner { // ShouldNotBeERR
		var i1:A[W]; // ShouldNotBeERR
	} 
    def test1() {
        var x:A[W];
    }
    def test2() { W haszero } {
        var x:A[W];
    }
}
class Test[W](p:Long) {
	def test() {
		m1.q(); // ERR
		m2.q();
	}
	var m1:M[Long{self!=0}];
	var m2:M[Long];

	var a0:A[W]; // ERR
    def test2() {
        var x:A[W]; // ERR
    }
    def test3() { W haszero } {
        var x:A[W];
    }
	var a1:A[Long];
	var a2:A[Long{self!=3}];
	var a3:A[Long{self==0}];
	var a4:A[Long{self==3}]; // ERR
	var a5:A[Long{self!=0}]; // ERR
	var a6:A[Long{self!=p}]; // ERR
	var a7:A[Long{self==p}]; // ERR
}
} // end HaszeroConstraints



class XTENLANG_967  {
    def test() {        
        class C[T] {
			val f1 = (){T isref} => "hi"; // ERR: Type constraints not permitted in closure guards.
			def f2(){T isref} = "hi";
		}
        val res1 =  new C[Long]().f1();
        val res2 =  new C[Long]().f2(); // ERR: Type guard {} cannot be established; inconsistent in calling context.
    }	
}
class XTENLANG_1574(v:Long) {v==1} {
	static def m(a:XTENLANG_1574) {
		val b:XTENLANG_1574{self.v==1} = a; 
		@ERR @ERR val b2:XTENLANG_1574{self.v==2} = a; // [Semantic Error: Cannot assign expression to target.	 Expression: a	 Expected type: x10.frontend.tests.XTENLANG_1574{self.v==2}	 Found type: x10.frontend.tests.XTENLANG_1574{self==a}, Semantic Error: Invalid type; the real clause of x10.frontend.tests.XTENLANG_1574{self.v==2} is inconsistent.]
	}
}
class TestMethodGuards[T](a:Long, p:Place) {
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
class ProblemsWithFieldsInConstraints {	// these errors are both with STATIC_CHECKS and with DYNAMIC_CHECKS
	val f1:Long;
	val f2:Long{self==f1};
	def this() {
		f2 = 2; // ERR
		f1 = 2;
	}
	def test() {
		val local1:Long;
		val local2:Long{self==local1};
		local2 = 1; // ERR
		local1 = 1;
	}
}
class InconsistentPropertyVsField(p:Long) {
	val f:Long = 2;
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

class FieldInInvariant1 {a==1} { 
	val a:Long;
	def this() { a=2; } // ERR
}
class FieldInInvariant2 {this.a==1} {  // ERR ERR
	val a:Long = 2;
}
class FieldInInvariant3 {self.a==1} { // ERR ERR ERR: Semantic Error: self may only be used within a dependent type
	val a:Long = 1;
}
class XTENLANG_688(a:Long) {
	val f1:Long{self==a} = a;
	val f2:Long{self==f1} = a;
}
class XTENLANG_688_2(a:Long) { // fine even with DYNAMIC_CHECKS (cause we do not generate a cast)
	val f2:Long{self==f1} = a;
	val f1:Long{self==a} = a;
}


class IllegalForwardRef {
    val f:IllegalForwardRef{this.f==null} = null;  // ERR (we need to determine the type of "f" when creating the constraint, so we can't refer to "f")
}
class LegalForwardRef { 
    val f1:LegalForwardRef{this.f2==self} = null; 
    val f2:LegalForwardRef = null; 

    val f3:LegalForwardRef{this.f4==this.f3} = f4; // ERR ERR: Cannot read from field 'f4' before it is definitely assigned.
    val f4:LegalForwardRef = null; 
}
class LegalForwardRef2 { 
	val x:Long{self==y} = 1; // legal forward reference to y (we don't really use it's value)
	val y:Long{self==1} = 1;
}
class IllegalForwardRef2 { 
	val x:Long{self==y} = 1; // ERR with STATIC_CHECKS (The type of the field initializer is not a subtype of the field type.) with DYNAMIC_CHECKS (Cannot read from field 'y' before it is definitely assigned.)
	val y:Long{self==2} = 2;
}
class XTENLANG_686_2(a:Long) {
	def this() : XTENLANG_686_2{1==this.a}{property(1);} // ok to use this on the return type
	def this(a:Long{self==this.a}) {property(a);} // ERR: Semantic Error: This or super cannot be used (implicitly or explicitly) in a constructor formal type.	 Formals: [val a: x10.lang.Long{self==FordesemiFoo#this.a}]
}
class XTENLANG_686(a:Long) {
	val f1:Long{self==a} = a;
	val f2:Long{self==f1} = a;

	val f3:XTENLANG_686{self.a==this.f4} = null; // ok
	val f4:Long = 2;

	val f5:Long{self==3};


	def this(b:XTENLANG_686{self.a==1}) {
		val q1: XTENLANG_686{self.a==this.a} = null; // ok
		val q2: XTENLANG_686{self.a==this.f1} = null; // ok
		property(1);
		// we put field initializers here
		val q3: XTENLANG_686{self.a==this.a} = null; // ok
		val q4: XTENLANG_686{self.a==this.f1} = null; // ok
		val q5: XTENLANG_686{self.a==this.f5} = null; // ok
		
		val i1:Long{self==f5} = 3; // ok
		val i2:Long{self==f5} = 4; // ERR in both STATIC_CHECKS (Cannot assign expression to target.) and DYNAMIC_CHECKS (Cannot read from field 'f5' before it is definitely assigned.)

		val i3:Long{3==f5} = 3; // ok
		f5 = 3;
		val i4:Long{3==f5} = 4; // ok
	}
}

class CastToTypeParam[T] { 
	val f1:T = 0 as T; // ERR: This is an unsound cast because X10 currently does not perform constraint solving at runtime for generic parameters.
	val f2:T = 1 as T; // ERR: This is an unsound cast because X10 currently does not perform constraint solving at runtime for generic parameters.
	def test(a:CastToTypeParam[String]) {
		val f:String = a.f1;
		val s = 0 as String; // ERR: Cannot cast expression to type
	}
	public static def main(Rail[String]) {
		Console.OUT.println(new CastToTypeParam[String]().f1); // throws x10.lang.ClassCastException
	}
}

class XTENLANG_685(a : Long, b : Long{this.a == 1}) {
	def this() {
		property(1,1);
	}  
	def this(Boolean) {
		property(1,2);
	}  
	def this(String):XTENLANG_685{self.a == 1} {
		property(2,1); // ERR
	}  
	def this(Float):XTENLANG_685{this.a == 1} {
		property(2,1); // ERR
	}  
	def this(Double) {// ShouldNotBeERR (Semantic Error: Invalid type; the real clause of XTENLANG_685{self.a==2, self.b==1} is inconsistent.)
		property(2,1); // ERR
	}  
}

//class Tree(left:Tree, right:Tree{this.left==self.left}) {} // ShouldNotBeERR, see XTENLANG-2117
class NonStaticTypedef(p:Long) { 
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
		var x1:haszeroExamples2[Long{self!=0}]; // ok
		var x2:haszeroExamples0[Long{self!=0}]; // ERR 
		val x3 = 
			new haszeroExamples2[Long{self!=0}](5); // ok
		val x4 = 
			new haszeroExamples2[Long{self!=0}]();  // ERR
		val x5 =  new haszeroExamples2[Long]();
		def test() {
			x3.m0();
			x3.m1(); // ERR 
			x5.m0();
			x5.m1(); 
		}
	}

}
class RuntimeTestsOfHaszero {
	public static def main(Rail[String]) {
		new RuntimeTestsOfHaszero().m();
	}

	var i:Long;
	var k:Long{self!=3};
	var l:Long;
	var s:String;
	val a1 = new A[String]();
	val a2 = new A[Long]();
	val a3 = new A[Long]();

	def m() {
		assert(1==++i);
		assert(0==k);
		assert(0L==l++);
		assert(null==a1.t);
		assert(1==++a2.t);
		assert(1L==(++a3.t));
		assert(4==foo(Zero.get[Double]()));
	}

	def foo(Long)=3;
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
    static def m() = 1; // ShouldNotBeERR: Semantic Error: m(): x10.lang.Long{self==1} in StaticOverriding.D cannot override m(): x10.lang.Long{self==0} in StaticOverriding.C; attempting to use incompatible return type.
  }
}

class TreeUsingFieldNotProperty { this.left==null } { // ERR
  val left:TreeUsingFieldNotProperty = null;
}
class XTENLANG_1149 {
    def m(b:Boolean, x:Any{self!=null}, y:Any{self!=null}):Any{self!=null} {
        val z:Any{self!=null} = b ? x : y;
        @ERR val z2:Any{self==null} = b ? x : y;
        return z;
    }
}
class XTENLANG_1149_2 {
	class B {}
	var f:Boolean;
	def test() {
		val b1 = new B();
		val b2 = new B();
		@ERR val c0:B{self==null} = f ? b1 : b2;
		val c1:B{self!=null} = f ? b1 : b2;
		val c2:B = f ? b1 : b2;
		val c3:B{self!=null} = f ? (b1 as B{self!=null}) : b2;
		val c4:B{self!=null} = f ? (b1 as B{self!=null}) : (b2 as B{self!=null});
		val c5:B{self!=null} = f ? b1 : b1;
		@ERR val c6:B{self==null} = f ? b1 : b1;

		val arr1 = [b1 as B{self!=null},b2 as B{self!=null}];
		  // the type inferred for [b1,b2] is 
        // x10.regionarray.Array[XTENLANG_1149_2.B{XTENLANG_1149_2.self==XTENLANG_1149_2#this, self!=null}]
        @ERR val arr2:Array[B{self!=null}] = [b1,b2]; 
		@ERR val arr3:Array[B] = [b1,b2]; 
	}
}

class TestClassInvariant78 {
class AA(v:Long) {v==1} {} // ShouldBeErr
class BB(v:Long) {v==1} {
	def this(q:Long) { property(q); } // ERR
}
static class A(v:Long) {v==1} {
	static def m(a:A) {
		val b:A{self.v==1} = a;
		@ERR @ERR val b2:A{self.v==2} = a;
	}
	def m2(a:A) {
		val b1:A{self.v==1} = this;
		val b2:A{this.v==1} = this;
		val b3:A{self.v==1} = a;
		@ERR @ERR val b33:A{self.v==2} = a; 
		val b4:A{this.v==1} = a;
	}
}
}

class TestDuplicateClass { // XTENLANG-2132
	class A(v:Long) {} 
	// static class A(v:Long) {}  // ShouldBeErr (causes a crash: AssertionError: TestDuplicateClass.A->TestDuplicateClass.A x10.types.X10ParsedClassType_c is already in the cache; cannot replace with TestDuplicateClass.A x10.types.X10ParsedClassType_c)
}

class TestSerialization {
class TestAt {
	var i:Long{self!=0};
	def this() { 
		at (Place.places().next(here)) 
			i=2; // ERR: 'this' or 'super' cannot escape via an 'at' statement during construction.
	}
}
class TestSerialize {
	var i:Long{self!=0};
	def this() {
		at (Place.places().next(here)) 
			this.set(3); // ERR: 'this' or 'super' cannot escape via an 'at' statement during construction.
	    this.set(2);
	}
	private def set(x:Long{self!=0}) { i=x; }
}

class ClosureAndSerialize {
    val x = 2;    
	val BigD = Dist.makeBlock(Region.make(0..10, 0..10), 0);
    val A = DistArray.make[Double](BigD,(p:Point)=>
		1.0*this.x); // ERR: 'this' or 'super' cannot escape via a closure during construction.
    val k:Long{self!=0} = 3;
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

class XTENLANG_2142 implements CustomSerialization { // ShouldBeErr: missing ctor "def this(Deserializer)"
	public def serialize(Serializer) {} 
}


class TestComparableAndArithmetic {
  def compare[T](x:T,y:T) { T <: Comparable[T] } = x.compareTo(y);
  def add[T](x:T,y:T) { T <: Arithmetic[T] } = x+y;
  def test() {
	  {
		  var x:Long=2;
		  var y:Long=3;
		  use(compare(x,y));
		  use(compare(x,x));
		  use(compare(y,y));
		  use(add(x,y));
		  use(add(x,x));
		  use(add(y,y));
	  }
	  {
		  var x:Double=2.0;
		  var y:Double=3.0;
		  use(compare(x,y));
		  use(compare(x,x));
		  use(compare(y,y));
		  use(add(x,y));
		  use(add(x,x));
		  use(add(y,y));
	  }
  }
  def use[T](i:T) {}
}

interface Ann42 //extends MethodAnnotation, ClassAnnotation, FieldAnnotation, ImportAnnotation, PackageAnnotation, TypeAnnotation, ExpressionAnnotation, StatementAnnotation 
	{ }
@ERR @Ann42 class TestAnnotationChecker( // Annotations on class declarations must implement x10.lang.annotations.ClassAnnotation
	@ERR @Ann42 p:Long) { // Annotations on field declarations must implement x10.lang.annotations.FieldAnnotation
	@ERR def use(@Ann42 x:Any)=x;  // Annotations must implement x10.lang.annotations.Annotation
	@ERR @Ann42 def m0() {} // Annotations on method declarations must implement x10.lang.annotations.MethodAnnotation
	def test() {
		use(@ERR @Ann42 "A"); // Annotations on expressions must implement x10.lang.annotations.ExpressionAnnotation
		@ERR @Ann42 {} // Annotations on statements must implement x10.lang.annotations.StatementAnnotation
	}
	var i:Long @ERR @Ann42; // Annotations on types must implement x10.lang.annotations.TypeAnnotation
	@ERR @Ann42 var j:Long; // Annotations on field declarations must implement x10.lang.annotations.FieldAnnotation

	def m11() {
		@ERR @Ann42 {}
	}
	def m1() {
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
		var y:Long = @ERR @Ann42 5;
		var x:Long = @ERR @Ann42 + 5;
		@ERR @Ann42 val z = 
			@ERR @Ann42 + 5;
		use(@ERR @Ann42 ++x);
		use(@ERR @Ann42 4+5);
		use(@ERR @Ann42 5);
		use(@ERR @Ann42 use(5));
		//@Ann42 use(5); // todo: it should parse!
	}
	@ERR @Ann42 def m2() = @ERR @Ann42 4;
	@ERR @Ann42 def m3() @ERR @Ann42 { return 5; };
}

class SubtypeCheckForUserDefinedConversion { // see also SubtypeCheckForUserDefinedConversion_MustFailCompile
	static class Foo {}
	static class A {
		// implicit_as
		@ERR public static operator (p:Int):Foo = null;
		public static operator (p:Long):A = null;
		@ERR public static operator (x:A):String = null;

		// explicit_as
		@ERR public static operator (x:Double) as Foo = null;
		public static operator (x:Float) as A = null;

		// far todo: trying 2+ or 0 formals causes a syntax error, and I think it should be a semantic error
		// public static operator (p:Long, x:Long) as A = null;
		// public static operator () as A = null;
	}
	static struct St {
		// implicit_as
		@ERR public static operator (p:Int):Foo = null;
		public static operator (p:Long):St = St();
		@ERR public static operator (x:St):String = null;

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
	    public static operator[T](x:Long):B[A] = null;
	    @ERR public static operator[T](x:T):T = x;
	    @ERR public static operator[T](x:String):C[T] = null;
		
		// explicit_as
		@ERR public static operator (x:Double) as Foo = null;
		public static operator[T] (x:Float) as B[T] = null;
		@ERR public static operator[T] (x:String) as B = null; // Type is missing parameters.
	}

	// what happens if we have two possible implicit/explicit coercions?
	// We give priority to coercions found in the target type (over the single one that can be found in the source type).
	static class Y(j:Long) {
		@ERR public static operator (p:Y):X{i==2} = null;
		@ERR public static operator (p:Y) as ? :X{i==3} = null;
	}
	static class X(i:Long) {
		public static operator (p:Y):X{i==1} = null;
		public static operator (p:Y) as ? :X{i==4} = null;
	}
	static class Z(i:Long) {
		public static operator (p:Long) as Z{i==4} = null; // see XTENLANG-2202
	}
	static class W(i:Long) {
		public static operator (p:Long) as ? :W{i==4} = null;
	}
	static class TestAmbiguity {
	    // there are two ways to implicitly convert Y to X (to either X{i==1} or X{i==2}), but we first search in X.
		def test1(y:Y):X{i==1} = y;
		@ERR def test2(y:Y):X{i==2} = y;
		@ERR def test3(y:Y):X{i==3} = y;
		@ERR def test4(y:Y):X{i==4} = y;

		// there are 2 additional ways to explicitly convert Y to X (we first search in X)
		@ERR def test5(y:Y):X{i==1} = y as X;
		@ERR def test6(y:Y):X{i==2} = y as X;
		@ERR def test7(y:Y):X{i==3} = y as X;
		def test8(y:Y):X{i==4} = y as X;
		@ERR def test9(y:Y):X{i==5} = y as X;
	}
}
class XTENLANG_2202 {
	static class Z(i:Long) {
		public static operator (p:Double) as Z{i==4} = new Z(4);
		public static operator (p:Long) as Z{i==4} = new Z(3); // ERR [Cannot return expression of given type.]
		public static operator (p:String) as Z{i==4} {p!=null} = new Z(4);
		public static operator (p:Float) as Z{i==4} {i==4} = new Z(4); // ERR ERR [Cannot access a non-static field field final property public Z.i: x10.lang.Long from a static context.	Cannot build constraint from expression |i == 4|.]
		public static operator (p:Byte) as Z{} {p==2y} = new Z(4);
		static def test() {
			val x:Z{i==4} = "asd" as Z;
			val y:Z{i==4} = 3.1 as Z;
			val x3:Z{i==3} = "asd" as Z; // ERR [Cannot assign expression to target.]
			val y3:Z{i==3} = 3.1 as Z; // ERR [Cannot assign expression to target.]
			val w1:Z = 2y as Z;
			val w2:Z = 3y as Z; // ERR
		}
	}
}

class Void_Is_Not_A_Type_Tests { // see also XTENLANG-2220
	static class B[T] {}
	val i:Long=1;
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
			def m(i:Long):void;
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

	interface A {}
	interface A2 extends A {}
	interface B[T] {
		def a():B[T];
		def a2():B[T];
		def b():Any;
		def c():A2;
		def c2():A;
		def c3():A;
		def d():Any;
		def e():void;
		def f():void;
	}
	interface C[T] {
		def a():C[T];
		def a2():C[T];
		def b():String;
		def c():A;
		def c2():A2;
		def c3():Any;
		def d():void;
		def e():A;
		def f():void;

	}
	// see XTENLANG-2332
	@ERR @ERR @ERR @ERR interface D extends B[D], C[A] { // a2, c3, d, e
		def a():D;
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
			def m():Long; // gives java-backend errors: XTENLANG-2221
		}
		static interface C extends A,B {}
		static class D implements C {
			public def m():Long = 2;
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
	static struct Z(u:Z) {} // ERR ERR: A class can only have properties of a 'simpler' type
	static struct W {
		@ERR val u:W; 
		def this(u:W) { this.u = u; }
	}
	
	static struct Cycle1(u:Cycle2) {} // ERR ERR: A class can only have properties of a 'simpler' type
	static struct Cycle2(u:Cycle1) {} // ERR ERR: A class can only have properties of a 'simpler' type

	// see XTENLANG-2144 that was closed
	//TestStructStaticConstant
    static struct S {
        static val ONE = S();
    }
	static struct U(u:U) {} // ERR ERR: A class can only have properties of a 'simpler' type


    public static def main(Rail[String]) {
        val x1 = new Array[S](2);
    }
}
class CircularityTestsWithInheritanceInterfacesAndStructs { // see XTENLANG-2187
	static struct Z(u:Z) {} // ERR ERR: A class can only have properties of a 'simpler' type
	static struct Z2 {
		static val z2:Z2 = Z2();
	}

	static struct Complex(i:Long,j:Long) {}

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
	static struct IgnoreGeneric[T] {} // the struct size doesn't not depend on T, therefore you can ignore whatever T we pass.
	@ERR static struct GenStructUsage44(x:IgnoreGeneric[IgnoreGeneric[GenStructUsage44]]) {} // far todo: even though the generic is "ignored" (there is no field of that type), I still report an error. We could do another analysis that checks which type parameters are actually used by fields and only handle such type parameters.
	
	static class IgnoreGeneric_class[T] {}
	static struct GenStructUsage44b(x:IgnoreGeneric_class[IgnoreGeneric[GenStructUsage44b]]) {} 
	

	static struct GenStructUsage5 {
		val x:GenStructUsage2[GenStructUsage2[GenStructUsage2[Generic[Long]]]];
		def this(x:GenStructUsage2[GenStructUsage2[GenStructUsage2[Generic[Long]]]]) { this.x = x; }
	}

	
	static struct Box[T] {}
	@ERR static struct GA[T](a:Box[GB[T]]) {}
	@ERR static struct GB[T](a:Box[GA[T]]) {}

	static struct Infinite[T] {
		@ERR val t:Infinite[Infinite[T]];
		def this(t:Infinite[Infinite[T]]) {
			this.t = t;
		}
	}
	static struct Infinite2[T](t:Infinite2[T]) {} // ERR ERR: A class can only have properties of a 'simpler' type
	static struct GenStructUsage6 {
		val x:Infinite[Long];
		def this(x:Infinite[Long]) { this.x = x; }
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
	@ERR @ERR @ERR @ERR class R1 {i()==3} {}
	@ERR @ERR @ERR @ERR class R2 {i()==3} extends R2 {} // [Semantic Error: Circular inheritance involving x10.frontend.tests.CircularityTestsWithInheritanceInterfacesAndStructs.R2, Semantic Error: Class invariant is inconsistent.]
	class R3 {}
	@ERR @ERR @ERR class R4 extends R3 {i()==3} {} // [Semantic Error: Invalid type; the real clause of x10.frontend.tests.CircularityTestsWithInheritanceInterfacesAndStructs.R3{inconsistent} is inconsistent., Semantic Error: Type x10.frontend.tests.CircularityTestsWithInheritanceInterfacesAndStructs.R3{inconsistent} is inconsistent.]
	
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
	@ERR interface I5 extends I5{self.i()==1} {
        public property i():Long;
	}
	interface I6 {
        public property i():I6;
	}

	interface Comparable[T] {
        public property i():T;
	}
	class Foo implements Comparable[Foo] {
        public property i():Foo = null;
    }
	class Foo2 implements Comparable[Foo2] {
        @ERR public property i():Comparable[Foo2] = null;
	}
}

class ConformanceChecks { // see XTENLANG-989
	interface A {
	   def m(i:Long){i==3}:void;
	}
	class IntDataPoint implements A { // ERR
	   @ERR public def m(i:Long){i==4}:void {}
	}
}

class TestThisAndInheritanceInConstraints {
	class A {
		def m(b1:A, var b2:A{A.this==b1}) {}
	}
	class B extends A {
		def m(b1:A, var b2:A{B.this==b1}) {}
	}
	class C extends A { // ERR
		def m(b1:A, var b2:A{C.this==b2}) {}
	}
}

class TestConstraintsAndProperties(i:Long, j:Long) {
	def test1(var x1:TestConstraintsAndProperties{self.i==1}, var x2:TestConstraintsAndProperties{self.i==2}) {
		x1 = @ERR x2;
	}
	def test2(var x1:TestConstraintsAndProperties{this.i==1}, var x2:TestConstraintsAndProperties{this.i==2}) {
		x1 = @ERR x2;
	}
	def test3(var x1:TestConstraintsAndProperties{this.i==1}, var x2:TestConstraintsAndProperties{this.j==2}) {
		x1 = @ERR x2;
	}
}

class TestFieldsInConstraints { // see XTENLANG-989
	class A {
	 public val i:Long = 2;
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
		  val Ak:Long{self==2} = B.super.k;
		  val Bk:Long{self==3} = B.this.k;
		  val Ck:Long{self==4} = C.this.k;
		  
		  @ERR val Ak2:Long{self==5} = B.super.k; // todo: Found type: x10.lang.Long{self==2, B#this.k==2}  ???
		  @ERR val Bk2:Long{self==5} = B.this.k; // ok: Found type: x10.lang.Long{self==3, B#this.k==3}
		  @ERR val Ck2:Long{self==5} = C.this.k; // ok: Found type: x10.lang.Long{self==4, B.C#this.k==4}
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
	interface A {
        public property i():Long;
	}
	class B(i:Long{self==0})  implements A {
        public property i():Long = i;

		val k1:Long{self==0} = this.i;
		@ERR val k2:Long{self==1} = this.i;
	}
}
class SettableAssignWithoutApply {
    operator this(i:Long)=(v: Long): void {}
	def m() {
        this(1) = 2;
        this(1) += @ERR @ERR 2;
	}
	def testSettableAssign(b:DistArray[Long], p:Point(b.rank)) {
		b(p)+=1;
		b(p)++;
	}
}





class TestTypeParamShaddowing {
class E[F] {F <: String } { //1
 def test(f:F):String = f; // making sure "F" refers to the type parameter

 @ERR val f1 = new F(1); // in Java: unexpected type. found: type parameter F.  required: class
 class F { //2 //
    @ShouldBeErr val ff = new F(1); // in Java: unexpected type. found: type parameter F.  required: class
    def this(i:Long) {}
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
    @ShouldBeErr public static type T3 = Long;

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
		val region = Region.make(1..1, 1..1);
		val i: Iterator[Point{self.rank==2}] = region.iterator();
	}
	def test2() {
		val i: Iterator[Point{self.rank==2}] = (Region.make(1..1, 1..1)).iterator(); // was XTENLANG-2275
	}
}
class CyclicTypeDefs {	
	static type B = B; // ERR
	static type X = Y; // 
	static type Y = Z; // ERR
	static type Z = X; // ERR ERR
}
class XTENLANG_2277 {	
	def m(a:Rail[Long]) {
		@ERR val x:Long{self==2} = a(0)+=2;
	}
}

class TestSetAndApplyOperators {
	static class OnlyApply {
		operator this(i:Long) = 0;
	}
	static class OnlySet {
		operator this(i:Long)=(v:Long) = 1;
	}
	static class BothSetApply {
		operator this(i:Long) = 2;
		operator this(i:Long)=(v:Long):Long{self==v} = v;
	}
	def test(s:OnlyApply, a:OnlySet, sa:BothSetApply) {
		s(2);
		a(2); // ERR
		sa(2);
		s(2) = 3; // ERR
		a(2) = 3; 
		sa(2) = 3; 
		s(2) += 3; // ERR
		a(2) += 3; // ERR ERR
		sa(2) += 5; 
		val i1:Long{self==5} = sa(2) = 5; 
		val i2:Long{self==5} = sa(2) += 5; // ERR
		val i3:Long{self==4} = sa(2) += 5; // ERR
	}
}

class ArrayAndRegionTests {
	def test(a1:Array[Long](1){rect}, r:Region{rect, rank==1}, a2:Array[Long](r), a3:Array[Long]{rect, rank==1}) {
		val reg5:Region{rect, rank==1} = Region.make(0..10);
		@ERR val reg6:Region{rank==2} = Region.make(0..10);
		val reg7:Region{rank==2} = Region.make(0..10, 0..10);

		val reg:Region{rect, rank==1} = Region.make(0..10);
		val arr1:Array[Long]{rect, rank==1} = new Array[Long](11,0);
		val arr2:Array[Long]{rect, rank==1} = new Array[Long](reg,0); 
		val arr3:Array[Long]{region.rect, region.rank==1} = new Array[Long](reg,0); 
		val arr4:Array[Long](reg) = null;
		m1(a1);
		m1(a2);
		m1(a3);
		m1(arr3);
		m1(arr4);

		m2(a1);
		m2(a2);
		m2(a3);
		m2(arr3);
		m2(arr4);
	}
	def m1(Array[Long]{rect, rank==1}) {}
	def m2(Array[Long]{region.rect, region.rank==1}) {}
}

class PropertyFieldTest42 { // XTENLANG-945
	interface I {
        public property a():Long;
	}
	class B {
		def m(i:I) = i.a();
	}
}

class MethodGuardEntailsOverriden {
	class A(i:Long) {
	  def m() {i==1} {}
	  def m2() {i!=0} {}
	}
	class B extends A {
	  def this() { super(3); }
	  def m() {i!=0} {}
	  @ERR def m2() {i==1} {} // guard can only be made stronger. see XTENLANG-2325
	} 
}


class TestOperatorsWithoutGuards {
	public operator - this:Long = 1;
	public operator this * (g:TestOperatorsWithoutGuards) = 2;
	public operator this(i:Long) = 3;
	public operator this(i:Long) = (j:Long) = 4;

	def test(g1:TestOperatorsWithoutGuards, g2:TestOperatorsWithoutGuards) {
		val a = -g1;
		val b = g1*g2;
		val c = g1(42);
		val d = g1(42)=43;
	}
}
class XTENLANG_2329(x:Long) { // see XTENLANG_2329, but here we check with VERBOSE_CHECKS (unlike in XTENLANG_2329.x10 where we check with STATIC_CHECKS)
	public operator this * (g:XTENLANG_2329) {x==0} = 2;
	public operator this(i:Long) {x==0} = 3;
	public operator this(i:Long) = (j:Long) {x==0}  = 4;

	def test(g1:XTENLANG_2329, g2:XTENLANG_2329) {
		@ERR val b = g1*g2;
		@ERR val c = g1(42);
		@ERR val d = g1(42)=43;
	}
	
	def closureTest(c: (i:Long) {i==0} => Long , k:Long ) {
		@ERR val a = c(k);
	}
}

class DynamicGuardCheck {
	class A {
		def this(q:Long) {q==0} {}
	}
	class B extends A {
		def m1(i:Long{self==0}) {}
		def m2(i:Long) {i==0} {}
		def this(i:Long, j:Long) {i==0} {
			super(j); // ERR: with VERBOSE:	Warning: Generated a dynamic check for the method guard.
		}
		def this(i1:Long) {
			this(i1,4); // ERR ERR: with VERBOSE:	Warning: Generated a dynamic check for the method guard.
		}

		def test(q:Long) {
			m1(q); // ERR: Warning: Expression 'q' was cast to type x10.lang.Long{self==0}.
			m2(q); // ERR: with VERBOSE:	Warning: Generated a dynamic check for the method guard.		with STATIC_CHECKS: Method m2(i: x10.lang.Long){i==0}[]: void in Hello{self==Hello#this} cannot be called with arguments (x10.lang.Long{self==q}); Call invalid; calling environment does not entail the method guard.
			new B(q,q); // ERR: with VERBOSE:	Warning: Generated a dynamic check for the method guard.
		}

		def closureTest(c: (i:Long) {i==0} => Long , k:Long ) {
			@ERR { c(k); }
		}
	}
}

class XTENLANG_2346[T](x:Long) {
	def m1() {x==1} {}
	def m2() {T<:Long} {}
	def m11(a:Long{x==1}) {}
	def m22(a:Long{T<:Long}) {}

	def test() {
		m1(); // ERR:  Warning: Generated a dynamic check for the method guard.
		m2(); // ERR: Semantic Error: Method m2(){}[T <: x10.lang.Long]: void in TestGuard{self==TestGuard#this} cannot be called with arguments (); Type guard [T <: x10.lang.Long] cannot be established; inconsistent in calling context.

		m11(3); // ERR:  Warning: Expression '3' was cast to type x10.lang.Long{TestGuard#this.x==1}.
		m22(3); // ShouldBeErr
	}	
}

class XTENLANG_2376 {
	class A {
	  def test(b:Long) { 
		val a = new A(b*3, b+5); // ERR: Warning: Generated a dynamic check for the method guard.
	  }
	  def this(i:Long, j:Long) {i==j} {}
	  def this(i:Long) {
		this(i*i, i*2); // ERR ERR: The constructor guard was not satisfied.
	  }
	}
	class B extends A {
	  def this(m:Long) {
		super(m*m, m*2); // ERR: The constructor guard was not satisfied.
	  }
	}
}

class TestInnerClassesThisQualifer {	
	static class B {
		static class B1 {
			def BLA1() = B1.this;
			def BLA2() = B.this; // ERR: The nested class "TestInnerClassesThisQualifer.A.B.B1" does not have an enclosing instance of type "TestInnerClassesThisQualifer.A.B".
		}
		class B2 {}
	}
	class A {
		class C {
			def BLA() = C.this;
			static class C1 {} // ERR: Semantic Error: Inner classes cannot declare static member classes.
			class C2 {}
		}
		class D extends C {
			def BLA2() = C.this; // ERR: The nested class "TestInnerClassesThisQualifer.A.D" does not have an enclosing instance of type "TestInnerClassesThisQualifer.A.C".
			def BLA3() = D.this;
			def BLA4() = A.this;
		}
	}
}

class XTENLANG_2377 {
class Outer {
	class A[T] {
		def this(b:A[T]) {
		}
		def test(i:A[Double], outer:Outer) {
			val z1 = outer.new A(i);   // works ok (infers the type arguments)
			val z2 = new A[Double](i); // works ok


			//Semantic Error: Constructor Outer.A.this(b: Outer.A[T]): Outer.A[T]
			// cannot be invoked with arguments 
			//(Outer.A[x10.lang.Double]{self==i}).
			//Semantic Error: Type is missing parameters.
			// Type: Outer.A
			// Expected parameters: [T]			
			val z3 = outer.new A[Double](i); // ShouldNotBeERR ShouldNotBeERR
		}
	}
}
}

class XTENLANG_2379 {
	class B(b:Long) {}
	class C(c:Long) extends B {
		def this(i1:Long, i2:Long) {i1!=i2} {
			super(5);
			property(6);
		}
	}
}
       class XTENLANG_2389 {
       	class A {
       		property foo()=A.this;
       		class B {
       			property bar()=A.this;
       			property foo2()=B.this;
       		}
       		def bla(a1:A, a2:A{this==a1}) {
       			@ERR val a3:A{self==a1} = a2;
       			@ERR val a4:A{self!=a1} = a2;
       		}

       		def test1(a1:A, a2:A{self.foo()==a1}) {}
       		def test2(a1:A, a2:A{A.this==a1}) {
       			@ERR { test1(a1,a2); }
       		}
       		def test3(a1:A, b2:B{self.bar()==a1}) {}
       		def test4(a:A, b:B{A.this==a}) {
       			@ERR { test3(a,b); }
       		}
       	}
}

class XTENLANG_2390 {
	protected val a:Long;
	def this(x:Long) {
		a = x;
	}
	def m1(i:Long{self==a}) {} // ShouldBeErr
	def m2() {2==a} {} // ShouldBeErr
}

class XTENLANG_2401 {
	def m(Long{self!=0}):String = "a";
	def m(Double):Long = 1;
	def test() {
		val z1:String = m(1); 
		val z2:Long = m(1); // ERR, but it should be a different error! The current error is: Semantic Error: Cannot assign expression to target.	 Expression: m(x10.lang.Double.implicit_operator_as(0))	 Expected type: x10.lang.String,  and it should complain about the constraint that is not satisfied.
		val z3:String = m(0); // ERR
		val z4:Long = m(0); // ERR
	}
}
class XTENLANG_2403 {
	def shouldFail(dist:Dist) {
		test(Region.make(0..2, 0..3), (Point) => 3 ); // ShouldBeErr
	}
	def test(r: Region, init: (Point(r.rank)) => Long) {}
}
class RegionCastTests {
	val e = Region.make(-10..10);
	val r0:Region(2){rect} = e*e;
	val r1 = (e*e) as Region(3); // ERR
	val r2 = e*e as Region(2); // ERR
}

class TestOverridingAndHasType {
	val i <: Double = 2;
	class A {
		def m():Long = 1;
		def t():Long = 1;
		def p():Double = 1;
	}
	class B extends A {
		def m() {throw new Exception();	} // ShouldNotBeERR
		def t() <: Long { throw new Exception();	} // ShouldNotBeERR ShouldNotBeERR
		def p() = 2;  // ShouldNotBeERR
	}
	class C {	
		def m() { throw new Exception();	} // infers void
		def m1():void { throw new Exception();	}
		def m2():Long { throw new Exception();	}
		def m3() <: Long {throw new Exception();	} // ShouldNotBeERR ShouldNotBeERR 
		def m3() <: Double = 2; // ShouldNotBeERR ShouldNotBeERR ShouldNotBeERR 
		def m4() <: void {throw new Exception();	}
	}
}

class CatchInitTest(a:Long) {
	def this() { // ERR: property(...) might not have been called
		try {
			property(2); 
		} catch(e:Exception) {}
	}
}

class TestStaticInitNoCycles {
static class A {
	static val x13 = B.x5;//13
	static val x3 = D.x2+2;//10
	static val x10_ = A.x3+C.x4;//20
}
static class B {
	static val x11 = A.x3+C.x4;//20
	static val x8 = D.x1+A.x3;//16
	static val x5 = C.x4+3; //13

}
static class C {
	static val x14 = B.x11;//20
	static val x4 = D.x2+2; //10
	static val x7 = D.x2+B.x5+C.x4;//31
	static val x9 = D.x6+A.x3;//41

}
static class D {
	static val x12 = A.x3+C.x4;//20
	static val x1 = Int.parse("6"); //6
	static val x2 = x1+2;//8
	static val x6 = x2+B.x5+C.x4; //31
	static val x15 = A.x13+B.x11+C.x14+D.x12+x6+C.x7;//13+20+20+20+31+31=135
}
}

class CopyBackTest {
    public def valTest() {
        val result1 : Long; // Uninitialized
        val result2 : Long; // Uninitialized
        val start = here;
        at(Place.places().next(here)) {
            result1 = 3; // ERR
            at(start) {
	            result2 = 3; // ERR: Local variable "x" is accessed at a different place, and must be declared final.
            }
        }
		use(result1); 
		use(result2);
    }
    public def varTest() {
        var result1 : Long; // Uninitialized
        var result2 : Long; // Uninitialized
        val start = here;
		use(result1); // ERR
		use(result2); // ERR
        at(Place.places().next(here)) {
            result1 = 3; // ERR
			use(result1); // ERR
            at(start) {
	            result2 = 3; // ERR: Local variable "x" is accessed at a different place, and must be declared final.
				use(result1); // ShouldBeErr
				use(result2);
            }
			use(result1); // ERR
			use(result2); // ERR [Local variable is accessed at a different place, and must be declared final.]
        }
		use(result1);
		use(result2);
	}
	def use(Any) {}
}

class XTENLANG_2447(a:Long) {a==1} {
	def this(x:Long):XTENLANG_2447{self.a==x} {
		property(x); // ERR
	}
	def test() {
		val y:XTENLANG_2447 = new XTENLANG_2447(2);
	}

	

	class X[T] {T haszero} {
		def this() {
		}	
		def test() {
			val y = new X[Long{self!=0}](); // ERR ERR ERR [Semantic Error: Inconsistent constructor return type, Semantic Error: Type X[x10.lang.Long{self!=0}] is inconsistent.]
		}
	}

}



class XTENLANG_2456 {
class Test1[T] {T haszero} {
	val z = Zero.get[T]();
}
class Test2[T] {T haszero, T isref} {
	val z = Zero.get[T]();
}

class LikeGlobalRef[T] {
	val t:T;
	def this(t:T) {
		this.t = t;
	}
}
class Test3[T] {
	var test:Test3[T] = null;
	val root = new LikeGlobalRef[Test3[T]](test);
}
class Test4[T] {T haszero} {
	var test:Test4[T] = null;
	val root = new LikeGlobalRef[Test4[T]](test);
}
class Accumulator1 {
  private val root = GlobalRef(this);
}
class Accumulator2 {
  private val root = GlobalRef[Accumulator2](this);
}
class Accumulator3[T] {
  private val root = GlobalRef(this);
}
class Accumulator4[T] {
  private val root = GlobalRef[Accumulator4[T]](this);
}
class Accumulator5[T] {T haszero} {
  private val root = GlobalRef[Accumulator5[T]](this);
}
}


class CollectingFinishTests {
    static struct Reducer implements Reducible[Long] {
     	public   def zero()=0;
     	public   operator this(a:Long,b:Long)=a+b;
    }
    static struct ReducerDouble implements Reducible[Double] {
     	public   def zero()=0.0;
     	public   operator this(a:Double,b:Double)=a+b;
    }
	public def run() {
		val x = finish (Reducer()){
			val y = finish (ReducerDouble()) {
					async offer 6.0;
			};
			async offer (y as Long)+1;
		};
	}
	def normalFinishInside() {//	XTENLANG-2457
		val x = finish (Reducer()){
			finish // ShouldBeErr (XTENLANG-2457)
				offer 6;
		};
	}
}


class TestInterfaceInvariants_1930 { // XTENLANG-1930
	interface I {p()==1} {
        public property p():Long;
    }
	class C(p:Long) implements I {
        public property p():Long = p;
		def this() { 
			property(0); // ERR
		}
	}
	interface I2a extends I{self.p()==1} {}
	interface I3a {this.p()==1} extends I {}
	interface I2 extends I{self.p()==2} {} // ERR [Invalid type; the real clause of x10.frontend.tests.TestInterfaceInvariants_1930.I{self.x10.frontend.tests.TestInterfaceInvariants_1930.I#p()==2} is inconsistent.]
	interface I3 {this.p()==3} extends I {} // ERR Semantic Error: Class invariant is inconsistent.
	static def test(i:I) {
		var i2:I{self.p()==1} = i;
		var i3:I{self.p()==4} = i; // ERR ERR
	}
}


// method overloading
interface TestOverloadingAndConstraints {  // ERR ERR ERR ERR
	static class Foo[T] {}

	def m00():void; // ERR
	def m00():Long; // ERR ERR

	def m0(Long):void;
	def m0(Long):void; // ERR (Semantic Error: Duplicate method "method abstract public TestOverloadingAndConstraints.m0(id$1:x10.lang.Long): void"; previous declaration at C:\cygwin\home\Yoav\test\Hello.x10:11,5-21.)
	
	def m1(Long):void;
	def m1(Long{self!=0}):void; // ERR

	def m11(j:Long):void;
	def m11(i:Long) {i!=0} :void; // ERR ERR
	
	def m2(Foo[Long]):void;
	def m2(Foo[Double]):void;

	def m3(Foo[Long]):void;
	def m3(Foo[Long]{self!=null}):void; // ERR

	def m4(Foo[Long]):void;
	def m4(Foo[Long{self!=0}]):void; // ERR
	
	def m5(Foo[Foo[Long]]):void;
	def m5(Foo[Foo[Long{self!=0}]]):void; // ERR
}
class TestOverloadingAndConstraints_static {// ERR ERR ERR ERR
	static class Foo[T] {}

	static def m00():void {} // ERR
	static def m00():Long {} // ERR ERR ERR

	static def m0(Long):void {}
	static def m0(Long):void {} // ERR (Semantic Error: Duplicate method "method abstract public TestOverloadingAndConstraints.m0(id$1:x10.lang.Long): void" {} previous declaration at C:\cygwin\home\Yoav\test\Hello.x10:11,5-21.)
	
	static def m1(Long):void {}
	static def m1(Long{self!=0}):void {} // ERR
	
	static def m11(j:Long):void {}
	static def m11(i:Long) {i!=0} :void {} // ERR ERR	
	
	static def m2(Foo[Long]):void {}
	static def m2(Foo[Double]):void {}

	static def m3(Foo[Long]):void {}
	static def m3(Foo[Long]{self!=null}):void {} // ERR

	static def m4(Foo[Long]):void {}
	static def m4(Foo[Long{self!=0}]):void {} // ERR
	
	static def m5(Foo[Foo[Long]]):void {}
	static def m5(Foo[Foo[Long{self!=0}]]):void {} // ERR
}
interface TestOverloadingAndConstraints_macros { // ERR  ERR  ERR  ERR
	static type Long1 = Long;
	static type Long2 = Long1;
	static type Long3Not0 = Long2{self!=0};
	static type LongNot0 = Long3Not0;
	static type FooNotNull[T] = Foo[T]{self!=null};
	
	static class Foo[T] {}

	def m1(Long):void;
	def m1(LongNot0):void; // ERR
	
	def m2(Foo[Long]):void;
	def m2(Foo[Double]):void;

	def m3(Foo[Long]):void;
	def m3(FooNotNull[Long]):void; // ERR

	def m4(Foo[Long]):void;
	def m4(Foo[LongNot0]):void; // ERR
	
	def m5(Foo[Foo[Long]]):void;
	def m5(Foo[Foo[LongNot0]]):void; // ERR
}
// constructor overloading
class TestOverloadingAndConstraints_ctors {
	static class Foo[T] {}
	class A0 {
		def this(Long) {}
		def this(Long) {} // ERR (Semantic Error: Duplicate method "method abstract public TestOverloadingAndConstraints.m0(id$1:x10.lang.Long): void"; previous declaration at C:\cygwin\home\Yoav\test\Hello.x10:11,5-21.)
	}	
	class A1 {
		def this(Long) {}
		def this(Long{self!=0}) {} // ERR
	}	
	class A11 {
		def this(j:Long) {}
		def this(i:Long) {i!=0} {} // ERR
	}
	class A2 {	
		def this(Foo[Long]) {}
		def this(Foo[Double]) {}
	}
	class A3 {	
		def this(Foo[Long]) {}
		def this(Foo[Long]{self!=null}) {} // ERR
	}
	class A4 {	
		def this(Foo[Long]) {}
		def this(Foo[Long{self!=0}]) {} // ERR
	}
	class A5 {		
		def this(Foo[Foo[Long]]) {}
		def this(Foo[Foo[Long{self!=0}]]) {} // ERR
	}
}
class TestOverloadingAndConstraints_ctors_macros {
	static type Long1 = Long;
	static type Long2 = Long1;
	static type Long3Not0 = Long2{self!=0};
	static type LongNot0 = Long3Not0;
	static type FooNotNull[T] = Foo[T]{self!=null};
	
	static class Foo[T] {}
	
	class A1 {
		def this(Long) {}
		def this(LongNot0) {} // ERR
	}
	class A2 {	
		def this(Foo[Long]) {}
		def this(Foo[Double]) {}
	}
	class A3 {	
		def this(Foo[Long]) {}
		def this(FooNotNull[Long]) {} // ERR
	}
	class A4 {	
		def this(Foo[Long]) {}
		def this(Foo[LongNot0]) {} // ERR
	}
	class A5 {		
		def this(Foo[Foo[Long]]) {}
		def this(Foo[Foo[LongNot0]]) {} // ERR
	}
}

// typedef overloading
interface TestTypeDefOverloadingAndConstraints {
	static class Foo[T] {}

	static type m00 = Long; 
	static type m00 = Double; // ERR [Semantic Error: Duplicate type definition "type static TestOverloadingAndConstraints.m00 = x10.lang.Double"; previous declaration at C:\cygwin\home\Yoav\test\Hello.x10:13,5-28.]

	static type m0(Long) = Long;
	static type m0(Long) = Long; // ERR (Semantic Error: Duplicate method "method abstract public TestOverloadingAndConstraints.m0(id$1:x10.lang.Long): void"; previous declaration at C:\cygwin\home\Yoav\test\Hello.x10:11,5-21.)
	
	static type m1(Long) = Long;
	static type m1(Long{self!=0}) = Long; // ERR

	static type m11(j:Long) = Long;
	static type m11(i:Long) {i!=0}  = Long; // ERR [Semantic Error: Duplicate type definition "type static TestOverloadingAndConstraints.m11(x10.lang.Long){i!=0} = x10.lang.Long"; previous declaration at C:\cygwin\home\Yoav\test\Hello.x10:22,5-33.]
	
	static type m2(Foo[Long]) = Long;
	static type m2(Foo[Double]) = Long;

	static type m3(Foo[Long]) = Long;
	static type m3(Foo[Long]{self!=null}) = Long; // ERR

	static type m4(Foo[Long]) = Long;
	static type m4(Foo[Long{self!=0}]) = Long; // ERR
	
	static type m5(Foo[Foo[Long]]) = Long;
	static type m5(Foo[Foo[Long{self!=0}]]) = Long; // ERR
}
interface TestTypeDefOverloadingAndConstraints_macros {
	static type Long1 = Long;
	static type Long2 = Long1;
	static type Long3Not0 = Long2{self!=0};
	static type LongNot0 = Long3Not0;
	static type FooNotNull[T] = Foo[T]{self!=null};
	
	static class Foo[T] {}

	static type m1(Long) = Long;
	static type m1(LongNot0) = Long; // ERR
	
	static type m2(Foo[Long]) = Long;
	static type m2(Foo[Double]) = Long;

	static type m3(Foo[Long]) = Long;
	static type m3(FooNotNull[Long]) = Long; // ERR

	static type m4(Foo[Long]) = Long;
	static type m4(Foo[LongNot0]) = Long; // ERR
	
	static type m5(Foo[Foo[Long]]) = Long;
	static type m5(Foo[Foo[LongNot0]]) = Long; // ERR
}

class TestMemberTypeResolution {
	static type Foo(i:Long{self!=0}) = Long;
	static type Foo(i:Double) = Long;
	var y:Foo(1);
	var x:Foo(0); // ERR: todo: improve error: Semantic Error: Could not find type "Foo".
	var z:Foo(0.1); 
}
class TestFieldResolution(p:Long) {
	val f = 2;
	val f2:Long{self==1 && self==this.p} = p as Long{self==1};
	def test1(me2:TestFieldResolution{self.p==2}) {
		use(me2.f2); // ERR: Type inconsistent
	}
	static def test() {
		use(f); // ERR ERR [Cannot access a non-static field f from a static context.]
	}
	static def use(Any) {}
}

class TestMultipleImplementAndFields {
	interface I1 {
        public property z():Long;
	    static val a = 1;
	}
	interface I2 extends I1 {}
	interface I3 extends I1 {}
	interface I4 extends I2,I3 {}
	interface I5 extends I4 {
		def m() {z==1} : void;
	}
	class Example1(z:Long) implements I5 {
      public property z():Long = z;
	  def example() = a;
	  public def m() {z==1} {};
	}
	class Example2(z:Long) implements I5,I3 {
      public property z():Long = z;
	  def example() = a;
	  public def m() {z==1} {};
	}
}


class ResolutionAndInference {
	def m[T](Long) = 1;
	def m(Double) = "1";

	def test(){
		val x2 = m(0);  // resolves to m(Double) because generic-type-inference failed on m[T](Long)
		val x3:Long = x2; // ERR
		val x5:String = m(0.0);
	}
}

// resolution should ignore constraints and method guards
class TestCtorResolutionAndConstraints(p:Long) {
	def this(Long{self!=0}) {
		property(1);
	}
	def this(Double) {
		property(2);
	}
	def test() {
		val x22 = new TestCtorResolutionAndConstraints(0); // ERR
		val x1:TestCtorResolutionAndConstraints{self.p==1} = new TestCtorResolutionAndConstraints(1);
		val x3:TestCtorResolutionAndConstraints{self.p==2} = new TestCtorResolutionAndConstraints(1.1);
	}
}
class TestCtorOverloadingAndConstraints {
	def this(Long{self!=0}) {
	}
	def this(Long) { // ERR
	}
}
class TestMethodResolutionAndConstraints_instance {
	def m(Long{self!=0}) = 1;
	def m(Double) = "1";
	def test() {
		val x1:Long = m(1);
		val x2 = m(0); // ERR
		val x3:Long = x2;
		val x4:String = m(0 as Double);
		val x5:String = m(0.0);
	}
}
class TestMethodResolutionAndConstraints_static {
	static def m(Long{self!=0}) = 1;
	static def m(Double) = "1";
	static def test() {
		val x1:Long = m(1);
		val x2 = m(0); // ERR
		val x3:Long = x2;
		val x4:String = m(0 as Double);
		val x5:String = m(0.0);
	}
}
class TestMethodResolutionAndConstraints_param_guard {
	static def m(i:Long) {i!=0} = 1;
	static def m(Double) = "1";
	static def test() {
		val x1:Long = m(1);
		val x2 = m(0); // ERR
		val x3:Long = x2;
		val x4:String = m(0 as Double);
		val x5:String = m(0.0);
	}
}
class TestMethodResolutionAndConstraints_this_guard(p:Long) {
	def m(i:Long) {p!=0} = 1;
	def m(Double) = "1";
	def test() {
		val x1:Long = (this as TestMethodResolutionAndConstraints_this_guard{self.p==1}).m(1);
		val x2 = (this as TestMethodResolutionAndConstraints_this_guard{this.p==1}).m(0); 
		val x3:Long = x2;
		val x4:String = m(0 as Double);
		val x5:String = m(0.0);
	}
	static def test(me0:TestMethodResolutionAndConstraints_this_guard{p==0}, me1:TestMethodResolutionAndConstraints_this_guard{p==1}) {
		val x1:Long = me1.m(1);
		val x2 = me0.m(0); // resolves to m(Double):String
		val x3:Long = x2; // ERR
		val x33:String = x2; 
		val x4:String = me0.m(0 as Double);
		val x5:String = me1.m(0.0);
	}
}
// type constraints
class TestMethodResolutionAndTypeConstraints_instance[T] {
	def m(Long) {T haszero} = 1;
	def m(Double) = "1";
	def test1() {T haszero} {
		val x1:Long = m(1);
		val x4:String = m(0 as Double);
		val x5:String = m(0.0);
	}
	def test2() {
		val x2 = m(0); // ERR
		val x3:Long = x2;
	}
}
class TestMethodResolutionAndTypeConstraints_static {
	static def m[T](Long{self!=0}) {T haszero} = 1;
	static def m[T](Double) = "1";
	static def test() {
		val x1:Long = m[Long{self==1}](1);// ERR (Semantic Error: Cannot assign expression to target.		 Expression: m[x10.lang.Long{self==1}](x10.lang.Double.implicit_operator_as(1))		 Expected type: x10.lang.Long		 Found type: x10.lang.String{self=="1"})
		val x2 = m[Long{self==0}](0); // ERR
		val x3:Long = x2;
		val x4:String = m[Long{self==1}](0 as Double);
		val x5:String = m[Long{self==0}](0.0);
	}
}

// method overloading
class TestStaticErr {
    def test(b:Int) {}
    static def test(b:Long) {}
    static def x() {
        test(1n); // ERR todo: should be:
			//Cannot access a non-static member or refer to "this" or "super" from a static context.
			// but it is: Method or static constructor not found for given call.
    }
}


class Call_resolution_tests {	
	class ClosureField {
		var test:()=>Long;
		def m() {
			val x:Long = test();
		}
	}
	class MethodTest {
		def T():String = "a";
		def m1[T]() {
			val y:String = T(); // method takes precedence over type-param
		}
		var U:()=>String;
		def m2[U]() {
			val y:String = U(); // closure takes precedence over type-param
		}
	}
	class LocalTest {
		def m1[T](T:String) {
			val y = T.substring(1n); // local takes precedence over type-param
		}
		def m2[T](T:()=>Long) {
			val y = T(); // local takes precedence over type-param
		}
		def m3[T](x:String) {
			val y = T.substring(1n); // ERR ERR [Cannot invoke a static method of a type parameter. Method not found in Any.]
		}
		
		def test():String = "a";
		def vsMethod(test:()=>Long) {
			val x:String = test();
			val y:Long = (test)();
		}
	}
	class FieldTest {
		var T:String;
		def m1[T]() {
			val y = T.substring(1n); // fields takes precedence over type-param
		}
		var U:()=>Long;
		def m2[U]() {
			val y = U(); // fields takes precedence over type-param
		}
		
		var test:()=>Long;
		def test():String = "a";
		def vsMethod() {
			val x:String = test();
			val y:Long = (test)();
		}
	}
}


class CallResolution_method_field_local {	 
	var f: ()=>Long;
	def f():Double = 0.1;
	def m(f: ()=>String) {
		val x1:Double = this.f();
		val x2:Double = f();
		val x3:String = (f)();
		val x4:Long = (this.f)();
	}
}
class CallResolution_struct_vs_field_and_local {	 
	static struct f {}
	var f: ()=>Long;
	def m(f: ()=>String) {
		val x1:String = f();
		val x2:Long = this.f();
		val x3:f = new f();
	}
}
class CallResolution_struct_method_field_local {	 
	static struct f {}
	var f: ()=>Long;
	def f():Double = 0.1;
	def m(f: ()=>String) {
		val x1:Double = this.f();
		val x2:Double = f(); 
		val x22:f = new f();
		val x222:f = new CallResolution_struct_method_field_local.f();
		val x3:String = (f)();
		val x4:Long = (this.f)();
	}
}
class CallResolution_typeParam_struct_method_field_local {	 
	static struct f {}
	var f: ()=>Long;
	def f():Double = 0.1;
	def m[f](f: ()=>String) {
		val x1:Double = this.f();
		val x2:Double = f(); // because the struct ctor is hidden
		val x22 = new f(); // ERR [Semantic Error: No valid constructor found for f().]
		val x222:CallResolution_typeParam_struct_method_field_local.f = new CallResolution_typeParam_struct_method_field_local.f();
		val x3:String = (f)();
		val x4:Long = (this.f)();
	}
}

class TestConstraintErrMessage {
	class A(a:Long) {}
	class B[T](b:Long) {}
	class F(r:Long) {
		var b:B[A{self.a==this.r}]{self.b==this.r};
		def test(f:F{self.r==1}) {
			val bb:B[A{self.a==1}]{self.b==1} = f.b;
			val err1:String = f;  // ERR
			val err2:String = f.b;// ERR: todo: the error message doesn't mention the constraint that f.r==1
		}
	}
}

class LoopTests {
	def bug(d:Dist(1)) {
		for (p:Point(1) in d) {}
	}
    public def run() {
        val r:Region(2){rect}  = Region.make(0..2, 0..3);
	}

	class P(r:Long) {}
	abstract class D(q:Long) implements Iterable[P{self.r==this.q}] {}
	def m2(d:D{q==1}) {
		for (p:P{r==1} in d) {}
	}

	
	class NonIterable {}
	interface Long2Iterable2 extends Iterable[Long{self==2}] {}
	abstract class Long2Iterable implements Long2Iterable2 {}

	def m0(x:NonIterable) {
		for (p:Any in x) {} // ERR
	}
	def m00(x:Long2Iterable) {
		for (p:Any in x) {}
		for (p:String in x) {} // ERR
		for (p:Long in x) {}
		for (p:Long{self==2} in x) {}
		for (p:Long{self==1} in x) {} // ERR
	}
	def m1[T,U](x:T) {T <:Iterable[U]} {
		for (p:Any in x) {}
		for (p:String in x) {} // ERR
		for (p:U in x) {}
		for (p:T in x) {} // ERR
	}
	def m11[T,U](x:T) {T <:Iterable[U]} {
		for (p:Any in x) {}
		for (p:String in x) {} // ERR
		for (p:U in x) {}
		for (p:T in x) {} // ERR
	}
	def m2[T](x:T) {T <:Iterable[Long{self==2}]} {
		for (p:Any in x) {}
		for (p:String in x) {} // ERR
		for (p:Long in x) {}
		for (p:Long{self==2} in x) {}
		for (p:Long{self==1} in x) {} // ERR
	}
	def m3(x:Iterable[Long{self==2}]) {
		for (p:Any in x) {}
		for (p:String in x) {} // ERR
		for (p:Long in x) {}
		for (p:Long{self==2} in x) {}
		for (p:Long{self==1} in x) {} // ERR
	}
}


class XTENLANG_2491 {
	static class B[T] {
		val t:T;
		def this(t:T) { this.t = t; }
	  public static operator[T] (x:T) as B[T] = new B[T](x);
	  public def equals(a:Any) {
		if (a instanceof B[T]) {
		  return (a as B[T]).t==t; // should "as" do the system-as or the user-explicit-as ?  // ERR: Warning: This is an unsound cast because X10 currently does not perform constraint solving at runtime for generic parameters.
		}
		return false;
	  }
	}
	static class MyBox[T] {	
		public static operator[T](x:T):MyBox[T] = new MyBox[T]();
	}
	static class MyHashMap[K,V] {
		val t:V;
		def this(t:V) { this.t = t; }
		def get(): MyBox[V] {
			return t as MyBox[V]; // should "as" do the system-as or the user-implicit-as? (currently it does the system-as, and fails at runtime with ClassCastException)  // ERR: Warning: This is an unsound cast because X10 currently does not perform constraint solving at runtime for generic parameters.
		}
	}
	static class Hello {
		public def main(Rail[String]) {
			val map = new MyHashMap[String,Long](5);
			Console.OUT.println(map.get());
		}
	}
	static class LiteralTests {
		val b1:Byte = 127y;
		val b2:Byte = 128y; // ShouldBeErr
		val b3:Byte = 127;  // ERR
		val b4:Byte = 128;  // ERR
		val b5:Byte = 128 as Byte;
	}
}


class SuperPropertyFieldResolution {
	class B(a:Long) {}
	class C1 extends B{a==1} {
		def this() { super(1); }
	}
	class C2 extends B{self.a==1} {
		def this() { super(1); }
	}
	class C3 extends B{this.a==1} { // ERR ERR ERR
		def this() { super(1); }
	}
	class C33 extends B{super.a==1} { // ERR ERR
		def this() { super(1); }
	}
	class C4 {a==1} extends B {
		def this() { super(1); }
	}
	class C5 {self.a==1} extends B { // ERR ERR
		def this() { super(1); }
	}
	class C6 {this.a==1} extends B {
		def this() { super(1); }
	}
}
class SuperPropertyMethodResolution {
	class B {
		property a():Long = 1;
		def this(z:Long) {}
	}
	class C1 extends B{a()==1} { // ShouldNotBeERR ShouldNotBeERR: Method or static constructor not found for given call.	 Call: a()
		def this() { super(1); }
	}
	class C2 extends B{self.a()==1} {
		def this() { super(1); }
	}
	class C3 extends B{this.a()==1} { // ERR ERR
		def this() { super(1); }
	}
	class C33 extends B{super.a()==1} { // ERR ERR
		def this() { super(1); }
	}
	class C4 {a()==1} extends B {
		def this() { super(1); }
	}
	class C5 {self.a()==1} extends B { // ERR ERR
		def this() { super(1); }
	}
	class C6 {this.a()==1} extends B {
		def this() { super(1); }
	}
}
class InterfaceSuperPropertyMethodResolution {
	interface B {
		property a():Long;
	}
	abstract class C1 implements B{a()==1} { // ERR ShouldNotBeERR: Method or static constructor not found for given call.	 Call: a()
	}
	abstract class C2 implements B{self.a()==1} { // ERR
	}
	abstract class C3 implements B{this.a()==1} { // ERR ERR ERR
	}
	abstract class C4 implements B{super.a()==1} { // ERR ERR ERR
	}
	abstract class C5 
			{a()==1} // ERR: Cannot create the default constructor because the class invariant uses property methods self or super. Please define a constructor explicitly.
		implements B { // ERR
	}
	abstract class C6 
			{self.a()==1} // ERR ERR ERR: Cannot create the default constructor because the class invariant uses property methods self or super. Please define a constructor explicitly.
		implements B { 
	}
	abstract class C7 
			{this.a()==1} // ERR: Cannot create the default constructor because the class invariant uses property methods self or super. Please define a constructor explicitly.
		implements B { // ERR
	}
}

// redesign of property fields and property methods in interfaces
//http://jira.codehaus.org/browse/XTENLANG-1914
class InterfacePropertyMethods {
	interface XX(i:Long) {} // ERR
	interface YY(i:Long) extends MethodAnnotation, FieldAnnotation { } // ok
	interface ZZ(j:String) extends YY {} // ok
	class TestAnnotations {
		@YY(23) val k1 = "1";
		@ZZ("",3) val k2 = "1"; // ShouldNotBeERR

		@YY(23,42) val k3 = "1"; // ERR
		@ZZ("") val k4 = "1"; // ShouldBeErr
		@XX(23) val k5 = "1"; // ERR
	}

	interface I {
		property i():Long;
	}
	abstract class A1 implements I {
		public abstract property i():Long;
	}
	class B1 extends A1 {
		public property i():Long = 1; // properties are final
	}
	class C1 extends B1 {
		public property i():Long = 2; // ERR: Semantic Error: i(): x10.lang.Long in C1 cannot override i(): x10.lang.Long in B1; overridden method is final
	}
	class A2 implements I {
		public property i():Long = 3;
	}
}

struct StructCannotBeUsedInGlobalRef {
  private val root = new GlobalRef(this); // ERR ERR
}

class RuntimeChecksOfConstraintsInGenerics {
    public static def main(args: Rail[String]) {
        val arr = new Array[Long{self==3}](101, (Long)=>3);
		//arr(0) = 1; // err: Cannot assign expression to array element of given type.             Expression: 1             Type: x10.lang.Long{self==1}             Array element: arr(0)             Type: x10.lang.Long{self==3}    
		arr(0) = 3; 
		val arrAlias = (arr as Any) as Array[Long](1); // it should have failed HERE  // ERR: Warning: This is an unsound cast because X10 currently does not perform constraint solving at runtime for generic parameters.
		arrAlias(0) = 1; 
		val x:Long{self==3} = arr(0); // Broke type safety
		assert x==3 : "We should have failed before"; // but it fails HERE 
    }
}

class PropDefConstraint_Circular(a: Boolean) { // see XTENLANG-2426

	@ERR @ERR property def prop1(i:Boolean):Boolean {  return prop1(i); }
	property prop2():Boolean = prop1(true);
	@ERR @ERR property propA():Boolean = propB();
	@ERR @ERR property propB():Boolean = propA();

	 def pMethod(j:Boolean) {prop1(j)} {} //this creates a cyclic constraint
	 def a() {prop2()} {}
	 def b() {propA()} {}
	 def c() {propB()} {}

    public def run()
    {
		val p = new PropDefConstraint_Circular(true);
        p.pMethod(true); // ERR
		p.a(); // ERR
		p.b(); // ERR
		p.c(); // ERR
    }
}
class TestPropertsFieldsAndNullaryPropertyMethods(m:Long, q:String, w:Long) {
    public property z():Long = 3;
    public property n():Long = n; // ERR ERR (circularity)
    public property m():Long = m;
	public property q():Double = 2.2;
	def test() {
		val x1:Long = m;
		val x2:Long = m();
		val x3:String = q;
		val x4:Double = q();
		val x5:Long = z;
		val x6:Long = z();
		val x7:Long = w;
		val x8:Long = w(); // ERR
	}

	class Super {
		property a():Long = 1;
	}
	class Sub(a:String) extends Super {
		def test(sub:Sub) {
			val sup:Super = sub;
			val x1:Long = sup.a;
			val x2:String = sub.a;
		}
	}
}

class PropertyTypeCannotBeParameterType[T](a:T) {} // ERR: A property type cannot be a type parameter.
class SimplerPropertyTest {
	class A(a:A) {} // ERR
	class B(b:Long) {
		def this() { property(2); }
	}
	class C(c:B) extends B {}
	class D(e:E) {} // ERR
	class E(d:D) {} // ERR
	class F(g:G) { // ERR
		def this() { property(null); }
	}
	class G extends F {} // ERR
}

class XTENLANG_2535[T](x:Array[T]) {
	def this() {
		property(null);
	}
	def this(Long):XTENLANG_2535[T] {
		property(null);
	}
}

class PropertyAndCtorArgumentsBug(R:Long) {
	public def this(r:Long)	{
		property(r);
		val x:Long{self==R} = r; 
	}
}
class TestUnsoundCastWarning {	
    def x(args: Array[String]) {
		val any = args as Any;
		val x = args as Array[String](3);
		val y = any as Array[String](3); // ERR: warning: unsound cast
    }
}

class TestConstraintLanguageAndRegions {
	def testComplexConstraint(a:Boolean, b:Boolean, z: Boolean{self==(a==b)}) {} // ERR

	def test() {	
		var t: Boolean(true) = false; 	// ERR
		val R:Region{rank==2&&rect} = Region.make(0..10, 0..10);

		val Universe: Region = Region.make(0..7, 0..7);
		for (val [i,j]: Point in Universe) {} // ERR
	}
}


class TestExceptionsFlow {
	var n:Long;
	def m() {
		val x:TestExceptionsFlow;
		try {
			x = new TestExceptionsFlow();
			val y = 3 / x.n;
		} catch (e:Exception) {} // misguided attempt to ignore ArithmeticException
		use(x); // ERR
	}
	def use(Any) {}
}
class ResolvingPropertyMethods { 
	interface I {
		property p():Long;
		def m():Long;
	}
	class C implements I {
		public property p():Long = 2;
		public def m():Long = 2;
	}
	// p()  should resolve to	self.p()
	static def test(i:I{p()==1}) { // ShouldNotBeERR ShouldNotBeERR: Method or static constructor not found for given call.	 Call: p()
		val x = i.p();
		val y = i.m();
	}
	static def test2(i:C{p()==1}) { // ShouldNotBeERR ShouldNotBeERR: Method or static constructor not found for given call.	 Call: p()
		val x = i.p();
		val y = i.m();
	}
}

class TestQuotes {
	static val `+` = (x:Long, y:Long) => x+y;
	def test() {
		val res:Long = (TestQuotes.`+`)(1,2);
	}
}

class DefaultConstructorBug1 { // see XTENLANG-2273
    public static def bar(y:Long):Foo{self.x==y} = new Foo(y);
    public static class Foo(x:Long) {}
}
class DefaultConstructorBug2 {
    public static class Foo(x:Long) {}
    public static def bar(y:Long):Foo{self.x==y} = new Foo(y);
}

class CheckConstraintLanguage {
  class A(x:Long) {
   property zero()= x==0;
   def z(a:A{self!=null && self.x==1}) {}
   def m(a:A, b:A) { a.zero()}{} // ok
   def n(a:A, b:A) { a.zero()==b.zero()}{} // ERR
  }
}

class F_Bounded_Polymorphism_example {
	class Subj[X,Y] { X <: Subj[X,Y], Y <: Obs[X,Y] } { 
		def m(x:X) {
			val y:Subj[X,Y] = x;
		}
	}
	class Obs[X,Y] { X <: Subj[X,Y], Y <: Obs[X,Y] } { 
		def m(x:Y) {
			val y:Obs[X,Y] = x;
		}
	}
}

class NullGuardTest {
	def test() {
		new NullGuardTest(null); // ShouldNotBeERR
	}
	def this(b:NullGuardTest) {b==null} {}
}
class NullGuardTest2 {
	def test() {
		new NullGuardTest2(null); // ERR
	}
	def this(b:NullGuardTest2) {b!=null} {}
}


class TestNonConstraintInPropertyAssignCall {
	class A(x:Long) {
		def this(a:Long) {
			property(a+1); // ERR
		}
	}
	class B(x:Long) {
		def this(a:Long) {
			val z = a+1;
			property(z);
		}
	}
}
class TestPropertyExpansion {
    static class A(x:Long) {
        property m(a:Long) = x;
        static type B(a:Long) = A{x==1};

        static def test1(i:Long, A{i==self.m(self.m(i))}) {}
        static def test2(i:Long, A{i*3==self.m(i)}) {} // ERR

        static def test3(i:Long, B(i*3)) {}
        static def test4(i:Long, A{x==i*3}) {} // ERR
    }
}
class SelfRestrictionTest {
	static class A(b:Long) {
		val a:A = null;
		def test1(A{this.a.a.a!=null}) {}
		def test2(x:A, A{x.a.a.a!=null}) {}
		def test3(A{self.a.a.a!=null}) {} // ERR: Only properties may be prefixed with self in a constraint.
	}
}

class TestInitChecker {
	def m1() {
		val x:Long;
		val y:Long = 1;
		class A {
			val a = 1;
			class B {
				val b = 1;
				def ab() {
					val c = 1;
					class C {
						val w1 = x+1; // ERR
						val w2 = y+a+b+c;
						def t() {
							val d = 1;
							val f = () => {
								val e = y+a+b+c+d;
								class D {
									val f = y+a+b+c+d+e;
									def r() {
										val g = y+a+b+c+d+e+f;
									}
								}
							};
						}
						val f:()=>void = () => {
							val e = y+a+b+c;
						};
						class E {
							val e = y+a+b+c+w2;
						}
					}
				}
			}
		}
	}
	def m2() {
		val y = 1;
		val o2 = new Any() {
			class M {
				val w = y;
			}
			val z = y;
			def q() {
				val a = 1;
				val w = () => a+y+z;
			}
		};
	}

	// local definite-assignment tests
	def testLoop() {
		for (i in 0..10) {
			val x = i;
		}
	}
	def testFormal(k:Long) {
		val x = ()=>k+1;
	}
	def test0() {
		val d0:Long;
		d0 = 2;
	}
	def test1() {
		val d1:Long = 1;
		val z1 = ()=>d1;
	}
	def test2() {
		val d2:Long;
		val z2 =
			()=>d2; // ERR
		d2 = 2;
	}
	def test3() {
		val d3:Long = 1;
		val z3 = ()=> { d3=3; }; // ERR
	}
	def test4() {
		val d4:Long;
		val z4 = ()=> { d4=4; }; // ERR
	}

    def testClosure() {
		val fun = () => {
			val d = 1;
			val z = ()=>d;
		};

		val x:Long;
		val y = () => {x=3;}; // ERR
		x = 2;

    }
	def test5() {
        val x:Long{self!=0};
        val y:Long{self!=0} = 2;
		val o = new Any() {
			val z = x; // ERR
		};
		val o2 = new Any() {
			val z = y;
		};
        val c = ()=>x; // ERR
		val fun = () => {
			val d = 1;
			val q1 = () => d;
			val q2 = () => d+y;
			val q3 = () => d+x; // ERR
		};
		x = 3;
	}
	def testInner() {
		val i1:Long;
		val i2:Long = 1;
		val x = new Any() {
			def qq() {
				use(i1); // ERR
				use(i2);
				val j1:Long;
				val j2:Long = 2;
				val w =  new Any() {
					def qq2() {
						use(i1); // ERR
						use(i2);
						use(j1); // ERR
						use(j2);
					}
				};
			}
		};
	}
	def use(Any) {}
	val a = 0;
	public def run() {
        val b:long = 1;
        class C {
            val c = 1;
            def foo() {
                val fun = () => {
                    val d:long = 1;
                    (() => a+b+c+d)()
                };
                return fun();
            }
        }
	}
}

class TriangleTest_6 // see XTENLANG-2582
{
    static class Triangle
     {
        def this() {}
        property def prop1(s:Long):Long = s;

        def area(s1:Long{prop1(self) == 1}) {}
     }

    public static def test() {
        new Triangle().area(1);
    }
}


class NonNullaryPropertiesInLongerfaces { // see XTENLANG-2609
	interface I {
		public property p(z:Long):Long;
	}
	class C implements I {
		public property p(z:Long):Long = 2;
	}
	static def test(i:I{self.p(3)==2}) {} // ShouldNotBeERR
}

class TestAnnotationCheck {
    @Inline public def m() {}
}

class AbstractPropertyMethodsTests1 {
	interface I {
	  public property p():Long;
	}
	abstract class A(x:Long) implements I {
	  def test(c:A{self.x==2}) {
		val i:I{self.p()==2} = c; // ERR
	  }
	}
	abstract class C(x:Long) implements I {
	  public property p() = x;
	  def test(c:C{self.x==2}) {
		val i:I{self.p()==2} = c; // ShouldNotBeERR XTENLANG-3266
	  }
	  // mixing this and self
	  def test2(c:C{this.x==2}, c2:C{this.p()==2}) {
		val i1:I{this.p()==2} = c;
		val i2:I{this.p()==2} = c2;
		val i3:I{self.p()==2} = c; // ERR
	  }
	  // testing different receivers
	  def test3(a:I, c2:C{a.p()==2}, c:C{self.x==2}) {
		val i:I{a.p()==2} = c; // ERR (we only expand "self")
		val i2:I{a.p()==2} = c2; 
	  }
	}
}
class AbstractPropertyMethodsTests2 {
	abstract class I {
	  public abstract property p():Long;
	}
	abstract class A(x:Long) extends I {
	  def test(c:A{self.x==2}) {
		val i:I{self.p()==2} = c; // ERR
	  }
	}
	abstract class C(x:Long) extends I {
	  public property p():Long = x;
	  def test(c:C{self.x==2}) {
		val i:I{self.p()==2} = c; // ShouldNotBeERR XTENLANG-3266
	  }
	}
}
class AbstractPropertyMethodsTests3 {
	interface I {
	  public property p(a:Long,b:Long):Boolean;
	}
	abstract class C(x:Long) implements I {
	  public property p(a:Long,b:Long):Boolean = a==3&&b==4&&x==2;
	  def test(c:C{self.x==2},c2:C{self.x==3}) {
		val i1:I{self.p(3,4)} = c; // ShouldNotBeERR XTENLANG-3266
		val i2:I{self.p(2,4)} = c; // ERR
		val i3:I{self.p(3,5)} = c; // ERR
		val i4:I{self.p(3,4)} = c2; // ERR
		val i5:I{self.p(3)} = c; // ERR ERR
		val i6:I{self.p()} = c; // ERR ERR
		val i7:I{self.p(3,4,5)} = c; // ERR ERR
	  }
	}
}

class ConstraintBug_2614 { // XTENLANG-2614
	interface I {
		property p():Long;
	}
	class C implements I {
		public property p():Long = 3;
	}
	class Test {
	  def test(a:I, c:C{a.p()==2}) {
		val i:I{a.p()==2} = c;
	  }
	}
}

class XTENLANG_2615 {
	interface A {
		def m(Long):void;
	}
	class B implements A { // ERR ERR: B should be declared abstract; it does not define m(x10.lang.Long): void, which is declared in A
		public def m(Long{self!=0}):void {}
	}
	abstract class C {
		abstract def m(Long):void;
	}
	class D extends C { // ERR ERR: D should be declared abstract; it does not define m(x10.lang.Long): void, which is declared in C
		public def m(Long{self!=0}):void {}
	}
}
class TestConformanceWithAbstractPropertyMethods3 {
	interface DataPoint { // see XTENLANG-989
	   public property dim(): Long;
	   public def distanceFrom(t: DataPoint{3 == this.dim()}):void;
	   public def distanceFrom2(t: DataPoint{3 == self.dim()}):void;
	}
	class LongDataPoint implements DataPoint {
	   public property dim(): Long = 3;
	   public def distanceFrom(t: DataPoint{3 == this.dim()}):void { }
	   public def distanceFrom2(t: DataPoint{3 == self.dim()}):void {}
	}
}
class TestConformanceWithAbstractPropertyMethods3b {
	interface DataPoint {
	   public property dim(): Long;
	   public def distanceFrom(t: DataPoint{3 == this.dim()}):void;
	   public def distanceFrom2(t: DataPoint{3 == self.dim()}):void;
	}
	class LongDataPoint(dim:Long) implements DataPoint {
	   public property dim(): Long = dim;
	   public def distanceFrom(t: DataPoint{3 == this.dim()}):void { }
	   public def distanceFrom2(t: DataPoint{3 == self.dim()}):void {}
	}
}
class TestConformanceWithAbstractPropertyMethods3c {
	interface DataPoint {
	   public property dim(): Long;
	   public def distanceFrom(t: DataPoint{3 == this.dim()}):void;
	   public def distanceFrom2(t: DataPoint{3 == self.dim()}):void;
	}
	class LongDataPoint(x:Long) implements DataPoint {
	   public property dim(): Long = x;
	   public def distanceFrom(t: DataPoint{3 == this.x}):void { }
	   public def distanceFrom2(t: DataPoint{3 == self.dim}):void {}
	}
}
class TestConformanceWithAbstractPropertyMethods3dGenerics {
	interface DataPoint {
	   public property dim(): Long;
	   public def distanceFrom(t: Array[DataPoint{3 == this.dim()}]):void;
	   public def distanceFrom2(t: Array[DataPoint{3 == self.dim()}]):void;
	}
	class LongDataPoint(x:Long) implements DataPoint {
	   public property dim(): Long = x;
	   public def distanceFrom(t: Array[DataPoint{3 == this.x}]):void { }
	   public def distanceFrom2(t: Array[DataPoint{3 == self.dim}]):void {}
	}
}
class TestConformanceWithAbstractPropertyMethods4 {
	interface DataPoint {
	   public property dim(): Long;
	   public def distanceFrom3(t: DataPoint{3 == self.dim()}):void;
	}
	class LongDataPoint implements DataPoint { // ShouldNotBeERR ShouldNotBeERR (XTENLANG-2615)
	   public property dim(): Long = 3;
	   public def distanceFrom3(t: DataPoint{3 == this.dim()}):void {} // ShouldBeErr
	}
}

class XTENLANG_1914 {
	interface I {
	  property i():Long;
	}
	class A implements I {
	  public property i() = 2;
	}
	class B(x:Long) implements I {
	  public property i() = x;
	}
	class Test {
	  def test(var i1:I{self.i()==2}, var i2:I{self.i()==2}, var a1:A, var a2:A{self.i()==2}, var b1:B{self.i()==2}, var b2:B{x==2}) {
		i1 = i2; 
		i2 = i1; 
		i1 = a1; // ShouldNotBeERR XTENLANG-3266
		i1 = a2; // ShouldNotBeERR XTENLANG-3266
		i1 = b1; // ShouldNotBeERR XTENLANG-3266
		i1 = b2; // ShouldNotBeERR XTENLANG-3266
		a2 = a1; 
		a1 = a2; 
		b2 = b1; 
		b1 = b2; 
	  }
	}

	interface R {
	  public property x():Long;
	}
	class S implements R {
	  public property x()=2;
	  def test(var i:R{self.x()==2}, var b:S) {
		i = b; // ShouldNotBeERR XTENLANG-3266
	  }
	}
}

class TestTypeParamShadowing { // XTENLANG-2163
	class A {}
	class B {}
	class C {}
	class Outer[X] {X<:A} {
		class Inner[X] {X<:B} { 
			def m[X](x:X) {X<:C} : C = x;
			def m2[X](x:X) {X<:C} = new Any() {  // test anon class creation
				def q(x:X):C = x;
				def qe(x:X):B = x; // ERR
				def t[X](x:X) {X<:A} : A = x;
				def te[X](x:X) {X<:A} : C = x; // ERR
			};
			def me[X](x:X) {X<:C} : B = x; // ERR
			def test2(x:X):B = x;
			def test2e(x:X):C = x; // ERR
		}
		def test1(x:X):A = x;
		def test1e(x:X):B = x; // ERR
	}
}

class XTENLANG_2617 {
	class MySuper(supval:Long) {}
	class MyClass(myval:Long) extends MySuper{self.supval==myval} {
		public def this(i:Long) {
			super(i);
			property(i);
		}
	}
	class AssignPropertyTest(myval:Long) {
		val x:Long{self==this.myval};
		public def this(i:Long) {
			property(i);
			x = i;
		}
	}
}

class ConstrainedCall(x:Long) { // XTENLANG-2416
    def m(){x==0} = 10;
    def test() { m(); } // ERR
}

class XTENLANG_2622 {
	class Hello {
		val a:Hello{self!=null} = new Hello();
		val c:Hello{self==this.a} = null; // ERR: The type of the field initializer is not a subtype of the field type
		val d:Hello{self!=null} = c;
		val f1:Hello{self==null && self==this.a} = null; // ERR (inconsistent constraint)
	}
}
class XTENLANG_1380 {
	class Hello {
		val a:Hello{self!=null} = new Hello();
		val e:Hello{self!=null} = (true ? null : a); // ERR
		val x:Hello{self!=null} = null; // ERR
	}
}

struct XTENLANG_2022(B:Region,NBRS: Array[Point(B.rank)](1)) {}

class XTENLANG_1767 {
    static def test1(args:Array[String](1)){
		for (x in args) {
			async { break; } // ERR: Cannot break in an async
		}
	}
    static def test2(args:Array[String](1)){
		for (x in args) {
			async { continue; } // ERR: Cannot continue in an async
		}
	}
    static def test3(args:Array[String](1)){
		ll: for (x in args) {
			async { break ll; } // ERR: Cannot continue in an async
		}
	}
    static def test4(args:Array[String](1)){
		for (x in args) {
			async { return; } // ERR: Cannot return from an async.
		}
	}
    static def test5(args:Array[String](1)){
		for (x in args) {
			val y = ()=> { break; }; // ERR: Target of branch statement not found.
		}
	}
	static def excTest(args:Array[String](1)){
		try {
			finish {
				try {
					async { throw new Exception(); } // will be caught in the second catch (with "e2"). However, in the CFG it is caught in "e1"
						// but I think that's actually a conservative approximation - the current CFG says it might be caught in the first or second catch,
						// and the more accurate one says it is definitely not caught in the first catch.
						// that's why I can't build any example that will cause a bug...
				} catch (e1:Exception) {}
			} 
		} catch (e2:Exception) {}
	}
}

class DefaultCtorTests {
	class A10(i:Long) {this.i==2} {}
	class A1(i:Long) {this.i==2} {
		def this() { property(2); }
	}
	class B1(b:Long) 
		{this.i!=3} // ERR: Cannot create the default constructor because the class invariant uses self or super.
		extends A1 {} 
	class B2(b:Long) {b!=3} extends A1 {}
	class D 
		{i!=3}  // ERR: Cannot create the default constructor because the class has no properties. Move the invariant as a constraint on the relevant supertype.
		extends A1 {} 
	class D2 
		extends A1{i!=3} {} 
}
class DefaultCtorTests2 {
	interface B {
		property a():Long;
	}
	abstract class C5(x:Long) {this.a()!=x} implements B {
		property a()=1;
		def this(x:Long) {1!=x} { property(x); }
	}
	abstract class C6(x:Long) {this.a()!=x} implements B { // ERR ERR
		property a()=1;
	}
}
class TestCheckingClassInvariant {
	class B(b:Long) {
		def this() { property(1); }
	}
	class C2 
		{this.b==4} 
		extends B {
		def this() { // ERR see XTENLANG-2628
			super();
		}	
	}
	class C3 
		{this.b==4} 
		extends B {
		def this() { // ERR see XTENLANG-2628
		}	
	}
	// correct way of writing C2 is:
	class C2_correct	
		extends B{self.b==1} {
		def this() { 
			super();  
		}	
	}
	class C4 
		extends B{self.b==4} {
		def this() {  // ERR
			super(); 
		}	
	}

	class C22(c:Long) 
		{this.b==1}
		extends B {
		def this() {
			super(); 
			property(5);
		}	
	}
	class C2b(c:Long) 
		{this.b==7}
		extends B {
		def this() { // ERR
			super(); 
			property(5); // ERR
		}	
	}
}
class XTENLANG_1636 {
	class A1(i:Long) {this.i==2} {}
	class A2(i:Long) {
		def this(p:Long) {this.i==p} { // ERR (can't use "this" in the guard)
			property(3);
		}
	}
	class LongSetter0(p:Long) {this.p==1} {}
	class LongSetter1(p:Long) {f()} { // ERR ERR (too complicated to create a default ctor for this case)
		property f() = p==1;
		// if we had the time, then the auto-generated ctor would have a guard, i.e., it would look like this:
		// def this(x:Long) {x==1} {	property(x); }
	}
	class LongSetter2(p:Long) {f()} {
		property f() = p==1;
		def this(x:Long) {
			property(x); // ERR
		}
		def this(x:Long,y:Long) {x==1} {
			property(x);
		}
		def this() {
			property(1);
		}
		def this(Double) { // ERR
			property(2); // ERR
		}
	} 
}

class Offery_1582 { //XTENLANG-1582
  def this() offers Long {
    offer 81;
  }
}


class ImplicitTargetResolutionTest { // ERR
	// Expr and Call have different disambuation rules!
	static def rankTest1(a:Dist{rank==2}) {} // ok
	static def rankTest1(a:Dist{rank()==2}) {} // ERR ERR ERR 
	static def rankTest2(a:Dist{self.rank()==2}) {} // ok

	class TypeName(p:Long) {
		property p1():Boolean = p==3;

		val f1:TypeName{p1()} = null;	// defaults to "this.p1()"
		val f2:TypeName{self.p1()} = f1; // ERR
		val f3:TypeName{this.p1()} = f1;
		val f4:TypeName{this.p1()} = f2; // ERR

		def bla1(x:TypeName{p1()}) { // defaults to "this.p1()"
		  val y:TypeName{self.p1()} = x; // ERR
		  val z:TypeName{this.p1()} = x;
		  val ok:TypeName{self.p1()} = new TypeName(3);
		}
	}
	static def staticTest(x:TypeName{p1()}) { // defaults to "this.p1()" therefore we have: ERR ERR: Cannot access a non-static field method final property public TypeName.p1() from a static context.
	  val bug3:TypeName{p1()} = new TypeName(3); // ERR ERR
	}

	static def rankTest(a:Array[Long]{rank==2}) {
		val xa:Array[Long]{self.rank==2} = a;
		val ya:Array[Long]{this.rank==2} = a; // ERR ERR ERR
	}
}

class StructLCATest { // see XTENLANG-2635
	static interface Op {}
	static struct A implements Op {}
	static struct B implements Op {}
	class Test {
		val x:Rail[Op] = [A(), B()]; 
		val y:Rail[Op] = [A() as Op, B()];
		val z:Rail[Op] = [A() as Op, B() as Op];
	}
}
class ClassLCATest {
	static interface Op {}
	static class A implements Op {}
	static class B implements Op {}
	class Test {
		// LCA of A and B should be Op or Any ?
		val w:Rail[Any{self!=null}] = [new A(), new B()]; // ERR
		val x:Rail[Op{self!=null}] = [new A(), new B()]; 
		val y:Rail[Op] = [new A() as Op, new B()];
		val z:Rail[Op] = [new A() as Op, new B() as Op];
	}
}
class LCATests { // see XTENLANG-2635
	static interface Op {}
	static interface I1 {}
	static interface I2 {}
	static interface I3 {}

	static class StructTests {
		static struct S1 implements Op,I1 {}
		static struct S2 implements Op,I2 {}
		static struct S3 implements Op,I3 {}
		static struct S13 implements Op,I1,I3 {}
		static struct S23 implements Op,I2,I3 {}
		static struct S123 implements Op,I1,I2,I3 {}
		def test() {
			val x1:Rail[Op] = [new S1(), new S2()]; 
			val x2:Rail[Op] = [new S1(), new S2(), new S3()]; 
			val x3:Rail[Any] = [new S1(), new S13()]; 
			val x4:Rail[Op] = [new S1(), new S23()]; 
			val x5:Rail[Any] = [new S1(), new S123()]; 
			val x6:Rail[Any] = [new S123(), new S2()]; 
			val x7:Rail[Any] = [new S23(), new S2()]; 
			val x8:Rail[Op] = [new S13(), new S2()];
		}
	}
	static class ClassTests {
		static class S1 implements Op,I1 {}
		static class S2 implements Op,I2 {}
		static class S3 implements Op,I3 {}
		static class S13 implements Op,I1,I3 {}
		static class S23 implements Op,I2,I3 {}
		static class S123 implements Op,I1,I2,I3 {}
		def test() {
			val x1:Rail[Op{self!=null}] = [new S1(), new S2()]; 
			val x2:Rail[Op{self!=null}] = [new S1(), new S2(), new S3()]; 
			val x3:Rail[Any{self!=null}] = [new S1(), new S13()];
			val x4:Rail[Op{self!=null}] = [new S1(), new S23()]; 
			val x5:Rail[Any{self!=null}] = [new S1(), new S123()];
			val x6:Rail[Any{self!=null}] = [new S123(), new S2()];
			val x7:Rail[Any{self!=null}] = [new S23(), new S2()]; 
			val x8:Rail[Op{self!=null}] = [new S13(), new S2()];
		}
	}
	static class ClassTests2 {
		static class A {}

		static class S1 extends A implements Op {}
		static class S2 extends A implements Op {}
		def test() {
			val x1:Rail[A{self!=null}] = [new S1(), new S2()]; 
			val x2:Rail[A{self!=null}] = [new S1(), new A()]; 
		}
	}
	
	static interface GI[T] {}
	static class ClassTests3 {
		static class A {}

		static class S1 implements GI[Long] {}
		static class S2 implements GI[S1] {}
		static class S3 implements GI[S1] {}
		def test() {
			val x1:Rail[Any{self!=null}] = [new S1(), new S2()]; 
			val x2:Rail[GI[S1]{self!=null}] = [new S2(), new S3()]; 
		}
	}

}


class TestClassConformance { // XTENLANG-2509

	public static def main(args:Rail[String]) { }

	public abstract static class Matrix(M:Long, N:Long) {
		protected def this(m:Long, n:Long) = property(m, n);
		public abstract def mult(A:Matrix{self.M==this.M}, 
								 B:Matrix{self.N==this.N, self.M==A.N}):void;
	}
	
	public class ConcreteMatrix extends Matrix {
		public def this(m:Long, n:Long) = super(m, n);
		public def mult(A:Matrix{self.M==this.M}, 
						B:Matrix{self.N==this.N, self.M==A.N}) { }
	}
}

class TestFakeLocalError[T] { // I'm testing there is only one error (cause there used to be also an error from the dataflow InitChecker)
  var a:Array[T];
  static def bar3[U](){U haszero} {
      a = new Array[U](10); // ERR: Cannot access a non-static field field TestFakeLocalError.a: x10.regionarray.Array[T]{self.x10.regionarray.Array#region!=null, self.x10.regionarray.Array#rank==self.x10.regionarray.Array#region.x10.regionarray.Region#rank, self.x10.regionarray.Array#rect==self.x10.regionarray.Array#region.x10.regionarray.Region#rect, self.x10.regionarray.Array#zeroBased==self.x10.regionarray.Array#region.x10.regionarray.Region#zeroBased, self.x10.regionarray.Array#rail==self.x10.regionarray.Array#region.x10.regionarray.Region#rail} from a static context.
  }
}


class XTENLANG_2302_test {
	def mMismatchExample() {
		val a <: Rail[Boolean] = [true, false];
		val p <: Point = [1,2] as Point;

		// These are good: 
		{
		  val [a1,a2,a3] = a; // ERR: The size of the exploded Array is 2 but it should be 3
		  assert a1 && a2 && !a3;
		  // The Jira says that a needs to be Array[Boolean](3).
		  // I think it's Array[Boolean]{rank==1,size==3}
		  val [p1,p2,p3] = p; // ERR: The rank of the exploded Point is 2 but it should be 3
		  assert p1+p2==p3;
		}

		// These are good too: 
		{
		  val aa[a1,a2,a3] = a; // ERR: The size of the exploded Array is 2 but it should be 3
		  // The JIRA says that type of aa is Array[Boolean](3)
		  // I think it's Array[Boolean]{rank==1, size==3}
		  // I'm going to stop saying this.
		  val pp[p1,p2,p3] = p; // ERR: The rank of the exploded Point is 2 but it should be 3
		  // The type of pp is Point(3)
		}
	}
	def mGoodExample() {
		val a <: Rail[Boolean] = [true, true, false];
		val p <: Point = [1,2,3] as Point;

		// These are good: 
		{
		  val [a1,a2,a3] = a; @ShouldNotBeERR
		  assert a1 && a2 && !a3;
		  // The Jira says that a needs to be Array[Boolean](3).
		  // I think it's Array[Boolean]{rank==1,size==3}
		  val [p1,p2,p3] = p;
		  assert p1+p2==p3;
		}

		// These are good too: 
		{
		  val aa[a1,a2,a3] = a; @ShouldNotBeERR
		  // The JIRA says that type of aa is Array[Boolean](3)
		  // I think it's Array[Boolean]{rank==1, size==3}
		  // I'm going to stop saying this.
		  val pp[p1,p2,p3] = p;
		  // The type of pp is Point(3)
		}
	}
}

class XTENLANG_2691_test {
	def m1():Long {
		val x:String = at (here) {val y= 1; "a"};
		return 3;
	}
	def m2():Long {
		at (here) {
			if (true) return 1; // ShouldNotBeERR ShouldNotBeERR
		}
		return 43;
	}
}


class DifferentResolutionInDynamicAndStatic {
	def m(Long{self!=0}):Long = 1;
	def m(Double):Long = 2;
	def test(x:Long) {
		val z1:Long = m(x); // ERR
	}
}

class XTENLANG_1772_test {
       static def test() {
               val i1:Int{self==0s};// ERR
               val i2:Float{self==0};// ERR
               val i3:Double{self==0};// ERR
               val i4:UInt{self==0us};// ERR
               val i5:Long{self==0n};// ERR
               val i6:ULong{self==0un};// ERR
               val i7:Char{self==0};   // ERR            
               val i8:Byte{self==0};// ERR
               val i9:UByte{self==0u};// ERR
       }
}

class XTENLANG_1448 {
	class X(p:Place) {p==here} { // ERR: Cannot use "here" in this context
		def this() {
			property(here);
		}
		def test(y:X) {
			val py:Place{self==here} = y.p; // ERR: Cannot assign expression to target; constraints not satisfied
			val x = new X();
			val px:Place{self==here} = x.p;
		}
	}
	class X2(p:Place{self==here}) { // ERR: Cannot use "here" in this context
		def this() {
			property(here);
		}
		def test(y:X2) {
			val py:Place{self==here} = y.p; // ERR: Cannot assign expression to target; constraints not satisfied
			val x = new X2();
			val px:Place{self==here} = x.p;
		}
	}
	class HereAndGenerics {
		def test(l:Box[Place{self==here}]) {
			val p1:Place{self==here} = l.value;
			at (Place.places().next(here)) {
				val p2:Place{self==here} = l.value; // ERR
			}
		}
	}
	interface Test[T] {
		def add(t:T):void;
	}
	class Impl(p:Place) implements Test[Impl{self.p==here}] { // ERR: Cannot use "here" in this context ERR ERR
		public def add(t:Impl{self.p==here}):void {}
	}
}
class LegalOverloading1[U] {	
	def foo(x:Any) {}
	def foo(x:U) {}
}
class LegalOverloading2[U] {	
	def foo(x:Long) {}
	def foo(x:U) {}
}
class LegalOverloading3[S,U] {
	def foo(x:S) {}
	def foo(x:U) {}
}
class LegalOverloading4[S] {
	def foo(x:S) {}
	def foo[T](x:T) {}
}

class PropertyMethodThatIsBothTopLevelAndNested {
	interface BI {
	  property m():Boolean;
	}
	class A(p:Long) {}
	class B(a:A,b:Long) implements BI {
	  property m() = a.p==2 && b==5;
	}
	class Test {
		def x(b:BI) {
			val t1 = b instanceof BI{self.m()};
			val t2 = b instanceof BI{self.m()==false};
		}
	}
}
class XTENLANG_1729 {
    var comparator:Long;
	static class A {
		class B {
			class AscendingSubMapEntrySet {
				def test() {
					val x = comparator();
				}
				public def comparator():Long = 5;
			}
		}
	}
}

class XTENLANG_2525 {
	static class X1 { // choosing system-as over user-defined-as
		static class A(p:Long) {
			static operator (x:B) as A{p==1} = null;
			def this() { property(2); }
		}
		static class B extends A {}
		static def test(b:B) {
			val c1 = b as A; // ERR: warning: choosing system-as over user-defined-as
			val c2:A{p==1} = (// ERR(return-type)
				b as A); // ERR(warning)
		}
	}
	static class X2 { // Now A extends B (instead of B extends A)
		static class A(p:Long) extends B {
			static operator (x:B) as A{p==1} = null;
			def this() { property(2); }
		}
		static class B  {}
		static def test(b:B) {
			val c1 = b as A; // ERR: warning: choosing system-as over user-defined-as
			val c2:A{p==1} = (// ERR(return-type)
				b as A); // ERR(warning)
		}
	}
	static class X3 { // Now A and B are not related - so we choose the user-defined-as
		static class A(p:Long) {
			static operator (x:B) as A{p==1} = null;
			def this() { property(2); }
		}
		static class B {} // not: extends A
		static def test(b:B) {
			val c1 = b as A; // ok
			val c2:A{p==1} = b as A; // ok
		}
	}
	// Now with implicit casts (not explicit)	
	static class X1_implicit { // choosing system-as over user-defined-as
		static class A(p:Long) {
			static operator (x:B) : A{p==1} = null;
			def this() { property(2); }
		}
		static class B extends A {}
		static def test(b:B) {
			val c1 = b as A; // ERR: warning: choosing system-as over user-defined-as
			val c2:A{p==1} = (// ERR(return-type)
				b as A); // ERR(warning)
		}
	}
	static class X2_implicit { // Now A extends B (instead of B extends A)
		static class A(p:Long) extends B {
			static operator (x:B) : A{p==1} = null;
			def this() { property(2); }
		}
		static class B  {}
		static def test(b:B) {
			val c1 = b as A; // ERR: warning: choosing system-as over user-defined-as
			val c2:A{p==1} = (// not an err(return-type) because we can (implicitly) coerce the result of the cast (A) into A{p==1}
				b as A); // ERR(warning)
		}
	}
	static class X3_implicit { // Now A and B are not related - so we choose the user-defined-as
		static class A(p:Long) {
			static operator (x:B) : A{p==1} = null;
			def this() { property(2); }
		}
		static class B {} // not: extends A
		static def test(b:B) {
			val c1 = b as A; // ok
			val c2:A{p==1} = b as A; // ok
		}
	}
}
class XTENLANG_1851 {
	val f:Long;
	var b:Boolean;
	def this(Double) {
		throw new Exception();
	}
	def this(Char) { // ERR
		try {
			throw new Exception();
		} catch (e:Error) {}
	}
	def this(Float) { // ERR
		async throw new Exception();
	}
	def this(String) { // ERR
		finish async throw new Exception();
	}
	def this(Int) { // ERR
	}
	def this(Any) {
		if (b)
			throw new Exception();
		else
			f = 1;
	}
	def this(Long) {
		try {
			throw new Exception();
		} catch (e:Error) {}
		f = 1;
	}
	def this(Byte) {
		try {
			throw new Exception();
		} catch (e:Error) {}
		throw new Exception();
	}
	def this(UByte) {// ERR
		try {
			throw new Exception();
		} catch (e:Error) {}
		if (b) throw new Exception();
	}
}

class XTENLANG_2745 {
	class Exn extends Exception{}
	static def example() {
		try {
		}
		catch (e : Exception) {}
		catch (e : Exn) {}// ERR
	}
}
class TestResolutionOfNull {
	def m(Any):Long = 1;
	def m(String):String = "1";
	def m2(String):String = "1";
	def m2(Any):Long = 1;
	def test() {
		val x:String = m(null);
		val y:String = m2(null);
	}
}


  class GlobalRefAndSerialize2 {
      private val root:GlobalRef[GlobalRefAndSerialize2];
  	public def this() {
  		this.root = new GlobalRef[GlobalRefAndSerialize2](this); // ERR
  		val x = new GlobalRef[GlobalRefAndSerialize2](this); // ERR
      }
  }
  class GlobalRefAndSerialize3 {
      @NonEscaping private val root:GlobalRef[GlobalRefAndSerialize3];
  	public def this() {
  		this.root = new GlobalRef[GlobalRefAndSerialize3](this);
  		val x = this.root; // ERR
      }
  }
  class GlobalRefAndSerialize4 {
      @NonEscaping val root = new GlobalRef[GlobalRefAndSerialize4](this); // ERR
  }
  class GlobalRefAndSerialize5 {
      @NonEscaping private val root = new GlobalRef[GlobalRefAndSerialize5](this);
  }
  final class GlobalRefAndSerialize6 {
      @NonEscaping val root = new GlobalRef[GlobalRefAndSerialize6](this);
  }

class XTENLANG_2863 {
  static def m[T](x:T):void {}
  static def foo():void {}
  static def c() {
	m(foo()); // ERR: Semantic Error: An actual cannot have a 'void' type.
	m[void](foo()); // ERR: Semantic Error: An actual cannot have a 'void' type.
  }
}
class XTENLANG_2891 {
  public static def say(s:String) {
    x10.lang._.Console.OUT.println(s); // ERR ERR ERR
  }
}

class XTENLANG_2860 {
	def m() {
		var a:String = "a";
		a++; // ERR: Semantic Error: Operator ++ and -- can only be used on built in numerical types.
	}
}
class XTENLANG_2931 {
	interface A {
		def a():void;
	}
	interface B {
		def b():void;
	}
	class Abs {
		public def testAB[T](x:T){T<:A, T<:B} {
			x.b();
		}

		public def abs1[T](x:T){T<:Arithmetic[T]} = x-x;
		public def abs2[T](x:T){T<:Ordered[T]} = x<x;
		public def abs3[T](x:T){T haszero} = Zero.get[T]();
		public def abs[T](x:T){T<:Arithmetic[T],T<:Ordered[T], T haszero} = x < Zero.get[T]() ? Zero.get[T]() -x : x;
	}
}
class XTENLANG_2925 {
	def test2(arr:Array[Long]) {
		for ([i] in arr) {} // ERR (Semantic Error: The loop iterator is a Point whose rank is not the same as the rank of the loop domain.)
	}
	def test(p:Point, p1:Point(1), p2:Point(2)) {
		val [i] = p; // should be an error?
		val [i1] = p1;
		val [i2] = p2; // ERR
	}
}

class XTENLANG_2855 {
//	interface Throws2[T]{T <: x10.lang.CheckedThrowable} extends x10.lang.annotations.MethodAnnotation { }
	class AnnotationTest {
		def test() throws
            x10.lang.CheckedThrowable,
            Any // ShouldBeErr
        { } 
	}
}

