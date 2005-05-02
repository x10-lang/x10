/**
 * Test for arrays, regions and distributions
 * Based on original arraycopy1 by vj
 *
 * @author kemal 1/2005
 * 
 */


public class ArrayCopy1 {
   /**
    * Throws an error iff b is false.
    */
   static void chk(boolean b) {
	if(!b) throw new Error();
   }
    

   /**
    * Does not throw an error iff A[i]==B[i] for all points i. 
    * A and B can have differing distributions
    * whose regions are equal.
    */
	void arrayEqual(final int[.] A,final int[.] B) {
		distribution D = A.distribution;
		distribution E = B.distribution;
		// Spawn an activity for each index to 
		// fetch the b[i] value 
		// Then compare it to the a[i] value
		finish
		ateach(point p:D) chk(A[p]==future(E[p]){B[p]}.force());
	}

	
    /**
     * Set A[i]=B[i] for all i.
     * A and B can have different distributions whose
     * regions are equal.
     * Returns false iff some checking assertion failed
     */
	void arrayCopy(final int[.] A, final int[.] B) {
		final distribution D = A.distribution;
		final distribution E = B.distribution;
		// Spawn an activity for each index to 
		// fetch and copy the value
		finish
		ateach (point p:D) { 
			chk(D[p]==here);
			async(E[p]) chk(E[p]==here);
			A[p] = future(E[p]){B[p]}.force();
		}
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
                finish b.val=(new ArrayCopy1()).run();
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
