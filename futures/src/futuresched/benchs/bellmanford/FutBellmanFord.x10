package futuresched.benchs.bellmanford;

import x10.util.ArrayList;
import x10.util.Pair;
import x10.util.Box;
import futuresched.core.FTask;

public class FutBellmanFord {

   public static def compute(g: FutGraph, n: FutNode) {

      val nodes = g.nodes;
      finish { // To make sure asyncs are done.
         val iter = nodes.iterator();
         while (iter.hasNext()) {
            val node = iter.next();
            if (node != n)
               async {
                  val task = FTask.sPhasedAsyncWaitOr(
                     node.neighbors,
                     (p: Pair[FutNode, Int])=> {
                        return p.first.dist;
                     },
                     (p: Pair[FutNode, Int])=> {

//                        val parentForThis = currentParent().first;
//                        node.parent.set(new Box(new Pair[FutNode, FutNode](node, parentForThis)));
                        val parent = p.first;
                        val parentSP = parent.dist.get();
                        val weight = p.second;
                        val nodeSP = node.dist.get();
                        val newSP = parentSP + weight;
                        if (newSP < nodeSP)
                           node.dist.set(newSP);
//                        Console.OUT.println("Setting parent of " + node.no + " to " + parentForThis.no + ".");
                     }
                  );
                  task.recurring = true;
               }
         }
      }
//      Console.OUT.println("Finished creating tasks");
      finish {
         n.dist.set(0);
      }
//      Console.OUT.println("Returning");
   }

}

