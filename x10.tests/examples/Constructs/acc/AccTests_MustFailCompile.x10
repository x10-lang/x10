import x10.compiler.*; // @Uncounted @NonEscaping @NoThisAccess
import x10.compiler.tests.*; // err markers
import x10.util.*;
import x10.lang.annotations.*;
import harness.x10Test;

class IntReducer implements Reducible[Int] {
	public def zero():Int = 0;	
 	public operator this(x:Int,y:Int):Int = x+y;
}
class AnyReducer implements Reducible[Any] {
	public def zero():Any = null;	
 	public operator this(x:Any,y:Any):Any = "a";
}
class CorrectTypingTest {
	def testFormalLoop(dist:Dist) {
        finish for (p in dist.places()) {}
        finish for (acc p:Place in dist.places()) {} // ERR ERR
	}
	def m() {
		try {} catch (acc e:Exception) {} // ERR

		acc j = new IntReducer();  // ERR ERR: Cannot infer type of a mutable (non-val) variable.
		acc j3 <: Int = new IntReducer();  // ERR ERR ERR: Cannot infer type of a mutable (non-val) variable.
		acc j2:Reducible[Int] = new IntReducer();  // ERR
		acc i:Int = new IntReducer(); 
		acc copyI1:Int = i;  // ERR
		acc copyI2 = i; // ERR ERR
		i = 3;
		i = "a"; // ERR
		val x:Int = i;
		acc i2:Any = new AnyReducer();
		i2 = "a";
		i2 = 2;
		val x2:Any = i2;

		acc i3:Any = new IntReducer(); // ERR
		i3 = "a"; // it is an error because it would fail at runtime cause it will call IntReduce with a string

		acc i4:Int = new AnyReducer(); // ERR
		i4 = 6; 
		val x4:Int = i4; // it is an error because it would fail at runtime cause it would return "a"
	}
}
class MethodCallTests {
	def m() {
		acc i:Int = new IntReducer();
		val z = i+1;
		i++; // -> i = i+1; ??? 
		i = i+1; 
		x2(i); // ERR
		finish {
			val w = i; // ERR: cannot read from an accumulator in a write-only state
			x2(i+1); // ERR ERR
			x1(i); // ERR: cannot read from an accumulator in a write-only state
			x2(i);
			x2((i)); // ERR ERR: cannot read from an accumulator in a write-only state
			val j = 5;
			x1(j);
			x2(j); // ERR
			x1(j+1);
			x2(j+1); // ERR
		}
	}
	def x1(a:Int) {}
	def x2(acc a:Int) {}
	def f(acc a:Int, j:Int) {
		x1(a); // ERR
		x1(j);
		x2(a);
		x2(a+1); // ERR ERR
		x2(j); // ERR
	}
}
class NoFieldAcc {
	acc i:Int; // ERR : A field cannot be an accumulator.
	// acc def m() {} - parsing error :)
}
class AccEscapeToHeap {
	def m() {
		acc i:Int = new IntReducer();
		val closure = 
			()=>i; // ERR: cannot capture an accumulator;
		val anon = new Object() {
			def m() = i; // ERR: cannot capture an accumulator;
		};
	}
}
class AccStateChanges {
	def use(Any) {}
	def illegalInit() {
		acc i:Int; // ERR: An accumulator must have an initializer.
		i = 2;
		use(i);
	}
	def m() {
		acc i:Int = new IntReducer();
		i = 2;
		use(i);
		async {
			async {
				i = 2; // ERR: can capture an accumulator only if there is a surrounding finish
				use(i); // ERR: can capture an accumulator only if there is a surrounding finish
			}
			i = 2; // ERR: can capture an accumulator only if there is a surrounding finish
			use(i); // ERR: can capture an accumulator only if there is a surrounding finish
		}
		finish async {
			acc j:Int = new IntReducer();
			i = 2; // ok
			use(i); // ERR: cannot read from an accumulator in a write-only state
			j = 2; // ok
			use(j); // ok
			async {
				i = 2; // ok
				use(i); // ERR: cannot read from an accumulator in a write-only state
				j = 2; // ERR: can capture an accumulator only if there is a surrounding finish
				use(j); // ERR: can capture an accumulator only if there is a surrounding finish
			}
			finish {
				async {
					i = 2; // ok
					use(i); // ERR: cannot read from an accumulator in a write-only state
					j = 2; // ok
					use(j); // ERR: cannot read from an accumulator in a write-only state
				}
			}
			i = 2; // ok
			use(i); // ERR: cannot read from an accumulator in a write-only state
			j = 2; // ok
			use(j); // ok
		}
	}
	def multipleAcc(acc i:Int, j:Int) {		
		acc l1:Int = new IntReducer(), l2:Int = new IntReducer();
		use(i); // ERR
		use(j);
		use(l1);
		use(l2);
		async {
			use(i); // ERR
			use(j);
			use(l1); // ERR
			use(l2); // ERR
			i = 1;
			j = 1; // ERR ERR
			l1 = 1; // ERR
			l2 = 1; // ERR
		}
		finish async {
			use(i); // ERR
			use(j);
			use(l1); // ERR
			use(l2); // ERR
			i = 1;
			j = 1; // ERR
			l1 = 1; 
			l2 = 1; 
		}
	}
	def f(acc i:Int):Int {
		acc j:Int = new IntReducer();
		
		i = 2; // ok
		use(i); // ERR: cannot read from an accumulator in a write-only state
		j = 2; // ok
		use(j); // ok

		( ()=> (j) ) (); // ERR (cannot capture acc)
		( new Object() { val z:Int=j; } ).toString(); // ERR (cannot capture acc)
		class A2 { 
			val z:Int = j; // ERR
		};

		val jcopy1 = j; // will be Int
		val jcopy2:Int = j; 		
		acc jcopy3 = j; // ERR ERR		
		acc jcopy4:Int = j; // ERR

		// i:[1,0],j:[0,0]
		at (here.next()) async { // local can be captured by async iff local:Num and Num>0
			// i:[1,1],j:[0,1]
			i = 2; // ok
			val z = (i=2); // ok (z will be 2, not the actual value of "i" which accumulated 2.)
			use(i); // ERR: cannot read from an accumulator in a write-only state

			j = 2; // ERR: can capture an accumulator only if there is a surrounding finish
			use(j); // ERR: can capture an accumulator only if there is a surrounding finish
		}
		// i:[1,0],j:[0,0]
		at (here.next()) finish {
			// i:[2,0],j:[1,0]
			val jcopy5 = j; // ERR (cannot read from j)

			finish {
				// i:[3,0],j:[2,0]
				at (here.next()) async {
					// i:[3,1],j:[2,1]
					( ()=> (i) ) (); // ERR (cannot capture acc)
					( new Object() { val z=(i); } ).toString(); // ERR (cannot capture acc)
					class A { 
						val z = (i); // ERR
					};
					i = 2; // ok
					use(i); // ERR: cannot read from an accumulator in a write-only state
					j = 2; // ok
					use(j); // ERR: cannot read from an accumulator in a write-only state
				}
				// i:[3,0],j:[2,0]
			}		
			// i:[2,0],j:[1,0]
			async {
				// i:[2,1],j:[1,1]
				async async {}
				i = 2; // ok
				use(i); // ERR: cannot read from an accumulator in a write-only state
				j = 2; // ok
				use(j); // ERR: cannot read from an accumulator in a write-only state
			}
		}
		// i:[1,0],j:[0,0]
		i = 2; // ok
		use(i); // ERR: cannot read from an accumulator in a write-only state
		j = 2; // ok
		use(j); // ok

		if (true) 
			return i; // ERR: cannot read from an accumulator in a write-only state
		else
			return j; // ok
	}
}


public class AccTests_MustFailCompile  extends x10Test {
	public def run() : boolean = true;
	public static def main(Array[String]) {
        new AccTests_MustFailCompile().execute();
		Console.OUT.println("ok");
	}
}

/*
Example of desugaring:

class A {
  def m() {
	  val l2 = new Accumulator[Int]( new IntReducer() );
		
	  if (false) {
		  assert l2.isWriteOnly() : "check1"; // fail at runtime
		  async { // this async captured l2
			  l2.supply(2); // err: An async can capture an accumulator only if it is enclosed by a finish.
		  }
	  }
	  l2.setWriteOnly();
	  finish {
  		  assert l2.isWriteOnly() : "check2";
		  async {
			  l2.supply(4);
		  }		  
	  }
	  l2.setReadWrite();
  }
}
*/
