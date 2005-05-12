import x10.lang.*;
/**
 * Test #2 for for loop with for(point p:D) syntax
 *
 * @author: kemal, 1/2005
 */
public class ForLoop2 {
	static final int N=3;
	
	public boolean run() {
		
		region r=region.factory.region(0,N-1);
		region r3=region.factory.region(new region[]{r,r,r});
		place P0=here;
		dist d3=dist.factory.constant(r3,P0);
		
		if(!d3.region.equals(r3)) return false;
		
		
		//Ensure iterator works in lexicographic order
		int n = 0;
		nullable point prev = null;
		for(point p:d3) {
			if (!successor(prev,p)) return false;
			prev=p;
			if (P0!=d3.get(p)) return false;
			n++;
		}
		if (n != N*N*N) return false;
		return true;
		
	}
	/**
	 * return true iff p is the lexicographic successor of prev
	 * For example for a [0..2,0..2,0..2] region
	 * i.e. we expect the order (0,0,0), (0,0,1),(0,0,2)
	 *  (0,1,0) ... (2,2,2) (row-major order)
	 */
	static boolean successor(nullable point prev,point p) {
		if (prev==null) return true;
		int i=prev.get(0);
		int j=prev.get(1);
		int k=prev.get(2);
		//System.out.println("Prev:"+i+" "+j+" "+k);
		//System.out.println("Actual:"+ p.get(0)+" "+p.get(1)+" "+p.get(2));
		k++;
		if (k==N) {
			k=0;
			j++;
			if (j==N) {
				j=0;
				i++;
			}
		}
		//System.out.println("Expected:"+i+" "+j+" "+k);
		if (i!=p.get(0)) return false;
		if (j!=p.get(1)) return false;
		if (k!=p.get(2)) return false;
		return true;
	}
	
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new ForLoop2()).run();
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
