import x10.lang.*;
/**
 * Test to check that unsafe is being parsed correctly.
 */
public class Unsafe {

    
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new Unsafe()).run();
        } catch (Throwable e) {
                e.printStackTrace();
                b.val=false;
        }
        System.out.println("++++++ "+(b.val?"Test succeeded.":"Test failed."));
        x10.lang.Runtime.setExitCode(b.val?0:1);
    }
    static class boxedBoolean {
        boolean val=false;
    }


    public boolean run() {
	region e= region.factory.region(1,10); //(low,high)
	region r = region.factory.region(new region[]{e, e, e, e}); 
	dist d=dist.factory.constant(r,here);
	  
	int [.] x = new int [d]; // ok
	int [.] y = new int unsafe[d]; //ok 
	int value [.] y1 = new int value unsafe[d]; // ok
	int [.] zz = new int unsafe[d] new intArray.pointwiseOp() { public int apply(point p) { return 41; }}; //bad
	return true;
    }
}
