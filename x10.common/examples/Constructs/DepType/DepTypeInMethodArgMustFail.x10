import harness.x10Test;

/**
 * Check that a method can have a deptype argument and it is checked properly.
 *
 * @author vj
 */
public class DepTypeInMethodArgMustFail extends x10Test {
    class Test(int i, int j) {
       public Test(int i, int j) { this.i=i; this.j=j;}
    }
   public boolean m(Test(:i==j) t) { 
      return true;
    }
	public boolean run() {
	// should fail because the declared type of the variable is just Test.
	   Test x =  (Test(:i==j)) new Test(1,1); 
	   return m(x);
	}
	public static void main(String[] args) {
		new DepTypeInMethodArgMustFail().execute();
	}
}