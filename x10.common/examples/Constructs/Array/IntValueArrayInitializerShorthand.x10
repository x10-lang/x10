/**
* Test the shorthand syntax for a value array initializer.
*/
import x10.lang.*;
public class IntValueArrayInitializerShorthand {

	public boolean run() {
		distribution d =  [1:10, 1:10] -> here;
		int value [.] ia = new int value[d] (point [i,j]) { return i+j; };
		
		for(point p[i,j]: [1:10,1:10]) 
			if(ia[p]!=i+j) return false;
		return true;
	}
	
	public static void main(String args[]) {
		boolean b= (new IntValueArrayInitializerShorthand()).run();
		System.out.println(b?"Test succeeded.":"Test failed.");
		System.exit(b?0:1);
	}
	
}
