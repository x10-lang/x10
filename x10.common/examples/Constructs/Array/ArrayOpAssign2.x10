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
	
	public static void main(String args[]) {
		boolean b=false;
		try {
			b= (new ArrayOpAssign2()).run();
		} catch (Throwable e) {
			e.printStackTrace();
		}	
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
}
