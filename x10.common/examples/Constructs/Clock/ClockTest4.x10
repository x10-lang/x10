/**
 * Clock test for  barrier functions
 *
 * foreach loop body represented with a method
 *
 * @author kemal 3/2005
 */
public class ClockTest4 {

	int val=0;
	const int N=32;

	public boolean run() {
      		final clock c = clock.factory.clock();
		
		foreach (point [i]: 1:(N-1)) clocked(c) {
			foreachBody(i,c);
		}
		foreachBody(0,c);
		int temp2;
		atomic {temp2=val;}
		if (temp2!=0) {
			throw new Error();
		}
		return true;
	}

	void foreachBody(final int i, final clock c) {
			now(c) {async(here) {atomic val+=i;}}
			next;
			int temp;
			atomic {temp=val;}
			if (temp != N*(N-1)/2) {
				throw new Error();
			}
			next;
			now(c) {async(here) {atomic val-=i;}}
			next;
	}


	public static void main(String args[]) {
		boolean b= (new ClockTest4()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}

}
