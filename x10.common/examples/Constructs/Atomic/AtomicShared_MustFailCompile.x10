//LIMITATION: 
//This test case will not meet expectations. It is a limitation of the current release.
/**
 * @author kemal 5/2005
 *
 * x10 Compiler must detect potentially shared and mutable lvalues 
 * and require that the accesses be enclosed in an 
 * atomic section.
 *
 */

public class AtomicShared_MustFailCompile {

	int x1=0;
	int x2=0;
	int[.] A1=new int[dist.factory.unique()];
	int[.] A2=new int[dist.factory.unique()];

	public boolean run() {
		final X x=new X();
                // A compiler may answer yes, no, or maybe
		// to the question: Is this lvalue ever
		// shared and mutable in any execution?

		//Compiler: NO, Actual: NO
	        // x10 still requires atomic.
		// ==> Compiler error here
		finish async (this) {
			x1++;
		}
		chk(x1==1);

		//Compiler: YES, Actual: YES
		//==> Compiler error here
		finish {
		   async (this) {
			x2++;
		   }
 		   async (this) {
			x2++;
		   }
		}

		//Compiler: MAYBE, Actual: NO
		//==> Compiler error here
		finish {
		   async(this) {
			A1[x.zero()] +=1;
		   }
		   async(this) {
			A1[1] +=1;
		   }
		}
		chk(A1[0]==1 && A1[1]==1);

		//Compiler: MAYBE, Actual: YES
		//==> Compiler error here
		finish {
		   async(this) {
			A2[x.one()] +=1;
		   }
		   async(this) {
			A2[1] +=1;
		   }
		}

		//Compiler: NO, Actual: NO
		// No compiler error here because of lack of atomic
		final foo@activity y= new foo();
		y.x++;

		chk(y.x==1);

		// activity local references cannot be passed to other activities
		// Compiler error here.
		finish async(y) { y.x++;}
		

		return true;
	}

	static void chk(boolean b) {	
		if(!b) throw new Error();
	}

  
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new AtomicShared_MustFailCompile()).run();
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

/**
 * dummy class to test activity local storage
 */
class foo {
	int x=0;
}
