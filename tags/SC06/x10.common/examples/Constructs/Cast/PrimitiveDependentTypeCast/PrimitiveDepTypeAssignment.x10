import harness.x10Test;

/**
 * Purpose: Checks assignment of primitive to constrained primitives.
 * @author vcave
 **/
public class PrimitiveDepTypeAssignment extends x10Test {

	 public boolean run() {
		byte(:self==1) bb = 1;
		short(:self==10) ss = 10;
		int(:self==20) ii = 20;
		// int(:self==-2) iii = -2; --> si dans la spec ajouter le test
		long(:self==30) ll = 30;
		// float(:self==0.001) ff = (float) 0.001;
		// double (: self == 0.001) i = 0.001;
		char(:self=='c') cc = 'c';
		
		return true;
	}

	public static void main(String[] args) {
		new PrimitiveDepTypeAssignment().execute();
	}
}
 

 