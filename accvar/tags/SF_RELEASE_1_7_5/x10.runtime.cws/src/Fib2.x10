import java.lang.Integer;
import java.lang.Exception;
public class Fib {
    static int fib(int r) {
		if (r < 2) return;
		@shared int x;
		int y;
		finish { 
		    async x = fib(r-1);
		    y = fib(r-2);
		}
		return x+y;
		    
	}
    
    public static int realFib(int n) {
	    if (n < 2) return n;
	    int y=0,x=1;
	    for (int i=0; i <= n-2; i++) {
	      int temp = x; x +=y; y=temp;
	    }
	    return x;
    }
    
    public static void main(String[] args)  {
        int procs=2, nReps=5, num=20;
        if (args.length > 0) 
        	try {
        		procs = Integer.parseInt(args[0]);
        		nReps = Integer.parseInt(args[1]);
        		num = Integer.parseInt(args[2]);
        		System.out.println("Number of procs=" + procs + " nReps=" + nReps + " N=" + num);
        	} catch (Exception e) {
        		System.out.println("Usage: Fib <threads> <numRepeatations> <num>");
        		return;
        	}
        
        final int[] points = new int[] {num};
        for (int i = 0; i < points.length; i++) {
          final int n = points[i];
          long s = System.nanoTime();
          for (int j = 0; j < nReps; j++) {
	      int v = fib(n), desired = realFib(n);
        	  if (v != desired) 
        		  System.out.println("Error computing fib(" + n+ ") got: " + v + " wanted " + desired);	  
          }
          long t = System.nanoTime();
          System.out.println(" Fib(" + n +")"+"\t"+(t-s)/1000000/nReps  + " ms");
        }
      }
    
}
