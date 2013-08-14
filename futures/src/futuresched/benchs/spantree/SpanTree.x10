package futuresched.benchs.spantree;

import x10.util.ArrayList;
import x10.util.Pair;
import x10.util.Box;
import futuresched.core.FTask;

public class SpanTree {

   public static def spanningTree(g: Graph, n: Node) {

//      n.parent.set(new Box(new Pair[Node, Node](n, n)));
//      finish {
//         val nodes = g.nodes;
//         val iter = nodes.iterator();
//         while (iter.hasNext()) {
//            val node = iter.next();
//            if (node != n)
//               async {
//                  FTask.asyncWaitOr(
//                     node.neighbors,
//                     (n: Node)=> {
//                        return n.parent;
//                     },
//                     (currentParent: Box[Pair[Node, Node]])=> {
//                        val parent = currentParent().first;
//                        Console.OUT.println("Setting parent of " + node.no + " to " + parent.no + ".");
//                        node.parent.set(new Box(new Pair[Node, Node](node, parent)));
//                     }
//                  );
//               }
//         }
//      }


      // For SFuture:
      val nodes = g.nodes;
      finish { // To make sure asyncs are done.
         val iter = nodes.iterator();
         while (iter.hasNext()) {
            val node = iter.next();
            if (node != n)
               async {
                  FTask.sAsyncWaitOr(
                     node.neighbors,
                     (n: Node)=> {
                        return n.parent;
                     },
                     (currentParent: Box[Pair[Node, Node]])=> {
                        val parent = currentParent().first;
//                        Console.OUT.println("Setting parent of " + node.no + " to " + parent.no + ".");
                        node.parent.set(new Box(new Pair[Node, Node](node, parent)));
                     }
                  );
               }
         }
      }
      finish {
         n.parent.set(new Box(new Pair[Node, Node](n, n)));
      }
//      Console.OUT.println("Returning");
   }

}




