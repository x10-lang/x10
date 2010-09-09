import harness.x10Test;

/**
 * A class with parameters, Test, is defined as an inner class. Check
 * that a type Tes(:i==j) can be defined. 
 *
 * @author vj
 */
public class EntailsPositiveInner_MustFailCompile extends x10Test {
    class Test(int i, int j) {
       public Test(int i, int j) { this.i=i; this.j=j;}
    }

	public boolean run() {
	    final int j = 0;
	    Test(: i==j) x =  new Test(1,2); // should fail
	    return true;
	}
	public static void main(String[] args) {
		new EntailsPositiveInner_MustFailCompile().execute();
	}
}

