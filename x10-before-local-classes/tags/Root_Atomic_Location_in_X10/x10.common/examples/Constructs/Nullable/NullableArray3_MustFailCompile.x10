/*
 *@author kemal 4/2005
 *
 * Testing arrays that are nullable 
 *
 * Comparing non-nullable to null should cause compiler
 * to report an error.
 */

class mycomplex {
int re;
int im;
}
public class NullableArray3_MustFailCompile {

	public boolean run() {
		
		int[] ia1 = new int[2];
		if (ia1==null) return false;
		mycomplex[.] ia2 = new mycomplex[[0:2]->here]
                 (point [i]) {return new mycomplex();};
		if (ia2==null) return false;
		if (ia2[2]==null) return false;
		return true;
	}

	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new NullableArray3_MustFailCompile()).run();
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
