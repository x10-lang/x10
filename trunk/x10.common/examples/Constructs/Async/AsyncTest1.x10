/**
 * @author Vivek Sarkar (vsarkar@us.ibm.com)
 * Minimal test for async.
 * Uses busy-wait to check for execution of async.
 * run() method returns true if successful, false otherwise.
 */
public class AsyncTest1  {

	boolean flag;

	public boolean run() {
		async (here) { atomic{flag = true;} }
                boolean b;
                do {
                   atomic{b=flag;}
		} while(!b);
	  	return flag;
	}
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new AsyncTest1()).run();
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

}
