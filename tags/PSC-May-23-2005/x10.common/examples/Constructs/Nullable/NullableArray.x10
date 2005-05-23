/*
 *@author kemal 3/2005
 *
 * testing arrays that are nullable 
 * (causes exception as of 3/12/05)
 */

import x10.lang.*;
class mycomplex {
int re;
int im;
}
public class NullableArray {

	public boolean run() {
		
		int[] ia1 = new int[2];
		if (ia1[1]!=0) return false;
		nullable int[] ia2 = null;
		if (ia2!=null) return false;
		nullable int[.] ia3 = null;
		if (ia3!=null) return false;
		ia3 = new int[(0:2)->here];
		if (ia3[2]!=0) return false;
		mycomplex[.] ia4 = new mycomplex[(0:2)->here]
                 (point [i]) {return new mycomplex();};
		if (ia4[2].im!=0) return false;
		nullable mycomplex[.] ia5 = new mycomplex[(0:2)->here]
                 (point [i]) {return new mycomplex();};
		 if (ia5[2].im!=0) return false;
		nullable mycomplex[.] ia7 = null;
		if (ia7!=null) return false;
		return true;
	}

	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new NullableArray()).run();
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
