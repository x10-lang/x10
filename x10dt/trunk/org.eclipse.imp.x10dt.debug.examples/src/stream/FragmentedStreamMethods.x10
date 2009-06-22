package stream;

/** Version of Stream with a collection of local arrays implementing a global array.
@seealso Stream
@author vj
* 
* This version has some methodx calls for a more interesting stack trace  BRT 11/25/08
*/
public class FragmentedStreamMethods { 
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
				double[] a = new double[LocalSize], b = new double[LocalSize], c=new double[LocalSize];
				// Step 1. init vector
				vectorInit(a, b, c, RLocal, p, LocalSize);
				// Step 2. addition in loop
				loopAdd(a, b, c, RLocal, p, times);
				// Step 3. verify
				for (point [i]: RLocal) // verify (a,b,c)
				    if (a[i] != b[i]+alpha* c[i]) 
				    	async(place.FIRST_PLACE) clocked (clk) verified[0]=false;

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
	public static void vectorInit(double[] a, double[] b, double[] c, region RLocal, int p, int LocalSize){
		for (point [i] : RLocal) {   //vectorInit
			b[i] =1.5*(p*LocalSize+i);
			c[i] = 2.5*(p*LocalSize+i);
		}
	}
	public static void loopAdd(double[] a, double[] b, double[] c, region RLocal, int p, double[] times){
		for (int j=0;j<NUM_TIMES; j++) { // loop add
			if (p==0) times[j]= -mySecond(); 
			vectorAdd(a,b,c,RLocal,p,times); 
			next; //barrier
			if (p==0) times[j] += mySecond();
		}
	}
	public static void vectorAdd(double[] a, double[] b, double[] c, region RLocal, int p, double[] times){
		for (point [i]:RLocal ) a[i]=b[i]+alpha*c[i];  //vector add (a,b,c)
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
