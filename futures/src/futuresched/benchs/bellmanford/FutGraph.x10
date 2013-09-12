package futuresched.benchs.bellmanford;

import x10.util.Random;
import x10.util.ArrayList;


public class FutGraph {

   public var nodes: ArrayList[FutNode];

   public def this(nodes: ArrayList[FutNode]) {
      this.nodes = nodes;
   }

   // A random connected undirected graph with n nodes and mb maximum branching factor.
   public static def random(n: Int, mb: Int, w: Int): FutGraph {
      val nRand = new Random();
      val bRand = new Random();
      val wRand = new Random();

      val list = new ArrayList[FutNode]();
      for (var i: Int = 0; i < n; i++) {
         list.add(new FutNode(i));
      }

      for (var i: Int = n - 1; i >= 1 ; i--) {
         val node1 = list.get(i);
         val node2 = list.get(i-1);
         val wt = wRand.nextInt(w);
         node1.addNeighbor(node2, wt);
         node2.addNeighbor(node1, wt);
      }
      var t: Int = 0;
      for (var i: Int = 0; i < n; i++) {
         val node1 = list.get(i);
         val b = 1 + bRand.nextInt(mb);
         //Console.OUT.println("branching: " + b);
         var d1: Int = node1.degree();
         t = 0;
         while (d1 < b) {
            val node2Index = nRand.nextInt(n);
            val node2 = list.get(node2Index);
            if ((node1 != node2 && !node1.contains(node2) && node2.degree() < mb) || t == 10) {
               val wt = wRand.nextInt(w) + 1;
               node1.addNeighbor(node2, wt);
               node2.addNeighbor(node1, wt);
               d1 = node1.degree();
            }
            else
               t = t + 1;
         }
      }
      return new FutGraph(list);
   }

   public def toString(): String {

      var s: String = "graph {\n";
      val iter = nodes.iterator();
      while (iter.hasNext()) {
         val node = iter.next();
         //s += node.no + ": ";
         val iter2 = node.neighbors.iterator();
         while (iter2.hasNext()) {
            val p = iter2.next();
            val node2 = p.first;
            val w = p.second;
            if (node.no <= node2.no)
               s += "\t" + node.no + " -- " + node2.no + "[label=\"" + w + "\",weight=\"" + w + "\"];\n";
         }
         //s += "\n";
      }
      s += "}\n";
      return s;
   }

   public def toStringDist(): String {

      var s: String = "Dist\n";
      val iter = nodes.iterator();
      while (iter.hasNext()) {
         val node = iter.next();
         //s += node.no + ": ";
         s += "\t" + node.no + ": " + node.dist.get() + "\n";
      }
      return s;
   }

}





