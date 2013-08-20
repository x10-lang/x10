package futuresched.core;

import x10.compiler.NoInline;
import x10.util.Box;
import x10.util.ArrayList;
import x10.util.concurrent.Lock;
import x10.util.concurrent.AtomicInteger;

public class AndFTask extends FTask {
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

  public static def asyncWait[T](
    futures: ArrayList[Future[T]],
    block: ()=>void){T isref, T haszero} {

    val thisAct = initActEnclosed(block);
    val task = new AndFTask(thisAct);

    val iter = futures.iterator();
    var count: Int = 0;
    while (iter.hasNext()) {
      val f = iter.next();
      val added = f.addIfNotSet(task);
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

    val added = future.addIfNotSet(task);
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
      val added = f.addIfNotSet(task);
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

    val added = future.addIfNotSet(task);
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
      val added = f.addIfNotSet(task);
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

    val added = future.addIfNotSet(task);
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
    block: ()=>void){T isref, T haszero} { //: FTask {

//    val thisAct = new Activity(block, here, mainFinish);
//    mainFinish.notifySubActivitySpawn(here);
//    val fTask = new FTask(thisAct);
    val fTask = new AndFTask();
    fTask.block = block;

    val iter = futures.iterator();
    while (iter.hasNext()) {
      val f = iter.next();
      f.add(fTask);
    }
    fTask.count.set(-futures.size() as Int);
  }

  public static def sAsyncWait[T](
    future: SFuture[T],
    block: ()=>void){T isref, T haszero} { //: FTask {

//    val thisAct = new Activity(block, here, mainFinish);
//    mainFinish.notifySubActivitySpawn(here);
//    val task = new FTask(thisAct);

    val fTask = new AndFTask();
    fTask.block = block;

    future.add(fTask);
    fTask.count.set(-1);
//    return task;
  }

  public static def sAsyncWait(
    futures: ArrayList[SNotifier],
    block: ()=>void){ //: FTask {

//    val thisAct = new Activity(block, here, mainFinish);
//    mainFinish.notifySubActivitySpawn(here);
//    val fTask = new FTask(thisAct);

    val fTask = new AndFTask();
    fTask.block = block;

    val iter = futures.iterator();
    while (iter.hasNext()) {
      val f = iter.next();
      f.add(fTask);
    }
    fTask.count.set(-futures.size() as Int);
//    return fTask;
  }


  public static def sAsyncWait(
    future: SNotifier,
    block: ()=>void) { //: FTask {

//    val thisAct = new Activity(block, here, mainFinish);
//    mainFinish.notifySubActivitySpawn(here);
//    val fTask = new FTask(thisAct);

    val fTask = new AndFTask();
    fTask.block = block;

    future.add(fTask);

    fTask.count.set(-1);
//    return fTask;
  }

  public static def sAsyncWait(
    futures: ArrayList[SIntFuture],
    block: ()=>void) { //:FTask {

//    val thisAct = new Activity(block, here, mainFinish);
//    mainFinish.notifySubActivitySpawn(here);
//    val fTask = new FTask(thisAct);

    val fTask = new AndFTask();
    fTask.block = block;

    val iter = futures.iterator();
    while (iter.hasNext()) {
      val f = iter.next();
      f.add(fTask);
    }
    fTask.count.set(-futures.size() as Int);
//    return fTask;
  }

  public static def sAsyncWait(
    future: SIntFuture,
    block: ()=>void) {

    val fTask = new AndFTask();
    fTask.block = block;

    future.add(fTask);

    fTask.count.set(-1);
  }

   public static def enclosedSAsyncWait[T](
      future: SFuture[T],
      block: ()=>void){T isref, T haszero} {

      val thisAct = initActEnclosed(block);
      val task = new AndFTask(thisAct);

      future.add(task);
      task.count.set(-1);
   }

// ------------------------------------------------------------

  public def inform(f: Notifier) {
     if (!isDone) {
        val c = count.incrementAndGet();
        if (c == 0)
          exec();
     }
  }

  public def inform(f: SNotifier) {
    if (!isDone) {
       val c = count.incrementAndGet();
       if (c == 0) {
         act = initActEnclosed(block);
//         val state = a.finishState();
//         state.notifySubActivitySpawn(here);

         exec();
       }
    }
  }

// ------------------------------------------------------------

}