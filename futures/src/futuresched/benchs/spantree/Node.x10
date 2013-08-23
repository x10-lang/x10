package futuresched.benchs.spantree;

import x10.util.ArrayList;
import x10.util.Pair;
import x10.util.Box;
import futuresched.core.*;



public class Node {

   public var no: Int;

//   public var parent: SFuture[Node];
   public var parent: SFuture[Node];

   public var neighbors: ArrayList[Node] = new ArrayList[Node]();

   public def this(no: Int) {
      this.no = no;
      this.parent = new SFuture[Node]();
   }

   public def addNeighbor(node: Node) {
      neighbors.add(node);
   }

   public def degree(): Int {
      return neighbors.size() as Int;
   }

   public def contains(n: Node): Boolean {
      return neighbors.contains(n);
   }

}

