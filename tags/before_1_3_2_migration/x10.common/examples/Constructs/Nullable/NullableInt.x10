/** Check that nullable int can be recognized by the compiler.
 * @author vj Added 6/4/2005
 */
public class NullableInt {

	    public boolean run() {    
	    	final int[] A = { 3,2,1};
	    	nullable int v = (A[1] == 2) ? null : 0;
	    	return v==null; 
	    }
	        
	    public static void main(String[] args) {
	    	final boolean b= false;
	    	try {
	    		finish b=(new NullableInt()).run();
	    	} catch (Throwable e) {
	    		e.printStackTrace();
	    	}
	        System.out.println("++++++ "+(b.val?"Test succeeded.":"Test failed."));
	        x10.lang.Runtime.setExitCode(b.val?0:1);
	    }

}