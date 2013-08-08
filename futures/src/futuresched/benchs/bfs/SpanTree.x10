package futuresched.benchs.bfs;

import x10.util.Random
//import x10.util.HashMap

public class SpanTree {
   public var nodes: ArrayList[Node];

   public def this(nodes: ArrayList[Node]) {
      this.nodes = nodes;
   }

   public static def spanningTree(g: Graph, n: Node) {
      finish {
         val nodes = g.nodes;
         val iter = nodes.iterator();
         while (iter.hasNext()) {
            val node = iter.next();
            FTask.asyncWaitOr(node.neighbors, ()=> {
               node.parent.set(pNode);
            });
         }
         n.parent.set(null);
      }
   }

}




