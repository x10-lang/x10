package futuresched.benchs.bellmanford;

import futuresched.core.*;

public class Main {

	public static def main(Rail[String]) {
      FTask.init();

      var n: Int;      // Number of nodes
      var b: Int;      // Max branching factor
      var w: Int;      // Max weight
      var p: Boolean;  // Print
      p = false;
      w = 10;

      n = 10;
      b = 3;
      test(n, b, w, p);

      n = 20;
      b = 7;
      test(n, b, w, p);

      n = 100;
      b = 15;
      test(n, b, w, p);

//      n = 200;
//      b = 40;
//      test(n, b, w, p);
//
//      n = 2000;
//      b = 100;
//      test(n, b, w, p);
//
//      n = 10000;
//      b = 500;
//      test(n, b, w, p);

   }

   public static def test(n: Int, b: Int, w: Int, p: Boolean) {

      var t1: Long;
      var t2: Long;
      var s: String;

      Console.OUT.println("========================================");
//      Console.OUT.println("n: " + n);
//      Console.OUT.println("b: " + b);
//      Console.OUT.println("w: " + w);
//      Console.OUT.println("----------------------------------------");
//      Console.OUT.println("Seq: ");
//
//      t1 = System.currentTimeMillis();
//      val sGraph = SeqGraph.random(n, b, w);
//      SeqBellmanFord.compute(sGraph, sGraph.nodes(0));
//      t2 = System.currentTimeMillis();
//
//      if (p) {
//         s = sGraph.toString();
//         Console.OUT.println("Graph: ");
//         Console.OUT.print(s);
//         Console.OUT.println("");
//      }
//      Console.OUT.println("Time = " + (t2 - t1));
//
//      Console.OUT.println("----------------------------------------");
      Console.OUT.println("Future: ");

      t1 = System.currentTimeMillis();
      val fGraph = FutGraph.random(n, b, w);
      FutBellmanFord.compute(fGraph, fGraph.nodes(0));
      t2 = System.currentTimeMillis();

      if (p) {
         s = fGraph.toString();
         Console.OUT.println("Graph: ");
         Console.OUT.print(s);
         Console.OUT.println("");
      }
      Console.OUT.println("Time = " + (t2 - t1));
      Console.OUT.println("========================================");

   }

}

