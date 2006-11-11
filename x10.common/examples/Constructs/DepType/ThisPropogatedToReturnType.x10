import harness.x10Test;

/** Tests that if t is of type Test(:i==1), and test() on Test returns int(:self==this.i),
* then t.test() returns int(:self==1). That is, information is propagating
* correctly from the type of the receiver into the return type of a method.
* @author vj
*/
public class ThisPropogatedToReturnType extends x10Test {

	 class Test(int i, int j) {
		Test(int i, int j) { this.i=i; this.j=j;}
		
		int(:self==this.i) test() {
			return (int(:self==this.i)) i;
		}
	}
	
	public boolean run() {
		Test(:i==1) t = (Test(:i==1)) new Test(1,2);
		int(:self==1) one = t.test();
		return true;
	}
	public static void main(String[] args) {
		 new ThisPropogatedToReturnType().execute();
	}
}
