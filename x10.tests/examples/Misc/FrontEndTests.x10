package x10.yoav.tests;

import x10.compiler.*; // @Uncounted @NonEscaping @NoThisAccess
import x10.util.*;

// test object initialization (and more)

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
		at (here) f=2; // ERR: Cannot assign a value to final field f
		f=2;
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
			Console.OUT.println("HELLO");
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
		val closure = () => z++; // ERR: Can use 'this' only after 'property(...)'
		val closure2 = () => q; // ERR: Can use 'this' only after 'property(...)'	 ERR: Cannot read from field 'q' before it is definitely assigned.
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
		k=o; // ERR ERR
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
    val closure1 = () =>i; // OK, "i" is initialized here
  }
  val closure2 = () =>i; // ERR: Cannot read from field 'i' before it is definitely assigned.
  val i = 3;
}
class ClosureIsNotAWrite {
	var i:Int{self != 0}; // ERR: Semantic Error: Field 'i' was not definitely assigned.
	val closure = () =>  { i=2; } ;
}

class TestPropertiesAndFields(i:Int, j:Int) {
	def this() {
		val x = 3;
		property(x,x);
		val closure = () => i+4;
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


//public class EscapingThisTest {

class TransientTest { // The transient field '...' must have a type with a default value.
	transient val x1 = 2; // ERR (because the type is infered to be Int{self==2}
	transient val x2:Int = 2;
	transient var y:Int;
	transient var y2:Int{self==3} = 3; // ERR
	transient var y3:Int{self!=0}; // ERR
	transient var y4:Int{self==0}; 
	def this() {
		y3 = 4;
	}
}
		
class XTENLANG_1643 {
  var i:Int{self!=0};
  def this(j:Int{self!=0}) { i = j; }
}


final class ClosureTest57 {
	val z = 1;
	val c1 = () => z+1;
	var x:Int{self!=0} = 1;
	val c2 = () => { 
		x=3; 
		return x+1; 
	};
	var y:Int{self!=0}; // ERR: Field 'y' was not definitely assigned.
	val c3 = () => { 
		y=3; 
		return y+1; // ERR: Cannot read from field 'y' before it is definitely assigned. (even though "y" was assigned before, I do not do flow-analysis within the closures)
	};
	
	val c4 = () => w; // ERR: Cannot read from field 'w' before it is definitely assigned.
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
            		+D.this.a() // ERR: The method call reads from field 'q' before it is definitely assigned.
            		+a()  // ERR: The method call reads from field 'q' before it is definitely assigned.
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
  val q = this.setW.();
  
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
		@NonEscaping def useError(i:Int):Void {} // ERR: A @NonEscaping method must be private or final.	
		@NonEscaping final def use(i:Int):Void {} 
		@NonEscaping private def useOk2(i:Int):Void {} 
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
		@NoThisAccess def calcSize(x:Int):Int { // ERR: You cannot use 'this' or 'super' in a method annotated with @NoThisAccess
			use(w); 
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

class TestGlobalRefRestriction {
    private var k:GlobalRef[TestGlobalRefRestriction] = GlobalRef[TestGlobalRefRestriction](this);
    private val z = GlobalRef[TestGlobalRefRestriction](this); 
	val z2 = GlobalRef[TestGlobalRefRestriction](this); // ERR (must be private)
	var f:GlobalRef[TestGlobalRefRestriction];
	val q1 = k; // ERR (can't use GlobalRef(this) )
	val w1 = GlobalRef[GlobalRef[TestGlobalRefRestriction]](this.k); // ERR (can't use "k" cause it is a GlobalRef(this) )
	var other:TestGlobalRefRestriction = null;
	val q2 = other.k;
	val w2 = GlobalRef[GlobalRef[TestGlobalRefRestriction]](other.k);

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
	@NonEscaping final def foo() {
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


    // We allow default values for:
    //    * a type that can be null  (e.g., Any, closures, but not a struct or Any{self!=null} )
    //    * primitive/basic structs  (user defined structs do not have a default).
    // includes: Int, Long, ULong, UInt, Float, Double, Boolean, Char
    // excludes: Short,UShort,Byte,UByte  (because we do not have a literal of that type, there is a jira opened for Charles)
class SimpleUserDefinedStructTest {
	static struct S {
	  val x:int = 4;
	  val y:int = 0;
	}

	static class C {
	  var s:S; // ERR: Field 's' was not definitely assigned.
	}
}
struct UserDefinedStruct {}
class TestFieldsWithoutDefaults[T] {
	// generic parameter test
	var f2:T; // ERR

	// includes: Int, Long, ULong, UInt, Float, Double, Boolean, Char
	// excludes: Short,UShort,Byte,UByte
	// primitive private struct tests (when we'll add literals for Short,UShort,Byte,UByte, I should add more tests)
	var i1:Int;
	var i2:Int{self==0};
	var i3:Int{self!=1};
	var i4:Int{self!=0}; // ERR
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
	// todo: do tests for Byte, UByte, Short, UShort

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
	var s1:UserDefinedStruct; // ERR

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
	@NonEscaping final def apply(that:EscapingCtorTest):EscapingCtorTest = null;

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
    new I(){}; // ERR: <anonymous class> should be declared abstract; it does not define i(): x10.lang.Void, which is declared in Possel811.I
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

class A564[T,U] {
    def foo(x:T,y:U, z:String):void {}
}
class B564 extends A564[String,String] {
    def foo(x:String,y:String, z:String):Int { return 1; } // ERR: foo(x: x10.lang.String, y: x10.lang.String, z: x10.lang.String): x10.lang.Int in B cannot override foo(x: x10.lang.String, y: x10.lang.String, z: x10.lang.String): x10.lang.Void in A[x10.lang.String, x10.lang.String]; attempting to use incompatible return type.
	// B.foo(String,String,String) cannot override A.foo(T,U,String); attempting to use incompatible return type.  found: int required: void
}


class ReturnStatementTest {
	
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
			return 2; // ShouldNotBeERR ERR todo: we get 2 errors: Cannot return value from void method or closure.		Cannot return a value from method public static x10.lang.Runtime.$dummyAsync(): x10.lang.Void
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
		// Today's error: Method foo(y: x10.lang.Place{self==here}): x10.lang.Void in TestHereInGenericTypes{self==TestHereInGenericTypes#this} cannot be called with arguments (x10.lang.Place{self==y, _place6==y});    Invalid Parameter.
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
		var i2:I{p==1} = i; // ShouldNotBeERR
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
		val x2:OuterThisConstraint{i==3} = inner.m2(); // ShouldNotBeERR: Cannot assign expression to target.	 Expression: inner.m2()	 Expected type: OuterThisConstraint{self.i==3}	 Found type: OuterThisConstraint{self.i==A#this.i, inner!=null}
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
		val x = h+h; // ShouldNotBeERR: Local variable cannot have type x10.lang.Void
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
		def m():void { CO <: CR };
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
		for(p[i,j] in (3..4)) // ERR: Loop domain is not of expected type.  ShouldNotBeERR (duplicate IDENTICAL error message) todo: should give better error message: Loop domain is not of expected type.	 Expected type: Iterable[x10.array.Point{self.x10.array.Point#rank==2}]	 Actual type: x10.array.Region
			return p(0)+i+j;
		return 3;
	}
	def test5() {
		var r:Region = null;
		for (p in r) {}
		for (p[i] in r) {} // ERR ShouldNotBeERR (duplicate)
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
	public def apply(Any):Int = 1;
	public def apply(String):String="";

	def test(c:C) {
		val x:Int = c(1);
		val y:String = c("");
		val f:(String)=>Int = c;
		val i:Int = f("str");
	}	
}
class D[T] implements (T)=>Int {
	public def apply(T):Int = 1;
	public def apply(String):String="";

	def test(c:D[Any]) {
		val x:Int = c(1);
		val y:String = c("");
		val f:(String)=>Int = c;
		val i:Int = f("str");
	}	
}
class C2 implements (Any)=>Int, (String)=>String {
	public def apply(Any):Int = 1;
	public def apply(String):String="";

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
  val bar: (Int)=>Int = this.f.(Int);  // ERR: should we dynamically generate a new closure that checks the guard?
  def f2(x:Int{self!=0}) = 1/x;
  val bar2: (Int)=>Int = this.f2.(Int{self!=0}); // ERR
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
		val x = f2(); // err: (only if f1 is before f2): Local variable cannot have type x10.lang.Void.
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

		// todo: we need short&byte literals 
		// in HEX
		//useByte(0x80y); // ShouldNotBeErr
		//useByte(0x7fy); // ShouldNotBeErr
		//useShort(0x8000s); // ShouldNotBeErr
		//useShort(0x7fffs); // ShouldNotBeErr
		// in decimal
		//useByte(-128y); // ShouldNotBeErr
		//useByte(127y); // ShouldNotBeErr
		//useShort(-32768s); // ShouldNotBeErr
		//useShort(32767s); // ShouldNotBeErr
		
		// in HEX
		//useUByte(0x80yU); // ShouldNotBeErr
		//useUByte(0x7fUy); // ShouldNotBeErr
		//useUShort(0x8000Us); // ShouldNotBeErr
		//useUShort(0x7fffsU); // ShouldNotBeErr
		// in decimal
		//useUByte(0yu); // ShouldNotBeErr
		//useUByte(125uy); // ShouldNotBeErr
		//useUShort(0su); // ShouldNotBeErr
		//useUShort(65535us); // ShouldNotBeErr
		
		// Int & Long
		// in HEX
		useInt(0x80000000);
		useInt(0x7fffffff);
		useLong(0x8000000000000000L);
		useLong(0x7fffffffffffffffL);
		useInt(0x800000000); // ERR: Integer literal 34359738368 is out of range. (todo: better err message)
		useLong(0x80000000000000000L); // ShouldBeErr (currently in generates 0L !)
		// in decimal
		useInt(-2147483648);
		useInt(2147483647);
		useLong(-9223372036854775808l);
		useLong(9223372036854775807l);		
		useInt(-2147483649); // ERR: Integer literal -2147483649 is out of range. (todo: better err message)
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


		// todo: test constant propogation (+1, *1, *2)

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
//  float w0=0;
//	double w1=0;
//
//	byte w2=0;
//	int w3=0;
//	long w4=0;
//	short w5=0;
	var w0:Float=0;
	var w1:Double=0;

	var w2:Byte=0;
	var w3:Int=0;
	var w4:Long=0;
	var w5:Short=0;

	def test() {
		w0 = w1; // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenJavaNumerics.this.w1		 Expected type: x10.lang.Float		 Found type: x10.lang.Double)
		w0 = w2;
		w0 = w3;
		w0 = w4;
		w0 = w5;
		w1 = w0;
		w1 = w2;
		w1 = w3;
		w1 = w4;
		w1 = w5;
		w2 = w0; // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenJavaNumerics.this.w0		 Expected type: x10.lang.Byte		 Found type: x10.lang.Float)
		w2 = w1; // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenJavaNumerics.this.w1		 Expected type: x10.lang.Byte		 Found type: x10.lang.Double)
		w2 = w3; // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenJavaNumerics.this.w3		 Expected type: x10.lang.Byte		 Found type: x10.lang.Int)
		w2 = w4; // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenJavaNumerics.this.w4		 Expected type: x10.lang.Byte		 Found type: x10.lang.Long)
		w2 = w5; // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenJavaNumerics.this.w5		 Expected type: x10.lang.Byte		 Found type: x10.lang.Short)
		w3 = w0; // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenJavaNumerics.this.w0		 Expected type: x10.lang.Int		 Found type: x10.lang.Float)
		w3 = w1; // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenJavaNumerics.this.w1		 Expected type: x10.lang.Int		 Found type: x10.lang.Double)
		w3 = w2;
		w3 = w4; // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenJavaNumerics.this.w4		 Expected type: x10.lang.Int		 Found type: x10.lang.Long)
		w3 = w5;
		w4 = w0; // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenJavaNumerics.this.w0		 Expected type: x10.lang.Long		 Found type: x10.lang.Float)
		w4 = w1; // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenJavaNumerics.this.w1		 Expected type: x10.lang.Long		 Found type: x10.lang.Double)
		w4 = w2;
		w4 = w3;
		w4 = w5;
		w5 = w0; // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenJavaNumerics.this.w0		 Expected type: x10.lang.Short		 Found type: x10.lang.Float)
		w5 = w1; // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenJavaNumerics.this.w1		 Expected type: x10.lang.Short		 Found type: x10.lang.Double)
		w5 = w2;
		w5 = w3; // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenJavaNumerics.this.w3		 Expected type: x10.lang.Short		 Found type: x10.lang.Int)
		w5 = w4; // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenJavaNumerics.this.w4		 Expected type: x10.lang.Short		 Found type: x10.lang.Long)
	}
}
class LegalCoercionsBetweenAllNumerics {	
	var w0:Float=0;
	var w1:Double=0;

	var w2:Byte=0;
	var w3:Int=0;
	var w4:Long=0;
	var w5:Short=0;

	var w6:UByte=0;
	var w7:UInt=0;
	var w8:ULong=0;
	var w9:UShort=0;

	def test() {
		w0 = w1;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.w1		 Expected type: x10.lang.Float		 Found type: x10.lang.Double)
		w0 = w2;
		w0 = w3;
		w0 = w4;
		w0 = w5;
		w0 = w6;
		w0 = w7;
		w0 = w8;
		w0 = w9;
		w1 = w0;
		w1 = w2;
		w1 = w3;
		w1 = w4;
		w1 = w5;
		w1 = w6;
		w1 = w7;
		w1 = w8;
		w1 = w9;
		w2 = w0;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.w0		 Expected type: x10.lang.Byte		 Found type: x10.lang.Float)
		w2 = w1;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.w1		 Expected type: x10.lang.Byte		 Found type: x10.lang.Double)
		w2 = w3;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.w3		 Expected type: x10.lang.Byte		 Found type: x10.lang.Int)
		w2 = w4;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.w4		 Expected type: x10.lang.Byte		 Found type: x10.lang.Long)
		w2 = w5;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.w5		 Expected type: x10.lang.Byte		 Found type: x10.lang.Short)
		w2 = w6;
		w2 = w7;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.w7		 Expected type: x10.lang.Byte		 Found type: x10.lang.UInt)
		w2 = w8;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.w8		 Expected type: x10.lang.Byte		 Found type: x10.lang.ULong)
		w2 = w9;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.w9		 Expected type: x10.lang.Byte		 Found type: x10.lang.UShort)
		w3 = w0;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.w0		 Expected type: x10.lang.Int		 Found type: x10.lang.Float)
		w3 = w1;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.w1		 Expected type: x10.lang.Int		 Found type: x10.lang.Double)
		w3 = w2;
		w3 = w4;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.w4		 Expected type: x10.lang.Int		 Found type: x10.lang.Long)
		w3 = w5;
		w3 = w6;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.w6		 Expected type: x10.lang.Int		 Found type: x10.lang.UByte)
		w3 = w7;
		w3 = w8;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.w8		 Expected type: x10.lang.Int		 Found type: x10.lang.ULong)
		w3 = w9;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.w9		 Expected type: x10.lang.Int		 Found type: x10.lang.UShort)
		w4 = w0;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.w0		 Expected type: x10.lang.Long		 Found type: x10.lang.Float)
		w4 = w1;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.w1		 Expected type: x10.lang.Long		 Found type: x10.lang.Double)
		w4 = w2;
		w4 = w3;
		w4 = w5;
		w4 = w6;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.w6		 Expected type: x10.lang.Long		 Found type: x10.lang.UByte)
		w4 = w7;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.w7		 Expected type: x10.lang.Long		 Found type: x10.lang.UInt)
		w4 = w8;
		w4 = w9;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.w9		 Expected type: x10.lang.Long		 Found type: x10.lang.UShort)
		w5 = w0;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.w0		 Expected type: x10.lang.Short		 Found type: x10.lang.Float)
		w5 = w1;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.w1		 Expected type: x10.lang.Short		 Found type: x10.lang.Double)
		w5 = w2;
		w5 = w3;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.w3		 Expected type: x10.lang.Short		 Found type: x10.lang.Int)
		w5 = w4;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.w4		 Expected type: x10.lang.Short		 Found type: x10.lang.Long)
		w5 = w6;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.w6		 Expected type: x10.lang.Short		 Found type: x10.lang.UByte)
		w5 = w7;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.w7		 Expected type: x10.lang.Short		 Found type: x10.lang.UInt)
		w5 = w8;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.w8		 Expected type: x10.lang.Short		 Found type: x10.lang.ULong)
		w5 = w9;
		w6 = w0;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.w0		 Expected type: x10.lang.UByte		 Found type: x10.lang.Float)
		w6 = w1;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.w1		 Expected type: x10.lang.UByte		 Found type: x10.lang.Double)
		w6 = w2;
		w6 = w3;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.w3		 Expected type: x10.lang.UByte		 Found type: x10.lang.Int)
		w6 = w4;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.w4		 Expected type: x10.lang.UByte		 Found type: x10.lang.Long)
		w6 = w5;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.w5		 Expected type: x10.lang.UByte		 Found type: x10.lang.Short)
		w6 = w7;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.w7		 Expected type: x10.lang.UByte		 Found type: x10.lang.UInt)
		w6 = w8;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.w8		 Expected type: x10.lang.UByte		 Found type: x10.lang.ULong)
		w6 = w9;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.w9		 Expected type: x10.lang.UByte		 Found type: x10.lang.UShort)
		w7 = w0;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.w0		 Expected type: x10.lang.UInt		 Found type: x10.lang.Float)
		w7 = w1;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.w1		 Expected type: x10.lang.UInt		 Found type: x10.lang.Double)
		w7 = w2;
		w7 = w3;
		w7 = w4;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.w4		 Expected type: x10.lang.UInt		 Found type: x10.lang.Long)
		w7 = w5;
		w7 = w6;
		w7 = w8;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.w8		 Expected type: x10.lang.UInt		 Found type: x10.lang.ULong)
		w7 = w9;
		w8 = w0;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.w0		 Expected type: x10.lang.ULong		 Found type: x10.lang.Float)
		w8 = w1;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.w1		 Expected type: x10.lang.ULong		 Found type: x10.lang.Double)
		w8 = w2;
		w8 = w3;
		w8 = w4;
		w8 = w5;
		w8 = w6;
		w8 = w7;
		w8 = w9;
		w9 = w0;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.w0		 Expected type: x10.lang.UShort		 Found type: x10.lang.Float)
		w9 = w1;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.w1		 Expected type: x10.lang.UShort		 Found type: x10.lang.Double)
		w9 = w2;
		w9 = w3;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.w3		 Expected type: x10.lang.UShort		 Found type: x10.lang.Int)
		w9 = w4;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.w4		 Expected type: x10.lang.UShort		 Found type: x10.lang.Long)
		w9 = w5;
		w9 = w6;
		w9 = w7;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.w7		 Expected type: x10.lang.UShort		 Found type: x10.lang.UInt)
		w9 = w8;  // ERR (Semantic Error: Cannot assign expression to target.		 Expression: LegalCoercionsBetweenAllNumerics.this.w8		 Expected type: x10.lang.UShort		 Found type: x10.lang.ULong)
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
