/**
 * Test for X10 arrays -- tests arrays passed as parameters and stored in fields.
 *
 */
public class Array4 {
	int[.] ia;

	public Array4() {}

	public Array4(int[.] ia) {
		this.ia = ia;
	}

	private boolean runtest() {
		ia[1,1] = 42;
		return 42 == ia[1,1];
		
	}

	/**
	 *Run method for the array. Returns true iff the test succeeds.
	 */
	public boolean run() {
		region e= [1:10];
		region r = [e,e];
		dist d=r->here;
		chk(d.equals([1:10,1:10]->here));
		return (new Array4(new int[d])).runtest();
	}

    static void chk(boolean b) {if (!b) throw new Error();}
	

	/** Harness for running the test.
	 * 
	 */
        
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new Array4()).run();
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
