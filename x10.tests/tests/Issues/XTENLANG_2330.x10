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

import harness.x10Test;
import x10.compiler.tests.*; // err markers
import x10.regionarray.*;

/**
 * @author yoav (yzibin) 1/2011
 */

public class XTENLANG_2330 extends x10Test {
    public def run() {
		new XTENLANG_2603().test();
		TestArrayMap.test();
		XTENLANG_2370.test();
        new Helper2330(50).run(0);
        XTENLANG_2712.test();
        new DynamicCallsTest().run();
        new MethodInstanceArgTest().test();
        if (!new NestedArray_73().run()) return false;
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_2330().execute();
    }
    def testDynamicCalls(D:Dist) { // XTENLANG_2611
        for (pt:Point(2) in D) { } // ERR
    }
}




class XTENLANG_2638_desugarer
{
	public static def test()
	{
	val mShape = Region.make(1..100, 1..200);
	val mDist:Dist(2) = Dist.makeBlock(mShape);
	val mat = DistArray.make[long] (mDist, 1);
	val rhs = DistArray.make[long] (mDist, 2);

	finish for (place in mat.dist.places()) async
	{
	at (place)
	{
	for (pt:Point in mat | here)//(mat.dist | here))
	{
		val x:Long = mat(pt); // ERR: Warning: Generated a dynamic check for the method call.
		val y:Long = rhs(pt); // ERR: Warning: Generated a dynamic check for the method call.
		mat(pt) = x + y; // ERR: Warning: Generated a dynamic check for the method call.
	}
	}
	}
	}
}

class ConstrainedCall(x:Long) { // XTENLANG-2416
    def m(){x==0} = 10;
    def test() { m(); } // ERR
}
class NestedArray_73    {  // see XTENLANG-2428
	class MyElement[VT]
	{
		def this(v:VT)
		{
			value = v;
			myField = v;
		}
		val value : VT;
		public var myField :VT;
	}

	class MyArray[ET](a:Array[ET]{rank==1}, size: Long)
	{
		def this(s: Long, e: ET) : MyArray[ET] {self.size == s}
		{
			val ma = new Array[ET](s, e);
			property(ma, s);
		}
	}

	 static type ElInt = MyElement[Long];
	 static type ArInt = MyArray[ElInt];

	 static type ElIntNo123 = MyElement[Long{self != 123}];
	 static type ElArrayInt = MyElement[ArInt];
	 static type ArArrayElInt = MyArray[ElArrayInt];

	public def run():Boolean
	{
		val myElInt :ElInt        = new ElInt(123);
		val myArInt :ArInt       = new ArInt(10, myElInt);
		val myElIntAr :ElArrayInt      = new ElArrayInt(myArInt);
		val myArTestInt :ArArrayElInt    = new ArArrayElInt(30, myElIntAr);

		try
		{
			for (i in myArTestInt.a)
			{
				val outerAr = myArTestInt.a(i);
				for (j in outerAr.value.a)
				{
					val innerEl = new ElIntNo123(outerAr.value.a(j).value);  // ERR: Warning: Generated a dynamic check for the method call.
					innerEl.myField = myElInt.value; // ERR: Warning: Expression 'myElInt.value' was cast to type x10.lang.Int{self==myElInt.NestedArray_73.MyElement#value, myElInt.NestedArray_73.MyElement#value!=123}.
				}
			}
			return false;
		}
		catch(e:ClassCastException)
		{
			x10.io.Console.OUT.println("Expected exception indeed occurred: " + e.getMessage());
		}
		return true;
	}
}

