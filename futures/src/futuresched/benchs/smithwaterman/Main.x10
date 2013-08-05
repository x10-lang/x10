package futuresched.benchs.smithwaterman;

import futuresched.core.*;

public class Main {
	public static def main(Rail[String]) {

	   FTask.init();

//      val i = 2;
//      val j = 2;
//      val i = 4;
//      val j = 4;
//      val i = 7;
//      val j = 7;
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
      Console.OUT.println("");

//      t1 = System.currentTimeMillis();
//      v = SmithWatermanRec.m(i, j);
//      t2 = System.currentTimeMillis();
//      Console.OUT.println("Rec: " + v);
//      Console.OUT.println("Time = " + (t2 - t1));
//      Console.OUT.println("");

      t1 = System.currentTimeMillis();
      v = SmithWatermanDyn.m(i, j);
      t2 = System.currentTimeMillis();
      Console.OUT.println("Dyn: " + v);
      Console.OUT.println("Time = " + (t2 - t1));
      Console.OUT.println("");

      t1 = System.currentTimeMillis();
      v = SmithWatermanFut1.m(i, j);
      t2 = System.currentTimeMillis();
      Console.OUT.println("Future1: " + v);
      Console.OUT.println("Time = " + (t2 - t1));
      Console.OUT.println("");

      t1 = System.currentTimeMillis();
	   v = SmithWatermanFut2.m(i, j);
	   t2 = System.currentTimeMillis();
	   Console.OUT.println("Future2: " + v);
      Console.OUT.println("Time = " + (t2 - t1));
   }
}

