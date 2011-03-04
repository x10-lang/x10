/**
 * Simple test for operator assignment of array elements.  
 */
public class ArrayOpAssign {

	public boolean run() {
		boolean result = true;
		int[.] ia = new int[[1:10,1:10]];
		ia[1,1] = 1;
		ia[1,1] += ia[1,1];
		result &= (2 == ia[1,1]);
		System.out.println("ia[1,1])" +	 ia[1,1]);
		ia[1,1] *= 2;
		System.out.println("ia[1,1])" +	 ia[1,1]);
		result &= (4 == ia[1,1]);
		double[.] id = new double[[1:10,1:10]];
		id[1,1] += 42;
		result &= (42 == id[1,1]);
		System.out.println("id[1,1])" +	 id[1,1]);
		id[1,1] *= 2;
		System.out.println("id[1,1])" +	 id[1,1]);
		result &= (84 == id[1,1]);
		return result;
	
	}
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new ArrayOpAssign()).run();
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
