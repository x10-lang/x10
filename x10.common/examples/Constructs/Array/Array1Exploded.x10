/**
* Simple array test #1
*/
import x10.lang.*;
public class Array1Exploded {
	public int select(point p[i,j], point [k,l]) {
		return i+k;
	}
	public boolean run() {
		dist d =  [1:10, 1:10] -> here;
		int[.] ia = new int[d];
		
		for(point p[i,j]: [1:10,1:10]) {
				if(ia[p]!=0) return false;
				ia[p] = i+j;
			}
		
		for(point p[i,j]: d) {
			point q1 = [i,j];
			if (i != q1[0]) return false;
			if ( j != q1[1]) return false;
			if(ia[i,j]!= i+j) return false;
			if(ia[i,j]!=ia[p]) return false;
			if(ia[q1]!=ia[p]) return false;
		}
		if (! (4 == select([1,2],[3,4]))) return false;
		return true;
	}
	
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new Array1Exploded()).run();
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
