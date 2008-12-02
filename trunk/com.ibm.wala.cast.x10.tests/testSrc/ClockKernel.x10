import x10.lang.Runtime.*;
import x10.lang.Exception;


class ClockKernel
{
  
	
	public static void tut1()
	{
	final int N= 100;
	final int MAX = 100;
	final int [] B = new int[N+1];
	final int [] A = new int [N+1];
	foreach (point[i]: [1:N] )
	{
		A[i] = i;
		B[i] = N - i;
	}
	 
	  final clock c = clock.factory.clock(); 

  	foreach (point[i]: [1:N]) clocked (c) {
  	  int numIterations = 0;
      while (true && numIterations < MAX) {
    	numIterations++;
    	int old_A_i = A[i]; 
       int new_A_i = Math.min(A[i],B[i]);
      if ( i > 1 ) 
          new_A_i = Math.min(new_A_i,B[i-1]);
       if ( i < N ) 
          new_A_i = Math.min(new_A_i,B[i+1]);
       A[i] = new_A_i;
       c.doNext();
       int old_B_i = B[i];
       int new_B_i = Math.min(B[i],A[i]);
       if ( i > 1 )
          new_B_i = Math.min(new_B_i,A[i-1]);
       if ( i < N )
          new_B_i = Math.min(new_B_i,A[i+1]);
            B[i] = new_B_i;
       		c.doNext();
      if ( old_A_i == new_A_i && old_B_i == new_B_i )
          break;
  
     } // while

   } // foreach

	
	}

	public static void main(String[] args) {
 		async {
			tut1();
		}
	}
}