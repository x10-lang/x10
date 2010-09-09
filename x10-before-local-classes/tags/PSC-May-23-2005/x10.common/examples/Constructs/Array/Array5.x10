/**
 * 
 * Testing int[] method parameters and fields.
 */

public class Array5 {

	int[] ia;

	public Array5() {}

	public Array5(int [] ia) {
		this.ia = ia;
	}

	private boolean runtest() {
		ia[0] = 42;
		return 42 == ia[0];
		
	}

	public boolean run() {
		int[] temp = new int[1];
		temp[0] = 43;
		return (new Array5(temp)).runtest();
	}

	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new Array5()).run();
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
