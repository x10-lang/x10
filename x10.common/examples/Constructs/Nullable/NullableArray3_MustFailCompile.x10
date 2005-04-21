/*
 *@author kemal 4/2005
 *
 * Testing arrays that are nullable 
 *
 * Comparing non-nullable to null should cause compiler
 * to report an error.
 */

class mycomplex {
int re;
int im;
}
public class NullableArray3_MustFailCompile {

	public boolean run() {
		
		int[] ia1 = new int[2];
		if (ia1==null) return false;
		mycomplex[.] ia2 = new mycomplex[(0:2)->here]
                 (point [i]) {return new mycomplex();};
		if (ia2==null) return false;
		if (ia2[2]==null) return false;
		return true;
	}

	public static void main(String args[]) {
		boolean b= (new NullableArray3_MustFailCompile()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}

}
