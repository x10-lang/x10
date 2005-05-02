import x10.lang.*;

/**
 * Automatic boxing and unboxing of a final value class
 * during up-cast and down-cast
 */
public class Boxing0 {
	public boolean run() {
		x10.lang.Object o=X.five();
		int i= (int) o + 1;
		return (i==6);
	}
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new Boxing0()).run();
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

class X {
	public static int five() { return 5;}
}
