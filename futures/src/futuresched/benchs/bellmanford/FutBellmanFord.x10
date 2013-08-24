package futuresched.benchs.bellmanford;

import x10.util.ArrayList;
import x10.util.Pair;
import x10.util.Box;
import futuresched.core.FTask;
import futuresched.core.SFuture;
import futuresched.core.SIntFuture;

public class FutBellmanFord {

   public static def compute(g: FutGraph, n: FutNode) {

      val nodes = g.nodes;
      finish { // To make sure asyncs are done.
         val iter = nodes.iterator();
         while (iter.hasNext()) {
            val node = iter.next();
            if (node != n)
               async {
                  val task = FTask.newPhasedOr(
                     node.neighbors,
                     (p: Pair[FutNode, Int])=> {
                        val adj = p.first;
                        val dist = adj.dist;
                        val weight = p.second;
                        return new Pair[SIntFuture, Int](dist, weight);
                     },
                     (parentDist: Int, weight: Int)=> {
                        val nodeDist = node.dist.get();
                        val newDist = parentDist + weight;
                        if (newDist < nodeDist) {
                           node.dist.set(newDist);
//                           Console.OUT.println("Setting dist of " + newDist);
                        }
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

