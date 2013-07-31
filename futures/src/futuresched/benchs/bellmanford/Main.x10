package futuresched.benchs.bellmanford;

import x10.util.concurrent.AtomicInteger;

public class Main {

	public static def main(Rail[String]) {
    val i = 10;
    val j = 10;

	  val v = SmithWaterman1.m(i, j);
	  //val v = SmithWaterman2.m(i, j);
	  Console.OUT.println("M(" + i + ", " + j + ") = " + v);

	}


}


