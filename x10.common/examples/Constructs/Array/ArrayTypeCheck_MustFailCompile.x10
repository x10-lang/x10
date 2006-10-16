import harness.x10Test;

/**
 * 
 * @author vj
 */
public class ArrayTypeCheck_MustFailCompile extends x10Test {

	public boolean run() {
		int [:rank==3] a1 = new int[[0:2,0:3]->here](point p[i]){ return i; };
		
		return true;
	}

	public static void main(String[] args) {
		new ArrayTypeCheck_MustFailCompile().execute();
	}
}

