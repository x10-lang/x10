/**
 * @author kemal 4/2005
 * Make sure we cannot assign a clock to an array element or
 * field
 */

class BoxedClock {
	public clock val;
	public BoxedClock(final clock x) {
		val=x;
	}
}
public class ClockTest8_MustFailCompile {

	public boolean run() {
		clock[] ca = new clock[] {clock.factory.clock(),
                  clock.factory.clock()};
		final clock c1=ca[1];
		final clock c2=c1;
		final clock c3=ca[0];
		BoxedClock bc=new BoxedClock(clock.factory.clock());
		bc.val.drop();
		ca[0].drop();
		
	    return true;
	}
	public static void main(String args[]) {
		boolean b= (new ClockTest8_MustFailCompile()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}

}
