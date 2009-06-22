package stream;

/** Version of Stream with a collection of local arrays implementing a global array.
@seealso Stream
@author vj
*/
public class FragmentedStream {
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
				for (point [i] : RLocal) {
					b[i] =1.5*(p*LocalSize+i);
					c[i] = 2.5*(p*LocalSize+i);
				}
				for (int j=0;j<NUM_TIMES; j++) {
					if (p==0) times[j]= -mySecond(); 
					for (point [i]:RLocal ) a[i]=b[i]+alpha*c[i];
					next; 
					if (p==0) times[j] += mySecond();
				}
				for (point [i]: RLocal) // verification
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
	public static void printStats(long N, double time, boolean verified) {
		System.out.println("Number of places=" + place.MAX_PLACES);
		long size = (3*8*N/MEG);
		double rate = (3*8*N)/(1.0E9*time);
		System.out.println("Size of arrays: " + size +" MB (total)" + size/place.MAX_PLACES + " MB (per place)");
		System.out.println("Min time: " + time + " rate=" + rate + " GB/s");
		System.out.println("Result is " + (verified ? "verified." : "NOT verified."));
	}                                
}
