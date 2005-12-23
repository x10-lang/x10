/*
 * Test the shorthand syntax for object array initializer
 *
 * @author igor, 12/2005
 */
public class ObjectArrayInitializerShorthand {

	public boolean run() {
		
		final dist d=[1:10,1:10]->here;
        final x10.lang.point o = x10.lang.point.factory.point(0);
		final x10.lang.point[.] ia = new x10.lang.point[d] (point [i,j]){return o;};
		for(point [i,j]:ia) chk(ia[i,j]==o);
		return true;
	}

    static void chk(boolean b) {if(!b) throw new Error();}
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new ObjectArrayInitializerShorthand()).run();
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

