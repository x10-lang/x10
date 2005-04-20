/*
 * @author kemal 4/2005
 *
 *
 * Array declarations using N instead of (0:N-1)
 *
 */
public class ArrayDecl {

	const int N=24;

	public boolean run() {
		
		int[.] ia0 = new int[N];
		chk(ia0.distribution.equals([0:N-1]->here));
		int[.] ia1 = new int[N,N];
		chk(ia1.distribution.equals([0:N-1,0:N-1]->here));
		int value[.] v_ia2 = new int value[N->here]
                      (point [i]){return i;};
		int[.] ia2 = new int[N->here];
		chk(ia2.distribution.equals([0:N-1]->here));
		return true;
	}

 	/**
	 * Throw an error if b is false
	 */
	static void chk(boolean b) {

		if(!b) throw new Error();
	}

	/**
	 * Main method
	 */
	public static void main(String args[]) {
		boolean b= (new ArrayDecl()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}

}
