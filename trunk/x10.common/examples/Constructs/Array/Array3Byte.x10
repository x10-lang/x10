/**
 * Simple array test #3
 */
public class Array3Byte {

	public boolean run() {
		
		region e= region.factory.region(1,10); //(low,high)
		region r = region.factory.region(e, e); 
		distribution d=distribution.factory.local(r);
		byte[.] ia = new byte[d];
		ia[1,1] = (byte) 42;
		return (42 == ia[1,1]);
	
	}
	public static void main(String args[]) {
		boolean b= (new Array3Byte()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
}
