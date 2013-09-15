package futuresched.benchs.pagerankdelta;

import x10.util.ArrayList;
import futuresched.core.Heart;


public class PageRank {

   public static def compute(g: Graph, dampFact: Double, sigma: Double) {

      val v = graph.nodeCount();

      finish {
         val iter = nodes.iterator();
         while (iter.hasNext()) {
            val node = iter.next();
            async {
               FTask.newPhasedAdd(
                  node.inNeighbors,
                  (n: Node) => {
                     return n.delta;
                  },
                  (deltas: Int) => {
                     if ((deltas / node.rank) > sigma) {
                        node.rank += dampFact * deltas;
                        node.delta.set();
                        return true;
                     } else
                        // deregister next nodes that are dependent on this delta.
                        return false; //stops this ftask
                  }
               );
            }
         }
      }

      finish {
         val iter = g.nodes.iterator();
         while (iter.hasNext()) {
            val n = iter.next();
            n.rank = 0;
            n.delta.set(1.0 / v);
         }
      }

   }

}


// node.rank = (dampFact * shares) + (1 - dampFact) / v;
// InitRank = (1 - gamma) / v + gamma * \Sigma_{u\indeg-} (1/v*deg+(u))
// delta = initRank - 1/v

