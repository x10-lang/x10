/**
 * A pointless recursive Fibonacci in straight X10.  
 * Meant to be used to test system performance.
 */ 

public class FibCX {
    static def fib(n:int):int = {
        if (n < 2) return n;
	return (async fib(n-1)) + fib(n-2);
    }

   static def realFib(n:int):int = {
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

    static def warmupReps(reps:int):int = {
        return (reps == 1 || reps == 2) ? 0 : reps/3;
    }


  public static def main(args:Rail[String]):void = {
	System.out.println("HelloWorld");
  }
}


     

/*
    static def main(args : Rail[String]): void {
	  final int procs, nReps, num;
	  try {
		  num = Integer.parseInt(args[2]);
		  procs = Integer.parseInt(args[0]);
		  nReps = Integer.parseInt(args[1]);
		  System.out.println("Number of procs=" + procs + " nReps=" + nReps + " N=" + num);

	  } catch (Exception e) {
		  System.out.println("Usage: FibC2 <threads> <numRepeatations> <N>");
		  return;
	  }

	  boolean valid = true;
	  int result=0;
	  final int realFibResult = realFib(num);
	  final long[] times = new long[nReps];
	  for (int j = 0; j < nReps; j++) {
		Job job = new Job(g) { 
		  int result;
		  public void setResultInt(int x) { result = x;}
		  public int resultInt() { return result;}
		  public int spawnTask(Worker ws) throws StealAbort { return fib(ws, num); }
		  public String toString() {
			return "Job(#" + hashCode() + ", fib(n=" + num +"," + status+ ",frame="+ frame+")";
		  }
		};
		long startTime = System.nanoTime();
		g.invoke(job);
		result = job.getInt();
		long endTime = System.nanoTime();
		long endSC = g.getStealCount();
		long endSA = g.getStealAttempts();
		times[j] = endTime - startTime;
		sc[j] = endSC - startSC;
		sa[j] = endSA - startSA;
		startSC = endSC;
		startSA = endSA;
		if (result != realFibResult) {
		  System.out.println("FAILURE: "+job);
		  valid = false;
		} else {
		  System.out.println("SUCCESS: "+times[j]+" "+sc[j]+" "+sa[j]+" "+job);
		}
	  }

	  int warmupReps = warmupReps(nReps);
	  int realReps = nReps - warmupReps;
	  long totalTime = 0;
	  long totalSC = 0;
	  long totalSA = 0;
	  for (int k = warmupReps; k<nReps; k++) {
		totalTime += times[k];
		totalSC += sc[k];
		totalSA += sa[k];
	  }
		  
	  System.out.println("Stats for first "+warmupReps+" iterations discarded as warmup");
	  System.out.println("VJCWS Fib(" + num +")"+"\t"+(totalTime)/1000000/realReps  + " ms" +"\t" 
						 + "\t" +"steals=" +((totalSC)/realReps)
						 + "\t"+"stealAttempts=" +((totalSA)/realReps)
						 + "\t" + valid);
	  g.shutdown();
  }
  
*/
