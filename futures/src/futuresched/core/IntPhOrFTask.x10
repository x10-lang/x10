package futuresched.core;

import x10.compiler.NoInline;
import x10.util.ArrayList;
import x10.util.Pair;
import x10.util.concurrent.Lock;
import x10.util.concurrent.AtomicInteger;

public class IntPhOrFTask[TP] extends FTask {

   public var finishState: FinishState;
   public var fun: (Int, TP)=>void;

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
// asyncPhasedOr
// ...

// -------------------------------------------------------------------
// newPhasedOr

   public static def newPhasedOr(
      futures: ArrayList[SIntFuture],
//    fun: (SFuture[T])=>void){T isref, T haszero}: FTask {
      fun: (Int, Any)=>void): IntPhOrFTask[Any] {

      val fTask = new IntPhOrFTask[Any]();
      fTask.fun = fun;

      val iter = futures.iterator();
      while (iter.hasNext()) {
         val f = iter.next();
         f.add(fTask, null);
      }
      return fTask;
   }

   public static def newPhasedOr[T1](
      futures: ArrayList[T1],
      trans: (T1)=>SIntFuture,
      fun: (Int, Any)=>void): IntPhOrFTask[Any] {

      val fTask = new IntPhOrFTask[Any]();
      fTask.fun = fun;

      val iter = futures.iterator();
      while (iter.hasNext()) {
         val o = iter.next();
         val f = trans(o);
         f.add(fTask, null);
      }
      return fTask;
   }

  public static def newPhasedOr[T, T2](
     futures: ArrayList[T],
     trans: (T)=>Pair[SIntFuture, T2],
     fun: (Int, T2)=>void): IntPhOrFTask[T2] {

     //val thisAct = initActEnclosed(block);
     val fTask = new IntPhOrFTask[T2]();
//     val finishState = mainFinish;
//     fTask.finishState = finishState;
     fTask.fun = fun; //as (Int, Any)=>void;

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


// -------------------------------------------------------------------
// Deferred task scheduling

   public def inform(g: Boolean, v: Any, obj: Any) {
//      Console.OUT.println("Informing");
      var go: Boolean = recurring;
      if (!recurring && !isDone)
         go = count.compareAndSet(0, 1);
      if (go) {
//         val f = n as IntFuture;
//         val v = f.get();
         val block = ()=>{
//            Console.OUT.println("1");
            val fun = this.fun;
//            Console.OUT.println("2");
            fun(v as Int, obj as TP);
//            Console.OUT.println("3");
            Phasing.end();
//            Console.OUT.println("4");
         };
         if (g) {
            val thisAct = new Activity(block, here, this.finishState);
            this.act = thisAct;
         } else {
            val thisAct = initActEnclosed(block);
            this.act = thisAct;
         }
         Phasing.schedule(this);
//         Console.OUT.println("Scheduling");
      }
   }

//   public def inform(n: SNotifier) {
//      var go: Boolean = recurring;
//      if (!recurring && !isDone)
//         go = count.compareAndSet(0, 1);
//      if (go) {
//         val f = n as SIntFuture;
//         val v = f.get();
//         val block = ()=>{ val fun = this.fun; fun(v); Phasing.end(); };
//
//         val thisAct = initActEnclosed(block);
//         this.act = thisAct;
//
//         Phasing.schedule(this);
//      }
//   }

}

