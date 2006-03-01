
/**
 * @author kemal 4/2005
 *
 * Non X10 java constructs should be flagged by compiler
 *
 */
public class NonX10Constructs_MustFailCompile {
   volatile boolean flag=false;
   int x=0;
   public boolean run() {
       boolean b=false;
       synchronized(this) { x++;}
       synchronized(this) { b=(x==1);}
       return b;
   }
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new NonX10Constructs_MustFailCompile()).run();
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
