/*
 * Simple array test #1
 */
import x10.lang.*;
public class Array1 {

	public boolean run() {
		
		region e= region.factory.region(1,10); //(low,high)
		region r = region.factory.region(new region[]{e, e}); 
		dist d=dist.factory.constant(r,here);
		int[.] ia = new int[d];
		
		for(point p: e)
			for (point q:e) {
				int i = i0(p);
				int j = i0(q);
				if(ia[i,j]!=0) return false;
				ia[i, j] = i+j;
			}
		
		for(point p: d) {
			int i = p[0];
			int j = p[1];
			point q1 = Pt(i,j);
			if (i !=i0(q1)) return false;
			if ( j !=i1(q1)) return false;
			if(ia[i,j]!= i+j) return false;
			if(ia[i,j]!=ia[p]) return false;
			if(ia[q1]!=ia[p]) return false;
				
		}
		
		return true;
	}

	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new Array1()).run();
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


  // utility functions for points
  /**
   * Convert an integer to a 1D point
   */
  private static point Pt(int i) {
	return point.factory.point(new int[]{i});
  }	
  /**
   * Convert a pair of integers to a 2D point
   */
  private static point Pt(int i, int j) {
	return point.factory.point(new int[]{i,j});
  }	
  /**
   * Convert the first coordinate of a point to an integer
   */
  private static int i0(point p) {
	return p.get(0);
  }	
  /**
   * Convert the second coordinate of a point to an integer
   */
  private static int i1(point p) {
	return p.get(1);
  }	
}
