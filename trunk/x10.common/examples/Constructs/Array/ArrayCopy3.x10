import x10.lang.*;

/**
 * Test for arrays, regions and distributions
 * Based on original arraycopy3
 * @author kemal 1/2005
 */

public class ArrayCopy3 {
	
	
	/**
	 * Returns true iff x is not in the domain of one dim
	 * distribution D
	 */
	static boolean outOfRange(final distribution D, final point x) {
		boolean gotException=false;
		// assert(D.dimension==1) 
		try{
			async(D[x]){}; // dummy op just to use D.get(x)
		} catch (Throwable e) {
			gotException=true;
		}
		return gotException;
	}
	
	/**
	 * Returns true iff a[i]==b[i] for all i 
	 */
	
	public boolean arrayEqual(final int[.] a, final int[.] b) {
		final distribution D=a.distribution;
		final distribution E=b.distribution;
		// Spawn an activity for each index to 
		// fetch the b[i] value 
		// Then compare it to the a[i] value
		// boolean[.] err=new boolean[D];
		finish
		ateach (point i : D) { 
			int t= future(E[i]){b[i]}.force();
			//err[i]|= (a[i]!=t); 
			if(a[i]!=t) throw new Error("0");
		}
		//return !err.reduce(boolean.or,false);
		return true;
	}
	
	/**
	 * Set a[i]=b[i] for all i
	 * return false iff some assertion failed
	 */
	
	public boolean arrayCopy(final int[.] a, final int[.] b) {
		final distribution D=a.distribution;
		final distribution E=b.distribution;
		// Allows message aggregation
		

		//boolean[D] err_a= new boolean[D];
		//boolean[E] err_b= new boolean[E];
		final distribution D_1=distribution.factory.unique(D.places()); 
		//boolean[D_1] err_1 = new boolean[D_1]; 
		// number of times elems of a are accessed
		final int[.] accessed_a = new int[D];
		// number of times elems of b are accessed
		final int[.] accessed_b = new int[E];
		
		finish
		ateach (point x:D_1) {
			final place px=D_1.get(x);
			
			//err_1[x] |= !(here==px); // check
			if(!(here==px)) throw new Error("1");
			final region LocalD = D.restriction(px).region;

			for ( place py : E.restriction(E.restriction(LocalD).region).places() ) {
				final region RemoteE = E.restriction(py).region;
				final region Common = LocalD.intersection(RemoteE);
				final distribution D_common= D.restriction(D.restriction(Common).region);
				// the future's can be aggregated
				for(point i:D_common) {
					async(py){atomic{accessed_b[i]=accessed_b[i]+1;}}
					final int temp=
						future(py){b[i]}.force();
					// the following may need to be bracketed in
					// async and atomic, unless the disambiguator
					// knows about distributions
					a[i]=temp;
					atomic{accessed_a[i]=accessed_a[i]+1;}
				}
				// check if distribution ops are working
				final distribution D_notCommon= D.difference(D_common.region);
				//err_1[x] |= !(D_common.union(D_notCommon).equals(D));
				if(!(D_common.union(D_notCommon).equals(D))) throw new Error("2");
				final distribution E_common= E.restriction(Common);
				final distribution E_notCommon= E.difference(E_common.region);
				//err_1[x] |= !(E_common.union(E_notCommon).equals(E));
					
				if(!(E_common.union(E_notCommon).equals(E))) throw new Error("3");
				for(point k:D_common) {
					//err_1[x]|= !(D_common[k]==px);
					if(!(D_common[k]==px)) throw new Error("4");
					//err_1[x]|= !outOfRange(D_notCommon,k);
					if(!outOfRange(D_notCommon,k)) throw new Error("5");
					//err_1[x]|= !(E_common[k]==py);
					if(!(E_common[k]==py)) throw new Error("6");
					//err_1[x]|= !outOfRange(E_notCommon,k);
					if(!outOfRange(E_notCommon,k)) throw new Error("7");
					//err_1[x]|= !(D[k]==px && E[k]==py);
					if(!(D[k]==px && E.get(k)==py)) throw new Error("8");
				}
				for (point k: D_notCommon) { 
					//err_1[x]|= !outOfRange(D_common,k);
					if(!outOfRange(D_common,k)) throw new Error("9");
					//err_1[x]|= !!outOfRange(D_notCommon,k);
					if(!!outOfRange(D_notCommon,k)) throw new Error("10");
					//err_1[x]|= !outOfRange(E_common,k);
					if(!outOfRange(E_common,k)) throw new Error("11");
					//err_1[x]|= !!outOfRange(E_notCommon,k);
					if(!!outOfRange(E_notCommon,k)) throw new Error("12");
					//err_1[x]|= !!(D[k]==px && E[k]==py);
					if(!!(D.get(k)==px && E.get(k)==py)) throw new Error("13");
				}
				
			}
		}
		// ensure each a[i] was accessed exactly once
		finish ateach(point i:D) {
			//err_a[i] |= !(accessed_a[i]==1);
			if(!(accessed_a[i]==1)) {
			    throw new Error("14");
			}
		}
		// ensure each b[i] was accessed exactly once
		finish ateach(point i:E) {
			//err_b[i] |= !(accessed_b[i]==1);
			if(!(accessed_b[i]==1)) throw new Error("15");
		}
		//return  !(err_1.reduce(boolean.or,false)
		//		| err_a.reduce(boolean.or,false)
		//		| err_b.reduce(boolean.or,false));
		return true;
	}
	
	final static int N=10;
	
	public boolean run() {
	    boolean ret = false;
	    try {
		final region r= region.factory.region(0, N-1);
		final region t1= region.factory.region(0, dist.N_DIST_TYPES-1);
		final region testDists= region.factory.region(t1,t1);
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
		ret = true;
	    } catch (Throwable t) {
		t.printStackTrace();
	    }
	    return ret;
	}
	
	
	public static void main(String args[]) {
		boolean b= (new ArrayCopy3()).run();
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
	// the following are optional for February
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
		}
		
	} 
}
