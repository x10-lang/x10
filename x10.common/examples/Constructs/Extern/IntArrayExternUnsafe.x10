
import x10.lang.*;
/**
 * Test to check that unsafe is being parsed correctly.
 */
public class IntArrayExternUnsafe {
	public static  extern void howdy(int [.] yy);
	static {System.loadLibrary("IntArrayExternUnsafe");}
	public static void main(String args[]) {
		boolean b= (new IntArrayExternUnsafe()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
	public boolean run(){
		int high = 10;
		boolean verified=false;
		distribution d= (0:high) -> here;
		int [.] y = new int unsafe[d]; 
		
		for(int j=0;j < 10;++j){
			y[j] = j;
		}
		
		howdy(y);
		
		for(int j=0;j < 10;++j){
			int expected = j+100;
			if(y[j] != expected){
				System.out.println("y["+j+"]="+y[j]+" != "+expected);
				return false;
			}
			//System.out.println("y["+j+"]:"+(y[j]));
		}
		return true;
	}
}