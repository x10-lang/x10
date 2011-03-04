package stream;

/** Version of Stream with a collection of local arrays implementing a global array.
@seealso Stream
@author vj
* 
* This version has some method calls for a more interesting stack trace  BRT 11/25/08
* This version also has a "bug" which causes it to hang and not complete.
* Spoiler alert: array a's size is one greater than b and c, and it should not be
* Debugger features to use to find the bug: 
*   0. Step, run, see that the Activities don't seem to complete, etc.
*   1. Set Exception breakpoint on ArrayIndexOutOfBoundsException
*   2. Run, land at line in vectorAdd() doing the addition
*   3. inspect values for i, and length of vectors
*   4. use stack view ("Debug view") to go up the stack and find declaration of a
*   5. "Correct" so a has same length as b and c, terminate the launch, save/recompile, and re-run.
*/ 
public class BuggyFragmentedStreamMethods { 
	const int MEG=1024*1024;
	const double alpha=3.0D; 
	const int NUM_TIMES=10; 
	public static void main(String[] args) {
		final boolean[] verified = new boolean[] { true };
		final double[] times = new double[NUM_TIMES];
		long N0 = 2*MEG; 
		if (args.length > 0) N0 = java.lang.Long.parseLong(args[0]);
		final long N=N0*place.MAX_PLACES;
		final int LocalSize = (int) N0; 
		System.out.println("LocalSize=" + LocalSize);
		final region(:rank==1&&zeroBased&&rect)  RLocal=[0:LocalSize-1];
		finish async {
			final clock clk=clock.factory.clock();
			
			ateach(point [p]:dist.UNIQUE) clocked (clk) {
				//try{
				double[] a = new double[LocalSize+1]; 
				double[] b = new double[LocalSize], c=new double[LocalSize];
				// Step 1. init vector
				vectorInit(b, c, LocalSize);
				// Step 2. addition in loop
				loopAdd(a, b, c,  times); 
				// Step 3. verify
				verify(a, b, c, clk, verified); 
			} 
		}
		double min=10000000L;  
		for (int j=0; j<NUM_TIMES; j++) if (times[j]<min) min=times[j];
		printStats(N, min, verified[0]);
	}
	public static double mySecond() {
		return (double) ((double)(System.nanoTime() / 1000) * 1.e-6);
	}
	/** Initialize Vector */
	public static void vectorInit(double[] b, double[] c, int LocalSize){
		assert(b.length==c.length);
		final int p=here.id;
		 
		for (int i=0; i<b.length;i++) {   //vectorInit
			b[i] =1.5*(p*LocalSize+i);
			c[i] = 2.5*(p*LocalSize+i);
		}
	}
	public static void loopAdd(double[] a, double[] b, double[] c,  double[] times){
		final int p = here.id;
		for (int j=0;j<NUM_TIMES; j++) { // loop add
			if (p==0) times[j]= -mySecond(); 
			vectorAdd(a,b,c); 
			next; //barrier
			if (p==0) times[j] += mySecond();
		}
	}
	public static void vectorAdd(double[] a, double[] b, double[] c){
		for (int i=0; i<a.length; i++)  
			a[i]=b[i]+alpha*c[i];  //vector add (a,b,c) 
	}
	public static void verify(double[] a, double[] b, double[] c, clock clk, final boolean[] verified){
		for (int i=0; i<a.length; i++) {
		    if (a[i] != b[i]+alpha* c[i]) {
		    	async(place.FIRST_PLACE) clocked (clk) verified[0]=false;// first false stops verification
		    	return;
	        }
		}
	}

	public static void printStats(long N, double time, boolean verified) {
		System.out.println("Number of places=" + place.MAX_PLACES);
		long size = (3*8*N/MEG);
		double rate = (3*8*N)/(1.0E9*time);  
		System.out.println("Size of arrays: " + size +" MB (total)" + size/place.MAX_PLACES + " MB (per place)");
		System.out.println("Min time: " + time + " rate=" + rate + " GB/s");
		System.out.println("Result is " + (verified ? "verified." : "NOT verified."));
	}                                
}
