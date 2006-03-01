/**
 * Test for an ateach loop on an array
 *
 * @author: vj
 */
public class AtEachLoopOnArray {
    boolean success = true;

    public boolean run() {
        final double[.] A = new double[[0:10] -> here] (point [i]) { return i;};
        
        finish ateach(point [i]: A) 
            if (A[i] != i) 
                async (this) atomic { success = false; }
        
        return success;

    }
    
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new AtEachLoopOnArray()).run();
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
