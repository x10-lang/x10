
// Automatically generated by the command
// m4 ClockTest2.m4 > ClockTest2.x10
// Do not edit
/**
 * Test for 'now'.  Very likely to fail if now is not translated
 * properly (but depends theoretically on the scheduler).
 */
public class ClockTest2 {

	int val=0;
	static final int N=10;

	public boolean run() {
		final clock c = clock.factory.clock();
 	    for (int i=0;i<N;i++) {
 		  async(here) clocked(c) finish async(here) {  
			async(here){ 
			  atomic { 
				val++; 
	 		  }  
			} 
	 	  }
		  next;
		  int temp;
		  atomic {temp=val;}
		  if (temp != i+1) return false;
		} 
		if (c.dropped())
			return false;
		c.drop();
		if(!c.dropped())
			return false;
		

		return true;
	}
	public static void main(String args[]) {
		boolean b= (new ClockTest2()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}

}
