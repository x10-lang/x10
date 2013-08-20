package futuresched.core;

import x10.compiler.NoInline;
import x10.util.Box;
import x10.util.ArrayList;
import x10.util.concurrent.Lock;
import x10.util.concurrent.AtomicInteger;

public class OrFTask[T]{T isref, T haszero} extends FTask {

   public var finishState: FinishState;
   public var orFun: (T)=>void;

   public def this(count: Int, act: Activity, worker: Runtime.Worker) {
      super(count, act, worker);
   }

   public def this(act: Activity) {
      super(act);
   }

   public def this() {
      super();
   }

// ------------------------------------------------------
// asyncWaitOr

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

     //val thisAct = initActEnclosed(block);
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

// ------------------------------------------------------
// sAsyncWaitOr

  public static def sAsyncWaitOr[T](
    futures: ArrayList[SFuture[T]],
//    fun: (SFuture[T])=>void){T isref, T haszero}: FTask {
    fun: (T)=>void){T isref, T haszero}: FTask {
//    val block = ()=>{ fun(orSFuture) };

     val fTask = new OrFTask[T]();
//     val finishState = mainFinish;
//     fTask.finishState = finishState;
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

     //val thisAct = initActEnclosed(block);
     val fTask = new OrFTask[T2]();
//     val finishState = mainFinish;
//     fTask.finishState = finishState;
     fTask.orFun = fun;

     val iter = futures.iterator();
     while (iter.hasNext()) {
        val o = iter.next();
        val f = trans(o);
        f.add(fTask);
     }
     return fTask;
  }

// ------------------------------------------------------

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
      if (!isDone) {
         val done = count.compareAndSet(0, 1);
         if (done) {
            val f = n as SFuture[T];
            val v = f.get();
            val block = ()=>{ val fun = this.orFun; fun(v); };

            val thisAct = initActEnclosed(block);
            this.act = thisAct;
            exec();
         }
      }
   }

// ------------------------------------------------------

}
