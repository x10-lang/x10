/**
 * Test that loops forever (or for 2 minutes).
 * Useful for testing the time limit feature.
 * This test is supposed to fail after the time limit elapses.
 */
public class InfiniteLoopTest_MustFailTimeout {
    boolean flag=X.t();
    public boolean run() {
        async (here) {
            // [IP] Thread.sleep() for now; add x10.lang.System.sleep() later
            try {
                java.lang.Thread.sleep(300000);
            } catch (java.lang.InterruptedException e) { }
            System.exit(128);
        }
        while(flag) ;
        return true;
    }

    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
            finish async b.val=(new InfiniteLoopTest_MustFailTimeout()).run();
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

class X {
    public static boolean t() { return true;}
}

