/**
 * Synthetic benchmark to time arary accesses
 */

public class Initialization {
	String _tests[] = {"testDouble"};
	static final int kArraySize=500;
	double[.] x_doubleArray1D;
	double[.] x_doubleArray2D;
	double[] x_javaArray;
	int[.] x_intArray1D;
	
	public boolean run(){
		long start,stop;
		final int OneDSize = kArraySize * kArraySize;
		
		start = System.currentTimeMillis();
		System.out.println("creating java array size "+OneDSize);
		x_javaArray = new double[OneDSize];
		stop = System.currentTimeMillis();
		System.out.println("Created array in "+((double)(stop-start)/1000)+" seconds");
		
		start = System.currentTimeMillis();
		System.out.println("creating array size "+OneDSize);
		region r = [0:OneDSize];
		final dist  D = dist.factory.block(r);
		x_doubleArray1D = new double[D];
		stop = System.currentTimeMillis();
		System.out.println("Created array in "+((double)(stop-start)/1000)+" seconds");
		
		System.out.println("creating array ["+kArraySize+","+kArraySize+"] ("+(kArraySize*kArraySize)+")");
		region r2 = [0:kArraySize,0:kArraySize];
		final dist  D2 = dist.factory.block(r2);
		System.out.println("Start allocation...");
		start = System.currentTimeMillis();
		x_doubleArray2D = new double[D2];
		stop = System.currentTimeMillis();
		System.out.println("Created array in "+((double)(stop-start)/1000)+" seconds");
		System.out.println("finished allocating");
		
		start = System.currentTimeMillis();
		x_intArray1D = new int[D];
		stop = System.currentTimeMillis();
		System.out.println("Created int array in "+((double)(stop-start)/1000)+" seconds");
		System.out.println("finished allocating");
		return true;
	}

	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new Initialization()).run();
        } catch (Throwable e) {
                e.printStackTrace();
                b.val=false;
        }
        System.out.println("++++++ "+(b.val?"Test succeeded.":"Test failed."));
        x10.lang.Runtime.setExitCode(b.val?0:1);
    }
    static class boxedBoolean {
        boolean val=false;
    }


}
