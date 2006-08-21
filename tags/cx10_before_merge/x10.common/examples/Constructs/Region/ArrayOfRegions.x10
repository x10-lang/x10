

/**
 * @author kemal 11/2005
 *
 * region[.] ra=...;
 * for(point[k]:ra[i]) {...} does not compile 
 * as of 11/2005.
 * (Parentheses missing in generated java code)
 *
 * Bug reported by Mehmet Fatih Su.
 */

public class ArrayOfRegions {
	
	public boolean run() {
		final int N=3;
		final region[.] ra = new region[[0:N-1]] (point[i]) { return [1:0]; };
		for(point[i]:ra)  {
			ra[i] = ra[i] || [10*i:10*i+9];
			ra[i] = ra[i] && [10*i+1:10*i+21];
		}
		for(point[i]:ra)
			System.out.println("ra["+i+"]="+ra[i]);

		for(point[i]:ra) 
			chk(ra[i].equals([10*i+1:10*i+9]));

                for (point [i]:ra) {
			int n=0; 
			for (point[k]:ra[i]) { 
				chk(k>=10*i+1 && k<= 10*i+9 && 
				  ra[i].contains([k]));
				++n;
			}
			chk(n==9);
		}

                return true;
		
	}
	static void chk(boolean b) {
		if(!b) throw new Error();
 	}
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new ArrayOfRegions()).run();
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
