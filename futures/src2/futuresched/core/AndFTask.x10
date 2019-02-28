package futuresched.core;

import x10.compiler.NoInline;
import x10.xrx.Activity;
import x10.xrx.FinishState;
import x10.xrx.Worker;
import x10.util.Box;
import x10.util.ArrayList;
import x10.util.concurrent.Lock;
import x10.util.concurrent.AtomicInteger;


public class AndFTask extends FTask {
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
// asyncAnd

  public static def asyncAnd[T](
    futures: ArrayList[Future[T]],
    block: ()=>void){T isref, T haszero}: AndFTask {

    val thisAct = initActEnclosed(block);
    val task = new AndFTask(thisAct, true);

    val iter = futures.iterator();
    var count: Int = 0;
    while (iter.hasNext()) {
      val f = iter.next();
      val added = f.addIfNotSet(task, null);
      if (added)
        count = count + 1;
    }
    if (count == 0)
      task.exec();
    else {
      count = task.count.addAndGet(-count);
      if (count == 0)
        task.exec();
    }
    return task;
  }

  public static def asyncAnd[T](
    future: Future[T],
    block: ()=>void){T isref, T haszero}: AndFTask {

    val thisAct = initActEnclosed(block);
    val task = new AndFTask(thisAct, true);

    val added = future.addIfNotSet(task, null);
    if (!added)
      task.exec();
    else {
      val count = task.count.decrementAndGet();
      if (count == 0)
        task.exec();
    }
    return task;
  }

  // To allow different types of futures.
  public static def asyncAnd(
    futures: ArrayList[Notifier],
    block: ()=>void): AndFTask {
    val thisAct = initActEnclosed(block);
    val task = new AndFTask(thisAct, true);

    val iter = futures.iterator();
    var count: Int = 0;
    while (iter.hasNext()) {
      val f = iter.next();
      val added = f.addIfNotSet(task, null);
      if (added)
        count = count + 1;
    }
    if (count == 0)
      task.exec();
    else {
      count = task.count.addAndGet(-count);
      if (count == 0)
        task.exec();
    }
    return task;
  }

  public static def asyncAnd(
    future: Notifier,
    block: ()=>void): AndFTask {

    val thisAct = initActEnclosed(block);
    val task = new AndFTask(thisAct, true);

    val added = future.addIfNotSet(task, null);
    if (!added)
      task.exec();
    else {
      val count = task.count.decrementAndGet();
      if (count == 0)
        task.exec();
    }
    return task;
  }

  public static def asyncAnd(
    futures: ArrayList[IntFuture],
    block: ()=>void): AndFTask  {

    val thisAct = initActEnclosed(block);
    val task = new AndFTask(thisAct, true);

    val iter = futures.iterator();
    var count: Int = 0;
    while (iter.hasNext()) {
      val f = iter.next();
      val added = f.addIfNotSet(task, null);
      if (added)
        count = count + 1;
    }
    if (count == 0)
      task.exec();
    else {
      count = task.count.addAndGet(-count);
      if (count == 0)
        task.exec();
    }
    return task;
  }

  public static def asyncAnd(
    futures: ArrayList[DoubleFuture],
    block: ()=>void): AndFTask  {

    val thisAct = initActEnclosed(block);
    val task = new AndFTask(thisAct, true);

    val iter = futures.iterator();
    var count: Int = 0;
    while (iter.hasNext()) {
      val f = iter.next();
      val added = f.addIfNotSet(task, null);
      if (added)
        count = count + 1;
    }
    if (count == 0)
      task.exec();
    else {
      count = task.count.addAndGet(-count);
      if (count == 0)
        task.exec();
    }
    return task;
  }


  public static def asyncAnd(
    future: IntFuture,
    block: ()=>void): AndFTask  {

    val thisAct = initActEnclosed(block);
    val task = new AndFTask(thisAct, true);

    val added = future.addIfNotSet(task, null);
    if (!added)
      task.exec();
    else {
      val count = task.count.decrementAndGet();
      if (count == 0)
        task.exec();
    }
    return task;
  }

//   public static def asyncAndSet(
//      futures: ArrayList[IntFuture],
//      fun: ()=>Int,
//      future: IntFuture): AndFTask {
//
//      val thisAct = initActEnclosed(future.set(fun());
//      val task = new AndFTask(thisAct);
//
//      val iter = futures.iterator();
//      var count: Int = 0;
//      while (iter.hasNext()) {
//         val f = iter.next();
//         val added = f.addIfNotSet(task, null);
//         if (added)
//         count = count + 1;
//      }
//      if (count == 0)
//         task.exec();
//      else {
//         count = task.count.addAndGet(-count);
//         if (count == 0)
//         task.exec();
//      }
//      return task;
//   }

// ------------------------------------------------------------
// newAnd
// Note that newAnd is called when the futures are not already set or being concurrently set.

