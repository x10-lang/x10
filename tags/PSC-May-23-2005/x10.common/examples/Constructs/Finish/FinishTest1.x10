import x10.lang.*;
/**
 * @author Vivek Sarkar (vsarkar@us.ibm.com)
 * Minimal test for finish, using an async.
 * run() method returns true if successful, false otherwise.
 */
public class FinishTest1  {

	boolean flag;

	public boolean run() {
		finish {
			async (here) { atomic{flag = true;} }
		}
          boolean b;
          atomic{b=flag;}
	  return b;
	}
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new FinishTest1()).run();
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
