package ImportTestPackage1.SubPackage;

import ImportTestPackage2._T4;

/**
 * auxiliary class for ImportTest, also a test by itself.
 */
public class T3 {
	public static boolean m3(final int x) {
		return future(here){_T4.m4(x)}.force();
	}
	public boolean run() {
		return m3(49);
	}
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new T3()).run();
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
