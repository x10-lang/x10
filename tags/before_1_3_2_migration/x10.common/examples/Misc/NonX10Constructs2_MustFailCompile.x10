
/**
 * @author kemal 6/2005
 *
 * Non X10 java constructs should be flagged by compiler
 *
 * Java jagged arrays should not be accepted by x10.
 *
 */
public class NonX10Constructs2_MustFailCompile {
   public boolean run() {
       int x[][]={{1,2},{3,4}};
       if (x[1][1]!= 4) return false;
       int[][] y={{1,2},{3,4}};
       if (y[1][1]!= 4) return false;
       return true;
   }
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new NonX10Constructs2_MustFailCompile()).run();
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
