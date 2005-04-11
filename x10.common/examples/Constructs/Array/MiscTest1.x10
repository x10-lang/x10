/**
 * Tests miscellaneous features together: async, future, atomic,
 * distributed array, distribution ops, reduction, span, lift.
 * @author kemal 12/2004
 *
 * disabled boolean arrays temporarily
 */
public class MiscTest1   {
	
	static final int N=50;
	
	static final int NP=x10.lang.place.MAX_PLACES;
	
	public boolean run() {
		
		final region R=region.factory.region(0,NP-1); 
		
		// verify that a blocked distribution for
		// (0..MAX_PLACES-1) is a unique distribution
		// verify that a cyclic distribution for
		// (0..MAX_PLACES-1) is again the same
		final distribution D=distribution.factory.block(R);
		final distribution D2=distribution.factory.unique(x10.lang.place.places);
		final distribution D3=distribution.factory.cyclic(R);
		
		if(!D.equals(D2)) return false;
		if(!D.equals(D3)) return false;
		
		// create zero int array x
		final int[D] x=new int[D];
		
		// set x[i]= N*i with N atomic updates
		finish
		for (final point pi:R) 
			for (final point pj:region.factory.region(0,N-1)) 
				async(D[pi]) atomic x[pi] += pi[0]; 
		

		// ensure sum= N*SUM(int i=0..NP-1)(i);
		// == N*((NP*(NP-1))/2)
		final int sum = x.sum();
		if ( sum !=(N*NP*(NP-1)/2)) throw new Error();
		
		
		// also verify each array elem x[i]==N*i;
		// test D|R restricton and also D-D1
		
		final region r_inner= region.factory.region(1,NP-2);
		
		final distribution D_inner = D.restriction(r_inner);
		final distribution D_boundary= D.difference(r_inner);
		//boolean[D_inner] p_inner=new boolean[D_inner]; 
		finish
		ateach(final point pi:D_inner) {
			final int i=pi.get(0);
			//p_inner[i]= (x[i]==N*i);
			//if (D[i]!=D_inner[i]|| D[i]!=here) 
			//      p_inner[i]=false;
			if(!(x[pi]==N*i)) throw new Error();
			// the following line causes a compiler error
			//if(!(x[i]==N*i)) throw new Error();
			if (D[pi] !=D_inner[pi] || D[pi] !=here) throw new Error();
		}
		
		//if(!p_inner.reduce(boolean.and,true)) return false;

		//boolean[D_boundary] p_boundary=new boolean[D_boundary]; 
		finish
		ateach(final point pi:D_boundary) {
			final int i=pi.get(0);
			//p_boundary[i]= (x[i]==N*i);
			if(!(x[pi]==N*i)) throw new Error();
			
			//if (D[i]!=D_boundary[i]|| D[i]!=here) 
			//     p_boundary[i]=false;
			if (D[pi]!=D_boundary[pi] || D[pi]!=here) throw new Error(); 
		}
		//if(!p_boundary.reduce(boolean.and,true)) return false;
		
		// test scan
		final int[D] y= x.scan(intArray.add,0);
		//boolean[D] q= new boolean[D];
		// y[i]==x[i]+y[i-1], for i>0
		finish
		ateach(final point pi:D) {
			final int i=pi[0];
			final point pi1=point.factory.point(i-1);
			//q[i]= 
			// (y[i]==x[i]+(i==0?0:future(D[i-1]){y[i-1]}.force()));
			if(!(y[pi]==x[pi]+(i==0?0:future(D[pi1]){y[pi1]}.force()))) 
				throw new Error();
		}
		//if(!(q.reduce(boolean.and,true))) return false;
		// y[NP-1] == SUM(x[0..NP-1])
		final point pNP_1=point.factory.point(NP-1);
		if (sum!= future(D[pNP_1]){y[pNP_1]}.force()) 
			throw new Error();
		
		// test lift
		final int[D] z = x.lift(intArray.add,y);
		finish
		ateach(final point pi:D) {
			//q[i]=(z[i]==x[i]+y[i]);
			if(!(z[pi]==x[pi]+y[pi])) throw new Error();
		}
		//if(!q.reduce(boolean.and,true)) return false;
		
		// now write back zeros to x
		int[D] xf=x.overlay(new int[D]);
		
		// ensure x is all zeros
		if(xf.sum()!=0) throw new Error();
		
		return true;
	}
	public static void main(String args[]) {
		boolean b= (new MiscTest1()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
	
}
