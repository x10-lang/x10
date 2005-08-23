/**
 * Should report the
 * type mismatch in the comparison in the loop, and fail to compile gracefully.
 * As of 08/22/2005 it was causing an NPE in the compiler.
 *
 * @author Bin Xin (xinb@cs.purdue.edu)
 */
public class CompilerNullPointerException {

    (nullable java.lang.Object)[] objList;
         
    public boolean run() {
           final java.lang.Object obj = new java.lang.Object();
           int i = 5;
           while(i>0 && (obj != objList[i]))
                     {i--;};
           return true;

    }

    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new CompilerNullPointerException()).run();
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
