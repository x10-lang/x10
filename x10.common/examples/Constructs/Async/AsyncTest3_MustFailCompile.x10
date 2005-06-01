//LIMITATION: 
//This test case will not meet expectations. It is a limitation of the current release.

/**
 * @author Kemal 4/2005
 * Remote accesses must be flagged by the compiler.
 * 
 */
public class AsyncTest3_MustFailCompile  {


	public boolean run() {
		final int[.] A=new int[dist.factory.unique()];
		chk(place.MAX_PLACES>=2);
		chk(A.distribution[0]==here);
		chk(A.distribution[1]!=here);
		final X x=new X();

		// Compiler can give one of the answers:
                // YES, NO, MAYBE for each	
		// question: is this lvalue expression ever nonlocal?
		// (x10 Language definition question needing clarification:
		// Which 'compiler algorithm' should be used?)

		// When the compiler answer is YES or NO
		// the actual answer at run time matches
		// the compiler answer on every execution.	.
		// When the compiler answer is MAYBE, the actual
		// answer may be either YES or NO.
		// X10 requirement: when the compiler
		// answer is YES or MAYBE, the lvalue
		// must be enclosed in an async or a future,
		// and should be run at the correct place of the lvalue.

		// Compiler: NO: Actual: NO
		// no compiler error on next lines
	        finish async(here) {A[0]+=1;}	
		A[0]+=1;

		// Compiler: YES, Actual: YES
		//==> Compiler errors expected
		finish async(here) {A[1]+=1;}
		A[1]+=1;

		// Compiler: MAYBE; Actual: NO
		//==> Compiler error expected here
		finish async(here) {A[x.zero()]+=1;}
		A[x.zero()]+=1;

 		// Compiler: MAYBE; Actual: YES
		// for lvalue A[X.one()]
		//==> Compiler errors expected here
		finish async(here) {A[0]+=A[x.one()];}
		A[0]+=A[x.one()];
 		
		// Compiler YES: Actual: YES (for lvalue A[1])
		// ==> Compiler errors expected on next line
		chk(A[0]==8 && A[1]==2);

	  	return true;
	}
	static void chk(boolean b) {
		if(!b) throw new Error();
	}
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new AsyncTest3_MustFailCompile()).run();
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

/** 
 * Dummy class to make static memory disambiguation difficult
 * for a typical compiler
 */
class X {
    public int[] z={1,0};
    int zero() { return z[z[z[1]]];} 
    int one() { return z[z[z[0]]];} 
    void modify() {z[0]++;}
}


