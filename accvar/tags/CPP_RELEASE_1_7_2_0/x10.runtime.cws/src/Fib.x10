import java.lang.Integer;
import java.lang.Exception;
public class Fib {
	int r;
	Fib(int n) { r=n;}
	void run() {
		if (r < 2) return;
		Fib f1=new Fib(r-1), new Fib(r-2);
		finish { 
		    async f1.run(); 
		    f2.run();
		}
		r = f1.r + f2.r;
		    
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
          int result=0;
          long s = System.nanoTime();
          for (int j = 0; j < nReps; j++) {
        	  Fib f = new Fib(n);
        	  f.run();
        	  int v = f.r, desired = realFib(n);
        	  if (v != desired) 
        		  System.out.println("Error computing fib(" + n+ ") got: " + v + " wanted " + desired);	  
          }
        	  
          long t = System.nanoTime();
          System.out.println(" Fib(" + n +")"+"\t"+(t-s)/1000000/nReps  + " ms");
        	
        }
       
      }
    
}
