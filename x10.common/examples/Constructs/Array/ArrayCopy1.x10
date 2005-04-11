import x10.lang.*;
/**
 * Test for arrays, regions and distributions
 * Based on original arraycopy1
 * @author kemal 1/2005
 * 
 * temporarily disabled boolean arrays
 */


public class ArrayCopy1 {
    

   /**
    * Returns true iff a[i]==b[i] for all i 
    * a and b can have differing distributions
    * whose regions are equal
    */
	public boolean arrayEqual(final int[.] a, final int[.] b) {
		final distribution D = a.distribution;
		final distribution E = b.distribution;
		// Spawn an activity for each index to 
		// fetch the b[i] value 
		// Then compare it to the a[i] value
		// boolean[D] err=new boolean[D];
		finish
		ateach (point p : D) { 
			int t= future(E[p]){b[p]}.force();
			//err[p]|= (a[p]!=t); 
			if (a[p]!=t) throw new Error(); 
		}
		//return !err.reduce(boolean.or,false);
		return true;
	}

	
    /**
     * Set a[i]=b[i] for all i
     * a and b can have different distributions whose
     * regions are equal
     * returns false iff some checking assertion failed
     */
	public boolean arrayCopy(final int[.] a, final int[.] b) {
		final distribution D = a.distribution;
		final distribution E = b.distribution;
		// Spawn an activity for each index to 
		// fetch and copy the value
		//boolean[D] err_a= new boolean[D];
		//boolean[E] err_b= new boolean[E];
		finish
		ateach (point p : D) { 
			//err_a[p] |= (D.get(p) !=here);
			if (D[p]!=here) throw new Error();
			async(E[p]){
                           //err_b[p] |= (E.get(p)!=here);
			   if(E[p] !=here) throw new Error();
                           }
			a[p] = future(E[p]){b[p]}.force();
		}
		//return  !(err_a.reduce(boolean.or,false)
		//	|| err_b.reduce(boolean.or,false));
		return true;
	}
    
   
    final static int N=2;

    public boolean run() {
         final region r= region.factory.region(0, N-1);
         final region t1= region.factory.region(0, dist.N_DIST_TYPES-1);
         final region testDists= region.factory.region(new region[]{t1,t1});
         for(point distP: testDists) {
		
             final distribution D=dist.getDist(distP.get(0),r);
             final distribution E=dist.getDist(distP.get(1),r);
             if(!(D.region.equals(E.region)&&D.region.equals(r))) 
                  return false;
             final int[.] a= new int[D];
             final int[.] b= new int[E];
             finish ateach(point p:E) {final int i=p.get(0); b[p]= i*i+1;}
             if (!arrayCopy(a,b)) return false;
             if (!arrayEqual(a,b)) return false;
         }
         return true;
    }


	public static void main(String args[]) {
		boolean b= (new ArrayCopy1()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
}

/**
 * utility for creating a distribution from a
 * a distribution type int value
 */
class dist {
   // Java has poor support for enum
   public final static int BLOCK=0;
   public final static int CYCLIC=1;
   public final static int CONSTANT=2;
   public final static int RANDOM=3;
   public final static int ARBITRARY=4;
   public final static int N_DIST_TYPES=5;

   /**
    * Return a distribution with region r, of type disttype
    *
    */

   public static distribution getDist(int distType, region r) {
      switch(distType) {
         case BLOCK: return distribution.factory.block(r);
         case CYCLIC: return distribution.factory.cyclic(r);
         case CONSTANT: return distribution.factory.constant(r, here);
         case RANDOM: return distribution.factory.random(r);
         case ARBITRARY: return distribution.factory.arbitrary(r);
         default: throw new Error("TODO");
         // default: should throw exception
      }
     
   } 
}
