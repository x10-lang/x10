/*
 * Simple array test.
 *
 * Same as Array1 but shorthand forms (such as ia[p]) are used.
 */
public class Array1b {

	public boolean run() {
		
		final region e= [1:10];
		final region r = [e,e];
		chk(r.equals([1:10,1:10]));
		//final dist d=r->here;
		final dist d=[1:10,1:10]->here;
		chk(d.equals([1:10,1:10]->here));
		chk(d.equals([e,e]->here));
		chk(d.equals(r->here));
		final int[.] ia = new int[d];
		
		for(point p: e)
			for(point q:e) {
				int i = p[0];
				int j = q[0];
				chk(ia[i,j]==0);
				ia[i,j]=i+j;
			}
		
		for(point p: ia) {
			int i = p[0];
			int j = p[1];
			point q1=[i,j];
			chk(i==q1[0]);
			chk(j==q1[1]);
			chk(ia[i,j]==i+j);
			chk(ia[i,j]==ia[p]);
			chk(ia[q1]==ia[p]);
			ia[p]=ia[p]-1;
			chk(ia[p]==i+j-1);
			chk(ia[q1]==ia[p]);
		}
		
		return true;
	}

    static void chk(boolean b) {if(!b) throw new Error();}

	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new Array1b()).run();
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
