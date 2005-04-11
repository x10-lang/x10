/**
 * Simple array test #3
 */
public class Array3Char {

	public boolean run() {
		
		region e= region.factory.region(1,10); //(low,high)
		region r = region.factory.region(e, e); 
		distribution d=distribution.factory.local(r);
		char[.] ia = new char[d];
		ia[1,1] = 'a';
		return ('a' == ia[1,1]);
	
	}
	public static void main(String args[]) {
		boolean b= (new Array3Char()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
}