class MethodInstanceArgTest  {//XTENLANG_2603
	class A(i:Long) {}
	def m(A{self.i==2}) {}
	def n(i:Long) {
		val a = new A(i);
		m(a); // ERR
	}
	def test() {
		try {
			n(3);
			Console.OUT.println("Failed");
			throw new Exception();
		} catch (ClassCastException) { }
	}
}
class XTENLANG_2370
{
    static def m[T](arr:T, p:Point){T<:Array[Long]} {
		//val z = ((x1:T,x2:Point)=>x1(x2 as Point(x1.rank)))(arr,p);
		// (x1:T,x2:Point)=>{ if (GUARD...) throw ...;  x1(x2); }
        arr(p); // ERR
    }
    static def m2[T](arr:Array[Long], p:T){T<:Point} {
		// (x1:Array[Long],x2:T)=>{ if (GUARD...) throw ...;  x1(x2); }
        arr(p); // ERR
    }
	static def fail():void { throw new Exception("test failed!"); }
	static def test() {
		m(new Array[long](3, [1,2,3]), Point.make(2));
		try { m(new Array[long](3, [1,2,3]), Point.make(2,3)); fail(); } catch (e:FailedDynamicCheckException) {}
	}
}

class XTENLANG_2603  {
	class A(i:Long) {}
	def m(A{self.i==2}) {}
	def n(i:Long) {
		val a = new A(i);
		m(a); // ERR
	}
	def test() {
		try {
			n(3);
			Console.OUT.println("Failed");
			throw new Exception();
		} catch (ClassCastException) { }
	}
}
class MyArray[T](region:Region) {
	static type MyArray[X](r:Region) = MyArray[X]{self.region==r};
	def this() { property(null); }
	public def map[S,U](dst:MyArray[S](region), src:MyArray[U](region), op:(T,U)=>S):MyArray[S](region) = null;
}
class TestArrayMap {
	static def test() {
        val testMap = new TestArrayMap(new MyArray[Double]());
		testMap.run();
		testMap.run2();
		testMap.run3();
	}

    val x:MyArray[Double];
    val y = new MyArray[Double]();
    public def this(x:MyArray[Double]) {
        this.x = x;
    }

    public def run() {
        x.map[Double,Double](y, x, (a : Double, b : Double) => a + b); // ERR
    }
    public def run2() {
        val y = new MyArray[Double]();
        x.map[Double,Double](y, x, (a : Double, b : Double) => a + b); // ERR
    }
    public def run3() {
        val x:MyArray[Double] = new MyArray[Double]();
        val y:MyArray[Double] = new MyArray[Double]();
        x.map[Double,Double](y, x, (a : Double, b : Double) => a + b); // ERR
    }
}

class DynamicCallsTest {
	def fail():void { throw new Exception("test failed!"); }
	def run() {
		new ConstrainedCall(0).test();
		try { new ConstrainedCall(1).test(); fail(); } catch (e:FailedDynamicCheckException) {}

		test(1,2);
		try { test(2,2); fail(); } catch (e:FailedDynamicCheckException) {}
		m([1,2]);
		try { m(Point.make(1)); fail(); } catch (e:ClassCastException) {}
		try { m(Point.make(1,2,3)); fail(); } catch (e:ClassCastException) {}
		Console.OUT.println("Test succeeded!");
	}
	def m(a:Long, b:Long{self==a}) {}
	def test(a:Long, b:Long) {
		m(a+1,b); // ERR
	}
	def m(p:Point) {
	  val x:Point(2) = p; // ERR
	}
}
class TestGeneric7[T] {
    def this(t:T) {}
}

struct AAA7(R:Long)
{
    private val a:Point;

    public def this (r:Long, v1:Rail[Long]{self.size==r}) {
        property(r);
        a = Point.make(v1); // ERR
    }
}

class Box77[T] {
	 val t:T;
	 def this(t:T) { this.t = t; }
}
class ABC23 {
  def test(b:Box77[Place{self==here}]) {
	at (Place.places().next(here)) {
		val p2:Place{self==here} = b.t; // ERR
	}
  }
}

class XTENLANG_2712[T](a:Long) {
	static type D=XTENLANG_2712[Long]{self.a==3};

	def this(a:Long) {
		property(a);
	}
	static def m(i:Long) {
		val v =
			new D(i); // ERR: Warning: Expression 'new C[x10.lang.Int]{self.a==3}(...)' was cast to type C[x10.lang.Int]{self.a==3}.
	}
	static def test() {
		m(3); // ok
		try { m(2);  fail(); } catch (e:FailedDynamicCheckException) {}
	}
	static def fail():void { throw new Exception("test failed!"); }
}
class Helper2330(p:Long) {
	// test inner classes (both instance & nested), inheritance, overriding, generics (for generics I just checked codegen below, not runtime behaviour)
	// new & call (with and without target/qualifier), operators

