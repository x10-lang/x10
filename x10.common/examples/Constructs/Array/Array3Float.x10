/**
 * Simple array test #3
 */
public class Array3Float {

	public boolean run() {
		
		region e= region.factory.region(1,10); //(low,high)
		region r = region.factory.region(e, e); 
		distribution d=distribution.factory.local(r);
		float[.] ia = new float[d];
		ia[1,1] = 42.0F;
		return (42.0F == ia[1,1]);
	
	}
	public static void main(String args[]) {
		boolean b= (new Array3Float()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
}
