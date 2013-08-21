package futuresched.benchs.bfs;

import x10.util.ArrayList;

public class SeqNode {

   public var no: Int;

//   public var parent: SFuture[Node];
   public var parent: SeqNode = null;

   public var neighbors: ArrayList[SeqNode] = new ArrayList[SeqNode]();

   public def this(no: Int) {
      this.no = no;
      this.parent = null;
   }

   public def addNeighbor(node: SeqNode) {
      neighbors.add(node);
   }

   public def degree(): Int {
      return neighbors.size() as Int;
   }

   public def contains(n: SeqNode): Boolean {
      return neighbors.contains(n);
   }

}


