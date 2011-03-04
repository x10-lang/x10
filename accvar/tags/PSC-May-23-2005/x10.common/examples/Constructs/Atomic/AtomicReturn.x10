/**
 * Atomic return test
 */
public class AtomicReturn  {
	int a = 0;
	static final int N=100;
	
	int update1() {
		atomic {
			a++;
			return a;
		}
	}
	int update3() {
		atomic {
			return a++;
		}
	}
	public boolean run() {
		update1();
		update3();
		System.out.println(a);
		return a == 2;
	}
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new AtomicReturn()).run();
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
