package futuresched.core;

import x10.compiler.NoInline;
import x10.util.Box;
import x10.util.ArrayList;
import x10.util.concurrent.Lock;
import x10.util.concurrent.AtomicInteger;

public class PhAndFTask extends FTask {
   var block: ()=>void;
   var inDeg: Int;

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
    block: ()=>void){T isref, T haszero}: PhAndFTask {

    val fTask = new PhAndFTask();
    fTask.block = ()=>{ block(); Phasing.end(); };

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

  public static def sPhasedAsyncWait[T](
    future: SFuture[T],
    block: ()=>void){T isref, T haszero}: PhAndFTask {

    val fTask = new PhAndFTask();
    fTask.block = ()=>{ block(); Phasing.end(); };

    future.add(fTask, null);
    fTask.inDeg = 1;
    fTask.count.set(-1);
    return fTask;
  }

  public static def sPhasedAsyncWait(
    futures: ArrayList[SNotifier],
    block: ()=>void): PhAndFTask {

    val fTask = new PhAndFTask();
    fTask.block = ()=>{ block(); Phasing.end(); };

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


  public static def sPhasedAsyncWait(
    future: SNotifier,
    block: ()=>void): PhAndFTask {

    val fTask = new PhAndFTask();
    fTask.block = ()=>{ block(); Phasing.end(); };

    future.add(fTask, null);
    fTask.inDeg = 1;
    fTask.count.set(-1);
    return fTask;
  }

   public static def sPhasedAsyncWait(
      futures: ArrayList[SIntFuture],
      block: ()=>void): PhAndFTask {

      val fTask = new PhAndFTask();
      fTask.block = ()=>{ block(); Phasing.end(); };

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

   public static def sPhasedAsyncWait(
      future: SIntFuture,
      block: ()=>void): PhAndFTask {

      val fTask = new PhAndFTask();
      fTask.block = ()=>{ block(); Phasing.end(); };

      future.add(fTask, null);
      fTask.inDeg = 1;
      fTask.count.set(-1);
      return fTask;
   }

   public static def enclosedSPhasedAsyncWait[T](
      future: SFuture[T],
      block: ()=>void){T isref, T haszero} {

      val newBlock = ()=>{ block(); Phasing.end(); };
      val thisAct = initActEnclosed(newBlock);
      val task = new PhAndFTask(thisAct);

      future.add(task, null);
      task.inDeg = 1;
      task.count.set(-1);
   }

// ------------------------------------------------------------
// Deferred task scheduling

   public def inform(g: Boolean, v: Any, obj: Any) {
      if (!isDone || recurring) {
         var c: Int;
         c = count.incrementAndGet();
         if (c == 0) {
            if (!g)
               act = initActEnclosed(block);
            Phasing.schedule(this);
            if (recurring) {
               c = count.addAndGet(-inDeg);
               while (c >= 0) {
                  Phasing.schedule(this);
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
//         if (c == 0) {
//            act = initActEnclosed(block);
//            Phasing.schedule(this);
//            if (recurring) {
//               c = count.addAndGet(-inDeg);
//               while (c >= 0) {
//                  Phasing.schedule(this);
//                  c = count.addAndGet(-inDeg);
//               }
//            }
//         }
//      }
//   }

// ------------------------------------------------------------

}

