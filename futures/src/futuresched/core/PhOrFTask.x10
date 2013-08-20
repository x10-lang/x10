package futuresched.core;

import x10.compiler.NoInline;
import x10.util.Box;
import x10.util.ArrayList;
import x10.util.concurrent.Lock;
import x10.util.concurrent.AtomicInteger;

public class PhOrFTask[T]{T isref, T haszero} extends FTask {

   public var finishState: FinishState;
   public var fun: (T)=>void;

   public def this(count: Int, act: Activity, worker: Runtime.Worker) {
      super(count, act, worker);
   }

   public def this(act: Activity) {
      super(act);
   }

   public def this() {
      super();
   }

   static val thisPhaseCount: AtomicInteger = new AtomicInteger();
   // Todo: This has to be replaced by a queue per thread.
   static class TaskBag {

      val lock = new Lock();
      public var list: ArrayList[FTask] = new ArrayList[FTask]();

      public def add(t: FTask) {
         lock.lock();
         list.add(t);
         lock.unlock();
      }

      var phase: Int = 0;
   }
   static val deferredTasks: TaskBag = new TaskBag();

   static def nextFun() {
    val i = thisPhaseCount.decrementAndGet();
    if (i == 0) {
       val s = deferredTasks.list.size() as Int;
       thisPhaseCount.set(s);
       val iter = deferredTasks.list.iterator();
       while (iter.hasNext()) {
          val task = iter.next();
          task.exec();
       }
       deferredTasks.list = new ArrayList[FTask]();
       deferredTasks.phase += 1;
    }
   }

// -------------------------------------------------------------------
// phasedAsyncWaitOr
// ...

// -------------------------------------------------------------------
// phasedSAsyncWaitOr

  public static def phasedSAsyncWaitOr[T](
    futures: ArrayList[SFuture[T]],
//    fun: (SFuture[T])=>void){T isref, T haszero}: FTask {
    fun: (T)=>void){T isref, T haszero}: FTask {
//    val block = ()=>{ fun(orSFuture) };

     val fTask = new PhOrFTask[T]();
//     val finishState = mainFinish;
//     fTask.finishState = finishState;
     fTask.fun = fun;

    val iter = futures.iterator();
    while (iter.hasNext()) {
      val f = iter.next();
      f.add(fTask);
    }
    return fTask;
  }

  public static def phasedSAsyncWaitOr[T1, T2](
     futures: ArrayList[T1],
     trans: (T1)=>SFuture[T2],
     fun: (T2)=>void){T2 isref, T2 haszero} {

     //val thisAct = initActEnclosed(block);
     val fTask = new PhOrFTask[T2]();
//     val finishState = mainFinish;
//     fTask.finishState = finishState;
     fTask.fun = fun;

     val iter = futures.iterator();
     while (iter.hasNext()) {
        val o = iter.next();
        val f = trans(o);
        f.add(fTask);
     }
     return fTask;

  }

// -------------------------------------------------------------------
   public def inform(n: Notifier) {
    if (!isDone) {
       val done = count.compareAndSet(0, 1);
       if (done) {
          val f = n as Future[T];
          val v = f.get();
          val block = ()=>{ val fun = this.fun; fun(v); nextFun(); };
          val thisAct = new Activity(block, here, this.finishState);
          this.act = thisAct;

          if (deferredTasks.phase == 0) {
             thisPhaseCount.incrementAndGet();
             deferredTasks.phase = 1;
             exec();
          } else
             deferredTasks.add(this);
       }
    }
   }

   public def inform(n: SNotifier) {
    if (!isDone) {
       val done = count.compareAndSet(0, 1);
       if (done) {
          val f = n as SFuture[T];
          val v = f.get();
          val block = ()=>{ val fun = this.fun; fun(v); nextFun(); };

          val thisAct = initActEnclosed(block);
          this.act = thisAct;

          if (deferredTasks.phase == 0) {
             thisPhaseCount.incrementAndGet();
             deferredTasks.phase = 1;
             exec();
          } else
             deferredTasks.add(this);
       }
    }
   }

}

