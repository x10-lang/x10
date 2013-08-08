package futuresched.benchs.bfs;

import x10.util.Random
//import x10.util.HashMap

public class SpanTree {
   public var nodes: ArrayList[Node];

   public def this(nodes: ArrayList[Node]) {
      this.nodes = nodes;
   }

   public static def spanningTree(graph: Graph, node: Node) {
      val nodes = graph.nodes;
      val iter = nodes.iterator();
      while (iter.hasNext()) {
         val node = iter.next();
         FTask.asyncWaitOr(node.neighbors, ()=> {

         });
      }
   }

}

