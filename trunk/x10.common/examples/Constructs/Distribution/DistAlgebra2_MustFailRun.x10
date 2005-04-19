
/**
 * @author kemal 4/2005
 *
 * Testing that disjoint union of distributions
 * actually checks for disjointness.
 *
 *
 */

public class DistAlgebra2_MustFailRun {
	
	const int N=24;

	public boolean run() {
		distribution D=distribution.factory.cyclic([0:N-1]);
		distribution D2=D|[0   : N/2-1];
		distribution D3=D|[N/2 : N-1  ];
		distribution D4= D2 || D3; // disjoint 
		chk(D4.equals(D));
		distribution D5=D||D2; // not disjoint
                return true;
		
	}

	static void chk(boolean b) {
		if (!b) throw new Error();
	}

	public static void main(String args[]) {
		boolean b= (new DistAlgebra2_MustFailRun()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
	
}
