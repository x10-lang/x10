package futuresched.benchs.bfs;

import futuresched.core.*;

public class Node {
   public var parent: SFuture[Node];
   public var neighbors: ArrayList[Node] = new ArrayList[Node]();

   public def addNeighbor(node: Node) {
      neighbors.add(node);
   }
}

