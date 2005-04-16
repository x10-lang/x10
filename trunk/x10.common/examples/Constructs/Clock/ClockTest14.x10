/**
 * Check if illegal uses of clock are raising exception.
 * Some language clarifications needed.
 * @author kemal 4/2005
 */
public class ClockTest14 {

	public boolean run() {
		final clock c = clock.factory.clock();
		boolean gotException;
                next;
		c.resume();
  		c.drop();		
		chk(c.dropped());
		next; // empty clock set should be acceptable?
		gotException=false;
		try {
		  c.resume();
		} catch (ClockUseException e) {
		  gotException=true;
		}
		chk(gotException);
		gotException=false;
		try {
		  async clocked(c) {}
		} catch (ClockUseException e) {
		  gotException=true;
		}
		chk(gotException);
         	return true;
	}
	static void chk(boolean b) {
		if(!b) throw new Error();
	}
	public static void main(String args[]) {
		boolean b= (new ClockTest14()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}

}
