/**
 * Simple array test #2
 */
import x10.lang.*;

public class Array2v {
	
	public boolean run() {
		
		region e= region.factory.region(0,9); //(low,high)
		region r = region.factory.region(new region[]{e, e, e}); 
		dist d=dist.factory.constant(r,here);
		int[d] ia = new int[d];
		
		for(point p:d) {
			int i = p.get(0);
			int j = p.get(1);
			int k = p.get(2);
			if (ia[i,j,k]!=0) return false;
			ia[i,j,k] = 100*i+10*j+k;
		}
		
		for ( point p:d ) {
			int i = p.get(0);
			int j = p.get(1);
			int k = p.get(2);
		
			if (ia[i,j,k] != 100*i+10*j+k)
				return false;
		}
		
		return true;
	}
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new Array2v()).run();
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
