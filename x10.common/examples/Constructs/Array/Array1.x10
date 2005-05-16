/*
 * Simple array test.
 *
 * Only uses the longhand forms such as ia.get(p) for ia[p]
 */
public class Array1 {

	public boolean run() {
		
		region e= region.factory.region(1,10);
		region r = region.factory.region(new region[]{e, e}); 
		dist d=dist.factory.constant(r,here);
		int[.] ia = new int[d];
		
		for(point p: e)
			for (point q:e) {
				int i = p.get(0);
				int j = q.get(0);
				chk(ia.get(i,j)==0);
				ia.set(i+j,i,j);
			}
		
		for(point p: d) {
			int i = p.get(0);
			int j = p.get(1);
			point q1 =
	                   point.factory.point(new int[]{i,j});
			chk(i ==q1.get(0));
			chk(j ==q1.get(1));
			chk(ia.get(i,j)== i+j);
			chk(ia.get(i,j)==ia.get(p));
			chk(ia.get(q1)==ia.get(p));
			ia.set(ia.get(p)-1,p);
			chk(ia.get(p)==i+j-1);
			chk(ia.get(q1)==ia.get(p));
				
		}
		
		return true;
	}

    void chk(boolean b) {if(!b) throw new Error(); }

	
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
}
