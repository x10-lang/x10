package futuresched.core;

import x10.compiler.NoInline;
import x10.util.Box;
import x10.util.ArrayList;
import x10.util.concurrent.Lock;
import x10.util.concurrent.AtomicInteger;

public abstract class FTask {
   
   var act: Activity;
   var worker: Runtime.Worker;

   var count: AtomicInteger;
   var isDone: Boolean = false;

   static val mainFinish: FinishState = Runtime.activity().finishState();
   public static def init(): FinishState {
      return mainFinish;
   }

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

   public static def initActEnclosed(block: ()=>void): Activity {
     // The usual initial steps:
     val a = Runtime.activity();
//     a.ensureNotInAtomic();

     val state = a.finishState();
     state.notifySubActivitySpawn(here);

     return new Activity(block, here, state);
   }

   public static def captureFinish(): FinishState {
      val a = Runtime.activity();
      a.ensureNotInAtomic();

      val state = a.finishState();
      state.notifySubActivitySpawn(here);

      return state;
   }

  public abstract def inform(f: Notifier): void;

  public abstract def inform(f: SNotifier): void;

// ------------------------------------------------------
// asyncWait (asyncWaitAnd)

   public static def asyncWait[T](
      futures: ArrayList[Future[T]],
      block: ()=>void){T isref, T haszero} {
      AndFTask.asyncWait(futures, block);
   }

   public static def asyncWait[T](
      future: Future[T],
      block: ()=>void){T isref, T haszero} {
      AndFTask.asyncWait(future, block);
   }

   // To allow different types of futures.
   public static def asyncWait(
      futures: ArrayList[Notifier],
      block: ()=>void) {
      AndFTask.asyncWait(futures, block);
   }

   public static def asyncWait(
      future: Notifier,
      block: ()=>void) {
      AndFTask.asyncWait(future, block);
   }


   public static def asyncWait(
      futures: ArrayList[IntFuture],
      block: ()=>void) {
      AndFTask.asyncWait(futures, block);
   }

   public static def asyncWait(
      future: IntFuture,
      block: ()=>void) {
      AndFTask.asyncWait(future, block);
   }

//----------------------------------------------------------------------------------
// sAsyncWait (sAsyncWaitAnd)
// Note that sAsyncWait is called when the futures are not already set or being concurrently set.

   var block: ()=>void;
   public static def sAsyncWait[T](
      futures: ArrayList[SFuture[T]],
      block: ()=>void){T isref, T haszero} {
      AndFTask.sAsyncWait(futures, block);
   }

   public static def sAsyncWait[T](
      future: SFuture[T],
      block: ()=>void){T isref, T haszero} {
      AndFTask.sAsyncWait(future, block);
   }

   public static def sAsyncWait(
      futures: ArrayList[SNotifier],
      block: ()=>void){
      AndFTask.sAsyncWait(futures, block);
   }

   public static def sAsyncWait(
      future: SNotifier,
      block: ()=>void) {
      AndFTask.sAsyncWait(future, block);
   }

   public static def sAsyncWait(
      futures: ArrayList[SIntFuture],
      block: ()=>void) {
      AndFTask.sAsyncWait(futures, block);
   }

   public static def sAsyncWait(
      future: SIntFuture,
      block: ()=>void) {
      AndFTask.sAsyncWait(future, block);
   }

   public static def enclosedSAsyncWait[T](
      future: SFuture[T],
      block: ()=>void){T isref, T haszero} {
      AndFTask.enclosedSAsyncWait(future, block);
   }

// ------------------------------------------------------
// asyncWaitOr

   public static def asyncWaitOr[T](
      futures: ArrayList[Future[T]],
      fun: (T)=>void){T isref, T haszero} {
      OrFTask.asyncWaitOr(futures, fun);
   }

   public static def asyncWaitOr[T1, T2](
      futures: ArrayList[T1],
      trans: (T1)=>Future[T2],
      fun: (T2)=>void){T2 isref, T2 haszero} {
      OrFTask.asyncWaitOr(futures, trans, fun);
   }

// ------------------------------------------------------
// sAsyncWaitOr

   public static def sAsyncWaitOr[T](
      futures: ArrayList[SFuture[T]],
//    fun: (SFuture[T])=>void){T isref, T haszero}: FTask {
      fun: (T)=>void){T isref, T haszero}: FTask {
      return OrFTask.sAsyncWaitOr(futures, fun);
   }

   public static def sAsyncWaitOr[T1, T2](
      futures: ArrayList[T1],
      trans: (T1)=>SFuture[T2],
      fun: (T2)=>void){T2 isref, T2 haszero} {
      return OrFTask.sAsyncWaitOr(futures, trans, fun);
   }

// -------------------------------------------------------------------
// phasedAsyncWaitOr
// ...

// -------------------------------------------------------------------
// phasedSAsyncWaitOr

   public static def phasedSAsyncWaitOr[T](
      futures: ArrayList[SFuture[T]],
      fun: (T)=>void){T isref, T haszero}: FTask {
      return PhOrFTask.phasedSAsyncWaitOr(futures, fun);
   }

  public static def phasedSAsyncWaitOr[T1, T2](
      futures: ArrayList[T1],
      trans: (T1)=>SFuture[T2],
      fun: (T2)=>void){T2 isref, T2 haszero} {
      return PhOrFTask.phasedSAsyncWaitOr(futures, trans, fun);
  }

// ------------------------------------------------------
  /*
  public def now() {
    val theAct = this.act;
//    val state = Runtime.getEnclosingFinish();
//    theAct.setFinish(state);
    val theWorker = this.worker;
    @NoInline { Runtime.executeLocalInWorker(theAct, theWorker); }
    // The annotation is added to go around a bug.
  }
  */
}

