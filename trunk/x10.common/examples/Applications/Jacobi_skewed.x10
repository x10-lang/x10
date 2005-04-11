 /** 
  * Jacobi iteration
  * 
  * At each step of the iteration, replace the value of a cell with
  * the average of its adjacent cells.
  * Compute the error at each iteration as the sum of the changes
  * in value across the whole array. Continue the iteration until
  * the error falls below a given bound.
  *
  * @author vj, cvp, kemal
  */

 public class Jacobi_skewed  {

     const int N=10;
     const double epsilon = 0.1;
     const double epsilon2 = 0.00000001;
     region R = [0:N+1];
     region RInner= [1:N];
     distribution D = createDist(R);
     distribution DInner = D | RInner;
     distribution DBoundary = D - RInner;
     const int EXPECTED_ITERS=72;
     const double EXPECTED_ERR=0.0998058359189411;
     
     double[.] B = new double[D] (point p[i])
         {return DBoundary.contains(p) ? (N-1)/2 : i-1;};
     
     public boolean run() {
     	int iters = 0;
     	double err;
     	double[.] Temp = new double[DInner];
     	while(true) {
     		finish ateach (point p[i]:DInner) 
     			{Temp[i]=(read(i-1)+read(i+1))/2.0;}     		
     		if((err=((B | DInner) - Temp).abs().sum()) < epsilon) break;     		
     		B.update(Temp);
     		iters++; 
        }
     	System.out.println("Iterations="+iters+" Error="+err);
     	return Math.abs(err-EXPECTED_ERR)<epsilon2 && iters==EXPECTED_ITERS;
     }
     
	 public double read(final int i) {
	 	return future(D[i]) {B[i]}.force();
	 }
	 
    const boolean SKEWED=true;
    
    /**
     * Create either a blocked or a custom skewed distribution
     */
    distribution createDist(region r) {
    	return SKEWED? 
    	       createSkewedDist(r.rank(0).high()-1,2):
    	       distribution.factory.block(r);
    }
    
    /**
     * This creates a custom skewed distribution
     * where all places get K array elements each,
     * except for the last place, which gets
     * all the remaining elements
     */
    static distribution createSkewedDist(int N, int K) {
	  final distribution u=distribution.factory.unique(x10.lang.place.places);
	  final int NP=x10.lang.place.MAX_PLACES;
	  if (K*(NP-1)>N+1) throw new Error("Too few array elements");
	  distribution d0=((0:(K-1))->u[0]);
	  for(int i=1;i<NP-1;i++) {
	    final int start=K*i;
        final distribution d1=((start:(start+K-1))->u[i]);
        d0=d0.union(d1);
	  }
      return d0.union(((K*(NP-1)):(N+1))->u[NP-1]);	
    }
	
     /**
      * The main method
      */
     public static void main(String args[]) {
     	boolean b= (new Jacobi_skewed()).run();
     	System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
     	System.exit(b?0:1);
     }
     
 }
