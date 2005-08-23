/**
 *
 * Test resulted in unreachable statement message
 * in the compiler, as of 7/29/2005
 *
 *@author Armando Solar-Lezama
 *
 */
public class Unreachable3_MustFailTimeout {
	

    public boolean run() {
        async (here) {
            while(true){}
        }
        return true;
    }
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new Unreachable3_MustFailTimeout()).run();
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
