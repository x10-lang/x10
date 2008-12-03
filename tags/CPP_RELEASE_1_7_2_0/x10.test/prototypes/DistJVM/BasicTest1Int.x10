/**
 * @author cmd
 * An embarassingly parallel program to test multi-node vm.
 * A simple manually coded reduction
 */

public class BasicTest1Int {
	
	private int[.] _sumarray;
	private int@places.FIRST_PLACE _totalSum;
	
	
	private void initialize(int size){
		long start = System.currentTimeMillis();
		_totalSum=0;
		region e= region.factory.region(0,size-1);
		region f = [0:3,e];
		dist d=dist.factory.block(f);
		final int theSize = size;
		_sumarray = new int[d] (point p[i,j]){return theSize*i +j;};
		long stop = System.currentTimeMillis();
		System.out.println("Initialization takes:"+((double)(stop-start)/1000)+" seconds");
		
		System.out.println("sumarray distribution:"+_sumarray.distribution);
	}
	private void run(){
		final boolean trace = false;
		final boolean verboseTrace=false;
		System.out.println("Starting run with "+_sumarray.region.size()+" array elements");
		
		long start = System.currentTimeMillis();
		place p1 = place.FIRST_PLACE;
		final  region[] precomputedRegions = new region[place.MAX_PLACES];
		
		final place rootPlace=here; // this place will report the sum
		place my_place = place.FIRST_PLACE;
		int count=0;
		do{
			region tempRegion = (_sumarray.distribution | my_place).region;
			precomputedRegions[my_place.id] = tempRegion;
			System.out.println("tempRegion:"+tempRegion);
			my_place = my_place.next();
			++count;
		} while (my_place != place.FIRST_PLACE);
		
		System.out.println("sum dist1:"+_sumarray.region);
		int i=0;
		finish do{
			
			final int placeId = p1.id;
			if(trace)System.out.println("Spawning work for place:"+p1.id);
			
			async(p1) {
				region 	my_region = precomputedRegions[placeId];
				int tempSum=0;
				if(false){
					System.out.println("this:"+this);
					System.out.println("at place "+placeId);
					System.out.println("current region:"+my_region);
				}
				for(point p: my_region){
					//System.out.println("Processing:"+p);
					tempSum += _sumarray[p];
					if(verboseTrace)System.out.println(placeId+":"+tempSum+"<-"+_sumarray[p]+" for "+p);
				
				}
				
				
				
				if(trace)System.out.println("sum is:"+tempSum);
				{
					final int tempS = tempSum;
					async(rootPlace){
						if(trace)System.out.println("here="+here+" this="+this+"("+this.hashCode()+") rootplace="+rootPlace+" Updating "+_totalSum+" with "+tempS);
						
						atomic{_totalSum += tempS;}
						System.out.println("sum is now:"+_totalSum);
					}
				}
			}
			//return tempSum;
			
			p1 =p1.next();
			++i;
		} while(i < count);
		long stop = System.currentTimeMillis();
		System.out.println("Total is:"+_totalSum);
		System.out.println("Elapsed time:"+((double)(stop-start)/1000)+" seconds");
	}
	private void verify(){
		// it should total (n^2 +n)/2
		int size =_sumarray.region.size()-1;//make it zero relative
		
		int expected_result = (size * size + size)/2;
		
		System.out.println("Expected result(size="+size+")="+expected_result);
		System.out.println("Calculated result:"+_totalSum);
		if(_totalSum != expected_result){
			throw new x10.lang.Exception(expected_result+"!="+_totalSum);
		}
	}
	
	public static void main(String[] args) {
		int arraySize=10;//0;
		
		boolean success = true;
		
		try {
			if(args.length> 0){
				arraySize = java.lang.Integer.parseInt(args[0]);
			}
			
			BasicTest1Int x = new BasicTest1Int();
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
