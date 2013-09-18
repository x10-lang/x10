package futuresched.core;

import x10.util.ArrayList;
import x10.util.concurrent.AtomicDouble;


public class PhDoubleAddFTask extends FTask {

   var fun: (Double)=>Boolean;
   var inDeg: Int;
   var stopped: Boolean = false;
   var value: AtomicDouble = new AtomicDouble(0);

   public def this(count: Int, act: Activity, worker: Runtime.Worker, enclosed: Boolean) {
      super(count, act, worker, enclosed);
   }

   public def this(act: Activity, enclosed: Boolean) {
      super(act, enclosed);
   }

   public def this(fun: (Double)=>Boolean, enclosed: Boolean) {
      this.fun  = fun;
      this.enclosed = enclosed;
   }

   public def this(enclosed: Boolean) {
      this.enclosed = enclosed;
   }

   public def this() {
      super();
   }


// ------------------------------------------------------------
// newPhasedAdd
// Note that newPhasedAdd is called when the futures are
// not already set or being concurrently set.

   public static def newPhasedDoubleAdd(
      futures: ArrayList[SDoubleFuture],
      fun: (Double)=>Boolean): PhDoubleAddFTask {

      val fTask = new PhDoubleAddFTask(fun, false);

      val iter = futures.iterator();
      while (iter.hasNext()) {
         val f = iter.next();
         f.add(fTask, null);
      }
      val count = futures.size() as Int;
      fTask.inDeg = count;
      fTask.count.set(-count);
      return fTask;
   }

   public static def newPhasedDoubleAdd(
      futures: ArrayList[SUDoubleFuture],
      fun: (Double)=>Boolean): PhDoubleAddFTask {

      val fTask = new PhDoubleAddFTask(fun, false);

      val iter = futures.iterator();
      while (iter.hasNext()) {
         val f = iter.next();
         f.add(fTask, null);
      }
      val count = futures.size() as Int;
      fTask.inDeg = count;
      fTask.count.set(-count);
      return fTask;
   }

   public static def newPhasedDoubleAdd[T](
      objs: ArrayList[T],
      trans: (T)=>SDoubleFuture,
      fun: (Double)=>Boolean): PhDoubleAddFTask {

      val fTask = new PhDoubleAddFTask(fun, false);

      val iter = objs.iterator();
      while (iter.hasNext()) {
         val f = trans(iter.next());
         f.add(fTask, null);
      }
      val count = objs.size() as Int;
      fTask.inDeg = count;
      fTask.count.set(-count);
      return fTask;
   }

   public static def newPhasedDoubleAdd[T](
      objs: ArrayList[T],
      trans: (T)=>SUDoubleFuture,
      fun: (Double)=>Boolean): PhDoubleAddFTask {

      val fTask = new PhDoubleAddFTask(fun, false);

      val iter = objs.iterator();
      while (iter.hasNext()) {
         val f = trans(iter.next());
         f.add(fTask, null);
      }
      val count = objs.size() as Int;
      fTask.inDeg = count;
      fTask.count.set(-count);
      return fTask;
   }

// ------------------------------------------------------------

   public def deregister() {
      this.inDeg -= 1;
   }

// ------------------------------------------------------------
// Deferred task scheduling

   public def inform(v: Any, obj: Any) {
//      Console.OUT.println("In inform ");
      if (stopped)
         return;
      if (isDone && !recurring)
         return;

      val doubleV = v as Double;
      value.addAndGet(doubleV);
      // Note that adding the value should be done before incrementing the counter.
      // The last process that finds the counter zero,
      // knows that the value contains the sum.
      var c: Int;
      c = count.incrementAndGet();
//      Console.OUT.println("Scheduling for next phase");
      if (c == 0) {
         if (!enclosed) {
            val sum = value.get();
            val block = () => {
               val stop = fun(sum);
               if (stop)
                  this.stopped = true;
               Phasing.end();
            };
            val act = initActEnclosed(block);
            Phasing.schedule(act);
         } else {
            Phasing.schedule(this.act);
         }
         if (recurring) {
            // It is assumed that the task is fired once in each phase.
            count.set(-inDeg);
            //c = count.addAndGet(-inDeg);
            //while (c >= 0) {
            //   Phasing.schedule(this);
            //   c = count.addAndGet(-inDeg);
            //}
         }
      }
   }

//   public def inform(f: SNotifier) {
//      if (!isDone || recurring) {
//         var c: Int;
//         c = count.incrementAndGet();
//         if (c == 0) {
//            act = initActEnclosed(block);
//            Phasing.schedule(this);
//            if (recurring) {
//               c = count.addAndGet(-inDeg);
//               while (c >= 0) {
//                  Phasing.schedule(this);
//                  c = count.addAndGet(-inDeg);
//               }
//            }
//         }
//      }
//   }

// ------------------------------------------------------------

}

