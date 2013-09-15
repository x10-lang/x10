package futuresched.core;

import x10.lang.Runtime;
import x10.lang.Activity;
import x10.compiler.NoInline;
import x10.util.ArrayList;
import x10.util.Pair;
import x10.util.concurrent.Lock;
import x10.util.concurrent.AtomicInteger;


public class Heart {

   private act: Activity;

   public def newHeart[T](obj: T, fun: (T)=>Boolean): Heart {
      val heart = new Heart();
      val block()=> {
         val repeat = fun(obj);
         if (repeat) {
            Phasing.addToNext(heart.act);
         }
         Phasing.end();
      };
      heart.act = initActEnclosed(block);
      return heart;
   }


   public static def initActEnclosed(block: ()=>void): Activity {
     val a = Runtime.activity();
     val state = a.finishState();
     state.notifySubActivitySpawn(here);
     return new Activity(block, here, state);
   }

   public static def beat[T](
      list: ArrayList[T],
      fun: (T)=>Boolean {

      finish {
         Phasing.thisPhaseCount.set(list.size() as Int);
         val iter = nodes.iterator();
         while (iter.hasNext()) {
            async {
               val obj = iter.next();
               val heart = newHeart(obj, fun);
               Phasing.addToNext(heart.act);
               Phasing.end();
            }
         }
      }
   }


}
