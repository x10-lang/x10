package futuresched.benchs.bellmanford;

import x10.util.ArrayList;
import x10.util.Pair;
import x10.util.Box;
import futuresched.core.*;


public class FutNode {

   public var no: Int;

//   public var parent: SFuture[FutNode];
   public var dist: SIntFuture;

   public var neighbors: ArrayList[Pair[FutNode, Int]] = new ArrayList[Pair[FutNode, Int]]();

   public def this(no: Int) {
      this.no = no;
      this.dist = new SIntFuture(Int.MAX_VALUE);
   }

   public def addNeighbor(node: FutNode, w: Int) {
      neighbors.add(new Pair[FutNode, Int](node, w));
   }

   public def degree(): Int {
      return neighbors.size() as Int;
   }

   public def contains(n: FutNode): Boolean {
      val iter = neighbors.iterator();
      while (iter.hasNext()) {
         val p = iter.next();
         val node = p.first;
         if (node == n) return true;
      }
      return false;
   }

}


