package futuresched.benchs.bfs;

import x10.util.Random
//import x10.util.HashMap

public class Graph {
   public var nodes: ArrayList[Node];

   public def this(nodes: ArrayList[Node]) {
      this.nodes = nodes;
   }

   // A random graph for with n nodes and mb maximum branching factor.
   public static def random(n: Int, mb: Int): Graph {
      val nRand = new Random();
      val bRand = new Random();
      val list = new ArrayList[Node]()
      for (var i: Int = 0; i < n; i++)
         list.add(new Node());

      for (var i: Int = 0; i < n; i++) {
         val node = list.get(i);
         val b = bRand.nextInt(mb + 1);
         for (var j: Int = 0; j < b; i++) {
            val node2Index = nRand.nextInt(n);
            val node2 = list.get(node2Index);
            node.addNeighbor(node2);
         }
      }
      return new Graph(list);
   }

}


