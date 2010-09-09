import harness.x10Test;

/**
 * Purpose: Checks code generated refer to this.p correctly (i.e. has CastConstraintOnThis.this.p)
 * @author vcave
 **/
public class CastConstraintOnThis extends x10Test {

	 private final int p;

	public CastConstraintOnThis(final int k) {
	    this.p=k;
	}

	public boolean run() {
		
		X10DepTypeClassTwo(:self.p==this.p) one = 
			(X10DepTypeClassTwo(:self.p==this.p)) new X10DepTypeClassTwo(this.p,0);
		
		return one.p() == 2;
	}
	
	public static void main(String[] args) {
		new CastConstraintOnThis(2).execute();
	}
}

