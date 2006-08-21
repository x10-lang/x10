/**
 * Minimal test for await.
 */

public class AwaitTest  {
	
	int val=0;
	
	public boolean run() {
		final clock c = clock.factory.clock();
		async(this) clocked(c) { 
			await (val > 43);
			atomic val = 42;
			await (val == 0);
			atomic val = 42;
		}
		atomic val = 44;
		await(val == 42);
		int temp;
		atomic temp=val;
		System.out.println("temp=" + temp);
		if (temp != 42)
			return false;
		atomic val = 0;
		await(val == 42);
		next;
		int temp2;
		atomic temp2=val;
		System.out.println("val=" + temp2);
		return temp2 == 42;
	}
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new AwaitTest()).run();
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
