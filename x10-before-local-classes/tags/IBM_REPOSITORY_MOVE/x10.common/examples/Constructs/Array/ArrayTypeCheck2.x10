import harness.x10Test;

/**
 * 
 * @author vj
 */
public class ArrayTypeCheck2 extends x10Test {

	public boolean run() {
		final int(:self==2) two = 2;
		
		int [:rank==two] a1 = new int[[0:2,0:3]->here](point p[i]){ return i; };
		
		return true;
	}

	public static void main(String[] args) {
		new ArrayTypeCheck2().execute();
	}
}

