package futuresched.benchs.bellmanford;

import x10.util.ArrayList;
import x10.util.Pair;
import x10.util.Box;
import futuresched.core.FTask;
import futuresched.core.SFuture;
import futuresched.core.SIntFuture;
import futuresched.core.Phasing;

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
                     // Each neighbor is a pair of vertex node and the edge weight to it.
                     (p: Pair[FutNode, Int])=> {
                        val adj = p.first;
                        val dist = adj.dist;
                        val weight = p.second;
                        // For each, we return a future that the task is dependent on and
                        // a value that we want to be returned back, when this dependency is resolved.
                        // In this case, for each neighbor, we return its distance future and
                        // the edge weight to the neighbor.
                        return new Pair[SIntFuture, Int](dist, weight);
                     },
                     (preDist: Int, weight: Int)=> {
                        while (true) {
                           val currDist = node.dist.get();
                           val newDist = preDist + weight;
                           if (newDist >= currDist)
                              return;
                           val done = node.dist.cas(currDist, newDist);
                           if (done)
                              return;
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
         Phasing.startPhasing();
      }
//      Console.OUT.println("Returning");
   }

}

