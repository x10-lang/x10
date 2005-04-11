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

	public static void main(String args[]) {
		boolean b= (new Array1Long()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
  
}
