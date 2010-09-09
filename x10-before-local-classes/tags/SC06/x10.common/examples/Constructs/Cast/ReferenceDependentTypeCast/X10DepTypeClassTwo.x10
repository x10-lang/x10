import harness.x10Test;

/**
 * Purpose: Checks variable name shadowing works correctly.
 * @author vcave
 **/
public class X10DepTypeClassTwo(int p, int q) extends x10Test {

	public X10DepTypeClassTwo(int p, int q) {
	    this.p=p;
	    this.q=q;
	}

	public boolean run() {
		
		X10DepTypeClassTwo(:self.p==p) one = 
			new X10DepTypeClassTwo(:self.p==p)(p,0);
		
		return one.p() == 0;
	}
	
	public static void main(String[] args) {
		new X10DepTypeClassTwo(0,0).execute();
	}
}

