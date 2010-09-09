import harness.x10Test;

/**
 * Check that a method's argument's deptype can reference a preceding non-final argument.
 *
 * @author pvarma
 */
public class DepTypeWithNonFinalMethodArg extends x10Test {
    class Test(int i, int j) {
       public Test(int i, int j) { this.i=i; this.j=j;}
    }
   public boolean m(Test t1, Test(:i == t1.i) t2) { 
      return true;
    }
	public boolean run() {
	   Test(: i==j) x =  (Test(:i==j)) new Test(1,1); 
	   return true;
	}
	public static void main(String[] args) {
		new DepTypeWithNonFinalMethodArg().execute();
	}
}