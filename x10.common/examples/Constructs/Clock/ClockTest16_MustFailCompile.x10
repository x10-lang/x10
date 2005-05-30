/**
 * @author kemal 4/2005
 *
 *The x10 compiler must conservatively check if an activity can 
 *potentially
 *pass a clock it is not registered with,
 *to a subactivity.
 *If so, it must report a compile time error.
 *
 *Language clarification needed on disambiguation
 *algorithm to use.
 *
 */

public class ClockTest16_MustFailCompile {

	public boolean run() {
           final X x=new X();
           finish async {
		final clock c0=clock.factory.clock(); 
		final clock c1=clock.factory.clock();
		final clock[] ca= new clock[] {c0, c1};

                // Question:
                // Can an activity ever pass a clock it is not
                // registered with to a 
                // subactivity of itself, in statement async(cx) S?

                // Compiler answer: NO, actual runtime answer: NO
                // no compiler error
	        async clocked(c1) {
                    final clock cx=ca[1];
		    async clocked(cx) {// no clock use error 
			next;
                    }
                    next;
                }

                // Compiler: MAYBE, actual: NO
                // must have a compiler error
	        async(c1) {
                    final clock cx=ca[x.zero()];
		    async clocked(cx) { //clock use error
                       next;
                    }
                    next;
                }

                // Compiler: MAYBE, actual: YES
                // must have a compiler error
	        async clocked(c1) {
                    final clock cx=ca[x.one()];
		    async clocked(cx) { // no clock use error 
                       next;
                    }
                    next;
                }

                // Compiler: YES, actual:YES
                // must have a compiler error
	        async clocked(c1) {
                    final clock cx=ca[0];
		    async clocked(cx) { // clock use error 
                       next;
                    }
                    next;
                }
                next;
           }
                

	   return true;

	}
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new ClockTest16_MustFailCompile()).run();
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

/** 
 * Dummy class to make static memory disambiguation difficult
 * for a typical compiler
 */
class X {
    public int[] z={1,0};
    int zero() { return z[z[z[1]]];} 
    int one() { return z[z[z[0]]];} 
    void modify() {z[0]+=1;}
}
