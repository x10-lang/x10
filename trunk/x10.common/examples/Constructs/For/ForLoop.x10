import x10.lang.*;
/**
 * Test for for loop with x10 for(point p:D) syntax
 *
 * @author: kemal, 12/2004
 */
public class ForLoop {
        static final int N=100;
        int nActivities=0;

	public boolean run() {
	
                region r=region.factory.region(0,N-1);
		place P0=here;
		distribution d=distribution.factory.constant(r,P0);

		if(!d.region.equals(r)) return false;
		if(d.region.low()!=0) return false;
		if(d.region.high()!=N-1) return false;

		
		//Ensure iterator works in lexicographic order
		int n = 0;
		int prev = d.region.low()-1;
                for(point p:d) {
			n += p.get(0);
			if (prev+1!=p.get(0)) return false;
			prev=p.get(0);
			if (P0!=d.get(p)) return false;
		}
		if (n != N*(N-1)/2) return false;

		// now iterate over a region
		n=0;
		prev = r.low()-1;
                for(point p:r) {
			n += p.get(0);
			if (prev+1!=p.get(0)) return false;
			prev=p.get(0);
		}
		if (n != N*(N-1)/2) return false;
		return true;
	}

	public static void main(String args[]) {
		boolean b= (new ForLoop()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
}
