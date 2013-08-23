package futuresched.core;

import x10.compiler.NoInline;
import x10.util.Box;
import x10.util.ArrayList;
import x10.util.concurrent.Lock;
import x10.util.concurrent.AtomicInteger;

public class AndFTask extends FTask {
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

  public static def asyncWait[T](
    futures: ArrayList[Future[T]],
    block: ()=>void){T isref, T haszero} {

    val thisAct = initActEnclosed(block);
    val task = new AndFTask(thisAct);

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
  }

  public static def asyncWait[T](
    future: Future[T],
    block: ()=>void){T isref, T haszero} {

    val thisAct = initActEnclosed(block);
    val task = new AndFTask(thisAct);

    val added = future.addIfNotSet(task, null);
    if (!added)
      task.exec();
    else {
      val count = task.count.decrementAndGet();
      if (count == 0)
        task.exec();
    }
  }

  // To allow different types of futures.
  public static def asyncWait(
    futures: ArrayList[Notifier],
    block: ()=>void) {
    val thisAct = initActEnclosed(block);
    val task = new AndFTask(thisAct);

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
  }

  public static def asyncWait(
    future: Notifier,
    block: ()=>void) {

    val thisAct = initActEnclosed(block);
    val task = new AndFTask(thisAct);

    val added = future.addIfNotSet(task, null);
    if (!added)
      task.exec();
    else {
      val count = task.count.decrementAndGet();
      if (count == 0)
        task.exec();
    }
  }

  public static def asyncWait(
    futures: ArrayList[IntFuture],
    block: ()=>void) {

    val thisAct = initActEnclosed(block);
    val task = new AndFTask(thisAct);

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
  }

  public static def asyncWait(
    future: IntFuture,
    block: ()=>void) {

    val thisAct = initActEnclosed(block);
    val task = new AndFTask(thisAct);

    val added = future.addIfNotSet(task, null);
    if (!added)
      task.exec();
    else {
      val count = task.count.decrementAndGet();
      if (count == 0)
        task.exec();
    }
  }

// ------------------------------------------------------------
// sAsyncWait
// Note that sAsyncWait is called when the futures are not already set or being concurrently set.

  public static def sAsyncWait[T](
    futures: ArrayList[SFuture[T]],
    block: ()=>void){T isref, T haszero}: AndFTask {

//    val thisAct = new Activity(block, here, mainFinish);
//    mainFinish.notifySubActivitySpawn(here);
//    val fTask = new FTask(thisAct);
    val fTask = new AndFTask();
    fTask.block = block;

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

  public static def sAsyncWait[T](
    future: SFuture[T],
    block: ()=>void){T isref, T haszero}: AndFTask {

//    val thisAct = new Activity(block, here, mainFinish);
//    mainFinish.notifySubActivitySpawn(here);
//    val task = new FTask(thisAct);

    val fTask = new AndFTask();
    fTask.block = block;

    future.add(fTask, null);
    fTask.inDeg = 1;
    fTask.count.set(-1);
    return fTask;
  }

  public static def sAsyncWait(
    futures: ArrayList[SNotifier],
    block: ()=>void): AndFTask {

//    val thisAct = new Activity(block, here, mainFinish);
//    mainFinish.notifySubActivitySpawn(here);
//    val fTask = new FTask(thisAct);

    val fTask = new AndFTask();
    fTask.block = block;

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


  public static def sAsyncWait(
    future: SNotifier,
    block: ()=>void): AndFTask {

//    val thisAct = new Activity(block, here, mainFinish);
//    mainFinish.notifySubActivitySpawn(here);
//    val fTask = new FTask(thisAct);

    val fTask = new AndFTask();
    fTask.block = block;

    future.add(fTask, null);
    fTask.inDeg = 1;
    fTask.count.set(-1);
    return fTask;
  }

  public static def sAsyncWait(
    futures: ArrayList[SIntFuture],
    block: ()=>void): AndFTask {

//    val thisAct = new Activity(block, here, mainFinish);
//    mainFinish.notifySubActivitySpawn(here);
//    val fTask = new FTask(thisAct);

    val fTask = new AndFTask();
    fTask.block = block;

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

  public static def sAsyncWait(
    future: SIntFuture,
    block: ()=>void): AndFTask {

    val fTask = new AndFTask();
    fTask.block = block;

    future.add(fTask, null);
    fTask.inDeg = 1;
    fTask.count.set(-1);
    return fTask;
  }

   public static def enclosedSAsyncWait[T](
      future: SFuture[T],
      block: ()=>void){T isref, T haszero} {

      val thisAct = initActEnclosed(block);
      val task = new AndFTask(thisAct);

      future.add(task, null);
      task.inDeg = 1;
      task.count.set(-1);
   }

// ------------------------------------------------------------
// Single task scheduling

   public def inform(g: Boolean, v: Any, obj: Any) {
      if (!isDone || recurring) {
         var c: Int;
         c = count.incrementAndGet();
         if (c == 0) {
            if (!g)
               act = initActEnclosed(block);
            exec();
            if (recurring) {
               // From the above incrementAndGet to this point,
               // futures may have informed this task.
               // Note: It is assumed that no incoming future informs for the n+1'th time,
               //       before all the other futures have informed for the n'th time.
               //       This is because we do not check the identity of the informers
               //       but their count.
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

