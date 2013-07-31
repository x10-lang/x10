package x10.lang;

import x10.util.concurrent.AtomicInteger;
import x10.util.ArrayList;

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
    // The initial value of AtomicInteger() is 0. (?)
    //this.count.set(0);
    this.act = act;
    this.worker = Runtime.worker();
  }

  // {T isref, T haszero}
  public static def asyncWait[T](
    futures: ArrayList[Future[T]],
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
      Runtime.executeLocal(thisAct);
    else {
      count = task.count.addAndGet(-count);
      if (count == 0)
        Runtime.executeLocal(thisAct);
    }
  }   

  public static def asyncWait(
    future: Future[Any],
    block: ()=>void) {
    
    val thisAct = Runtime.initAsync(block);
    val task = new FTask(thisAct);

    val added = future.addIfNotSet(task);
    if (!added)
      Runtime.executeLocal(thisAct);
    else {
      val count = task.count.decrementAndGet();
      if (count == 0)
        Runtime.executeLocal(thisAct);      
    }    	  
  }
  
  public static def asyncWait[T](
    future: SFuture[T],
    block: ()=>void) {
    
    val thisAct = Runtime.initAsync(block);
    val task = new FTask(thisAct);

    future.add(task);
  }

  public def inform() {
    val c = count.incrementAndGet();
    if (c == 0) {
      val theAct = this.act;
      val theWorker = this.worker;
      Runtime.executeLocalInWorker(theAct, theWorker);
    }
  }

  // Note that this is called when the futures are not set or being concurrently set.
  public static def newFTask(
    futures: ArrayList[SFuture[Any]],
    block: ()=>void) {

    val thisAct = Runtime.initAsync(block);
    val fTask = new FTask(thisAct);
    val iter = futures.iterator();
    var c: Int = 0;
    while (iter.hasNext()) {
      val f = iter.next();
      f.add(fTask);
      c = c + 1;
    }
    fTask.count.set(-c);
  }

  public def now() {
    val theAct = this.act;
    val theWorker = this.worker;
    Runtime.executeLocalInWorker(theAct, theWorker);    
  }

}

