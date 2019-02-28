package futuresched.benchs.pagerankdelta;

import futuresched.core.*;


public class Main {

	public static def main(Rail[String]) {
      FTask.init();

      var gamma: Double;
      var sigma: Double;

      var n: Int;      // Number of nodes
      var b: Int;      // Max branching factor
      var p: Boolean;  // Print
      p = true;
//      p = false;

      gamma = 0.85;
//      gamma = 1;
      sigma = 0.001;
//      sigma = 0.00000000001;

      n = 10;
      b = 3;
//      n = 4;
//      b = 2;
      test(n, b, gamma, sigma, p);

//      n = 20;
//      b = 7;
//      test(n, b, gamma, sigma, p);

//      n = 100;
//      b = 15;
//      test(n, b, p);


//      n = 200;
//      b = 40;
//      test(n, b, p);
//
//      n = 2000;
//      b = 100;
//      test(n, b, p);
//
//      n = 10000;
//      b = 500;
//      test(n, b, p);

   }

   public static def test(n: Int, b: Int, gamma: Double, sigma: Double, p: Boolean) {

      var t1: Long;
      var t2: Long;
      var s: String;

      Console.OUT.println("========================================");
      Console.OUT.println("n: " + n);
      Console.OUT.println("b: " + b);
//      Console.OUT.println("----------------------------------------");
//      Console.OUT.println("Seq: ");
//
//      val sGraph = SeqGraph.random(n, b);
//      t1 = System.currentTimeMillis();
//      SeqBFS.bfs(sGraph, sGraph.nodes(0));
//      t2 = System.currentTimeMillis();
//
//      if (p) {
//         s = sGraph.toString();
//         Console.OUT.println("Graph: ");
//         Console.OUT.print(s);
//         Console.OUT.println("");
//         s = sGraph.toStringParents();
//         Console.OUT.println("Parents: ");
//         Console.OUT.print(s);
//      }
//      Console.OUT.println("Time = " + (t2 - t1));

      Console.OUT.println("----------------------------------------");
      Console.OUT.println("Future: ");

      val graph = Graph.random(n, b);

      if (p) {
         s = graph.toString();
         Console.OUT.println("Graph: ");
         Console.OUT.print(s);
         Console.OUT.println("");
      }

      t1 = System.currentTimeMillis();
      PageRank.compute(graph, gamma, sigma);
      t2 = System.currentTimeMillis();
      if (p) {
         s = graph.toStringRanks();
         Console.OUT.println("Ranks: ");
         Console.OUT.print(s);
      }
      Console.OUT.println("Time = " + (t2 - t1));
      Console.OUT.println("========================================");

   }

}


