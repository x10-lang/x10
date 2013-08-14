package futuresched.core;

import x10.compiler.NoInline;
import x10.util.concurrent.AtomicInteger;
import x10.util.ArrayList;
import x10.compiler.NoInline;

public class FTask {
   
  var count: AtomicInteger;
  var act: Activity;
  var worker: Runtime.Worker;
  //var isAnd: Boolean = true;
  var isDone: Boolean = false;

  public def this(count: Int, act: Activity, worker: Runtime.Worker) {
    this.count = new AtomicInteger();
    this.count.set(count);
    this.act = act;
    this.worker = worker;
  }
   
  public def this(act: Activity) {
    this.count = new AtomicInteger();
    this.act = act;
    this.worker = Runtime.worker();
    // Scheduling the task in the worker that created it brings locality.
  }

  public def this() {
    this.count = new AtomicInteger();
    this.worker = Runtime.worker();
    // Scheduling the task in the worker that created it brings locality.
  }

  public def exec() {
    val theAct = this.act;
    val theWorker = this.worker;
    @NoInline { Runtime.executeLocalInWorker(theAct, theWorker); }
    isDone = true;
    // The annotation is added to go around a bug.
  }

  public static def asyncWait[T](
    futures: ArrayList[Future[T]],
    block: ()=>void){T isref, T haszero} {

    val thisAct = Runtime.initAsync(block);
    val task = new FTask(thisAct);

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

    val thisAct = Runtime.initAsync(block);
    val task = new FTask(thisAct);

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
    val thisAct = Runtime.initAsync(block);
    val task = new FTask(thisAct);

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
    
    val thisAct = Runtime.initAsync(block);
    val task = new FTask(thisAct);

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

    val thisAct = Runtime.initAsync(block);
    val task = new FTask(thisAct);

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

    val thisAct = Runtime.initAsync(block);
    val task = new FTask(thisAct);

    val added = future.addIfNotSet(task);
    if (!added)
      task.exec();
    else {
      val count = task.count.decrementAndGet();
      if (count == 0)
        task.exec();
    }
  }

  public def inform(f: Notifier) {
     if (!isDone) {
        val c = count.incrementAndGet();
        if (c == 0)
          exec();
     }
  }

  public static def captureFinish(): FinishState {
     val a = Runtime.activity();
     a.ensureNotInAtomic();

     val state = a.finishState();
     state.notifySubActivitySpawn(here);

     return state;
  }

  static class OrFTask[T]{T isref, T haszero} extends FTask {

    public var finishState: FinishState;
    public var orFun: (T)=>void;

    public def inform(n: Notifier) {
      if (!isDone) {
         val done = count.compareAndSet(0, 1);
         if (done) {
            val f = n as Future[T];
            val v = f.get();
            val block = ()=>{ val fun = this.orFun; fun(v); };
            val thisAct = new Activity(block, here, this.finishState);
            this.act = thisAct;
            exec();
         }
       }
    }

    public def inform(n: SNotifier) {
//      Console.OUT.println("notifying " + n);
      if (!isDone) {
         val done = count.compareAndSet(0, 1);
         if (done) {
            val f = n as SFuture[T];
            val v = f.get();
            val block = ()=>{ val fun = this.orFun; fun(v); };

            val thisAct = Runtime.initAsync(block);
//            val finishState = this.finishState;
//            val finishState = captureFinish();
//            val thisAct = new Activity(block, here, finishState);
            this.act = thisAct;
            exec();
         }
      }
    }

  }

  public static def asyncWaitOr[T](
     futures: ArrayList[Future[T]],
     fun: (T)=>void){T isref, T haszero} {

     val task = new OrFTask[T]();
     val finishState = captureFinish();
     task.finishState = finishState;
     task.orFun = fun;
//     task.isAnd = false;

     val iter = futures.iterator();
     while (iter.hasNext()) {
        val f = iter.next();
        val added = f.addIfNotSet(task);
        if (!added) {
           val done = task.count.compareAndSet(0, 1);
           if (done) {
              val v = f.get();
              //Console.OUT.println("Calling with " + v);
              val block = ()=>{ fun(v); };
              val thisAct = new Activity(block, here, finishState);
              task.act = thisAct;
              task.exec();
           }
        }
     }
  }

  public static def asyncWaitOr[T1, T2](
     futures: ArrayList[T1],
     trans: (T1)=>Future[T2],
     fun: (T2)=>void){T2 isref, T2 haszero} {

     //val thisAct = Runtime.initAsync(block);
     val task = new OrFTask[T2]();
     val finishState = captureFinish();
     task.finishState = finishState;
     task.orFun = fun;
//     task.isAnd = false;

     //Console.OUT.println(futures);
     val iter = futures.iterator();
     while (iter.hasNext()) {
        val o = iter.next();
        val f = trans(o);
        val added = f.addIfNotSet(task);
//        Console.OUT.println(added);
        if (!added) {
           val done = task.count.compareAndSet(0, 1);
           if (done) {
              val v = f.get();
              val block = ()=>{ fun(v); };
              val thisAct = new Activity(block, here, finishState);
              task.act = thisAct;
              task.exec();
           }
        }
     }
  }


  //----------------------------------------------------------------------------------
  // Note that sAsyncWait is called when the futures are not already set or being concurrently set.

