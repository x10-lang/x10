/**
 * Tests point (p[i,j]) notation. 
 */
public class Array1Exploded {

	public int select(point p[i,j], point [k,l]) {
		return i+k;
	}

	public boolean run() {
		dist d =  [1:10, 1:10] -> here;
		int[.] ia = new int[d];
		
		for(point p[i,j]: [1:10,1:10]) {
				chk(ia[p]==0);
				ia[p] = i+j;
		}
		
		for(point p[i,j]: d) {
			point q1 = [i,j];
			chk(i == q1[0]);
			chk(j == q1[1]);
			chk(ia[i,j]== i+j);
			chk(ia[i,j]==ia[p]);
			chk(ia[q1]==ia[p]);
		}

		chk(4 == select([1,2],[3,4]));

		return true;
	}

    static void chk(boolean b) {if (!b) throw new Error();}
	
	
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
