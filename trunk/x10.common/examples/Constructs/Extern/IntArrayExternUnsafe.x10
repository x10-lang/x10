
import x10.lang.*;
/**
 * Test to check that unsafe is being parsed correctly.
 */
public class IntArrayExternUnsafe {
	public static  extern void howdy(int [.] yy);
	static {System.loadLibrary("IntArrayExternUnsafe");}
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new IntArrayExternUnsafe()).run();
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

	public boolean run(){
		int high = 10;
		boolean verified=false;
		dist d= (0:high) -> here;
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