  static val mainFinish: FinishState = Runtime.activity().finishState();
  public static def init(): FinishState {
    return mainFinish;
  }

  public static def sAsyncWait[T](
    futures: ArrayList[SFuture[T]],
    block: ()=>void){T isref, T haszero}: FTask {

    val thisAct = new Activity(block, here, mainFinish);
    mainFinish.notifySubActivitySpawn(here);
//    val thisAct = Runtime.initAsyncExtern(block);
//    thisAct.setFinish(mainFinish);

    val fTask = new FTask(thisAct);
    val iter = futures.iterator();
    while (iter.hasNext()) {
      val f = iter.next();
      f.add(fTask);
    }
    fTask.count.set(-futures.size() as Int);
    return fTask;
  }

  public static def sAsyncWait[T](
    future: SFuture[T],
    block: ()=>void){T isref, T haszero}: FTask {

//    val thisAct = Runtime.initAsync(block);
    val thisAct = new Activity(block, here, mainFinish);
    mainFinish.notifySubActivitySpawn(here);

    val task = new FTask(thisAct);

    future.add(task);
    task.count.set(-1);
    return task;
  }

  public static def sAsyncWait(
    futures: ArrayList[SNotifier],
    block: ()=>void): FTask {

    val thisAct = new Activity(block, here, mainFinish);
    mainFinish.notifySubActivitySpawn(here);
//    val thisAct = Runtime.initAsyncExtern(block);
//    thisAct.setFinish(mainFinish);

    val fTask = new FTask(thisAct);
    val iter = futures.iterator();
    while (iter.hasNext()) {
      val f = iter.next();
      f.add(fTask);
    }
    fTask.count.set(-futures.size() as Int);
    return fTask;
  }


  public static def sAsyncWait(
    future: SNotifier,
    block: ()=>void): FTask {
//    val thisAct = Runtime.initAsyncExtern(block);
//    thisAct.setFinish(mainFinish);
    val thisAct = new Activity(block, here, mainFinish);
    mainFinish.notifySubActivitySpawn(here);

    val fTask = new FTask(thisAct);
    future.add(fTask);

    fTask.count.set(-1);

    return fTask;
  }

  public static def sAsyncWait(
    futures: ArrayList[SIntFuture],
    block: ()=>void): FTask {

    val thisAct = new Activity(block, here, mainFinish);
    mainFinish.notifySubActivitySpawn(here);
//    val thisAct = Runtime.initAsyncExtern(block);
//    thisAct.setFinish(mainFinish);

    val fTask = new FTask(thisAct);
    val iter = futures.iterator();
    while (iter.hasNext()) {
      val f = iter.next();
      f.add(fTask);
    }
    fTask.count.set(-futures.size() as Int);
    return fTask;
  }

  public static def sAsyncWait(
    future: SIntFuture,
    block: ()=>void): FTask {
//    val thisAct = Runtime.initAsyncExtern(block);
//    thisAct.setFinish(mainFinish);
    val thisAct = new Activity(block, here, mainFinish);
    mainFinish.notifySubActivitySpawn(here);

    val fTask = new FTask(thisAct);
    future.add(fTask);

    fTask.count.set(-1);

    return fTask;
  }

  public static def enclosedSAsyncWait[T](
    future: SFuture[T],
    block: ()=>void){T isref, T haszero} {

    val thisAct = Runtime.initAsync(block);
    val task = new FTask(thisAct);

    future.add(task);
    task.count.set(-1);
  }

  public static def sAsyncWaitOr[T](
    futures: ArrayList[SFuture[T]],
//    fun: (SFuture[T])=>void){T isref, T haszero}: FTask {
    fun: (T)=>void){T isref, T haszero}: FTask {
//    val block = ()=>{ fun(orSFuture) };

     val fTask = new OrFTask[T]();
     val finishState = mainFinish;
     fTask.finishState = finishState;
     fTask.orFun = fun;

    val iter = futures.iterator();
    while (iter.hasNext()) {
      val f = iter.next();
      f.add(fTask);
    }
    return fTask;
  }

  public static def sAsyncWaitOr[T1, T2](
     futures: ArrayList[T1],
     trans: (T1)=>SFuture[T2],
     fun: (T2)=>void){T2 isref, T2 haszero} {

     //val thisAct = Runtime.initAsync(block);
     val fTask = new OrFTask[T2]();
//     val finishState = mainFinish;
//     fTask.finishState = finishState;
     fTask.orFun = fun;
//     task.isAnd = false;

     //Console.OUT.println(futures);
     val iter = futures.iterator();
     while (iter.hasNext()) {
        val o = iter.next();
        val f = trans(o);
        f.add(fTask);
     }
     return fTask;

  }

  public def inform(f: SNotifier) {
    if (!isDone) {
       val c = count.incrementAndGet();
       if (c == 0)
         exec();
    }
  }

  public def now() {
    val theAct = this.act;
//    Console.OUT.println("1");
//    val state = Runtime.getEnclosingFinish();
//    Console.OUT.println("2");
//    theAct.setFinish(state);
//    Console.OUT.println("3");
    val theWorker = this.worker;
//    Console.OUT.println("4");
    @NoInline { Runtime.executeLocalInWorker(theAct, theWorker); }
    // The annotation is added to go around a bug.
//    Console.OUT.println("5");
  }

  //----------------------------------------------------------------------------------

}

