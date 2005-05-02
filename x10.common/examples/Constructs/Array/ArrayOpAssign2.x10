/**
 * Simple test for operator assignment of array elements.  
 * Tests post and pre increment/decrement;
 */
public class ArrayOpAssign2 {
	int i=1;
	int j=1;
	public boolean run() {
		int[.] ia = new int[[1:10,1:10]];
		ia[i,j] = 1;	
		chk(ia[i,j]==1);
		chk((ia[i,j]++)==1);
		chk(ia[i,j]==2);
		chk((ia[i,j]--)==2);
		chk(ia[i,j]==1);
		chk((++ia[i,j])==2);
		chk(ia[i,j]==2);
		chk((--ia[i,j])==1);
		chk(ia[i,j]==1);
		return true;
	}
	
	static void chk(boolean b) {if (!b) throw new Error();}
	
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new ArrayOpAssign2()).run();
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
