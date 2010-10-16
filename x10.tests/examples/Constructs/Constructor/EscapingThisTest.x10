package x10.yoav.tests;

import x10.compiler.*; // @Uncounted @NonEscaping @NoThisAccess
import x10.util.*;

// test object initialization (and more)

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
	def this(b:Boolean) { // ERR: Final field "p" might have already been initialized
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
	def this(i:Int) { // ERR: Final field "y" might have already been initialized
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
	def this(x:Float) {
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
	def this(i:String) { // ERR: property(...) might not have been called
		async property(1,2); // ERR: A property statement may only occur in the body of a constructor.   (todo: err could be improved)
	}
	def this(b:Boolean) { 
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
	interface Covariant[+T] {
		def get():T;
		def set(t:T):void; // ShouldBeErr
	}
	interface Contravariant[-T] {
		def get():T; // ShouldBeErr
		def set(t:T):void; 
	}
	interface Invariant[T] {
		def get():T;
		def set(t:T):void;
	}

	// check extends
	interface E1[+T] extends Covariant[T] {}
	interface E2[-T] extends Covariant[T] {} // ShouldBeErr
	interface E3[+T] extends Contravariant[T] {} // ShouldBeErr
	interface E4[-T] extends Contravariant[T] {} 
	interface E5[+T] extends 
		Contravariant[T], // ShouldBeErr
		Covariant[T] {} 
	interface E6[-T] extends 
		Contravariant[T],
		Covariant[T] {} // ShouldBeErr
	interface E7[T] extends Contravariant[T],Covariant[T] {}
	interface E8[+T] extends Contravariant[Contravariant[T]] {}
	interface E9[-T] extends Contravariant[Contravariant[
		T // ShouldBeErr (error should be on the use of T): "Cannot use contravariant type parameter T in a covariant position"
		]] {} 
	interface E10[-T] extends Invariant[T] {} // ShouldBeErr: "Cannot use contravariant type parameter T in an invariant position"

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

		def err1():CR; // ShouldBeErr
		def err2(CO):void; // ShouldBeErr
		def err3():Contravariant[CO]; // ShouldBeErr
		def err4():Covariant[CR]; // ShouldBeErr
		def err5(GenericsAndVariance[CO,IN,IN]):void; // ShouldBeErr
		def err6(GenericsAndVariance[IN,CR,IN]):void; // ShouldBeErr
		def err7(GenericsAndVariance[IN,IN,CO]):void; // ShouldBeErr
		def err8(GenericsAndVariance[IN,IN,CR]):void; // ShouldBeErr
		def err9():GenericsAndVariance[CR,IN,IN]; // ShouldBeErr
		def err10(): ( (CO)=>void ); // ShouldBeErr
		def err11(): ( ()=>CR ); // ShouldBeErr
		def err12((CR)=>void):void; // ShouldBeErr
		def err13(()=>CO):void; // ShouldBeErr
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