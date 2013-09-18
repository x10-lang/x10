package futuresched.benchs.pagerankdelta;

import x10.util.ArrayList;
import futuresched.core.*;


public class PageRank {

   public static def compute(g: Graph, dampFact: Double, sigma: Double) {

      val v = g.nodeCount();

      finish {
         val iter = g.nodes.iterator();
         while (iter.hasNext()) {
            val node = iter.next();
            async {
               val task = FTask.newPhasedDoubleAdd(
                  // The task is dependent on the deltas of incoming neighbors
                  node.inNeighbors,
                  (n: Node) => {
                     return n.delta;
                  },
                  // One all the deltas are accumulated, in the next phase
                  // either set the rank and set delta for outgoing neighbors
                  // or deregister outgoing neighbors and stop the task.
                  (deltas: Int) => {
                     if ((deltas / node.rank) > sigma) {
                        val delta = dampFact * deltas;
                        node.rank += delta;
                        node.delta.set(delta);
                        return true;
                     } else {
                        // Deregister next nodes that are dependent on this delta.
                        val iter2 = node.outNeighbors();
                        while (iter2.hasNext()) {
                           val n = iter2.next();
                           n.deregister();
                        }
                        return false; //stops this task
                     }
                  }
               );
               task.recurring = true;
            }
         }
      }

      finish {
         val iter = g.nodes.iterator();
         while (iter.hasNext()) {
            val n = iter.next();
            val outs = node.inNeighbors;
            val share = (1.0 / v) / outs.size();
            val outsIter = outs.iterator();
            while (outsIter.hasNext()) {
               val outNode = insIter.next();
               outNode.rank += share;
            }
         }
         val iter2 = g.nodes.iterator();
         while (iter2.hasNext()) {
            val n = iter2.next();
            async {
               val shares = gamma * n.rank;
               val rank = (1 - gamma) / v + shares;
               n.rank = rank;
               val delta = rank - (1 / v);
               n.delta.set(delta);
            }
         }
         Phasing.startPhasing();
      }

   }
}


// Rank = (1 - gamma) / v + (gamma * shares);
// InitRank = (1 - gamma) / v + gamma * \Sigma_{u \in deg-} (1 / v * deg+(u))
// InitDelta = InitRank - 1/v

