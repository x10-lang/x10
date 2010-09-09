import harness.x10Test;

/**
 * Check that a method can have a deptype argument and it is checked properly.
 *
 * @author vj
 */
public class DepTypeInMethodArg extends x10Test {
    class Test(int i, int j) {
       public Test(int i, int j) { this.i=i; this.j=j;}
    }
   public boolean m(Test(:i==j) t) { 
      return true;
    }
	public boolean run() {
	   Test(: i==j) x =  (Test(:i==j)) new Test(1,1); 
	   return true;
	}
	public static void main(String[] args) {
		new DepTypeInMethodArg().execute();
	}
}