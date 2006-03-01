/**
 *
 * Ensures byte arrays are implemented
 */
public class Array3Byte {

	public boolean run() {
		
		region e= [1:10];
		region r = [e,e];
		dist d=r->here;
		chk(d.equals([1:10,1:10]->here));
		byte[.] ia = new byte[d];
		ia[1,1] = (byte) 42;
		return (42 == ia[1,1]);
	
	}

    static void chk(boolean b) {if (!b) throw new Error();}
	
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new Array3Byte()).run();
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
