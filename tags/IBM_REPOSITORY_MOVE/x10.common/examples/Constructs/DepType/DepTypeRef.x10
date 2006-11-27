import harness.x10Test;

/**
 * Check that array deptypes are properly processed.
 *
 * @author vj
 */
public class DepTypeRef extends x10Test {
   /*
	public DepTypeRef(int f, double g) {
		this.f = f; this.g=g;
	}
   public boolean(:self==t) m(final boolean t) { 
      return t;
    }
   
   public double foo(double [:rect] a) {
       return a[1,1];
   }*/
	public boolean run() {
		region(:rect) R = (region(:rect)) [1:2,1:2];
		double [:rect] a = (double[:rect]) new double[R] (point p) { return 1.0;};
		//System.out.println("" );//+ foo(a));
	   return true;
	}
	public static void main(String[] args) {
		new DepTypeRef().execute();
	}
}