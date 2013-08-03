package futuresched.core;

import x10.compiler.NoInline;
import x10.util.concurrent.AtomicInteger;
import x10.util.ArrayList;
import x10.compiler.NoInline;

public class FTask {
   
  var count: AtomicInteger;
  var act: Activity;
  var worker: Runtime.Worker;
   
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

  public def exec() {
    val theAct = this.act;
    val theWorker = this.worker;
    @NoInline { Runtime.executeLocalInWorker(theAct, theWorker); }
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

  public def inform() {
    val c = count.incrementAndGet();
    if (c == 0)
      exec();
  }

  //----------------------------------------------------------------------------------

  public static def asyncWait[T](
    future: SFuture[T],
    block: ()=>void){T isref, T haszero} {

    val thisAct = Runtime.initAsync(block);
    val task = new FTask(thisAct);

    future.add(task);
    task.count.set(-1);
  }

  static val mainFinish: FinishState = Runtime.activity().finishState();
  public static def init(): FinishState {
    return mainFinish;
  }

  // Note that this is called when the futures are not set or being concurrently set.
  public static def newAsyncWait[T](
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

  public static def newAsyncWait[T](
    future: SFuture[T],
    block: ()=>void){T isref, T haszero}: FTask {


//    val thisAct = Runtime.initAsyncExtern(block);
//    thisAct.setFinish(mainFinish);
    val thisAct = new Activity(block, here, mainFinish);
    mainFinish.notifySubActivitySpawn(here);

    val fTask = new FTask(thisAct);
    future.add(fTask);

    fTask.count.set(-1);

    return fTask;
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

