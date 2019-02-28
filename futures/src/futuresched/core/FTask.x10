package futuresched.core;

import x10.compiler.NoInline;
import x10.xrx.Activity;
import x10.xrx.FinishState;
import x10.xrx.Runtime;
import x10.xrx.Worker;
import x10.util.Box;
import x10.util.Pair;
import x10.util.ArrayList;
import x10.util.concurrent.Lock;
import x10.util.concurrent.AtomicInteger;



public abstract class FTask {
   
   var act: Activity;
   var worker: Worker;
   // Scheduling the task in the worker that created it for locality.

   var count: AtomicInteger;
   var isDone: Boolean = false;
   var enclosed: Boolean;

   public var recurring: Boolean = false;
   // For a task that depends on only futures that not already set,
   // setting recurring to true makes it callable multiple times.

   static val mainFinish: FinishState = Runtime.activity().finishState();
   public static def init(): FinishState {
      return mainFinish;
   }

   public def this(count: Int, act: Activity, worker: Worker, enclosed: Boolean) {
      this.count = new AtomicInteger();
      this.count.set(count);
      this.act = act;
      this.worker = worker;
      this.enclosed = enclosed;
   }
   
   public def this(act: Activity, enclosed: Boolean) {
      this.count = new AtomicInteger();
      this.act = act;
      this.worker = Runtime.worker();
      this.enclosed = enclosed;
   }

   public def this(enclosed: Boolean) {
      this.count = new AtomicInteger();
      this.act = act;
      this.worker = Runtime.worker();
      this.enclosed = enclosed;
   }

