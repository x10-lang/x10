import x10.runtime.xws.Closure;
import x10.runtime.xws.Frame;
import x10.runtime.xws.Job;
import x10.runtime.xws.Pool;
import x10.runtime.xws.StealAbort;
import x10.runtime.xws.Worker;

/**
 * A pointless recursive Fibonacci written against X10 implementation of XWS.  
 * Meant to be used to test system performance.
 * Can be used to micro-benchmark the overheads of specific pieces of the XWS implementation.
 */ 
public class FibCX {  
  /* Flags for microbenchmarking; enable/disable various pieces of XWS implementation */
  private const ELISION = false;
  private const ELIDE_DEQUE = false;
  private const ONE_FRAME = false;
    
  /**
   * Closure that implements the Cilk-style Fast and Slow paths for 
   * the following silly recursive fib method written in X10.
   * <pre>  
   * static def fib(n:int):int {
   *   if (n < 2) return n;
   *   return (async fib(n-1)) + fib(n-2);
   * }
   * </pre>
   */
  private static class FibClosure extends Closure {
    /* PC labels */
    private const ENTRY = 0;
    private const LABEL_1 = 1;
    private const LABEL_2 = 2;
    private const LABEL_3 = 4;

    /* Only used when ONE_FRAME is true */
    private const dummyFrame = new FibFrame(10);
    /* Used when ELIDE DEQUE to prevent JIT from optimizing away allocations of the FibFrame objects */
    var foolTheJIT:FibFrame;
    
    private var result:int;
    public def resultInt():int { return result;}
    public def setResultInt(x:int):void { result=x;}
      
    def this(frame:FibFrame) { super(frame); }
    
    def this(n:int) { super(new FibFrame(n)); }
    
    def fib(w:Worker, n:int):int throws StealAbort { // fast mode
        if (n < 2) return n;
        var frame:FibFrame;
        if (ELISION) {
    	    frame = null;
        } else {
	    	if (ONE_FRAME) {
	    		frame = dummyFrame;
	    	} else {
	    		frame = new FibFrame(n);
	    	}
	    	frame.PC=LABEL_1; // continuation pointer
	    	if (ELIDE_DEQUE && !ONE_FRAME) {
	    		foolTheJIT = frame;
	    	} else {
	    		w.pushFrame(frame);
	    	}
	    }
	    
	    // this thread will definitely execute fib(n-1), and
	    // hence set the value in the frame.
	    val x = fib(w, n-1);
	    
	    if (!ELISION && !ELIDE_DEQUE) {
	    	// Now need to figure out who is doing fib(n-2).
	    	// If frame has been stolen, then this thread wont do fib(n-2).
	    	// it should just return, and subsequent work will be done
	    	// by others. 
	    	w.abortOnSteal(x);
	    }
	        
	    // Now we are back in the current frame, it has not been stolen. 
	    // Execute the local code to the next spawn. 
	    
	    if (!ELISION) {
	    	// Now at the next spawn, exactly as before, set up the 
	    	// continuation pointer. 
	    	frame.x=x;
	    	frame.PC=LABEL_2;
	    }
	    val y=fib(w, n-2);
	    if (!ELISION && !ELIDE_DEQUE) {
	    	w.abortOnSteal(y);
	    }
	  
	    // Now there is nothing more to spawn -- so no need for the frame.
	    // i.e. since the worker has made it so far, it is going to complete 
	    // execution of this procedure.
	    if (!ELISION && !ELIDE_DEQUE) {
	    	// pop the task -- it is guaranteed to be garbage.
	    	w.popFrame();
	    }
	    
	    // the sync is a no-op.
	    // return the computed value.
	    val result = x+y;
	    return result;
	  }
	
	  public def compute(w:Worker, frame:Frame):void throws StealAbort { // slow mode
	    val f = frame as FibFrame;
	    val n = f.n;
	    switch (f.PC) { // NOTE: all cases in switch are falling through!
	    case ENTRY: 
	    	if (n < 2) {
	    		result = n;
	    		setupReturn();
	    		return;
	    	}
	    	f.PC=LABEL_1;
	    	val x = fib(w, n-1);
	    	w.abortOnSteal(x);
	    	f.x=x;
	    	
	    case LABEL_1: 
	    	f.PC=LABEL_2;
	    	val y=fib(w,n-2);
	    	w.abortOnSteal(y);
	    	f.y=y;
	    	
	    case LABEL_2: 
	    	f.PC=LABEL_3;
	    	if (sync(w)) {
	    		return;
	    	}
	    case LABEL_3:
	    	result=f.x+f.y;
	    	setupReturn();
	    }
	    return;
	  }