	def fail():void { throw new Exception("test failed!"); }

	def test() {
		test(1);
		try { test(0); fail(); } catch (e:FailedDynamicCheckException) {}
	}
	def test(i:Long) {
		val x = new TestGeneric7[Long{self!=0}](i); // ERR: Warning: Generated a dynamic check for the method call.
	}

	def run(z:Long) { // z is 0 at runtime (I use it to make sure the guard cannot be statically resolved)
        test();

        AAA7(1, [0 as Long]);
		try { AAA7(1, [0 as Long, 0]); fail(); } catch (e:ClassCastException) {}
	AAA7(2, [0 as Long, 0]);
		try { AAA7(2, [0 as Long]); fail(); } catch (e:ClassCastException) {} // ERR

		/////////////////////////////////////////////////////////////
		// testing method calls (that return void or non-void)
		/////////////////////////////////////////////////////////////
		val r = new Helper2330(z+51);
		// without qualifier
		m1(z+1); // ERR
		try { m1(z+42); fail(); } catch (e:FailedDynamicCheckException) {} // ERR
		// with this qualifier
		m2(z+2); // ERR
		try { m2(z+42); fail(); } catch (e:FailedDynamicCheckException) {} // ERR
		// with type qualifier
		Helper2330.m1(z+1); // ERR
		try { Helper2330.m1(z+42); fail(); } catch (e:FailedDynamicCheckException) {} // ERR
		// with expr/receiver qualifier
		r.m1(z+1); // ERR
		try { r.m1(z+42); fail(); } catch (e:FailedDynamicCheckException) {} // ERR
		r.m2(z+2); // ERR
		try { r.m2(z+42); fail(); } catch (e:FailedDynamicCheckException) {} // ERR

		m3(z+50); // ERR
		try { m3(z+42); fail(); } catch (e:FailedDynamicCheckException) {} // ERR
		r.m3(z+51); // ERR
		try { r.m3(z+42); fail(); } catch (e:FailedDynamicCheckException) {} // ERR
		// test non-void method
		m33(z+50); // ERR
		try { m33(z+42); fail(); } catch (e:FailedDynamicCheckException) {} // ERR
		r.m33(z+51); // ERR
		try { r.m33(z+42); fail(); } catch (e:FailedDynamicCheckException) {} // ERR

		// testing static and instance methods that use a guard
		val a = new A(z+52);
		A.m4(z+4); // ERR
		try { A.m4(z+42); fail(); } catch (e:FailedDynamicCheckException) {} // ERR
		a.m4(z+4); // ERR
		try { a.m4(z+42); fail(); } catch (e:FailedDynamicCheckException) {} // ERR
		a.m5(z+52); // ERR
		try { a.m5(z+42); fail(); } catch (e:FailedDynamicCheckException) {} // ERR

		// testing accessing the outer instance properties in a guard
		val b1 = new B(z+53);
		val b2 = r.new B(z+53);
		b1.m6(z+50); // ERR
		try { b1.m6(z+42); fail(); } catch (e:FailedDynamicCheckException) {} // ERR
		b2.m6(z+51); // ERR
		try { b2.m6(z+42); fail(); } catch (e:FailedDynamicCheckException) {} // ERR
		// testing overriding
		val cc1 = new C(z+53);
		val cc2 = r.new C(z+53);
		cc1.m6(z+50); // ERR
		try { cc1.m6(z+42); fail(); } catch (e:FailedDynamicCheckException) {} // ERR
		cc2.m6(z+51); // ERR
		try { cc2.m6(z+42); fail(); } catch (e:FailedDynamicCheckException) {} // ERR
		// testing dynamic dispatching
		val bc1:B = cc1;
		val bc2:B = cc2;
		bc1.m6(z+50); // ERR
		try { bc1.m6(z+42); fail(); } catch (e:FailedDynamicCheckException) {} // ERR
		bc2.m6(z+51); // ERR
		try { bc2.m6(z+42); fail(); } catch (e:FailedDynamicCheckException) {} // ERR


		/////////////////////////////////////////////////////////////
		// testing new expressions
		/////////////////////////////////////////////////////////////
		new Helper2330(z+1,z+2); // ERR
		try { new Helper2330(z+1,z+1); fail(); } catch (e:FailedDynamicCheckException) {} // ERR
		val q1 = new Helper2330(z+1,z+2); // ERR
		try { val q2 = new Helper2330(z+1,z+1); fail(); } catch (e:FailedDynamicCheckException) {} // ERR
		(()=>{return new Helper2330(z+1,z+2);})(); // ERR
		try { (()=>{return new Helper2330(z+1,z+1);})(); fail(); } catch (e:FailedDynamicCheckException) {} // ERR

		// testing creating a static nested class (without any qualifier, with type qualifier, with expr qualifier)
		new A(z+1,z+2); // ERR
		try { new A(z+1,z+1); fail(); } catch (e:FailedDynamicCheckException) {} // ERR
		new Helper2330.A(z+1,z+2); // ERR
		try { new Helper2330.A(z+1,z+1); fail(); } catch (e:FailedDynamicCheckException) {} // ERR
		//r.new A(z+1,z+2); // Semantic Error: Cannot provide a containing instance for non-inner class Helper2330.A.

		// testing creating an instance (non-static) nested class (without any qualifier, with type qualifier, with expr qualifier)
		new B(z+1,z+2); // ERR
		try { new B(z+1,z+1); fail(); } catch (e:FailedDynamicCheckException) {} // ERR
		new Helper2330.B(z+1,z+2); // ERR
		try { new Helper2330.B(z+1,z+1); fail(); } catch (e:FailedDynamicCheckException) {} // ERR
		r.new B(z+1,z+2); // ERR
		try { r.new B(z+1,z+1); fail(); } catch (e:FailedDynamicCheckException) {} // ERR
		this.new B(z+1,z+2); // ERR
		try { this.new B(z+1,z+1); fail(); } catch (e:FailedDynamicCheckException) {} // ERR

		// C's guard: (i1:Long, i2:Long) {i1!=i2, i1==p}
		val c1 = new C(z+50,z+42); // ERR
		val c2 = r.new C(z+51,z+42); // ERR
		try { new C(z+1,z+2); fail(); } catch (e:FailedDynamicCheckException) {} // ERR
		try { new C(z+50,z+50); fail(); } catch (e:FailedDynamicCheckException) {} // ERR
		try { r.new C(z+1,z+2); fail(); } catch (e:FailedDynamicCheckException) {} // ERR
		try { r.new C(z+51,z+51); fail(); } catch (e:FailedDynamicCheckException) {} // ERR
		// D's guard: (i1:Long, i2:Long) {i1!=i2, i1==p, i2==c}
		val d1 = c1. new D(z+50,z+42); // ERR
		val d2 = c2. new D(z+51,z+42); // ERR
		try { c1. new D(z+1,z+42); fail(); } catch (e:FailedDynamicCheckException) {} // ERR
		try { c1. new D(z+50,z+50); fail(); } catch (e:FailedDynamicCheckException) {} // ERR
		try { c2.new D(z+1,z+42); fail(); } catch (e:FailedDynamicCheckException) {} // ERR
		try { c2.new D(z+51,z+51); fail(); } catch (e:FailedDynamicCheckException) {} // ERR

		/////////////////////////////////////////////////////////////
		// test binary operators
		/////////////////////////////////////////////////////////////
		use(this+(z+50)); // ERR
		try { use(this+(z+42)); fail(); } catch (e:FailedDynamicCheckException) {} // ERR
		use(r+(z+51)); // ERR
		try { use(r+(z+42)); fail(); } catch (e:FailedDynamicCheckException) {} // ERR
		use(this*(z+50)); // ERR
		try { use(this*(z+42)); fail(); } catch (e:FailedDynamicCheckException) {} // ERR
		use(r*(z+51)); // ERR
		try { use(r*(z+42)); fail(); } catch (e:FailedDynamicCheckException) {} // ERR

		// test unary operators
		use(+this); // ERR
		use(-this); // ERR
		try { use(+r); fail(); } catch (e:FailedDynamicCheckException) {} // ERR
		try { use(-r); fail(); } catch (e:FailedDynamicCheckException) {} // ERR

		// implicit and explicit as (casting)
		val dd1:Helper2330 = z+5.5; // ERR
		val ss1:Helper2330 = (((z as int)+'a') as Char) as Helper2330; // ERR
		try { val dd2:Helper2330 = z+5.6; fail(); } catch (e:FailedDynamicCheckException) {} // ERR
		try { val ss2:Helper2330 = (((z as int)+'b') as Char) as Helper2330; fail(); } catch (e:FailedDynamicCheckException) {} // ERR

		// apply & set (and SettableAssign)
		this(z+50); // ERR
		r(z+51); // ERR
		this(z+50) = z+50; // ERR
		r(z+51) = z+51; // ERR

		this(z+50) += z+0; // ERR

		try { this(z+51); fail(); } catch (e:FailedDynamicCheckException) {} // ERR
		try { r(z+52); fail(); } catch (e:FailedDynamicCheckException) {} // ERR
		try { this(z+50) = z+51; fail(); } catch (e:FailedDynamicCheckException) {} // ERR
		try { this(z+51) = z+50; fail(); } catch (e:FailedDynamicCheckException) {} // ERR
		try { r(z+51) = z+52; fail(); } catch (e:FailedDynamicCheckException) {} // ERR
		try { r(z+52) = z+51; fail(); } catch (e:FailedDynamicCheckException) {} // ERR
		try { this(z+50) += z+1; fail(); } catch (e:FailedDynamicCheckException) {} // ERR
		try { this(z+51) += z+0; fail(); } catch (e:FailedDynamicCheckException) {} // ERR

		// We already handle: Call, New, Binary, Unary, SettableAssign, Cast
		// We still need to handle in the desugarer: ConstructorCall (XTENLANG_2376) , ClosureCall (XTENLANG_2329)
		// far to do: after XTENLANG_2376 is resolved, we should check runtime checks for guards for ctor calls (super&this)
		// todo: After XTENLANG_2329 is resolved, we could check guards for closure calls

		Console.OUT.println("Test succeeded!");
		return true;
	}
	def use(x:Any) {}

