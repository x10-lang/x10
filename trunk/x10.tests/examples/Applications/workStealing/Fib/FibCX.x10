import x10.runtime.xws.Job;
import x10.runtime.xws.Pool;
import x10.runtime.xws.StealAbort;
import x10.runtime.xws.Worker;

/**
 * A pointless recursive Fibonacci in straight X10.  
 * Meant to be used to test system performance.
 */ 

public class FibCX {
  static def fib(n:int):int {
	if (n < 2) return n;
	// return (async fib(n-1)) + fib(n-2);
	return fib(n-1) + fib(n-2);
  }

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
	    public def spawnTask(ws:Worker):int throws StealAbort { return fib(/*ws, */num); }
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
