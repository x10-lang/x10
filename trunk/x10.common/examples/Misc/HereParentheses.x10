/**
 *@author kemal, 5/2005
 *
 * (here).id compiled properly but here.id did not.
 * (as of 5/28/2005)
 *
 */
public class HereParentheses {

    public boolean run() {
	System.out.println("(here).id="+(here).id+" (here).next()="+(here).next()+ " (here).prev()="+(here).prev());
	System.out.println("here.id="+here.id+ " here.next()=" + here.next()+" here.prev()"+here.prev());
	return true;
    }
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new HereParentheses()).run();
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
