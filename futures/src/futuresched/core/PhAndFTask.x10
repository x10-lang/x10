package futuresched.core;

import x10.compiler.NoInline;
import x10.xrx.Activity;
import x10.xrx.FinishState;
import x10.xrx.Worker;
import x10.util.Box;
import x10.util.ArrayList;
import x10.util.concurrent.Lock;
import x10.util.concurrent.AtomicInteger;

public class PhAndFTask extends FTask {
   var block: ()=>void;
   var inDeg: Int;

   public def this(count: Int, act: Activity, worker: Worker, enclosed: Boolean) {
      super(count, act, worker, enclosed);
   }

   public def this(act: Activity, enclosed: Boolean) {
      super(act, enclosed);
   }

   public def this(block: ()=>void, enclosed: Boolean) {
      this.block  = block;
      this.enclosed = enclosed;
   }

   public def this() {
      super();
   }

// ------------------------------------------------------------
// asyncWait (asyncWaitAnd)
// ...

// ------------------------------------------------------------
// newPhasedAnd
// Note that newPhasedAnd is called when the futures are not already set or being concurrently set.

  public static def newPhasedAnd[T](
    futures: ArrayList[SFuture[T]],
    block: ()=>void){T isref, T haszero}: PhAndFTask {

    val newBlock = ()=>{ block(); Phasing.end(); };
    val fTask = new PhAndFTask(newBlock, false);

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

  public static def newPhasedAnd[T](
    future: SFuture[T],
    block: ()=>void){T isref, T haszero}: PhAndFTask {

    val newBlock = ()=>{ block(); Phasing.end(); };
    val fTask = new PhAndFTask(newBlock, false);

    future.add(fTask, null);
    fTask.inDeg = 1n;
    fTask.count.set(-1n);
    return fTask;
  }

  public static def newPhasedAnd(
    futures: ArrayList[SNotifier],
    block: ()=>void): PhAndFTask {

    val newBlock = ()=>{ block(); Phasing.end(); };
    val fTask = new PhAndFTask(newBlock, false);

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


  public static def newPhasedAnd(
    future: SNotifier,
    block: ()=>void): PhAndFTask {

    val newBlock = ()=>{ block(); Phasing.end(); };
    val fTask = new PhAndFTask(newBlock, false);


    future.add(fTask, null);
    fTask.inDeg = 1n;
    fTask.count.set(-1n);
    return fTask;
  }

   public static def newPhasedAnd(
      futures: ArrayList[SIntFuture],
      block: ()=>void): PhAndFTask {

      val newBlock = ()=>{ block(); Phasing.end(); };
      val fTask = new PhAndFTask(newBlock, false);

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

   public static def newPhasedAnd(
      future: SIntFuture,
      block: ()=>void): PhAndFTask {

      val newBlock = ()=>{ block(); Phasing.end(); };
      val fTask = new PhAndFTask(newBlock, false);


      future.add(fTask, null);
      fTask.inDeg = 1n;
      fTask.count.set(-1n);
      return fTask;
   }

   public static def enclosedSPhasedAsyncWait[T](
      future: SFuture[T],
      block: ()=>void){T isref, T haszero} {

      val newBlock = ()=>{ block(); Phasing.end(); };
      val thisAct = initActEnclosed(newBlock);
      val task = new PhAndFTask(thisAct, true);

      future.add(task, null);
      task.inDeg = 1n;
      task.count.set(-1n);
   }

// ------------------------------------------------------------
// Deferred task scheduling

   public def inform(v: Any, obj: Any) {
      if (!isDone || recurring) {
         var c: Int;
         c = count.incrementAndGet();
         if (c == 0n) {
            if (!enclosed)
               act = initActEnclosed(block);
            Phasing.schedule(this.act);
            if (recurring) {
               c = count.addAndGet(-inDeg);
               while (c >= 0n) {
                  Phasing.schedule(this.act);
                  c = count.addAndGet(-inDeg);
               }
            }
         }
      }
   }

//   public def inform(f: SNotifier) {
//      if (!isDone || recurring) {
//         var c: Int;
//         c = count.incrementAndGet();
//         if (c == 0n) {
//            act = initActEnclosed(block);
//            Phasing.schedule(this);
//            if (recurring) {
//               c = count.addAndGet(-inDeg);
//               while (c >= 0n) {
//                  Phasing.schedule(this);
//                  c = count.addAndGet(-inDeg);
//               }
//            }
//         }
//      }
//   }

// ------------------------------------------------------------

}

