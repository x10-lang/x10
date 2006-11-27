/**
 * @author cmd
 * An embarassingly parallel program to test multi-node vm.
 * A simple manually coded reduction. Similar to BasicTest1, but with futures
 */

public class BasicTest4 {
	
	private float[.] _sumarray;
	private float[.] _sumarray2;
	private float@places.FIRST_PLACE _totalSum;
	private float@places.FIRST_PLACE _totalSum2;
	
	final static int equalBlockSize=1;
	final static int unEqualBlockSize=2;
	final static int equalBlockCyclicSize=3;
	final static int unEqualBlockCyclicSize=4;
	
	private void initialize(int distributionType,int size){
		long start = System.currentTimeMillis();
		_totalSum=0;
		region e= region.factory.region(0,size-1);
		region f =[0:3,e];
		// create distributions which will trigger different types of layouts
		
		switch(distributionType){
		case equalBlockSize:
			// do nothing--take default value
			  // assume an even number of places
			break;
		case unEqualBlockSize:
			f=[0:4,e];  // assume an even number of places
			break;
		}
		
		dist d=dist.factory.block(f);
		final int theSize = size;
		_sumarray = new float[d] (point p[i,j]){return theSize*i +j;};
		_sumarray2 = new float[d] (point p){return 5;};
		
		long stop = System.currentTimeMillis();
		System.out.println("Initialization takes:"+((double)(stop-start)/1000)+" seconds");
		
		}
	
	
	/**
	 * hack to get future construct to work
	 */
	private float sumRegion(region my_region,float [.] theArray,int placeId,
			boolean trace,boolean verboseTrace){
		
		if(trace)System.out.println("summing region over "+my_region+" for place "+placeId);
		float tempSum=0;
		for(point p: my_region){
			if(verboseTrace)System.out.println(p+":"+tempSum+" <+-"+theArray[p]);
			tempSum += theArray[p];
		}
		
		if(trace)System.out.println("sum is:"+tempSum);
		return tempSum; // this will be returned and stored in partialSums[i]
	}
	
	private void run(boolean trace, boolean verboseTrace){
		System.out.println("Starting run with "+_sumarray.region.size()+" array elements");
		
		long start = System.currentTimeMillis();
	
		int i=0;
		// will hold intermediate sums
		future <float>[] partialSums = new future<float>[place.MAX_PLACES];
		future <float>[] partialSums2 = new future<float>[place.MAX_PLACES];
		final boolean tr=trace;
		final boolean verboseTr=verboseTrace;
		place currentPlace = place.FIRST_PLACE;
		do{
			
			if(trace)System.out.println("Spawning work for place:"+i);
			final int placeId = currentPlace.id;
			final region my_region = (_sumarray.distribution|currentPlace).region;
			partialSums[placeId] = future(currentPlace) {sumRegion(my_region,_sumarray,placeId,tr,verboseTr)};
			partialSums2[placeId] = future(currentPlace) {sumRegion(my_region,_sumarray2,placeId,tr,verboseTr)};
			currentPlace =currentPlace.next();
			++i;
		} while(i < place.MAX_PLACES);
		
		for(int k=0;k < partialSums.length;++k){
			_totalSum += partialSums[k].force();
		}
		for(int k=0;k < partialSums2.length;++k){
			_totalSum2 += partialSums2[k].force();
		}
		long stop = System.currentTimeMillis();
		System.out.println("Total is:"+_totalSum);
		System.out.println("Elapsed time:"+((double)(stop-start)/1000)+" seconds");
	}
	private void verify(){
		// it should total (n^2 +n)/2
		float size =_sumarray.region.size()-1;//make it zero relative
		
		float expected_result = (size * size + size)/2;
		
		System.out.println("Expected result1(size="+size+")="+expected_result);
		System.out.println("Calculated result1:"+_totalSum);
		if(_totalSum != expected_result){
			throw new x10.lang.Exception(expected_result+"!="+_totalSum);
		}
		float expected_result2 = 5* (size+1);
		System.out.println("Expected result2(size="+size+")="+expected_result2);
		System.out.println("Calculated result2:"+_totalSum2);
		if(_totalSum2 != expected_result2){
			throw new x10.lang.Exception(expected_result2+"!="+_totalSum2);
		}
	}
	
	
	private void RunTest(String title,int distributionType,int arraySize,boolean trace,boolean verboseTrace){
		System.out.println(title);
		initialize(distributionType,arraySize);
		run(trace,verboseTrace);
		verify();
		System.out.println("--------------------------------------------------------");
	}
	
	
	public static void main(String[] args) {
		int arraySize=10;//0;
		
		boolean success = true;
		
		System.out.println("Starting Basic multi-node test with futures: two arrays");
		try {
			if(args.length> 0){
				arraySize = java.lang.Integer.parseInt(args[0]);
			}
			
			if(place.MAX_PLACES %2 == 1){
				throw new RuntimeException("Testcase must run with an even number of places.  Current setting="+place.MAX_PLACES);
			}
			BasicTest4 x = new BasicTest4();
			
			x.RunTest("Equal Sized Blocks",equalBlockSize,arraySize,false,false);
			if(false)x.RunTest("Unequal Sized Blocks",unEqualBlockSize,arraySize,false,false);
						
			
		} catch (Throwable e) {
			e.printStackTrace();
			success=false;        
		}
		System.out.println("++++++ "+(success?"Test succeeded.":"Test failed."));
		x10.lang.Runtime.setExitCode(success?0:1);
	}
	
}
