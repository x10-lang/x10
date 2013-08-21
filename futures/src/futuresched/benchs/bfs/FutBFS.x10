package futuresched.benchs.bfs;

import x10.util.ArrayList;
import x10.util.Pair;
import x10.util.Box;
import futuresched.core.FTask;

public class FutBFS {

   public static def bfs(g: FutGraph, n: FutNode) {

      val nodes = g.nodes;
      finish { // To make sure asyncs are done.
         val iter = nodes.iterator();
         while (iter.hasNext()) {
            val node = iter.next();
            if (node != n)
               async {
                  FTask.sPhasedAsyncWaitOr(
                     node.neighbors,
                     (n: FutNode)=> {
                        return n.parent;
                     },
                     (currentParent: Box[Pair[FutNode, FutNode]])=> {
                        val parentForThis = currentParent().first;
//                        Console.OUT.println("Setting parent of " + node.no + " to " + parentForThis.no + ".");
                        node.parent.set(new Box(new Pair[FutNode, FutNode](node, parentForThis)));
                     }
                  );
               }
         }
      }
//      Console.OUT.println("Finished creating tasks");
      finish {
         n.parent.set(new Box(new Pair[FutNode, FutNode](n, n)));
      }
//      Console.OUT.println("Returning");
   }

}




