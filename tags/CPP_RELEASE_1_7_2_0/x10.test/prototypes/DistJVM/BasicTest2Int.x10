/**
 * @author cmd
 * An embarassingly parallel program to test multi-node vm.
 * A simple manually coded reduction
 */

public class BasicTest2Int {
	boolean returnCode=true;
	
	int[.] _Unusedfield1;
	
	private void testOverlay(){
		final int START0 = 0;
		final int STOP0 = 59;
		final int STARTA4 = 39;
		final int STOPA4= 59;
		
		dist D0 =dist.factory.block([START0:STOP0]);
		final int[.] a1 = new int[D0](point p){ return 1;};
		final int[.] a2 = new int[D0](point p){return 2;};
		
		
		performTestOverlay(a1,a2,START0,STOP0);
		
		/* the generated dist will be very odd--elements scattered in non-uniform way*/
		final int[.] a3 = new int[dist.factory.block([START0:STOP0])](point p){ return 1;};
		final int[.] a4 = new int[dist.factory.block([STARTA4:STOPA4])](point p){return 2;};
		performTestOverlay(a3,a4,STARTA4,STOPA4);
		
	}
	private void performTestOverlay(final int [.] array1,final int[.] array2,final int starta2,final int stopa2){
		
		final place rootPlace = here;
		
		// verify initializtion
		finish ateach(point p:array1.distribution){
			if(array1[p] != 1){
				System.out.println("Unexpected value array1"+p+"="+array1[p]);
				async(rootPlace){returnCode=false;}
			}
		}
		if(!returnCode) throw new x10.lang.Exception("Failed to verify array1");
		finish ateach(point p:array2.distribution){
			if(array2[p] != 2){
				System.out.println("Unexpected value array2"+p+"="+array2[p]);
				async(rootPlace){returnCode=false;}
			}
		}
		if(!returnCode) throw new x10.lang.Exception("Failed to verify array2");
		
		final int[.] a3 = array1.overlay(array2);
		
		//System.out.println("array2:"+a3.distribution);
		finish ateach(point p[i]:a3.distribution){
			if(i >= starta2 && i <= stopa2){
				if(a3[p] != 2){
					System.out.println("Unexpected overlay a3"+p+"="+a3[p]);
					async(rootPlace){returnCode=false;}
				}
			}
			else if(a3[p] != 1){
				System.out.println("Unexpected overlay array1"+p+"="+a3[p]);
				async(rootPlace){returnCode=false;}
			}
		}
		if(!returnCode) throw new x10.lang.Exception("Failed to overlay a3");
	}
	
	/* test the scan operator */
	private void testScan(){
		final place rootPlace = here;
		final int local_init[] = {3,1,7,0,1,6,3,4};
		final int local_expect[] = {3,4,11,11,12,18,21,25};
		dist D1 = dist.factory.block([0:local_init.length-1]);
		final int[.] expected_result = new int[D1](point p[i]){return local_expect[i];};
		final int[.] input = new int[D1](point p[i]){return local_init[i];};
		
		final int[.] result = input.scan(intArray.add,0);
		ateach(point p[i]:result.distribution){
			if(result[p] != local_expect[i]) {
				async(rootPlace){returnCode=false;}
				System.out.println("Scan verifcation:"+p+" "+result[p]+"!="+local_expect[i]);
				//throw new x10.lang.Exception("Scan verifcation:"+p+" "+result[p]+"!="+local_expect[i]);
			}
		}
		if(!returnCode) throw new x10.lang.Exception("Failed to verify scan");
	}
	
	private void testReduce(){
		final int arraySize=100;
		final int[.] theArray= new int[dist.factory.block([0:arraySize])](point p[i]){return i;};
		final int expectedResult = (arraySize *(arraySize + 1))/2;
		
		int result = theArray.reduce(intArray.add,0);
		if(result != expectedResult){
			System.out.println("FAIL: "+result+"!="+expectedResult);
			returnCode=false;
		}
		if(!returnCode) throw new x10.lang.Exception("Failed to verify reduction");	
	}
	
	private void chk(boolean b,String msg){
		if(!b){
			System.out.println(msg);
			returnCode =false;
			throw new x10.lang.Exception(msg);	
		}
	}
	private void testValueEquals(){
		// First a simple case where distributions are identical
		dist d1 = dist.factory.block([0:9]);
		final int[.] array1 = new int[d1](point p[i]){ return i;};
		final int[.] array2 = new int[d1](point p[i]){ return i;};
		final int[.] array3 = new int[d1](point p[i]){ return i;};
		
		finish async(array3.distribution[5]){array3[5] = 9999;}; // make sure array3 is different than array 1
		
		final int[.] array4 = new int[dist.factory.block([0:5])](point p[i]){ return i;};
		final int[.] array5 = new int[dist.factory.block([4:5])](point p[i]){return i;};
		
		chk(array1.valueEquals(array2),"FAIL: array1 != array2");
		chk(!array1.valueEquals(array3),"FAIL: array1 = array3");
		chk(!array1.valueEquals(array4),"FAIL: array1 = array4");
		final int [.] array6 = array1.overlay(array4);
		//System.out.println("array 5:"+array5.distribution);
		chk(array1.overlay(array5).valueEquals(array1),"FAIL overlaid array");
	}
	
	private void run(){
		
		final boolean trace = false;
		final boolean verboseTrace=false;
		
		testOverlay();
		testScan();
		testReduce();
		testValueEquals();
		testUnion();
		testUpdate();
		testLift();
	}
	
	
	
	private void testUnion(){
		final int [.] array1 = new int[dist.factory.block([0:7])](point p[i]){return 1;};
		final int [.] array2 = new int[[8:14]->here](point p){return 2;};
		chk(array1.reduce(intArray.add,0) == 8,"reduce array1");
		chk(14 == array2.reduce(intArray.add,0),"reduce array2");
		
		final int[.] array3 = array1.union(array2);
		chk(22 == array3.reduce(intArray.add,0),"reduced union");
	}
	private void testUpdate(){
		final int [.] array1 = new int[dist.factory.block([0:15])](point p[i]){return 1;};
		final int [.] array2 = new int[[4:14]](point p){return 2;};
		array1.update(array2);
		chk(array1.reduce(intArray.add,0)==27,"Fail update");
		
	}
	
	private void testLift(){
		final int [.] array1 = new int[dist.factory.block([0:15])](point p[i]){return 1;};
		final int [.] array2 = new int[dist.factory.block([0:15])](point p[i]){return 2;};
		final int [.] array3 = array1.lift(intArray.add,array2);
		chk(array3.reduce(intArray.add,0) == 48,"fail lift");
	}
	
	public static void main(String[] args) {
		boolean success = true;
		
		try {
			
			(new BasicTest2Int()).run();
			
		} catch (Throwable e) {
			e.printStackTrace();
			success=false;        
		}
		System.out.println("++++++ "+(success?"Test succeeded.":"Test failed."+success));
		x10.lang.Runtime.setExitCode(success?0:1);
	}
	
}
