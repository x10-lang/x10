package futuresched.benchs.pagerankdelta;

import x10.util.ArrayList;
import x10.util.concurrent.AtomicDouble;



public class Node {

   public var no: Int;

   public var inNeighbors: ArrayList[Node] = new ArrayList[Node]();
   public var outNeighbors: ArrayList[Node] = new ArrayList[Node]();

   public rank: Double;
   public delta: ;

   public def this(no: Int) {
      this.no = no;
   }

   public def addInNeighbor(node: Node) {
      inNeighbors.add(node);
   }

   public def addOutNeighbor(node: Node) {
      outNeighbors.add(node);
   }

   public def inDegree(): Int {
      return inNeighbors.size() as Int;
   }

   public def outDegree(): Int {
      return outNeighbors.size() as Int;
   }

}


//   public var currShares = new AtomicDouble();
//   public var prevShares: Double;
//   public var currRank: Double;
//   public var prevRank: Double;
//   public var oddPhase: Boolean = true;