  public static def newAnd[T](
    futures: ArrayList[SFuture[T]],
    block: ()=>void){T isref, T haszero}: AndFTask {

//    val thisAct = new Activity(block, here, mainFinish);
//    mainFinish.notifySubActivitySpawn(here);
//    val fTask = new FTask(thisAct);
    val fTask = new AndFTask(block, false);

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

  public static def newAnd[T](
    future: SFuture[T],
    block: ()=>void){T isref, T haszero}: AndFTask {

//    val thisAct = new Activity(block, here, mainFinish);
//    mainFinish.notifySubActivitySpawn(here);
//    val task = new FTask(thisAct);

    val fTask = new AndFTask(block, false);

    future.add(fTask, null);
    fTask.inDeg = 1;
    fTask.count.set(-1);
    return fTask;
  }

  public static def newAnd(
    futures: ArrayList[SNotifier],
    block: ()=>void): AndFTask {

//    val thisAct = new Activity(block, here, mainFinish);
//    mainFinish.notifySubActivitySpawn(here);
//    val fTask = new FTask(thisAct);

    val fTask = new AndFTask(block, false);

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


  public static def newAnd(
    future: SNotifier,
    block: ()=>void): AndFTask {

//    val thisAct = new Activity(block, here, mainFinish);
//    mainFinish.notifySubActivitySpawn(here);
//    val fTask = new FTask(thisAct);

    val fTask = new AndFTask(block, false);

    future.add(fTask, null);
    fTask.inDeg = 1;
    fTask.count.set(-1);
    return fTask;
  }

  public static def newAnd(
    futures: ArrayList[SIntFuture],
    block: ()=>void): AndFTask {

//    val thisAct = new Activity(block, here, mainFinish);
//    mainFinish.notifySubActivitySpawn(here);
//    val fTask = new FTask(thisAct);

    val fTask = new AndFTask(block, false);

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

  public static def newAnd(
      futures: ArrayList[SDoubleFuture],
      block: ()=>void): AndFTask {

      val fTask = new AndFTask(block, false);

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

  public static def newAnd(
    future: SIntFuture,
    block: ()=>void): AndFTask {

    val fTask = new AndFTask(block, false);

    future.add(fTask, null);
    fTask.inDeg = 1;
    fTask.count.set(-1);
    return fTask;
  }

   public static def enclosedSAsyncWait[T](
      future: SFuture[T],
      block: ()=>void){T isref, T haszero} {

      val thisAct = initActEnclosed(block);
      val task = new AndFTask(thisAct, true);

      future.add(task, null);
      task.inDeg = 1;
      task.count.set(-1);
   }

// ------------------------------------------------------------
// Single task scheduling

   public def inform(v: Any, obj: Any) {
      if (!isDone || recurring) {
         var c: Int;
         c = count.incrementAndGet();
         if (c == 0) {
            if (!enclosed)
               act = initActEnclosed(block);
            exec();
            if (recurring) {
               // From the above incrementAndGet to this point,
               // futures may have informed this task.
               // Note: It is assumed that no incoming future informs for the n+1'th time,
               //       before all the other futures have informed for the n'th time.
               //       This is because we do not check the identity of the informers
               //       but their count.
               // For example, recurring AndTasks can be used to construct a computation
               // graph and then to fire it with an input and get the result and then to
               // fire it again with another input.
               c = count.addAndGet(-inDeg);
               while (c >= 0) {
                  exec();
                  c = count.addAndGet(-inDeg);
               }
            }
         }
      }
   }

//   public def inform(f: SNotifier, obj) {
//      if (!isDone || recurring) {
//         var c: Int;
//         c = count.incrementAndGet();
//         if (c == 0) {
//            act = initActEnclosed(block);
//            exec();
//            if (recurring) {
//               c = count.addAndGet(-inDeg);
//               while (c >= 0) {
//                  exec();
//                  c = count.addAndGet(-inDeg);
//               }
//            }
//         }
//      }
//   }

// ------------------------------------------------------------

}

