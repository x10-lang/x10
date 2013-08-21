package futuresched.core;

import x10.compiler.NoInline;
import x10.util.Box;
import x10.util.ArrayList;
import x10.util.concurrent.Lock;
import x10.util.concurrent.AtomicInteger;

public class PhAndFTask extends FTask {
   var block: ()=>void;

   public def this(count: Int, act: Activity, worker: Runtime.Worker) {
      super(count, act, worker);
   }

   public def this(act: Activity) {
      super(act);
   }

   public def this() {
      super();
   }

// ------------------------------------------------------------
// asyncWait (asyncWaitAnd)
// ...

// ------------------------------------------------------------
// sAsyncWait
// Note that sAsyncWait is called when the futures are not already set or being concurrently set.

  public static def sPhasedAsyncWait[T](
    futures: ArrayList[SFuture[T]],
    block: ()=>void){T isref, T haszero} {

    val fTask = new PhAndFTask();
    fTask.block = ()=>{ block(); Phasing.end(); };

    val iter = futures.iterator();
    while (iter.hasNext()) {
      val f = iter.next();
      f.add(fTask);
    }
    fTask.count.set(-futures.size() as Int);
  }

  public static def sPhasedAsyncWait[T](
    future: SFuture[T],
    block: ()=>void){T isref, T haszero} {

    val fTask = new PhAndFTask();
    fTask.block = ()=>{ block(); Phasing.end(); };

    future.add(fTask);
    fTask.count.set(-1);
  }

  public static def sPhasedAsyncWait(
    futures: ArrayList[SNotifier],
    block: ()=>void){

    val fTask = new PhAndFTask();
    fTask.block = ()=>{ block(); Phasing.end(); };

    val iter = futures.iterator();
    while (iter.hasNext()) {
      val f = iter.next();
      f.add(fTask);
    }
    fTask.count.set(-futures.size() as Int);
  }


  public static def sPhasedAsyncWait(
    future: SNotifier,
    block: ()=>void) {

    val fTask = new PhAndFTask();
    fTask.block = ()=>{ block(); Phasing.end(); };

    future.add(fTask);

    fTask.count.set(-1);
  }

  public static def sPhasedAsyncWait(
    futures: ArrayList[SIntFuture],
    block: ()=>void) {

    val fTask = new PhAndFTask();
    fTask.block = ()=>{ block(); Phasing.end(); };

    val iter = futures.iterator();
    while (iter.hasNext()) {
      val f = iter.next();
      f.add(fTask);
    }
    fTask.count.set(-futures.size() as Int);
  }

  public static def sPhasedAsyncWait(
    future: SIntFuture,
    block: ()=>void) {

    val fTask = new PhAndFTask();
    fTask.block = ()=>{ block(); Phasing.end(); };

    future.add(fTask);

    fTask.count.set(-1);
  }

   public static def enclosedSPhasedAsyncWait[T](
      future: SFuture[T],
      block: ()=>void){T isref, T haszero} {

      val newBlock = ()=>{ block(); Phasing.end(); };
      val thisAct = initActEnclosed(newBlock);
      val task = new PhAndFTask(thisAct);

      future.add(task);
      task.count.set(-1);
   }

// ------------------------------------------------------------
// Batch task scheduling

   public def inform(f: Notifier) {
      if (!isDone) {
         val c = count.incrementAndGet();
         if (c == 0)
            Phasing.schedule(this);
      }
   }

   public def inform(f: SNotifier) {
      if (!isDone) {
         val c = count.incrementAndGet();
         if (c == 0) {
            act = initActEnclosed(block);
            Phasing.schedule(this);
         }
      }
   }

// ------------------------------------------------------------

}