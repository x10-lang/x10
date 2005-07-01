/**
 *Combination of finish and clocks. 
 *Finish cannot pass any clock to a subactivity.
 *
 *OLD SEMANTICS:
 *Language clarification needed: 
 *(what if async clocked(c) S occurs inside a library method 
 *or is invoked via an indirect function? 
 *Compiler analysis may be difficult).
 *This test currently causes a deadlock at run time.
 *How should it behave in the ultimate x10 definition?
 *
 *Temporarily declaring that this is an error that should be
 *caught at compile time (this may not be possible).
 *
 * NEW SEMANTICS: Clock Use Exception such as
 *
 * 'Transmission of c (to a child) requires that I am registered with c'
 *
 * 'Transmission of c requires that I am not between c.resume() and a next'
 *
 * 'The immediate body of finish  can never transmit any clocks'
 *
 * are now caught at run time. The compiler
 * can remove the run time checks using static techniques,
 * and can issue warnings when it is statically detected that
 * clock use exceptions will
 * definitely occur, or will likely occur.
 * 
 * Hence this file is renamed as *MustFailRun.x10
 *
 *
 * @author kemal 3/2005
 */
public class ClockTest7_MustFailRun {
    
    int val=0;
    static final int N=16;
    
    public boolean run() {
        
        final clock c = clock.factory.clock();

        finish foreach (point [i]: [0:(N-1)]) clocked(c) {
            atomic val++;
            System.out.println("Activity "+i+" phase 0");
            next;
            atomic chk(val == N);
            System.out.println("Activity "+i+" phase 1");
            next;
            atomic val++;
            System.out.println("Activity "+i+" phase 2");
            next;
        }

        next; next; next;

        atomic chk(val ==2*N);

        return true;
    }

    static void chk(boolean b) {if (!b) throw new Error();}
    
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new ClockTest7_MustFailRun()).run();
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
