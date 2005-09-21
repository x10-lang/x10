
/**
 * @author cmd
 */

public class bb {
	
	private int value [.] _sumarray;
	
	private void initialize(int size){
		long start = System.currentTimeMillis();
		region e= region.factory.region(0,size-1);
		dist d=dist.factory.block(e);
		_sumarray = new int[d];
		//_sumarray = new int[d] (point p[i]){return i;};
		long stop = System.currentTimeMillis();
		System.out.println("Initialization takes:"+((double)(stop-start)/1000)+" seconds");
	}
	
	private void run(){
		final boolean trace = false;
		System.out.println("Starting run with "+_sumarray.region.size()+" array elements");
		
		long start = System.currentTimeMillis();
		long stop = System.currentTimeMillis();
		System.out.println("Elapsed time:"+((double)(stop-start)/1000)+" seconds");
	}
	
	public static void main(String[] args) {
		int arraySize=10;
		
		boolean success = true;
		
		try {
			if(args.length> 0){
				arraySize = Integer.parseInt(args[0]);
			}
			
			bb x = new bb();
			x.initialize(arraySize);
			x.run();
			
		} catch (Throwable e) {
			e.printStackTrace();
			success=false;        
		}
		System.out.println("++++++ "+(success?"Test succeeded.":"Test failed."));
		x10.lang.Runtime.setExitCode(success?0:1);
	}
	
}
