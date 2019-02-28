package futuresched.core;

import x10.xrx.Runtime;
import x10.xrx.Activity;
import x10.util.ArrayList;
import x10.util.concurrent.Lock;
import x10.util.concurrent.AtomicInteger;

import futuresched.benchs.pagerankdelta.Graph;

public class Phasing {

   static class IntHolder {
      var v: Int = 0n;
   }
   public static val phaseNoHolder: IntHolder = new IntHolder();

   public static val thisPhaseCount: AtomicInteger = new AtomicInteger();

//   static class Holder {
//      var start: Boolean = true;
//   }
//   static val holder: Holder = new Holder();

//   public static def schedule(task: FTask) {
   public static def schedule(act: Activity) {
//      Console.OUT.println("Scheduling");
//      if (holder.start) {
         // This is the first firing task
//         Console.OUT.println("First Task");
//         thisPhaseCount.set(1);
//         holder.start = false;
//         task.exec();
//      } else
//         Runtime.worker().addToNextPhase(task.act);
      Runtime.worker().addToNextPhase(act);
   }

   public static def addToNext(act: Activity) {
      Runtime.worker().addToNextPhase(act);
   }

   public static def startPhasing() {
      phaseNoHolder.v = 0n;
      nextPhase();
   }

   public static def nextPhase() {
      phaseNoHolder.v = phaseNoHolder.v + 1n;
      var count: Int = 0n;
      var j: Int;
      val workers = Runtime.pool.workers.workers;
      for(j = 0n; j < Runtime.NTHREADS; j++)
         count += workers(j).nextPhaseCount();
      thisPhaseCount.set(count);

//      Console.OUT.println("Next phase with " + count + " tasks.");

      for(j = 0n; j < Runtime.NTHREADS; j++)
         workers(j).nextPhase();

//      if (count == 0n)
//         holder.start = true;
   }

   public static def end() {
      val i = thisPhaseCount.decrementAndGet();
//      Console.OUT.println("Current count: " + i);
      if (i == 0n) {

//         val s = graphHolder.v.toStringRanks();
//         Console.OUT.println("Ranks: ");
//         Console.OUT.print(s);
//         Console.OUT.println("");

         nextPhase();
      }
   }

   static class GraphHolder {
      var v: Graph = null;
   }

   static val graphHolder: GraphHolder = new GraphHolder();

   public static def startPhasing(g: Graph) {
      graphHolder.v = g;
      phaseNoHolder.v = 0n;
      nextPhase();
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

      var phase: Int = 0n;
   }

   static val thisPhaseCount: AtomicInteger = new AtomicInteger();
   static val deferredTasks: TaskBag = new TaskBag();


   static def schedule(task: FTask) {
      if (deferredTasks.phase == 0n) {
         // This is the first firing task
         thisPhaseCount.incrementAndGet();
         deferredTasks.phase = 1;
         task.exec();
      } else
         deferredTasks.add(task);
   }

   static def end() {
      val i = thisPhaseCount.decrementAndGet();
      if (i == 0n) {
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

