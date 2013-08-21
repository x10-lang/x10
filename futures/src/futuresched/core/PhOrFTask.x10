package futuresched.core;

import x10.compiler.NoInline;
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

// -------------------------------------------------------------------
// phasedAsyncWaitOr
// ...

// -------------------------------------------------------------------
// phasedSAsyncWaitOr

   public static def sPhasedAsyncWaitOr[T](
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

   public static def sPhasedAsyncWaitOr[T1, T2](
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
// Batch task scheduling

   public def inform(n: Notifier) {
      if (!isDone) {
         val done = count.compareAndSet(0, 1);
         if (done) {
            val f = n as Future[T];
            val v = f.get();
            val block = ()=>{ val fun = this.fun; fun(v); Phasing.end(); };
            val thisAct = new Activity(block, here, this.finishState);
            this.act = thisAct;

            Phasing.schedule(this);
         }
      }
   }

   public def inform(n: SNotifier) {
      if (!isDone) {
         val done = count.compareAndSet(0, 1);
         if (done) {
            val f = n as SFuture[T];
            val v = f.get();
            val block = ()=>{ val fun = this.fun; fun(v); Phasing.end(); };

            val thisAct = initActEnclosed(block);
            this.act = thisAct;

            Phasing.schedule(this);
         }
      }
   }

}

