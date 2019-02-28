package futuresched.core;

import x10.util.ArrayList;
import x10.xrx.Activity;
import x10.xrx.FinishState;
import x10.xrx.Runtime;
import x10.util.concurrent.AtomicDouble;
import x10.util.concurrent.AtomicInteger;


public class PhDoubleAddFTask extends FTask {

   var cond: (Double)=>Boolean;
   var block: (Double)=>void;
   var stop: ()=>void;
   var restart: ()=>void;

   var inDeg: Int;
   var stopped: Boolean = false;
   var sum: AtomicDouble = new AtomicDouble(0);


   public def this(count: Int, act: Activity, worker: Runtime.Worker, enclosed: Boolean) {
      super(count, act, worker, enclosed);
   }

   public def this(act: Activity, enclosed: Boolean) {
      super(act, enclosed);
   }

   public def this(cond: (Double)=>Boolean, block: (Double)=>void, stop: ()=>void, restart: ()=>void, enclosed: Boolean) {
      this.cond = cond;
      this.block = block;
      this.stop = stop;
      this.restart = restart;
      this.enclosed = enclosed;
   }

   public def this(enclosed: Boolean) {
      this.enclosed = enclosed;
   }

   public def this() {
      super();
   }

// ------------------------------------------------------------

   public static def newPhasedDoubleAdd(
      futures: ArrayList[SDoubleFuture],
      cond: (Double)=>Boolean,
      block: (Double)=>void,
      stop: ()=>void,
      restart: ()=>void): PhDoubleAddFTask {

      val fTask = new PhDoubleAddFTask(cond, block, stop, restart, false);

      val iter = futures.iterator();
      while (iter.hasNext()) {
         val f = iter.next();
         f.add(fTask, null);
      }
      val count = futures.size() as Int;
      fTask.inDeg = count;
      fTask.count.set(-count);
      return fTask;
   }

   public static def newPhasedDoubleAdd(
      futures: ArrayList[SUDoubleFuture],
      cond: (Double)=>Boolean,
      block: (Double)=>void,
      stop: ()=>void,
      restart: ()=>void): PhDoubleAddFTask {

      val fTask = new PhDoubleAddFTask(cond, block, stop, restart, false);

      val iter = futures.iterator();
      while (iter.hasNext()) {
         val f = iter.next();
         f.add(fTask, null);
      }
      val count = futures.size() as Int;
      fTask.inDeg = count;
      fTask.count.set(-count);
      return fTask;
   }

   public static def newPhasedDoubleAdd[T](
      objs: ArrayList[T],
      trans: (T)=>SDoubleFuture,
      cond: (Double)=>Boolean,
      block: (Double)=>void,
      stop: ()=>void,
      restart: ()=>void): PhDoubleAddFTask {


      val fTask = new PhDoubleAddFTask(cond, block, stop, restart, false);

      val iter = objs.iterator();
      while (iter.hasNext()) {
         val f = trans(iter.next());
         f.add(fTask, null);
      }
      val count = objs.size() as Int;
      fTask.inDeg = count;
      fTask.count.set(-count);
      return fTask;
   }

   public static def newPhasedDoubleAdd[T](
      objs: ArrayList[T],
      trans: (T)=>SUDoubleFuture,
      cond: (Double)=>Boolean,
      block: (Double)=>void,
      stop: ()=>void,
      restart: ()=>void): PhDoubleAddFTask {

      val fTask = new PhDoubleAddFTask(cond, block, stop, restart, false);

      val iter = objs.iterator();
      while (iter.hasNext()) {
         val f = trans(iter.next());
         f.add(fTask, null);
      }
      val count = objs.size() as Int;
      fTask.inDeg = count;
      fTask.count.set(-count);
      return fTask;
   }

// ------------------------------------------------------------
   var count2: AtomicInteger = new AtomicInteger();

   public def tie() {
      nextCount().decrementAndGet();
   }

   public def untie() {
      nextCount().incrementAndGet();
   }

   public def currCount(): AtomicInteger {
      val n = Phasing.phaseNoHolder.v;
      if (n % 2 == 0)
         return count;
      else
         return count2;
   }

   public def nextCount(): AtomicInteger {
      val n = Phasing.phaseNoHolder.v;
      if (n % 2 == 0)
         return count2;
      else
         return count;
   }

// ------------------------------------------------------------
// Deferred task scheduling

   public def inform(v: Any, obj: Any) {
//      Console.OUT.println("In inform ");
      if (isDone && !recurring)
         return;

      val doubleV = v as Double;
      sum.addAndGet(doubleV);
      // Note that adding the value should be done before incrementing the counter.
      // The last process that finds the counter zero,
      // knows that the sum contains the complete sum.
      var c: Int;
      c = currCount().incrementAndGet();
//      Console.OUT.println("Count: " + c);
      if (c == 0) {
         val s = sum.get();
         val fire = cond(s);
         if (fire) {
            if (!enclosed) {
               val newBlock = () => {
                  this.block(s);
                  Phasing.end();
               };
               val act = initActEnclosed(newBlock);
               //Console.OUT.println("Scheduling for next phase");
               Phasing.schedule(act);
            } else
               Phasing.schedule(this.act);
            if (stopped) {
               this.restart();
               stopped = false;
            }
         } else {
            if (! stopped) {
               this.stop();
               stopped = true;
            }
         }
         if (recurring) {
            // It is assumed that the task is fired once in each phase.
            sum.set(0);
            nextCount().addAndGet(-inDeg);
         }
      }
   }

// ------------------------------------------------------------

}

