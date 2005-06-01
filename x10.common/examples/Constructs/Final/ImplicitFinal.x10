/*
 * Simple array test #1
 */
import x10.lang.*;
public class ImplicitFinal {
	
	public boolean run() {
		point p = [1,2,3];
		region r = [10:10];
		point p1 = [1+1,2+2,3+3];
		return true;
	}
	
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new ImplicitFinal()).run();
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
