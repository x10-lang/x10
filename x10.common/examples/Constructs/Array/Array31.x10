/**
 *Simple array test #3. Tests declaration of arrays, storing in local
 *variables, accessing and updating for 1-d arrays.
 *
 */
public class Array31 {

	public boolean run() {
		
		region e= [1:10];
		dist d=[e];
		chk(d.equals([1:10]->here));
		int[.] ia = new int[d];
		ia[1] = 42;
		return 42 == ia[1];
	
	}
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new Array31()).run();
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