	def this(i:Long) {
		property(i);
	}
	def this(i1:Long, i2:Long) {i1!=i2} {
		property(42);
	}

	static def m1(x:Long) {x==1} {}
	def m2(x:Long) {x==2} {}
	def m3(x:Long) {x==p} {}
	def m33(x:Long) {x==p} = this;

	// binary ops
	static operator (x:Helper2330)+(i:long) {x.p==i} : Long = i;
	operator this * (i:Long) {p==i} : Long = i;
	// unary op
	static operator +(x:Helper2330) {x.p==50} : Long = 42;
	operator - this  {p==50} : Long = 43;

	// implicit_as
	static operator (x:Double) {x==5.5} : Helper2330= null;
	// explicit as
	static operator (x:Char) as ? {x=='a'} : Helper2330 = null;

	// apply
	operator this(i:Long) {i==p} :Long = i;
	// settable assign
	operator this(i:Long)=(v: Long) {p==i, i==v} : void {}


	static class A(a:Long) {
		def this(i:Long) {
			property(i);
		}
		def this(i1:Long, i2:Long) {i1!=i2} {
			property(42);
		}

		static def m4(x:Long) {x==4} {}
		def m5(x:Long) {x==A.this.a} {}
	}
	class B(b:Long) {
		def this(i:Long) {
			property(i);
		}
		def this(i1:Long, i2:Long) {i1!=i2} {
			property(42);
		}

