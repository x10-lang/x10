/**
 * @author cmd
 * An embarassingly parallel program to test multi-node vm.
 * A simple manually coded reduction
 */

public class BasicTest1 {
	
	private float[.] _sumarray;
	private float _totalSum;
	
	private void initialize(int size){
		long start = System.currentTimeMillis();
		_totalSum=0;
		region e= region.factory.region(0,size-1);
		dist d=dist.factory.block(e);
		_sumarray = new float[d] (point p[i]){return i;};
		long stop = System.currentTimeMillis();
		System.out.println("Initialization takes:"+((double)(stop-start)/1000)+" seconds");
	}
	
	private void run(){
		final boolean trace = false;
		System.out.println("Starting run with "+_sumarray.region.size()+" array elements");
		
		long start = System.currentTimeMillis();
		place p1 = place.FIRST_PLACE;
		final  region[] precomputedRegions = new region[place.MAX_PLACES];
		
		place my_place = place.FIRST_PLACE;
		
		do{
			precomputedRegions[my_place.id] = (_sumarray.distribution | my_place).region;
			my_place = my_place.next();
		} while (my_place != place.FIRST_PLACE);
		
		int i=0;
		finish do{
			
			if(trace)System.out.println("Spawning work for place:"+i);
			final int placeId = i;
			async(p1) {
				region 	my_region = precomputedRegions[placeId];
				int lo = my_region.rank(0).low();
				int hi = my_region.rank(0).high();
				float tempSum=0;
				//System.out.println("place:"+placeId+" "+lo+".."+hi);
				for(int index = lo; index <= hi;++index){
					tempSum += _sumarray[index];
					//System.out.println(placeId+":"+tempSum+"<-"+_sumarray[index]+" for index:"+index);
				}
				if(trace)System.out.println("sum is:"+tempSum);
				{
					final float tempS = tempSum;
					async(_totalSum){
						if(trace)System.out.println("Updating with "+tempS);
						atomic{_totalSum += tempS;}
					}
				}
			}
			
			p1 =p1.next();
			++i;
		} while(i < place.MAX_PLACES);
		long stop = System.currentTimeMillis();
		System.out.println("Total is:"+_totalSum);
		System.out.println("Elapsed time:"+((double)(stop-start)/1000)+" seconds");
	}
	private void verify(){
		// it should total (n^2 +n)/2
		float size = _sumarray.region.rank(0).size()-1;
		float expected_result = (size * size + size)/2;
		
		System.out.println("Expected result:"+expected_result);
		System.out.println("Calculated result:"+_totalSum);
		if(_totalSum != expected_result){
			throw new x10.lang.Exception(expected_result+"!="+_totalSum);
		}
	}
	
	public static void main(String[] args) {
		int arraySize=1001;
		
		boolean success = true;
		
		try {
			if(args.length> 0){
				arraySize = Integer.parseInt(args[0]);
			}
			
			BasicTest1 x = new BasicTest1();
			x.initialize(arraySize);
			x.run();
			x.verify();
			
		} catch (Throwable e) {
			e.printStackTrace();
			success=false;        
		}
		System.out.println("++++++ "+(success?"Test succeeded.":"Test failed."));
		x10.lang.Runtime.setExitCode(success?0:1);
	}
	
}
