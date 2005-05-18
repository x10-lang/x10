/**
 * Testing 3D arrays.
 */

public class Array2v {
	
	public boolean run() {
		
		region e= [0:9];
		region r = [e,e,e];
		dist d=r->here;
		chk(d.equals([0:9,0:9,0:9]->here));
		int[d] ia = new int[d];
		
		for(point [i,j,k]:d) {
			chk(ia[i,j,k]==0);
			ia[i,j,k] = 100*i+10*j+k;
		}
		
		for ( point [i,j,k]:d ) {
			chk(ia[i,j,k] == 100*i+10*j+k);
		}
		
		return true;
	}

    static void chk(boolean b) {if (!b) throw new Error();}
	
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
