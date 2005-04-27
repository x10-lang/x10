

/**
 * @author Kemal 4/2005
 * Async with no place-- implicit syntax
 * For now, assume it should cause a compile time error
 * 
 * 
 */
public class AsyncTest3_MustFailCompile  {


	public boolean run() {
		final int[.] A=new int[distribution.factory.unique()];
		chk(place.FIRST_PLACE.next()!=place.FIRST_PLACE);
		finish async {}
		//==> Compiler error expected unless
		//place inference is correctly done
		finish async {A[1]+=1; chk(here==A.distribution[1]);}
		//==> Compiler error definitely expected
		finish async {A[0]+=A[1];}
	  	return true;
	}
	static void chk(boolean b) {
		if(!b) throw new Error();
	}
	public static void main(String args[]) {
		boolean b= (new AsyncTest3_MustFailCompile()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
}
