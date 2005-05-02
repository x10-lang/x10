/**
 * Test for arrays, regions and distributions.
 * Based on original arraycopy2 by vj.
 *
 * @author kemal 1/2005
 *
 */

public class ArrayCopy2 {
	
       /**
        * Throws an error iff b is false.
        */
	static void chk(boolean b) {
		if(!b) throw new Error();
	}

	/**
	 * Returns true iff point x is not in the domain of 
	 * distribution D
	 */
	static boolean outOfRange(final distribution D, final point x) {
		boolean gotException=false;
		try{
			async(D[x]){}; // just to use place
		} catch (Throwable e) {
			gotException=true;
		}
		return gotException;
	}
	
	/**
	 * Does not throw an error iff A[i]==B[i] for all i 
	 */
	
	void arrayEqual(final int[.] A, final int[.] B) {
		// Spawn an activity for each index to 
		// fetch the B[i] value 
		// Then compare it to the A[i] value
		final distribution D=A.distribution;
		final distribution E=B.distribution;
		finish
		ateach(point i:D) chk(A[i]==future(E[i]){B[i]}.force());
	}
	
	/**
	 * Set A[i]=B[i] for all points i.
	 * Throws an error iff some assertion failed.
	 */
	
	public void arrayCopy(final int[.] A,final int[.] B) {
		final distribution D=A.distribution;
		final distribution E=B.distribution;
		// Spawn one activity per place 
		
		final distribution D_1= distribution.factory.unique(D.places()); 
		// number of times elems of A are accessed
		final int[.] accessed_a = new int[D];
		// number of times elems of B are accessed
		final int[.] accessed_b = new int[E];
		
		finish
		ateach (point x:D_1)  {
			final place px= D_1[x];
			chk(px==here);
			final distribution D_local= (D|px);
			for (point i : D_local ) { 
				// assignment to A[i] may need to be atomic
				// unless disambiguator has high level
				// knowledge about distributions
				async(E[i]) {
					chk(E[i]==here);
					atomic accessed_b[i]+=1;
				}
				A[i] = future(E[i]){B[i]}.force();
				atomic accessed_a[i]+=1;
			}
			// check if distribution ops are working
			
			final distribution D_nonlocal= D-D_local;
			chk((D_local||D_nonlocal).equals(D));
			for(point k:D_local) {
				chk(outOfRange(D_nonlocal,k));
				chk(D_local[k]==px);
			}
			for (point k: D_nonlocal) {
				chk(outOfRange(D_local,k));
				chk(D_nonlocal[k]!=px);
			}
			
			
		}
		// ensure each a[i] was accessed exactly once
		finish ateach(point i:D) chk(accessed_a[i]==1);
		// ensure each b[i] was accessed exactly once
		finish ateach(point i:E) chk(accessed_b[i]==1);
	}

    const int N=3;

    /**
     * For all combinations of distributions of arrays B and A,
     * do an array copy from B to A, and verify.
     */
    public boolean run() {
         final region R= [0:N-1,0:N-1,0:N-1,0:N-1];
         final region TestDists= [0:dist.N_DIST_TYPES-1,0:dist.N_DIST_TYPES-1];

         for(point distP[dX,dY]: TestDists) {
		
             final distribution D=dist.getDist(dX,R);
             final distribution E=dist.getDist(dY,R);
             chk(D.region.equals(E.region)&&D.region.equals(R)); 
             final int[.] A= new int[D];
             final int[.] B= new int[E]
	      (point p[i,j,k,l]){int x=((i*N+j)*N+k)*N+l; return x*x+1;};
             arrayCopy(A,B);
             arrayEqual(A,B);
         }
         return true;
    }


	/**
	 * main method
	 */
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new ArrayCopy2()).run();
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
 * utility for creating a distribution from a
 * a distribution type int value and a region
 */
class dist {
   const int BLOCK=0;
   const int CYCLIC=1;
   const int BLOCKCYCLIC=2;
   const int CONSTANT=3;
   const int RANDOM=4;
   const int ARBITRARY=5;
   public const int N_DIST_TYPES=6;

   /**
    * Return a distribution with region r, of type disttype
    *
    */

   public static distribution getDist(int distType, region r) {
      switch(distType) {
         case BLOCK: return distribution.factory.block(r);
         case CYCLIC: return distribution.factory.cyclic(r);
         case BLOCKCYCLIC: return distribution.factory.blockCyclic(r,3);
         case CONSTANT: return distribution.factory.constant(r, here);
         case RANDOM: return distribution.factory.random(r);
         case ARBITRARY: return distribution.factory.arbitrary(r);
         default: throw new Error();
      }
     
   } 
}
	
