import x10.lang.*;
/**
 * Test to check that unsafe is being parsed correctly.
 */
public class Unsafe {

    public static void main(String args[]) {
	boolean b=(new Unsafe()).run();
	System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
	System.exit(b?0:1);
    }

    public boolean run() {
	region e= region.factory.region(1,10); //(low,high)
	region r = region.factory.region(new region[]{e, e, e, e}); 
	distribution d=distribution.factory.constant(r,here);
	  
	int [.] x = new int [d]; // ok
	int [.] y = new int unsafe[d]; //ok 
	int value [.] y1 = new int value unsafe[d]; // ok
	int [.] zz = new int unsafe[d] new intArray.pointwiseOp() { public int apply(point p) { return 41; }}; //bad
	return true;
    }
}
