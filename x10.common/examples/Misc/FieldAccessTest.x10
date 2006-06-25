import harness.x10Test;
/**
This produces an infinite loop for the type checker, on 06/25/06
*/

import x10.lang.Object;
import x10.lang.Integer;
public class FieldAccessTest extends x10Test {
	public nullable Object n;
//	 the method is deliberately type-incorrect.
// it should return nullable Object. 
// The problem is that this incorrect program causes the compiler to loop.

	public FieldAccessTest n() { 
		return n;
	}
		public boolean run() {
			return true;
		}
	
		public static void main(String[] args) {
			new FieldAccessTest().execute();
		}
}