      private static class FibFrame extends Frame {
	    val n:int;
	    var PC:int;
	    var x:int;
	    var y:int;
	    
	  	def this(n:int) { 
	  	  super(); 
	  	  this.n = n; 
	  	}
	
		public def acceptInlet(index:int, value:int):void {
	 	  if (index==LABEL_1) { 
		    x=value;
	 	  } else {
			y=value;
		  }
		}
		
		public def setOutletOn(c:Closure):void {
		  c.setOutlet(PC);
		}
		
		public def makeClosure(): Closure {
	      return new FibClosure(this);
		}
		
		public def toString():String { 
		  return "FibFrame(n="+n+",x="+x+",y="+y+",PC=" + PC + ")";
		}  
	  };
  };

  static def realFib(n:int):int {
	if (n < 2) return n;
	var y:int = 0;
	var x:int = 1;
	for (var i:int=0; i <= n-2; i++) {
	  val temp = x; 
	  x +=y; 
	  y=temp;
	}
	return x;
  }

  static def warmupReps(reps:int):int {
	return (reps == 1 || reps == 2) ? 0 : reps/3;
  }


  public static def main(args:Rail[String]):void {
	var procs:int;
	var nReps:int;
	var num:int;
	try {
	  procs = Int.parseInt(args(0));
	  nReps = Int.parseInt(args(1));
	  num = Int.parseInt(args(2));
	  System.out.println("Number of procs=" + procs + " nReps=" + nReps + " N=" + num);

	} catch (e:Exception) {
	  System.out.println("Usage: FibC2 <threads> <numRepeatations> <N>");
	  return;
	}
	try {
	  doWork(procs, nReps, num);
	} catch (e:Exception) {
	  System.out.println("Unexpected exception" +e);
	  e.printStackTrace();
    }
  }

  public static def doWork(procs:int, nReps:int, num:int) throws Exception {
	val pool = new Pool(procs);
	var startSC:long = 0;
	var startSA:long = 0;
	var valid:boolean = true;
	val realFibResult = realFib(num);
	val times:Rail[long] = Rail.makeVar[long](nReps, (x:nat)=>0L);
	val sc:Rail[long] = Rail.makeVar[long](nReps, (x:nat)=>0L);
	val sa:Rail[long] = Rail.makeVar[long](nReps, (x:nat)=>0L);

	for (var j:int = 0; j < nReps; j++) {
	  val job = new Job(pool) {
            var result:int;
	    public def spawnTask(ws:Worker):int throws StealAbort { return new FibClosure(num).fib(ws, num); }
	    public def setResultInt(x:int):void { result = x;}
            public def resultInt():int { return result;}
	  };
	  val startTime = System.nanoTime();
	  pool.invoke(job);
          val result = job.getInt();
	  val endTime = System.nanoTime();
	  val endSC = pool.getStealCount();
	  val endSA = pool.getStealAttempts();
	  times(j) = endTime - startTime;
	  sc(j) = endSC - startSC;
	  sa(j) = endSA - startSA;
	  startSC = endSC;
	  startSA = endSA;
	  if (result != realFibResult) {
		System.out.println("FAILURE: "); // +job
		valid = false;
	  } else {
		System.out.println("SUCCESS: "+times(j)+" "+sc(j)+" "+sa(j)/*+" "+job*/);
	  }
	}

	val warmupReps:int = warmupReps(nReps);
	val realReps = nReps - warmupReps;
	var totalTime:long = 0;
	var totalSC:long = 0;
	var totalSA:long = 0;
	for (var k:int = warmupReps; k<nReps; k++) {
	  totalTime += times(k);
	  totalSC += sc(k);
	  totalSA += sa(k);
	}

	System.out.println("Stats for first "+warmupReps+" iterations discarded as warmup");
	System.out.println("XWS Fib(" + num +")"+"\t"+(totalTime)/1000000/realReps  + " ms" +"\t" 
					   + "\t" +"steals=" +((totalSC)/realReps)
					   + "\t"+"stealAttempts=" +((totalSA)/realReps)
					   + "\t" + valid);
	pool.shutdown();
  }
}
