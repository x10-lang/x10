package x10.lang;

import x10.compiler.NoInline;
import x10.util.concurrent.AtomicInteger;

public class WaitingTask {
   
   var count: AtomicInteger;
   var act: Activity;
   var worker: Runtime.Worker;
   
   public def this(count: Int, act: Activity, worker: Runtime.Worker) {
     this.count = new AtomicInteger();
     this.count.set(count);
     this.act = act;
     this.worker = worker;
   }
   
   public def this(act: Activity, worker: Runtime.Worker) {
     this.count = new AtomicInteger();
     this.count.set(0);
     this.act = act;
     this.worker = worker;
   }
   
   public def inform() {
     val c = count.incrementAndGet();
     if (c == 0) {
       val theAct = this.act;
       val theWorker = this.worker;
       @NoInline { Runtime.executeLocalInWorker(theAct, theWorker); }
       // The annotation is added to go around a bug.
     }
   }
   
}


