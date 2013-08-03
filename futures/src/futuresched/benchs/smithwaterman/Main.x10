package futuresched.benchs.smithwaterman;

import futuresched.core.*;

public class Main {
	public static def main(Rail[String]) {

	   FTask.init();

//      val i = 2;
//      val j = 2;
//      val i = 9;
//      val j = 9;
//      val i = 20;
//      val j = 20;
      val i = 100;
      val j = 100;

      var t1: Long;
      var t2: Long;
      var v: Int;

      Console.OUT.println("SmithWaterman");
      Console.OUT.println("M(" + i + ", " + j + ")");

//      t1 = System.currentTimeMillis();
//      v = SmithWaterman1.seqM(i, j);
//      t2 = System.currentTimeMillis();
//      Console.OUT.println("Seq: " + v);
//      Console.OUT.println("Time = " + (t2 - t1));

      t1 = System.currentTimeMillis();
      v = SmithWaterman1.futureM(i, j);
      t2 = System.currentTimeMillis();
      Console.OUT.println("Future1: " + v);
      Console.OUT.println("Time = " + (t2 - t1));

      t1 = System.currentTimeMillis();
	   v = SmithWaterman2.futureM(i, j);
	   t2 = System.currentTimeMillis();
	   Console.OUT.println("Future2: " + v);
      Console.OUT.println("Time = " + (t2 - t1));
   }
}

