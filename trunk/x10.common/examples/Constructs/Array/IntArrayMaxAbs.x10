
/**
 * Testing the maxAbs function on arrays.
 */

public class IntArrayMaxAbs {
	
	public boolean run() {
		final dist D=[1:10,1:10]->here;
		final int[.] ia = new int[D];
			
		finish ateach(point p[i,j]:D) { ia[p]= -i;}
	
		return ia.maxAbs()==10;
	}
	
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new IntArrayMaxAbs()).run();
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
