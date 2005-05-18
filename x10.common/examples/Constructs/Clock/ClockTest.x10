/**
 * Minimal test for clock.  Does not do anything
 * interesting.  Only possible failure is to not
 * compile or hang.             
 */
public class ClockTest {

    public boolean run() {
        clock c = clock.factory.clock();
        next;
        c.resume();
        c.drop();               
        return true;
    }
        
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new ClockTest()).run();
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
