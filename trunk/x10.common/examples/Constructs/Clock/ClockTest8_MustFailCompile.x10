/**
 * @author kemal 4/2005
 * Tests if we can assign a clock to an array element or
 * field.
 * This could lead to static memory disambiguation difficulties
 * For example:
 * <code>
   (a[b[k]]).drop(); 
   async clocked(a[b[i]]) {
     async cloked(a[b[j]]) S
   }
   // Compiler does not know if b[k]==b[i] or b[i]==b[j]
 * </code>
 * Definition needs to be cleared up here.
 */

class BoxedClock {
	public clock val;
	public BoxedClock(final clock x) {
		val=x;
	}
}
public class ClockTest8_MustFailCompile {

	public boolean run() {
            finish {
		BoxedClock bc=new BoxedClock(clock.factory.clock());
		clock[] ca = new clock[] {clock.factory.clock(), bc};
		final clock c1=ca[1];
		final clock c2=c1;
		final clock c3=ca[0];
		bc.val.drop();
		async clocked(ca[U.zero()]) {
			async clocked[c2] {System.out.println("hello");}
		}
            }
	    return true;
	}
	public static void main(String args[]) {
		boolean b= (new ClockTest8_MustFailCompile()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}

}

class U {
	public static int zero() {return 0;}
}
