/**
 * A pointless recursive Fibonacci in straight X10.  
 * Meant to be used to test system performance.
 */ 

public class FibX {
  static def fib(n:int):int {
	if (n < 2) return n;
	return (async fib(n-1)) + fib(n-2);
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


  public static def main(args:Rail[String]):void = {
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


	var valid:boolean = true;
	val realFibResult = realFib(num);
	val times : Rail[long] = Rail.makeVar[long](nReps, (x:nat)=>0L);
	for (var j:int = 0; j < nReps; j++) {
	  val startTime = System.nanoTime();
	  val result = fib(num);
	  val endTime = System.nanoTime();
	  times(j) = endTime - startTime;
	  if (result != realFibResult) {
		System.out.println("FAILURE: "); // +job
		valid = false;
	  } else {
		System.out.println("SUCCESS: "+times(j));
	  }
	}

	val warmupReps:int = warmupReps(nReps);
	val realReps = nReps - warmupReps;
	var totalTime:long = 0;
	for (var k:int = warmupReps; k<nReps; k++) {
	  totalTime += times(k);
	}

	System.out.println("Stats for first "+warmupReps+" iterations discarded as warmup");
	System.out.println("Fib(" + num +")"+"\t"+(totalTime)/1000000/realReps  + " ms" +"\t" + "\t" + valid);
  }
}
