package futuresched.benchs.pagerankdelta;

import x10.util.ArrayList;
import futuresched.core.*;


public class PageRank {

   public static def compute(g: Graph, gamma: Double, sigma: Double) {

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
                  (deltas: Double) => {
                     Console.OUT.println("Executing task for " + node.no);
                     val ratio = Math.abs(deltas / node.rank);
//                     Console.OUT.println("Ratio " + ratio);
                     if (ratio > sigma) {
                        val delta = gamma * deltas;
                        node.rank += delta;
                        node.delta.set(delta);
                        return true;
                     } else {
                        // Deregister next nodes that are dependent on this delta.
                        val iter2 = node.outNeighbors.iterator();
                        while (iter2.hasNext()) {
                           val n = iter2.next();
                           n.task.deregister();
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
            val outs = n.outNeighbors;
            val share = (1.0 / v) / outs.size();
            val outsIter = outs.iterator();
            while (outsIter.hasNext()) {
               val outNode = outsIter.next();
               outNode.rank += share;
            }
         }
         val iter2 = g.nodes.iterator();
         while (iter2.hasNext()) {
            val n = iter2.next();
//            async {
               val shares = gamma * n.rank;
               val rank = (1.0 - gamma) / v + shares;
               val delta = rank - (1.0 / v);
               n.rank = 1.0 / v;
               val outDeg = n.outDegree();
               n.delta.set(delta / outDeg);
//            }
         }
         val s = g.toStringRanks();
         Console.OUT.println("Ranks: ");
         Console.OUT.print(s);
         Console.OUT.println("");
         //Phasing.startPhasing();
         Phasing.startPhasing(g);
      }

   }
}


// Rank = (1 - gamma) / v + (gamma * shares);
// InitRank = (1 - gamma) / v + gamma * \Sigma_{u \in deg-} (1 / v * deg+(u))
// InitDelta = InitRank - 1/v