   public def this() {
      this.count = new AtomicInteger();
      this.worker = Runtime.worker();
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

  public abstract def inform(v: Any, obj: Any): void;

//  public abstract def inform(f: SNotifier): void;

// ------------------------------------------------------
// asyncAnd

   public static def asyncAnd[T](
      futures: ArrayList[Future[T]],
      block: ()=>void){T isref, T haszero}: AndFTask {
      return AndFTask.asyncAnd(futures, block);
   }

   public static def asyncAnd[T](
      future: Future[T],
      block: ()=>void){T isref, T haszero}: AndFTask {
      return AndFTask.asyncAnd(future, block);
   }

   // To allow different types of futures.
   public static def asyncAnd(
      futures: ArrayList[Notifier],
      block: ()=>void): AndFTask {
      return AndFTask.asyncAnd(futures, block);
   }

   public static def asyncAnd(
      future: Notifier,
      block: ()=>void): AndFTask {
      return AndFTask.asyncAnd(future, block);
   }

   public static def asyncAnd(
      futures: ArrayList[IntFuture],
      block: ()=>void): AndFTask {
      return AndFTask.asyncAnd(futures, block);
   }

   public static def asyncAnd(
      futures: ArrayList[DoubleFuture],
      block: ()=>void): AndFTask {
      return AndFTask.asyncAnd(futures, block);
   }

   public static def asyncAnd(
      future: IntFuture,
      block: ()=>void): AndFTask {
      return AndFTask.asyncAnd(future, block);
   }

//   // Not to create extra closures:
//   public static def asyncAndSet(
//      futures: ArrayList[IntFuture],
//      fun: ()=>Int,
//      future: IntFuture): AndFTask {
//      return AndFTask.asyncAndSet(futures, fun, future);
//   }

//----------------------------------------------------------------------------------
// newAnd
// Note that newAnd is called when the futures are not already set or being concurrently set.

   var block: ()=>void;
   public static def newAnd[T](
      futures: ArrayList[SFuture[T]],
      block: ()=>void){T isref, T haszero}: AndFTask {
      return AndFTask.newAnd(futures, block);
   }

   public static def newAnd[T](
      future: SFuture[T],
      block: ()=>void){T isref, T haszero}: AndFTask {
      return AndFTask.newAnd(future, block);
   }

   public static def newAnd(
      futures: ArrayList[SNotifier],
      block: ()=>void): AndFTask {
      return AndFTask.newAnd(futures, block);
   }

   public static def newAnd(
      future: SNotifier,
      block: ()=>void): AndFTask {
      return AndFTask.newAnd(future, block);
   }

   public static def newAnd(
      futures: ArrayList[SIntFuture],
      block: ()=>void): AndFTask {
      return AndFTask.newAnd(futures, block);
   }

   public static def newAnd(
      future: SIntFuture,
      block: ()=>void): AndFTask {
      return AndFTask.newAnd(future, block);
   }

   public static def newAnd(
      futures: ArrayList[SDoubleFuture],
      block: ()=>void): AndFTask {
      return AndFTask.newAnd(futures, block);
   }

   public static def enclosedSAsyncWait[T](
      future: SFuture[T],
      block: ()=>void){T isref, T haszero} {
      AndFTask.enclosedSAsyncWait(future, block);
   }

// ------------------------------------------------------
// asyncOr

   public static def asyncOr[T](
      futures: ArrayList[Future[T]],
      fun: (T, Any)=>void){T isref, T haszero}: OrFTask[T, Any] {
      return OrFTask.asyncOr(futures, fun);
   }

   public static def asyncOr[T1, T2](
      futures: ArrayList[T1],
      trans: (T1)=>Future[T2],
      fun: (T2, Any)=>void){T2 isref, T2 haszero}: OrFTask[T2, Any] {
      return OrFTask.asyncOr(futures, trans, fun);
   }

// ------------------------------------------------------
// newOr

   public static def newOr[T](
      futures: ArrayList[SFuture[T]],
//    fun: (SFuture[T])=>void){T isref, T haszero}: FTask {
      fun: (T, Any)=>void){T isref, T haszero}: OrFTask[T, Any] {
      return OrFTask.newOr(futures, fun);
   }

  public static def newOr[T1, T2, T3](
     futures: ArrayList[T1],
     trans: (T1)=>Pair[SFuture[T2], T3],
     fun: (T2, T3)=>void){T2 isref, T2 haszero}: OrFTask[T2, T3] {
     return OrFTask.newOr(futures, trans, fun);
  }
// -------------------------------------------------------------------
// asyncPhasedAnd
// ...

// -------------------------------------------------------------------
// newPhasedAnd

  public static def newPhasedAnd[T](
    futures: ArrayList[SFuture[T]],
    block: ()=>void){T isref, T haszero}: PhAndFTask {
    return PhAndFTask.newPhasedAnd(futures, block);
  }

  public static def newPhasedAnd[T](
    future: SFuture[T],
    block: ()=>void){T isref, T haszero}: PhAndFTask {
    return PhAndFTask.newPhasedAnd(future, block);
  }

  public static def newPhasedAnd(
    futures: ArrayList[SNotifier],
    block: ()=>void): PhAndFTask {
    return PhAndFTask.newPhasedAnd(futures, block);
  }

  public static def newPhasedAnd(
    future: SNotifier,
    block: ()=>void): PhAndFTask {
    return PhAndFTask.newPhasedAnd(future, block);
  }

  public static def newPhasedAnd(
    futures: ArrayList[SIntFuture],
    block: ()=>void): PhAndFTask {
    return PhAndFTask.newPhasedAnd(futures, block);
  }

  public static def newPhasedAnd(
    future: SIntFuture,
    block: ()=>void): PhAndFTask {
    return PhAndFTask.newPhasedAnd(future, block);
  }

  // ---------------------------------------

   public static def newPhasedDoubleAdd(
      futures: ArrayList[SDoubleFuture],
      cond: (Double)=>Boolean,
      block: (Double)=>void,
      stop: ()=>void,
      restart: ()=>void): PhDoubleAddFTask {

      return PhDoubleAddFTask.newPhasedDoubleAdd(
         futures, cond, block, stop, restart);
   }

   public static def newPhasedDoubleAdd(
      futures: ArrayList[SUDoubleFuture],
      cond: (Double)=>Boolean,
      block: (Double)=>void,
      stop: ()=>void,
      restart: ()=>void): PhDoubleAddFTask {
      return PhDoubleAddFTask.newPhasedDoubleAdd(
         futures, cond, block, stop, restart);
   }

   public static def newPhasedDoubleAdd[T](
      objs: ArrayList[T],
      trans: (T)=>SDoubleFuture,
      cond: (Double)=>Boolean,
      block: (Double)=>void,
      stop: ()=>void,
      restart: ()=>void): PhDoubleAddFTask {
      return PhDoubleAddFTask.newPhasedDoubleAdd(
         objs, trans, cond, block, stop, restart);
   }

   public static def newPhasedDoubleAdd[T](
      objs: ArrayList[T],
      trans: (T)=>SUDoubleFuture,
      cond: (Double)=>Boolean,
      block: (Double)=>void,
      stop: ()=>void,
      restart: ()=>void): PhDoubleAddFTask {
      return PhDoubleAddFTask.newPhasedDoubleAdd(
         objs, trans, cond, block, stop, restart);
   }

// -------------------------------------------------------------------
// asyncPhasedOr
// ...

// -------------------------------------------------------------------
// newPhasedOr

   public static def newPhasedOr[T](
      futures: ArrayList[SFuture[T]],
      fun: (T, Any)=>void){T isref, T haszero}: PhOrFTask[T, Any] {
      return PhOrFTask.newPhasedOr(futures, fun);
   }

   public static def newPhasedOr[T1, T2](
      futures: ArrayList[T1],
      trans: (T1)=>SFuture[T2],
      fun: (T2, Any)=>void){T2 isref, T2 haszero}: PhOrFTask[T2, Any] {
      return PhOrFTask.newPhasedOr(futures, trans, fun);
   }

   public static def newPhasedOr[T1, T2, T3](
      list: ArrayList[T1],
      trans: (T1)=>Pair[SFuture[T2], T3],
      fun: (T2, T3)=>void){T2 isref, T2 haszero}: PhOrFTask[T2, T3] {
      return PhOrFTask.newPhasedOr(list, trans, fun);
   }

   // -----

   public static def newPhasedOr(
      futures: ArrayList[SIntFuture],
      fun: (Int, Any)=>void): IntPhOrFTask[Any] {
      return IntPhOrFTask.newPhasedOr(futures, fun);
   }

   public static def newPhasedOr[T](
      futures: ArrayList[T],
      trans: (T)=>SIntFuture,
      fun: (Int, Any)=>void): IntPhOrFTask[Any] {
      return IntPhOrFTask.newPhasedOr(futures, trans, fun);
   }

   public static def newPhasedOr[T, T2](
     futures: ArrayList[T],
     trans: (T)=>Pair[SIntFuture, T2],
     fun: (Int, T2)=>void): IntPhOrFTask[T2] {
     return IntPhOrFTask.newPhasedOr(futures, trans, fun);
   }

// ------------------------------------------------------

}

