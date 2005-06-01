
/**
 * @author kemal, 6/2005
 *
 * This test was complaining that
 * "finish cannot spawn clocked async" even though there is
 * only a 'finish async S' surrounding the code.
 * as of 6/1/2005
 */
/**
 * A class to invoke a 'function pointer'
 */
class Y {
	static void test(foo f) {
		finish {
			f.apply(); // it is hard to determine what f does at compile time
		}
	}
}


public class ClockTest19 {
	
	
	public boolean run() {
		/*A0*/
		final clock c0=clock.factory.clock();
		X x= new X();
		// f0 does not transmit clocks to subactivity
		foo f0= new foo() {
			public void apply() {
                                /* Activity A3 */
				async {
					System.out.println("#A3: hello from A3");
				}
			}    
		};
		// f1 transmits clock c0 to subactivity
		foo f1=new foo() {
			public void apply() { 
				/*Activity A2*/
				async clocked(c0){
					System.out.println("#A2 before resume");
					c0.resume();
					System.out.println("#A2 before next");
					next;
					System.out.println("#A2 after next");
				}
			}
		};
		
		foo[] fooArray=new foo[] {f0,f1};
		
		
	        System.out.println("#A0 before spawning A1");
	        async clocked(c0) {System.out.println("#A1: hello from A1");}
	        System.out.println("#A0 before spawning A3");
		Y.test(fooArray[x.zero()]);
	        System.out.println("#A0 before spawning A2");
		Y.test(fooArray[x.one()]);
		System.out.println("#A0 before resume");
		c0.resume();
		System.out.println("#A0 before next");
	        next;
		System.out.println("#A0 after next");
            
		return true;
	}
	
	public static void main(String[] args) {
		final boxedBoolean b=new boxedBoolean();
		try {
			finish async b.val=(new ClockTest19()).run();
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

/**
 * An interface to use like a simple 'function pointer'
 *
 * foo f1=new foo(){public void apply() S1}; //assign body S1 to f1
 *
 * // values of free final variables of S1 are also captured in f1.
 *
 * f1.apply(); // invoke S1 indirectly using its captured
 *
 * // free variables 
 *
 */
interface foo {
	public void apply();
}

/** 
 * Dummy class to make static memory disambiguation difficult
 * for a typical compiler
 */
class X {
	public int[] z={1,0};
	int zero() { return z[z[z[1]]]; /* that is a 0 */} 
	int one() { return z[z[z[0]]];/* that is a 1 */} 
	void modify() {z[0]+=1;}
}
