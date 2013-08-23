package futuresched.benchs.bfs;

import futuresched.core.*;


public class Main {

	public static def main(Rail[String]) {
      FTask.init();

      var n: Int;      // Number of nodes
      var b: Int;      // Max branching factor
      var p: Boolean;  // Print
//      p = true;
      p = false;

//      n = 10;
//      b = 3;
//      test(n, b, p);
//
//      n = 20;
//      b = 7;
//      test(n, b, p);
//
//      n = 100;
//      b = 15;
//      test(n, b, p);
//
      n = 200;
      b = 40;
      test(n, b, p);

      n = 2000;
      b = 100;
      test(n, b, p);

      n = 10000;
      b = 500;
      test(n, b, p);

   }

   public static def test(n: Int, b: Int, p: Boolean) {

      var t1: Long;
      var t2: Long;
      var s: String;

      Console.OUT.println("========================================");
      Console.OUT.println("n: " + n);
      Console.OUT.println("b: " + b);
      Console.OUT.println("----------------------------------------");
      Console.OUT.println("Seq: ");

      val sGraph = SeqGraph.random(n, b);
      t1 = System.currentTimeMillis();
      SeqBFS.bfs(sGraph, sGraph.nodes(0));
      t2 = System.currentTimeMillis();

      if (p) {
         s = sGraph.toString();
         Console.OUT.println("Graph: ");
         Console.OUT.print(s);
         Console.OUT.println("");
         s = sGraph.toStringParents();
         Console.OUT.println("Parents: ");
         Console.OUT.print(s);
      }
      Console.OUT.println("Time = " + (t2 - t1));

      Console.OUT.println("----------------------------------------");
      Console.OUT.println("Future: ");

      val fGraph = FutGraph.random(n, b);
      t1 = System.currentTimeMillis();
      FutBFS.bfs(fGraph, fGraph.nodes(0));
      t2 = System.currentTimeMillis();

      if (p) {
         s = fGraph.toString();
         Console.OUT.println("Graph: ");
         Console.OUT.print(s);
         Console.OUT.println("");
         s = fGraph.toStringParents();
         Console.OUT.println("Parents: ");
         Console.OUT.print(s);
      }
      Console.OUT.println("Time = " + (t2 - t1));
      Console.OUT.println("========================================");

   }

}


