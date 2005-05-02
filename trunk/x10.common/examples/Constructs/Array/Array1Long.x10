/*
 * Simple array test #1
 */
import x10.lang.*;
public class Array1Long {

	public boolean run() {
		
		region e= region.factory.region(1,10); //(low,high)
		region r = region.factory.region(new region[]{e, e}); 
		distribution d=distribution.factory.constant(r,here);
		long[.] ia = new long[d];
		
		for(point p: e)
			for (point q:e) {
				int i = p.get(0);
				int j = q.get(0);
				if(ia[i,j]!=0) return false;
				ia[i, j] = i+j;
			}
		
		for(point p: d) {
			int i = p.get(0);
			int j = p.get(1);
			point q1 = point.factory.point(i,j);
			if(ia[q1]!=i+j) return false;
			if(ia[q1]!=ia[p]) return false;
		}
		
		return true;
	}

	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new Array1Long()).run();
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
