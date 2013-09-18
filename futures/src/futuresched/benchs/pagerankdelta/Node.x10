package futuresched.benchs.pagerankdelta;

import x10.util.ArrayList;
import x10.util.concurrent.AtomicDouble;
import futuresched.core.*;


public class Node {

   public var no: Int;

   public var inNeighbors: ArrayList[Node] = new ArrayList[Node]();
   public var outNeighbors: ArrayList[Node] = new ArrayList[Node]();

   public var rank: Double;
   public var delta: SUDoubleFuture;

   public def this(no: Int) {
      this.no = no;
      this.delta = new SUDoubleFuture();
   }

   public def nodeCount(): Int {
      return inNeighbors.size() as Int;
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