		def m6(x:Long) {x==Helper2330.this.p} {} // testing access to outer instance properties
	}
	class C(c:Long) extends B {
		def m6(x:Long) {x==Helper2330.this.p} {} // testing overriding

		def this(i:Long) {
			super(i);
			property(i);
		}
		def this(i1:Long, i2:Long) {i1!=i2, i1==p}  { // testing 2 binary expressions
			super(i1);
			property(i2);
		}

		class D(d:Long) extends A {
			def this(i1:Long, i2:Long) {i1!=i2, i1==p, i2==c}  { // testing 3 binary expressions
				super(i1);
				property(i2);
			}
		}
	}

}








// Here I just test that post-compilation works on more complicated examples (but I don't check runtime behaviour)

class GenericInstantiateTest[T] {
	def test() {
		new GenericInstantiateTest[Double](null); // ShouldNotBeERR  Warning: Generated a dynamic check for the method guard.
	}
	def this(b:GenericInstantiateTest[T]) {b==null} {}
}
class NullTestInGuard(x:String) {
	def m(b:NullTestInGuard) {b.x!=null}  = 1;
	def test() {
		val z = m(null);  // ERR:  Warning: Generated a dynamic check for the method guard.
	}
	def test2():void {
		val z = m(null);  // ERR:  Warning: Generated a dynamic check for the method guard.
	}
}

