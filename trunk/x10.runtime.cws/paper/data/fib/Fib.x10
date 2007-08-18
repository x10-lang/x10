/**
 * A pointless recursive Fibonacci. Meant to be used
 * to test system performance. Implements recursive Fib
 * Cilk style.
 * @author vj
 *
 */

public final class Fib  {
    int value;
    Fib(int v) {
	value = v;
    }
    void compute() {
	final Fib f1 = new Fib(value-1), f2= new Fib(value-2);
	finish {
	    async f1.compute();
	    async f2.compute();
	}
	value = f1.value+f2.value;
    }
    public static void main(String[] args) {
    int nReps;
    try {
      nReps = Integer.parseInt(args[0]);
      System.out.println(" nReps" + nReps);
    } catch (Exception e) {
      System.out.println("Usage: Fib  <numRepeatations>");
      return;
    }

    for (int i = 0; i < points.length; i++) {
      final int n = points[i];
		  
    	  long s = System.nanoTime();
    	  
    	  for (int j = 0; j < nReps; j++) {
    		  Job job = new Job(g) { 
    	    	  int result;
    	    	  public void setResultInt(int x) { result = x;}
    	    	  public int resultInt() { return result;}
    	    	  public int spawnTask(Worker ws) throws StealAbort { return fib(ws, n);}
    	    	  public String toString() {
    	    		  return "Job(#" + hashCode() + ", fib(n=" + n +"," + status+ ",frame="+ frame+")";
    	    	  }};
    		  g.submit(job);
    		  result = job.getInt();
    	  }
    	  
    	  long t = System.nanoTime();
    	  System.out.println("VJCWS Fib(" + n +")"+"\t"+(t-s)/1000000/nReps  + " ms" +"\t" + 
    			  (result==realfib(n)?"ok" : "fail")
    			  + "\t" +"steals=" +((g.getStealCount()-sc)/nReps)
    			  + "\t"+"stealAttempts=" +((g.getStealAttempts()-sa)/nReps));
    	  //System.out.println(points[i] + " " + (t-s)/1000000/nReps  + "ms  " + result + " " + (result==realfib(n)?"ok" : "fail") );
    	  sc=g.getStealCount();
    	  sa=g.getStealAttempts();
    }
    g.shutdown();
  }

    }
}


