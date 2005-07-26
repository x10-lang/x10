/**
 * Test for for loop with x10 for(point p:D) syntax
 *
 * @author: kemal, 12/2004
 */
public class ForLoop {
        const int N=100;
        int nActivities=0;

	public boolean run() {
	
                region r=[0:N-1];
		place P0=here;
		dist d=r->P0;

		if(!d.region.equals(r)) return false;
		if(d.region.low()!=0) return false;
		if(d.region.high()!=N-1) return false;

		
		//Ensure iterator works in lexicographic order
		int n = 0;
		int prev = d.region.low()-1;
                for(point p:d) {
			n += p[0];
			if (prev+1!=p[0]) return false;
			prev=p[0];
			if (P0!=d[p]) return false;
		}
		if (n != N*(N-1)/2) return false;

		// now iterate over a region
		n=0;
		prev = r.low()-1;
                for(point p:r) {
			n += p[0];
			if (prev+1!=p[0]) return false;
			prev=p[0];
		}
		if (n != N*(N-1)/2) return false;
		return true;
	}

	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new ForLoop()).run();
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
