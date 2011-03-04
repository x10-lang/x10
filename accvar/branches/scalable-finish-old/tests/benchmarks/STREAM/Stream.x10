/*(C) Copyright IBM Corp. 2007*/
package stream;
public class Stream {
	const int MEG=1024*1024, NUM_TIMES=10;
	const double alpha=3.0D;
	public static void main(String[] args) {
		final boolean[] verified = new boolean[] { true };
		long N0=2*MEG;
		if (args.length > 0) N0 = java.lang.Long.parseLong(args[0]);
		final long N=N0*place.MAX_PLACES;
		final region(:rail) R=[0:(int) (N-1)];
		final dist(:rail) D=dist.factory.block(R);
		final double[] times =new double[NUM_TIMES];
		final double[:rail] 
	                    a = new double[D],
	                    b = new double[D] (point [i]) {return 1.5*i;},
	                    c = new double[D] (point [i]) {return 2.5*i;};
		finish async {
			final clock clk=clock.factory.clock();
			ateach(point [i]:dist.UNIQUE) clocked (clk) {
				for (int j=0;j<NUM_TIMES; j++) {
					if (i==0) times[j]= -mySecond(); 
					for (point [p]:D|here) a[p]=b[p]+alpha*c[p];
					next; 
					if (i==0) times[j] += mySecond();
				}
				for (point [p]:D|here) // verification
				    if (a[p] != b[p]+alpha* c[p]) 
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
