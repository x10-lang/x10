package futuresched.core;

import x10.compiler.NoInline;
import x10.util.ArrayList;
import x10.util.Pair;
import x10.util.concurrent.Lock;
import x10.util.concurrent.AtomicInteger;

public class PhOrFTask[T, TP]{T isref, T haszero} extends FTask {

   public var finishState: FinishState;
   public var fun: (T, TP)=>void;
   //public var fun: (T, Any)=>void;
   //public var fun: Any;

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

   public static def newPhasedOr[T](
      futures: ArrayList[SFuture[T]],
//    fun: (SFuture[T])=>void){T isref, T haszero}: FTask {
      fun: (T, Any)=>void){T isref, T haszero}: PhOrFTask[T, Any] {
//    val block = ()=>{ fun(orSFuture) };

      val fTask = new PhOrFTask[T, Any]();
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

   public static def newPhasedOr[T1, T2](
      futures: ArrayList[T1],
      trans: (T1)=>SFuture[T2],
      fun: (T2, Any)=>void){T2 isref, T2 haszero}: PhOrFTask[T2, Any] {

      //val thisAct = initActEnclosed(block);
      val fTask = new PhOrFTask[T2, Any]();
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

  public static def newPhasedOr[T1, T2, T3](
     futures: ArrayList[T1],
     trans: (T1)=>Pair[SFuture[T2], T3],
     fun: (T2, T3)=>void){T2 isref, T2 haszero}: PhOrFTask[T2, T3] {

     //val thisAct = initActEnclosed(block);
     val fTask = new PhOrFTask[T2, T3]();
//     val finishState = mainFinish;
//     fTask.finishState = finishState;
//     Console.OUT.println("Before");
     fTask.fun = fun; //as (T2, Any)=>void;
//     Console.OUT.println("After");

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
      var go: Boolean = recurring;
      if (!recurring && !isDone)
         go = count.compareAndSet(0, 1);
//      Console.OUT.println("In inform");
      if (go) {
//         val f = n as Future[T];
//         val v = f.get();
         val block = ()=>{
//            Console.OUT.println("1");
            val fun = this.fun; // as (T, Any)=>void;
//            Console.OUT.println("2");
            fun(v as T, obj as TP);
//            Console.OUT.println("3");
            Phasing.end();
//            Console.OUT.println("4");
         };
         if (g) {
//            Console.OUT.println("In g");
            val thisAct = new Activity(block, here, this.finishState);
            this.act = thisAct;
         } else {
//            Console.OUT.println("In s");
            val thisAct = initActEnclosed(block);
            this.act = thisAct;
         }

         Phasing.schedule(this);
      }
   }

//   public def inform(n: SNotifier) {
//      var go: Boolean = recurring;
//      if (!recurring && !isDone)
//         go = count.compareAndSet(0, 1);
//      if (go) {
//         val f = n as SFuture[T];
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

