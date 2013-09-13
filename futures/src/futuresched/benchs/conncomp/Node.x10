package futuresched.benchs.pagerankdelta;

import x10.util.ArrayList;
import x10.util.concurrent.AtomicDouble;



public class Node {

   public var no: Int;

   public var oddPhase: Boolean = true;

   public var currShares = new AtomicDouble();
   public var prevShares: Double;
   public var currRank: Double;
   public var prevRank: Double;

   public var neighbors: ArrayList[Node] = new ArrayList[Node]();

   public def this(no: Int) {
      this.no = no;
   }

   public def addNeighbor(node: Node) {
      neighbors.add(node);
   }

   public def degree(): Int {
      return neighbors.size() as Int;
   }

   public def contains(n: FutNode): Boolean {
      return neighbors.contains(n);
   }

}


