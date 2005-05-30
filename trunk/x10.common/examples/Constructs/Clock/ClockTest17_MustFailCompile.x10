/**
 * @author kemal 4/2005
 *
 *The x10 compiler must check if a finish body 
 *can pass any clock to a subactivity.
 *If so, it must report a compile time error.
 *(Otherwise a deadlock occurs at run time).
 *
 */

public class ClockTest17_MustFailCompile {

    public boolean run() {
           /*A0*/
           final clock c0=clock.factory.clock();
           finish { // still part of A0
		final clock c1=clock.factory.clock();
                /*A1*/async clocked(c0) { //old clock
                   System.out.println("#1 before next");
                   next;
                   System.out.println("#1 after next");
                }
                /*A2*/async clocked(c1) { // new clock
                   System.out.println("#2 before next");
                   next;
                   System.out.println("#2 after next");
                }
           }
           // Execution never reaches here since:
           // A1 and A2 must first finish, but they
           // cannot since A0 has not executed next yet.
           System.out.println("#0 before next");
           next;
           System.out.println("#0 after next");
	   return true;
    }
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new ClockTest17_MustFailCompile()).run();
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
