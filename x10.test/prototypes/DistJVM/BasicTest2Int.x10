/**
 * @author cmd
 * An embarassingly parallel program to test multi-node vm.
 * A simple manually coded reduction
 */

public class BasicTest2Int {
	boolean returnCode;
	private void testOverlay(){
		final int STARTA2 = 39;
		final int STOPA2 = 59;
		System.out.println("Testing overlay...");
		dist D1 = dist.factory.block([0:99]);
		dist D2 = dist.factory.block([STARTA2:STOPA2]);
		
		final int[.] a1 = new int[dist.factory.block([0:99])](point p){ return 1;};
		final int[.] a2 = new int[dist.factory.block([STARTA2:STOPA2])](point p){return 2;};
		
		returnCode = true;
		final place rootPlace = here;
		
		// verify initializtion
		finish ateach(point p:a1.distribution){
			if(a1[p] != 1){
				System.out.println("Unexpected value a1"+p+"="+a1[p]);
				async(rootPlace){returnCode=false;}
			}
		}
		if(!returnCode) throw new x10.lang.Exception("Failed to verify a1");
		finish ateach(point p:a2.distribution){
			if(a2[p] != 2){
				System.out.println("Unexpected value a2"+p+"="+a2[p]);
				async(rootPlace){returnCode=false;}
			}
		}
		if(!returnCode) throw new x10.lang.Exception("Failed to verify a2");
		
		final int[.] a3 = a1.overlay(a2);
		
		finish ateach(point p[i]:a3.distribution){
			if(i >= STARTA2 && i <= STOPA2){
				if(a3[p] != 2){
					System.out.println("Unexpected overlay a3"+p+"="+a3[p]);
					returnCode = false;
				}
			}
			else if(a3[p] != 1){
				System.out.println("Unexpected overlay a1"+p+"="+a3[p]);
				returnCode = false;
			}
		}
		if(!returnCode) throw new x10.lang.Exception("Failed to overlay a3");
		
	}
	
	private void run(){
		
		final boolean trace = false;
		final boolean verboseTrace=false;
		
		testOverlay();
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