
/**
 *
 * Cannot register a child with a clock
 * I am not registered with.
 *
 * Must fail at compile time.
 *
 * @author kemal 4/2005
 */
public class ClockTest11_MustFailCompile {

	public boolean run() {
            finish {
      		final clock c = clock.factory.clock();
      		final clock d = clock.factory.clock();
		async clocked(d) {
		    async clocked(c) {System.out.println("hello");} 
		}
             }
	     return true;
	}


	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new ClockTest11_MustFailCompile()).run();
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
