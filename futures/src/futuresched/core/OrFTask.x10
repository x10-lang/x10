package futuresched.core;

import x10.compiler.NoInline;
import x10.util.ArrayList;
import x10.util.Pair;
import x10.util.concurrent.Lock;
import x10.util.concurrent.AtomicInteger;

public class OrFTask[T, TP]{T isref, T haszero} extends FTask {

   public var finishState: FinishState;
   public var fun: (T, Any)=>void;

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
     fun: (T, Any)=>void){T isref, T haszero}: OrFTask[T, Any] {

     val task = new OrFTask[T, Any]();
     val finishState = captureFinish();
     task.finishState = finishState;
     task.fun = fun;

     val iter = futures.iterator();
     while (iter.hasNext()) {
        val f = iter.next();
        val added = f.addIfNotSet(task, null);
        if (!added) {
           val c = task.count.incrementAndGet();
           if (c == 1) {
              val v = f.get();
              //Console.OUT.println("Calling with " + v);
              val block = ()=>{ fun(v, null); };
              val thisAct = new Activity(block, here, finishState);
              task.act = thisAct;
              task.exec();
           }
        }
     }
     return task;
  }

  public static def asyncWaitOr[T1, T2](
     futures: ArrayList[T1],
     trans: (T1)=>Future[T2],
     fun: (T2, Any)=>void){T2 isref, T2 haszero}: OrFTask[T2, Any] {

     //val thisAct = initActEnclosed(block);
     val task = new OrFTask[T2, Any]();
     val finishState = captureFinish();
     task.finishState = finishState;
     task.fun = fun;

     //Console.OUT.println(futures);
     val iter = futures.iterator();
     while (iter.hasNext()) {
        val o = iter.next();
        val f = trans(o);
        val added = f.addIfNotSet(task, null);
//        Console.OUT.println(added);
        if (!added) {
           val c = task.count.incrementAndGet();
           if (c == 1) {
              val v = f.get();
              val block = ()=>{ fun(v, null); };
              val thisAct = new Activity(block, here, finishState);
              task.act = thisAct;
              task.exec();
           }
        }
     }
     return task;
  }

// ------------------------------------------------------
// sAsyncWaitOr

  public static def sAsyncWaitOr[T](
    futures: ArrayList[SFuture[T]],
//    fun: (SFuture[T])=>void){T isref, T haszero}: FTask {
    fun: (T, Any)=>void){T isref, T haszero}: OrFTask[T, Any] {
//    val block = ()=>{ fun(orSFuture) };

     val fTask = new OrFTask[T, Any]();
//     val finishState = mainFinish;
//     fTask.finishState = finishState;
     fTask.fun = fun;

    val iter = futures.iterator();
    while (iter.hasNext()) {
      val f = iter.next();
      f.add(fTask, null);
    }
    return fTask;
  }

  public static def sAsyncWaitOr[T1, T2](
     futures: ArrayList[T1],
     trans: (T1)=>SFuture[T2],
     fun: (T2, Any)=>void){T2 isref, T2 haszero}: OrFTask[T2, Any] {

     //val thisAct = initActEnclosed(block);
     val fTask = new OrFTask[T2, Any]();
//     val finishState = mainFinish;
//     fTask.finishState = finishState;
     fTask.fun = fun;

     val iter = futures.iterator();
     while (iter.hasNext()) {
        val o = iter.next();
        val f = trans(o);
        f.add(fTask, null);
     }
     return fTask;
  }

  public static def sAsyncWaitOr[T1, T2, T3](
     futures: ArrayList[T1],
     trans: (T1)=>Pair[SFuture[T2], T3],
     fun: (T2, T3)=>void){T2 isref, T2 haszero}: OrFTask[T2, T3] {

     //val thisAct = initActEnclosed(block);
     val fTask = new OrFTask[T2, T3]();
//     val finishState = mainFinish;
//     fTask.finishState = finishState;
     fTask.fun = fun as (T2, Any)=>void;

     val iter = futures.iterator();
     while (iter.hasNext()) {
        val o = iter.next();
        val p = trans(o);
        val f = p.first;
        val obj = p.second;
        f.add(fTask, obj);
     }
     return fTask;
  }

// ------------------------------------------------------

   public def inform(g: Boolean, v: Any, obj: Any) {
      var go: Boolean = recurring;
      if (!recurring && !isDone)
         go = count.compareAndSet(0, 1);
      if (go) {
//         val f = n as Future[T];
//         val v = f.get();
         val block = ()=>{ val fun = this.fun; fun(v as T, obj); };
         if (g) {
            val thisAct = new Activity(block, here, this.finishState);
            this.act = thisAct;
         } else {
            val thisAct = initActEnclosed(block);
            this.act = thisAct;
         }


         exec();
      }
   }

//   public def inform(n: SNotifier) {
//      var go: Boolean = recurring;
//      if (!recurring && !isDone)
//         go = count.compareAndSet(0, 1);
//      if (go) {
//         val f = n as SFuture[T];
//         val v = f.get();
//         val block = ()=>{ val fun = this.fun; fun(v); };
//
//         val thisAct = initActEnclosed(block);
//         this.act = thisAct;
//         exec();
//      }
//   }

// ------------------------------------------------------

}
