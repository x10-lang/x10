
/*
 * @author kemal 4/2005
 *
 * Abbreviated array declarations
 *
 * N means 0:N-1
 *
 * missing distribution mapping: use ->here
 */
public class ArrayDecl {

	const int N=10;

	public boolean run() {
		
		int value[.] v_ia1 = new int value[N]
                      (point [i]){return i;};
		int[.] ia1 = new int[N];
		int value[.] v_ia2 = new int value[N->here]
                      (point [i]){return i;};
		int[.] ia2 = new int[N->here];
		
		return true;
	}

	public static void main(String args[]) {
		boolean b= (new ArrayDecl()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}

}
