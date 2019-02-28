package futuresched.benchs.pagerankdelta;


import x10.util.Random;
import x10.util.ArrayList;


public class Graph {

   public var nodes: ArrayList[Node];

   public def this(nodes: ArrayList[Node]) {
      this.nodes = nodes;
   }

   public def nodeCount(): Int {
      return nodes.size() as Int;
   }
   // A random connected undirected graph with n nodes and mb maximum branching factor.
   public static def random(n: Int, mb: Int): Graph {
      val list = new ArrayList[Node]();
      for (var i: Int = 0n; i < n; i++)
         list.add(new Node(i));

      for (var i: Int = n - 1n; i >= 1n ; i--) {
         val node1 = list.get(i);
         val node2 = list.get(i-1);
         node2.addOutNeighbor(node1);
         node1.addInNeighbor(node2);
      }

      val nRand = new Random();
      val bRand = new Random();

      var t: Int = 0n;
      for (var i: Int = 0n; i < n; i++) {
         val node1 = list.get(i);
         val b = 1n + bRand.nextInt(mb);
         var d1: Int = node1.outDegree();
         t = 0n;
         while (d1 < b) {
            val node2Index = nRand.nextInt(n);
            val node2 = list.get(node2Index);
            if ((node1 != node2 &&
                 !node1.containsOut(node2) && !node2.containsOut(node1) &&
                 node2.outDegree() < mb) || t == 10n) {
               node1.addOutNeighbor(node2);
               node2.addInNeighbor(node1);
               d1 = node1.outDegree();
            } else
               t = t + 1n;
         }
      }

      return new Graph(list);
   }

   public def toString(): String {

      var s: String = "digraph {\n";
      val iter = nodes.iterator();
      while (iter.hasNext()) {
         val node = iter.next();
         //s += node.no + ": ";
         val iter2 = node.outNeighbors.iterator();
         while (iter2.hasNext()) {
            val node2 = iter2.next();
            s += "\t" + node.no + " -> " + node2.no + ";\n";
         }
         //s += "\n";
      }
      s += "}\n";
      return s;
   }

   public def toStringRanks(): String {

      var s: String = "";
      var sum: Double = 0n;
      val iter = nodes.iterator();
      while (iter.hasNext()) {
         val node = iter.next();
         //s += node.no + ": ";
         s += "\t" + node.no + ":\n";
//         s += "\t\t" + node.prevRank + "\n";
         s += "\t\t" + node.currRank + "\n";
//         s += "\t\t" + node.delta.get() + "\n";
         sum += node.currRank;
      }
      s += "Sum: " + sum + "\n";
      s += "Normalized:\n";
      val iter2 = nodes.iterator();
      while (iter2.hasNext()) {
         val node = iter2.next();
         //s += node.no + ": ";
         s += "\t" + node.no + ":\n";
//         s += "\t\t" + node.prevRank + "\n";
         s += "\t\t" + (node.currRank/sum) + "\n";
//         s += "\t\t" + node.delta.get() + "\n";
      }
      return s;
   }

}





