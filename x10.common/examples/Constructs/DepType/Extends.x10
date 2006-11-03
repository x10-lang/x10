import harness.x10Test;

/**
 * Check that a deptype can be extended
 *
 * @author vj
 */
public class Extends extends x10Test {
	class Test(int i, int j) {
		Test(int i, int j) {
			this.i=i;
			this.j=j;
		}
	}
		
	class Test2(int k) extends Test(:i==j){
		Test2(int k) {
			super(k,k);
			this.k=k;
		}
	}
	public boolean run() {
		Test2(:k==1) a = (Test2(:k==1)) new Test2(1);
		Test(:i==j) b = a;
	   return true;
	}
	public static void main(String[] args) {
		new DepTypeRef().execute();
	}
}