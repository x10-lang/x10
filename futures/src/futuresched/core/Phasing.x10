package futuresched.core;

import x10.lang.Runtime;
import x10.util.ArrayList;
import x10.util.ArrayList;
import x10.util.concurrent.Lock;
import x10.util.concurrent.AtomicInteger;


public class Phasing {
   // Thread-local bags for next phase

   static val thisPhaseCount: AtomicInteger = new AtomicInteger();

   static class Holder {
      var start: Boolean = true;
   }
   static val holder: Holder = new Holder();

   static def schedule(task: FTask) {
      if (holder.start) {
         // This is the first firing task
         thisPhaseCount.set(1);
         holder.start = false;
         task.exec();
      } else
         Runtime.worker().addToNextPhase(task.act);
   }

   static def end() {
      val i = thisPhaseCount.decrementAndGet();
      if (i == 0) {
         var count: Int = 0;
         var j: Int;
         val workers = Runtime.pool.workers.workers;
         for(j = 0; j < Runtime.NTHREADS; j++)
            count += workers(j).nextPhaseCount();
         thisPhaseCount.set(count);
         for(j = 0; j < Runtime.NTHREADS; j++)
            workers(j).nextPhase();
      }
   }

   /*
   // A lock protected bag
   static class TaskBag {

      val lock = new Lock();
      public var list: ArrayList[FTask] = new ArrayList[FTask]();

      public def add(t: FTask) {
         lock.lock();
         list.add(t);
         lock.unlock();
      }

      var phase: Int = 0;
   }

   static val thisPhaseCount: AtomicInteger = new AtomicInteger();
   static val deferredTasks: TaskBag = new TaskBag();


   static def schedule(task: FTask) {
      if (deferredTasks.phase == 0) {
         // This is the first firing task
         thisPhaseCount.incrementAndGet();
         deferredTasks.phase = 1;
         task.exec();
      } else
         deferredTasks.add(task);
   }

   static def end() {
      val i = thisPhaseCount.decrementAndGet();
      if (i == 0) {
         val s = deferredTasks.list.size() as Int;
         thisPhaseCount.set(s);
         val iter = deferredTasks.list.iterator();
         while (iter.hasNext()) {
            val task = iter.next();
            task.exec();
         }
         deferredTasks.list = new ArrayList[FTask]();
         deferredTasks.phase += 1;
      }
   }
   */
}

