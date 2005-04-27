import x10.lang.*;

/**
 * Test for arrays, regions and distributions
 * Based on original arraycopy2
 * @author kemal 1/2005
 *
 * Temporarily disabled boolean arrays
 */

public class ArrayCopy2v {
	
	boolean errorsFound=false;
	
	/**
	 * Returns true iff x is not in the domain of one dim
	 * distribution D
	 */
	static boolean outOfRange(final distribution D, final point x) {
		boolean gotException=false;
		// assert(D.dimension==1) 
		try{
			async(D[x]){}; // just to use place
		} catch (Throwable e) {
			gotException=true;
		}
		return gotException;
	}
	
	/**
	 * Returns true iff a[i]==b[i] for all i 
	 */
	
	public boolean arrayEqual(final int[.] a, final int[.] b) {
		// Spawn an activity for each index to 
		// fetch the b[i] value 
		// Then compare it to the a[i] value
		final distribution D=a.distribution;
		final distribution E=b.distribution;
		// boolean[D] err=new boolean[D];
		finish
		ateach (final point i : D) { 
			final int t= future(E[i]){b[i]}.force();
			//err[i]|= (a[i]!=t); 
			if (a[i]!=t) throw new Error();
		}
		//return !err.reduce(boolean.or,false);
		return true;
	}
	
	/**
	 * Set a[i]=b[i] for all i
	 * return false iff some assertion failed.
	 */
	
	public boolean arrayCopy(final int[.] a, final int[.] b) {
		final distribution D=a.distribution;
		final distribution E=b.distribution;
		// Spawn one activity per place 
		//boolean[D] err_a= new boolean[D];
		//boolean[E] err_b= new boolean[E];
		
		final distribution D_1= distribution.factory.unique(D.places()); 
		//boolean[D_1] err_1= new boolean[D_1];
		// number of times elems of a are accessed
		final int[.] accessed_a = new int[D];
		// number of times elems of b are accessed
		final int[.] accessed_b = new int[E];
		
		finish
		ateach (final point x:D_1)  {
			final place px= D_1.get(x);
			//err_1[x] |= !(px==here);
			if(px!=here) throw new Error();
			final distribution D_local= D.restriction(D.restriction(px));
			for (final point i : D_local ) { 
				// assignment to a[i] may need to be atomic
				// unless disambiguator has high level
				// knowledge about distributions
				async(E[i]) {
					//atomic{err_b[i] |= !(E[i]==here);}
					if (E.get(i)!=here) throw new Error();
					atomic{accessed_b[i] = accessed_b[i]+1;}
				}
				a[i] = future(E[i]){b[i]}.force();
				atomic{accessed_a[i] = accessed_a[i]+1;};
			}
			// check if distribution ops are working
			
			final distribution D_nonlocal= D.difference(D_local.region);
			//err_1[x] |= !(D_local.union(D_nonlocal).equals(D));
			if(!(D_local.union(D_nonlocal).equals(D))) throw new Error();
			for(final point k:D_local) {
				//err_1[x]|= !outOfRange(D_nonlocal,k);
				if (!outOfRange(D_nonlocal,k)) throw new Error();
				//err_1[x]|= !(D_local[k]==px);
				if (!(D_local[k]==px)) throw new Error();
			}
			for (final point k: D_nonlocal) {
				//err_1[x]|= !outOfRange(D_local,k);
				if(!outOfRange(D_local,k)) throw new Error();
				//err_1[x]|= !(D_nonlocal[k]!=px);
				if(!(D_nonlocal[k]!=px)) throw new Error();
			}
			
			
		}
		// ensure each a[i] was accessed exactly once
		finish ateach(final point i:D) {
			//err_a[i] |= !(accessed_a[i]==1);
			if(!(accessed_a[i]==1)) throw new Error();
		}
		// ensure each b[i] was accessed exactly once
		finish ateach(final point i:E) {
			//err_b[i] |= !(accessed_b[i]==1);
			if(!(accessed_b[i]==1)) throw new Error();
		}
		//return  !(err_1.reduce(boolean.or,false)
		//		| err_a.reduce(boolean.or,false)
		//		| err_b.reduce(boolean.or,false));
		return true;
	}
	
	final static int N=2;
	
	public boolean run() {
		final region r= region.factory.region(0, N-1);
		final region t1= region.factory.region(0, dist.N_DIST_TYPES-1);
		final region testDists= region.factory.region(t1,t1);
		for(final point distP: testDists) {
			final distribution D=dist.getDist(distP.get(0),r);
			final distribution E=dist.getDist(distP.get(1),r);
			if(!(D.region.equals(E.region)&&D.region.equals(r))) 
				return false;
			final int[.] a= new int[D];
			final int[.] b= new int[E];
			finish ateach(final point p:E) {final int i=p.get(0); b[p]= i*i+1;}
			if (!arrayCopy(a,b)) return false;
			if (!arrayEqual(a,b)) return false;
		}
		return true;
	}
	
	public static void main(String args[]) {
		boolean b= (new ArrayCopy2v()).run();
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
		default: throw new Error();
		// default: should throw exception
		}
		
	} 
}
