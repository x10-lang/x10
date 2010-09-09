/*
 *@author kemal 4/2005
 *
 *A test case that illustrates that deadlock is possible
 *with futures, by creating a circular wait-for dependency. 
 *
 *A0 spawns future activities A1 and A2 and saves the "future"
 *handles from A1 and A2 in global future variables f1 and f2,
 *respectively.
 *
 *A1 sleeps a sufficient time to ensure A0 is done setting f2, then
 *reads global variable f2 and waits for the corresponding activity
 *(A2) to finish.
 *
 *A2 also sleeps a sufficient time to ensure A0 is done setting f1,
 *and then reads global variable f1 and waits for the corresponding
 *activity (A1) to finish.
 *
 *In the meantime A0 starts waiting for A1 to finish.
 *
 *Expected result: must deadlock.
 *
 */

public class FutureDeadlock_MustFailTimeout {
        nullable future<int> f1=null;
        nullable future<int> f2=null;

	int a1() {
		delay(5000); // to make deadlock occur deterministically
		nullable future<int> tmpf=null; 
		atomic tmpf=f2; 
		System.out.println("Activity #1 about to force "+tmpf+" to wait for #2 to complete");
		return ((future<int>)tmpf).force();
	}

        int a2() {
		delay(5000); // to make deadlock occur deterministically
		nullable future<int> tmpf=null; 
		atomic tmpf=f1; 
		System.out.println("Activity #2 about to force "+tmpf+" to wait for #1 to complete");
		return ((future<int>)tmpf).force();
	}

	public boolean run() {
		future<int> tmpf1=future(here){a1()};
		atomic f1=tmpf1;
		future<int> tmpf2=future(here){a2()};
		atomic f2=tmpf2;
		System.out.println("Activity #0 spawned both activities #1 and #2, waiting for completion of #1");
	 	return tmpf1.force()==42;	
	}

	static void delay(int millis) {
		try {
			java.lang.Thread.sleep(millis);
		} catch(InterruptedException e) {
		}
	}

	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new FutureDeadlock_MustFailTimeout()).run();
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
