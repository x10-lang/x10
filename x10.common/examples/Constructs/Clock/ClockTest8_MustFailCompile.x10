/**
 * @author kemal 4/2005
 * Tests if we can assign a clock to an array element or
 * field.
 * Tests if clocks can be "aliased" per x10 manual terminology.
 * E.g.: clock c1=clock.factory.clock(); clock c2=c1; 
 * //clocks c1 and c2 are "aliased"
 * ca[0]=c1; ca[1]=ca[0]; 
 * // ca[0] and ca[1] are "aliased"
 * The language definition needs to be cleared up, to define the behavior of this test.
 *
 * This test currently fails at run time since it tries to transmit a dropped clock.
 * 
 */

class BoxedClock {
	public clock val;
	public BoxedClock(final clock x) {
		val=x;
	}
}
public class ClockTest8_MustFailCompile {

	public boolean run() {
            finish async {
		BoxedClock bc=new BoxedClock(clock.factory.clock());
		clock[] ca = new clock[] {clock.factory.clock(), bc.val};
		final clock c1=ca[1];
		final clock c2=c1; //aliased clocks c2 and c1
		final clock c3=ca[0];
		bc.val.drop();
                //TODO: the following line (arrays of clocks) does not parse

		//async clocked(ca[U.zero()]) 
                final clock c4=ca[U.zero()];
		async clocked(c4) 
                {
			async clocked(c2) {System.out.println("hello");}
		}
            }
	    return true;
	}
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new ClockTest8_MustFailCompile()).run();
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

class U {
	public static int zero() {return 0;}
}
