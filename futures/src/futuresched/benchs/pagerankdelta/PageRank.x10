package futuresched.benchs.pagerankdelta;

import x10.util.ArrayList;
import futuresched.core.Heart;



public class PageRank {

   public static def compute(g: Graph, dampFact: Double) {

      val v = graph.nodeCount();

      // init graph
      val iter = g.nodes.iterator();
      while (iter.hasNext()) {
         val n = iter.next();
         n.prevRank = 1.0 / v;
      }

      Heart.beat(
         g.nodes
         (node: Node) => {
            if (node.oddPhase) {
               // Set the share of neighbors:
               val neighbors = node.neighbors;
               val deg = node.degree();
               val share = node.prevRank / deg;
               val iter = neighbors.iterator();
               while (iter.hasNext()) {
                  val n = iter.next();
                  n.currShares.addAndGet(share);
               }
               // Set the current rank
               val currRank = (dampFact * node.prevShares) + (1 - dampFact) / v;
               node.currRank.set(currRank);
               node.oddPhase = false;
               return true;
            } else {
               node.prevRank = node.currRank;
               node.prevShares = node.currShares.get();
               node.currShares.set(0);
               node.oddPhase = true;

               return ;
            }
         }
      );
   }

}



