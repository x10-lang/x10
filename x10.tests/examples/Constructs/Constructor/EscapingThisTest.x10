// test object initialization

import x10.compiler.*; // @Uncounted @NonEscaping

class InfiniteInit234 {
	var i:Int{self!=0};
	def this() {
		foo();
	}
	private def foo() = foo();
}

class AllowCallsIfNoReadNorWrite {
	class Inner(i:Int) {
		def this() {
			val w = this.foo1();
			property(4);
		}
		private def foo1() = 3 + foo2();
		private def foo2() = 3;
	}
}

class DisallowCallsIfReadOrWrite {
	class Inner(i:Int) {
		static y=5;
		var x:Int=2;
		val z:Int=3;
		def this() {
			val w = this.foo1(); // ERR: You can use 'this' before 'property(...)' to call only methods that do not read nor write any fields.
			property(4);
		}
		def this(i:Int) {
			val w = this.bar1(); // ERR: You can use 'this' before 'property(...)' to call only methods that do not read nor write any fields.
			property(4);
		}
		private def foo1() = foo2()+2;
		private def foo2() = foo3()+3;
		private def foo3() {
			x=2; // There is a write to a field in this method!
			return y;
		}
		private def bar1() = bar2()+2;
		final def bar2() {
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
		val mShape = [1..r, 1..c] as Region;
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
	def this(x:Double) { super(1); } // ERR: You must call 'property(...)' at least once	ERR: Final field "p" might not have been initialized
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
	def this(i:String) { // ERR: You must call 'property(...)' at least once	 ERR: Final field "y" might not have been initialized
	}
	def this(i:Any) { // ERR: Final field "y" might not have been initialized
		property(1);
		property(1); // ERR: You can call 'property(...)' at most once		ERR: Property "y" might already have been initialized
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



class TestPropertyCalls(p:Int) {
	def this() {} // ERR: Final field "p" might not have been initialized	ERR: You must call 'property(...)' at least once
	def this(i:Int) {
		property(1);
	}
	def this(b:Boolean) { // ERR: Final field "p" might not have been initialized
		property(1);
		property(1); // ERR: Property "p" might already have been initialized	ERR: You can call 'property(...)' at most once
	}
	def this(b:Double) { // ERR: Final field "p" might not have been initialized
		if (p==1) // ERR: must call 'property(...)' immediately after the 'super(...)' call.
			property(1);
	}
	def m() {
		property(1); // ERR: A property statement may only occur in the body of a constructor.
	}
	static def q() {
		property(1); // ERR: A property statement may only occur in the body of a constructor.
	}
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
            def a() = q+4;
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
		var size:Int;
		def this() {
			this.x = 3*4;
			size = calcSize();
		}
		@NonEscaping("x") abstract def calcSize():Int;
	}
	class Sub1 extends Super {
		@NonEscaping("x") def calcSize():Int = x*2;
	}
	class Sub2 extends Super {
		@NonEscaping("x") def calcSize():Int = x+2;
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
}


class PropertyTest(p:Int) {
	static val i = 3;
	def this() {
		property(1);
		val w = p();
		val q = p;
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
	def this() { // ERR: Field 'x' was not definitely assigned in this conprivate structor. (because assignments in non final/private @NonEscaping methods do not count, cause they might be overriden)
		super();
		q();
		f0(); // ERR: Cannot read from field 'c' before it is definitely assigned.
		c = 2;
		f0();
		setX();
		setY();
		setZ();
		f1();
		d=4;
	}

	@NonEscaping("") def setX() {
		x = 42;
	}
	@NonEscaping("") final def setZ() {
		z = 42;
	}
	final def setY() {
		y = 42;
	}

	def g():Int = 1;
	abstract @NonEscaping("a,b") def q():Int;
	@NonEscaping("b,a") def ba():Int = a+b;
	@NonEscaping("a,b,c") def f0():Int = a+b+c;
	@NonEscaping("a,c") def f1():Int = a+c;
	@NonEscaping("a,e") def e1():Int = 1; // ERR: Could not find field 'e' used in the annotation @NonEscaping.
	@NonEscaping("a,c") def e2():Int = a+b+c; // ERR: reading from "b"
}
class Sub1Test extends SuperClassTest {
	val w = 1;
	var q:Int{self!=0} = 1;
	def this(i:Int) { this(); x = y; }
	def this() {
		super();
		readD();
		g();
		f2(); // ERR: The call Sub1.this.f2() is illegal because you can only call private/final/@NonEscaping methods from a NonEscaping method (such as a conprivate structor, a field initializer, or a @NonEscaping method)
	}
	final def readD() {
		val q = d;
	}
	@NonEscaping("a,b,c") def f0():Int = a+b+c+d; // ERR: Cannot read from field 'd' before it is definitely assigned.
	@NonEscaping("a,b") def q():Int {
		f0(); // ERR: Cannot read from field 'c' before it is definitely assigned.
		readD(); // ERR: Cannot read from field 'd' before it is definitely assigned.
		super.ba();
		val t = w; // ERR: Cannot read from field 'w' before it is definitely assigned.
		return a+b;
	}
	@NonEscaping("g") def e3():Int = 1; // ERR: Could not find field 'g' used in the annotation @NonEscaping.
	@NonEscaping("w") def g():Int = 1;
	@NonEscaping("c,a") def f1():Int = 1; // ERR: You must annotate x10.lang.Int f1(...) with @NonEscaping("a,c") because it overrides a method annotated with that.
	def f2():Int = 1;
}
class Sub2Test extends Sub1Test {
	@NonEscaping("a,b") def q():Int {
		val t = w; // ERR: Cannot read from field 'w' before it is definitely assigned.
		return a+b;
	}

	def g():Int = 1; // ERR (annotation @NonEscaping must be preserved)
	@NonEscaping("a,c") def f0():Int = 1;  // ERR: You must annotate x10.lang.Int f0(...) with @NonEscaping("a,b,c") because it overrides a method annotated with that.
	def f2():Int = 1;
	def f3():Int = 1;
}


class TypeNameTest {
	val n = typeName();
}
struct TypeNameTest2 {
	val n = typeName();
}



class TestNonEscaping {
	val x = foo();

	@NonEscaping("") def f1() {} 

	@NonEscaping("") final def f5() {
		bar(); // ERR: The call TestNonEscaping.this.bar() is illegal because you can only call private/final/@NonEscaping methods from a NonEscaping method (such as a conprivate structor, a field initializer, or a @NonEscaping method)
	}
	def bar() {} 



	@NonEscaping("") final def foo() {
		this.foo2();
		return 3;
	}
	final def foo2() {
	}
}



interface BlaInterface {
	def bla():Int{self!=0};
}
class TestAnonymousClass {
	static val anonymous1 = new Object() {};
	val anonymous2 = new TestAnonymousClass() {}; // ERR
	def foo() {
		val x = new Object() {};
	}
	@NonEscaping("") final def foo2() {
		val x = new Object() {}; // ERR
	}

	val anonymous = new BlaInterface() { // ERR
		public def bla():Int{self!=0} {
			return k;
		}
	};
	val inner = new Inner(); // ERR
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
 private final def ctorLike() {
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
	@NonEscaping("k") def getK2() = 
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
	private def foo1() {
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
	final def foo() {
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
	private def bla() {
		Console.OUT.println(layout); // previously printed 0
	}
}

class Person {
  var name:String{name!=null};
  def this(name:String{name != null}) {
    setName(name);
  }
  public final def setName(name:String{name != null}) {
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
  final def setI() {
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
  final def m1() {
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
  private def m2() {
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
  final def m1() { 
    i1 = 1;
    m2();
  }
  //Read=[] SeqWrite=[i1,i2,i3] Write=[i1,i2,i3]
  private def m2() {
    i2 = 2;
    m3();
  }
  //Read=[] SeqWrite=[i1,i2,i3] Write=[i1,i2,i3]
  private def m3() {
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
  private def m1():Int = v1++;
  public final def setV2(i:Int{self!=0}) { v2 = i; }
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
  final def setV2() { v2 = 3; }
}
class IllegalExample2[T] {
  var t:T; // ERR (not initialized)
}

class SuperTest22 {
	def this() {
		foo();
	}
	final def foo() {
	}
}
class SuperCallTest extends SuperTest22 {
	def this() {
		super();
		foo(); // ERR (cannot call super methods in a conprivate structor unless annotated with @NonEscaping)
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

	private def foo(arg:TestFieldInitForwardRef):Int = 3;
}


struct UserDefinedStruct {}
class TestFieldsWithoutDefaults[T] {
	// generic parameter test
	var f2:T; // ERR

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
	// todo: do tests for Char, Byte, UByte, Short, UShort

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
	final def apply(that:EscapingCtorTest):EscapingCtorTest = null;

	final def m() {
		g();
	}
	private def g() {
		z(null);
	}
	final def z(q:EscapingCtorTest) {
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
			f(null); // ERR
			this.f(null); // ERR
			val z:Inner = null;
			z.f(z);
			z.f(this); // ERR
			bar(this); // ERR
			bar(z);
		}
		def f(inner:Inner) {}
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
  final def m1() {
    i1 = 1;
    m2();
  }
  private def m2() {
    i2 = 2;
    m3();
  }
  private def m3() {
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