package futuresched.benchs.conncomp;

import x10.util.ArrayList;
import x10.util.Pair;
import x10.util.Box;
import futuresched.core.FTask;
import futuresched.core.SFuture;
import futuresched.core.Phasing;

public class FutComp {

   public static def compute(g: FutGraph) {

      val nodes = g.nodes;
      finish { // To make sure asyncs are done.
         val iter = nodes.iterator();
         while (iter.hasNext()) {
            val node = iter.next();
            async {
               val task = FTask.newPhasedOr(
                  node.neighbors,
                  (n: FutNode)=> {
                     return n.comp;
                  },
                  (hisComp: Int, a: Any)=> {
                     val myComp = node.comp.get();
                     if (hisComp < myComp)
                        node.comp.set(hisComp);
                  }
               );
               task.recurring = true;
            }
         }
      }
//      Console.OUT.println("Finished creating tasks");
      finish {
         val iter = nodes.iterator();
         while (iter.hasNext()) {
            val n = iter.next();
            n.comp.set(n.no);
         }
         Phasing.startPhasing();
      }
//      Console.OUT.println("Returning");
   }

}

/*
   public static def newPhasedOr[T](
      futures: ArrayList[T],
      trans: (T)=>SIntFuture,
      fun: (Int, Any)=>void): IntPhOrFTask[Any] {
      return IntPhOrFTask.newPhasedOr(futures, trans, fun);
   }
*/