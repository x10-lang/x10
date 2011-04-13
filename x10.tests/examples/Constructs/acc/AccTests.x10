import x10.compiler.*; // @Uncounted @NonEscaping @NoThisAccess
import x10.compiler.tests.*; // err markers
import x10.util.*;
import x10.lang.annotations.*;
import harness.x10Test;



struct IntReducer implements Reducible[Int] {
	public def zero():Int = 0;	
 	public operator this(x:Int,y:Int):Int = x+y;
}
class FibAccumulators {
	def fib(n:Int):Int {
	  acc x:Int = new IntReducer();
	  finish fib1(n, x); // fib1 may write into x.
	  // read the value in x and return it.
	  return x;
	}
	def fib1(n:Int, acc z:Int) {
	   if (n < 2) { z=n; return; }
	   async fib1(n-1, z);
	   fib1(n-2, z);
	}
}

class Manually_Desugared_FibAccumulators {
	def fib(n:Int):Int {
	  val x:Accumulator[Int] = new Accumulator[Int](new IntReducer());
	  finish fib1(n, x); // fib1 may write into x.
	  // read the value in x and return it.
	  return x.result();
	}
	def fib1(n:Int, val x:Accumulator[Int]) {
	   if (n < 2) { x.supply(n); return; }
	   async fib1(n-1, x);
	   fib1(n-2, x);
	}
}
class CollectingFinish_Fib {
	def fib(n:Int):Int {
	  var x:Int;
	  x = finish (new IntReducer()) {
		  fib1(n); // fib1 may write into x.
	  };
	  // read the value in x and return it.
	  return x;
	}
	def fib1(n:Int) offers Int {
	   if (n < 2) { offer n; return; }
	   async fib1(n-1);
	   fib1(n-2);
	}
}


public class AccTests  extends x10Test {
	public def run() : boolean = true;
	public static def main(Array[String]) {
		val res0 = new FibAccumulators().fib(7);
		val res1 = new Manually_Desugared_FibAccumulators().fib(7);
		val res2 = new CollectingFinish_Fib().fib(7);
		Console.OUT.println(res0+" "+res1+" "+res2+" ");
		assert res0==res1 && res1==res2;
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
