import x10.lang.*;
/**
 * Minimal package and import test
 * @author kemal
 * 1/2005
 */
public class ImportTest {

	public boolean run() {
		return future(here){_T2.m2(49)}.force();
	}
		
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new ImportTest()).run();
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
