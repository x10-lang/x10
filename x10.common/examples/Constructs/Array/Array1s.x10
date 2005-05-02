/**
* Simple array test #1
*/
import x10.lang.*;
public class Array1s {
	
	public boolean run() {
		distribution d =  [1:10, 1:10] -> here;
		int[.] ia = new int[d];
		
		for(point p: 1:10)
			for (point q: 1:10) {
				int i = p[0];
				int j = q[0];
				if(ia[i,j]!=0) return false;
				ia[i, j] = i+j;
			}
		
		for(point p: d) {
			int i = p[0];
			int j = p[1];
			point q1 = Pt(i,j);
			if (i != q1[0]) return false;
			if ( j != q1[1]) return false;
			if(ia[i,j]!= i+j) return false;
			if(ia[i,j]!=ia[p]) return false;
			if(ia[q1]!=ia[p]) return false;
			
		}
		
		return true;
	}
	
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new Array1s()).run();
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
		return point.factory.point(i);
	}	
	/**
	 * Convert a pair of integers to a 2D point
	 */
	private static point Pt(int i, int j) {
		return point.factory.point(i,j);
	}	
	
}