class TestWithoutGenerics {
	static def m2(a:Long, b:Outer.A) {a==44, b.x==null} = 1;

	class Outer(zz:Long) {
		class A(x:String,y:Long) {
			def m0(a:Long, b:A) = 1;
			def m(a:Long, b:A) {a==y, b.x==null, zz==33}  = 1;
			def this(a:A,b:A,c:Long) {a==null && b.x!=null && c==42, zz==c} : A {
				property(null,5);
			}

			def test(i:A,j:A,k:A, w:Long, h:TestWithoutGenerics, outer:Outer) {
				val z:A = new A(k,null,3*4);  // ERR:  Warning: Generated a dynamic check for the method guard.
				val z1:A = outer.new A(k,null,3*4);  // ERR:  Warning: Generated a dynamic check for the method guard.

				// this is what the compiler generate
				val z2 = ((x0:A, x1:Long, x2:A)=> {
						if (!(x1==x0.y && x2.x==null)) throw new ClassCastException();
						return x0.m0(x1,x2);
					}) (null, w*2, i);
				val z22 = j.m(w*2,i); // ERR: Warning: Generated a dynamic check for the method guard.
				val z3 = h.m2(w*2,i);  // ERR:  Warning: Generated a dynamic check for the method guard.
				val z4 = TestWithoutGenerics.m2(w*2,i);  // ERR:  Warning: Generated a dynamic check for the method guard.
				m(w*2,this); // ERR:  Warning: Generated a dynamic check for the method guard.

				return 43;
			}
		}
	}
}
class TestWithGenerics {
	static def m2(a:Long, b:Outer.A[Double]) {a==44} = 1;

	class Outer(zz:Long) {
		class A[T](x:String,y:Long) {
			def m0(a:Long, b:A[T]) = 1;
			def m(a:Long, b:A[T]) {a==y, b.x==null, zz==33}  = 1;
			def this(a:A[A[Long]],b:A[T],c:Long) {a==null && b.x!=null && c==42, zz==c} : A[T] {
				property(null,5);
			}

			def test(i:A[Double],j:A[Double],k:A[A[Long]], w:Long, h:TestWithGenerics, outer:Outer) {
				val z:A[Double] = new A[Double](k,null,3*4);  // ERR:  Warning: Generated a dynamic check for the method guard.
			    // gives an error (see XTENLANG_2377) :val z1 = outer.new A[Double](k,null,3*4);

				// this is what the compiler generate
				val z2 = ((x0:A[Double], x1:Long, x2:A[Double])=> {
						if (!(x1==x0.y && x2.x==null)) throw new ClassCastException();
						return x0.m0(x1,x2);
					}) (null, w*2, i);
				val z22 = j.m(w*2,i); // ERR: Warning: Generated a dynamic check for the method guard.
				val z3 = h.m2(w*2,i);  // ERR:  Warning: Generated a dynamic check for the method guard.
				val z4 = TestWithGenerics.m2(w*2,i);  // ERR:  Warning: Generated a dynamic check for the method guard.
				m(w*2,this); // ERR:  Warning: Generated a dynamic check for the method guard.

				return 43;
			}
		}
	}
}
