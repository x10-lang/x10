/**
 * Test for for loop on an array
 *
 * @author: vj
 */
public class ForLoopOnArray {
        static final int N=3;

	public boolean run() {
		double[.] a = new double[0:10] (point [i]) { return i;};
		
		for(point [i]: a) {
			if (a[i] != i) return false;
		}
		return true;

	}

	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new ForLoopOnArray()).run();
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