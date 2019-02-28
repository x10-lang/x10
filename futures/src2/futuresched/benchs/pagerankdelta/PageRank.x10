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
                  // Once all the deltas are accumulated, in the next phase
                  // either set the rank and set delta for outgoing neighbors
                  // or untie outgoing neighbors and stop the task.
                  (deltas: Double) => {
                     val v = Math.abs(deltas);
                     if (v < 0.000000001)
                        return false;
                     else {
                        val ratio = v / node.currRank;
                        return (ratio > sigma);
                     }
//                     return true;
                  },
                  (deltas: Double) => {
//                     Console.OUT.println("Executing task for " + node.no);
//                     Console.OUT.println("deltas for " + node.no + ": " + deltas);
                     val delta = gamma * deltas;
                     val initRank = node.prevRank;
                     node.prevRank = node.currRank;
                     val rank = initRank + delta;
                     if (rank < 0)
                        node.currRank = 0.0;
//                     else if (rank > 1)
//                        node.currRank = 1.0;
                     else
                        node.currRank = rank;

                     val diff = node.currRank - node.prevRank;
//                     if (Math.abs(diff) < 0.000000001)
//                        node.delta.set(0);
//                     else {
                        val outDeg = node.outDegree();
                        val d = diff / outDeg;
                        node.delta.set(d);
//                     }
                  },
                  () => {
                     // Untie next nodes that are dependent on this delta.
//                     Console.OUT.println("Untie for " + node.no);
                     val iter2 = node.outNeighbors.iterator();
                     while (iter2.hasNext()) {
                        val n = iter2.next();
                        n.task.untie();
                     }
                  },
                  () => {
                     // Retie next nodes that are dependent on this delta.
//                     Console.OUT.println("Retie for " + node.no);
                     val iter2 = node.outNeighbors.iterator();
                     while (iter2.hasNext()) {
                        val n = iter2.next();
                        n.task.tie();
                     }
                  }
               );
               node.task = task;
               task.recurring = true;
            }
         }
      }

      finish {

/*
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
               val outDeg = n.outDegree();
               n.delta.set(delta / outDeg);
               n.rank = 1.0 / v;
//            }
         }
*/
         val iter2 = g.nodes.iterator();
         while (iter2.hasNext()) {
            val n = iter2.next();
            val outDeg = n.outDegree();
            n.prevRank = 0;
            n.currRank = 1.0 / v;
            n.delta.set((1.0 / v) / outDeg);
         }

//         val s = g.toStringRanks();
//         Console.OUT.println("Ranks: ");
//         Console.OUT.print(s);
//         Console.OUT.println("");
//         Phasing.startPhasing(g);

         Phasing.startPhasing();
      }
   }
}


// Rank = (1 - gamma) / v + (gamma * shares);
// InitRank = (1 - gamma) / v + gamma * \Sigma_{u \in deg-} (1 / v * deg+(u))
// InitDelta = InitRank - 1/v

