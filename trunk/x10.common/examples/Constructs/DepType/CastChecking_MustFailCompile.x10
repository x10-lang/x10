import harness.x10Test;

/**
 * Check that a cast involving types which are not related by supertype or subtype
 * relation fails
 *
 * @author pvarma
 */
public class CastChecking_MustFailCompile extends x10Test {
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
	
	class Test3 (int l) {
		Test3(int l) { this.l = l;}
	}
	public boolean run() {
		Test2(:k==1) a = (Test2(:k==1)) new Test2(1);
		Test(:i==j) b = a;
		Test d = (Test) new Test3(1);  // must fail compile
	   return true;
	}
	public static void main(String[] args) {
		new CastChecking_MustFailCompile().execute();
	}
}