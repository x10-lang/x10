
/**
 *
 * Cannot register a child with a clock
 * I am not registered with.
 *
 * Must fail at compile time.
 *
 * @author kemal 4/2005
 */
public class Clockest11_MustFailCompile {

         	

	public boolean run() {
      		final clock c = clock.factory.clock();
      		final clock d = clock.factory.clock();
		async clocked(d) {
		    async clocked(c) {System.out.println("hello");} 
		}
		return true;
	}


	public static void main(String args[]) {
		boolean b= (new Clockest11_MustFailCompile()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}

}
