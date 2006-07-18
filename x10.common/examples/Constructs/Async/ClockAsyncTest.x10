import harness.x10Test;

/**
 * Code generation for clocked asyn uses "clocks" as the name of the clock list.
 *
 * @author Tong Wen 7/2006
 */
public class ClockAsyncTest extends x10Test {

	public boolean run() {
		finish async{
			final clock value [.] clocks=new clock [[0:5,0:2]] (point i)
			{return clock.factory.clock();};
			async clocked(clocks[1,1]){
				next;
			}
		}
		return true;
	}

	

	public static void main(String[] args) {
		new ClockAsyncTest().execute();
	}
}

