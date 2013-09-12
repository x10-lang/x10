package futuresched.benchs.conncomp;

import x10.util.ArrayList;
import x10.util.Pair;
import x10.util.Box;
import futuresched.core.*;



public class FutNode {

   public var no: Int;

//   public var parent: SFuture[Node];
   public var comp: SIntFuture;

   public var neighbors: ArrayList[FutNode] = new ArrayList[FutNode]();

   public def this(no: Int) {
      this.no = no;
      this.comp = new SIntFuture();
//      this.parent = new SFuture[Box[Pair[FutNode, FutNode]]]();
   }

   public def addNeighbor(node: FutNode) {
      neighbors.add(node);
   }

   public def degree(): Int {
      return neighbors.size() as Int;
   }

   public def contains(n: FutNode): Boolean {
      return neighbors.contains(n);
   }

}


