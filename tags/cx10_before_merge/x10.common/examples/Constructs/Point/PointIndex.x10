/**
 * Accessing p[3] in a 2D point should cause an array
 * index out of  bounds exception.
 *
 * @author kemal, 6/2005
 */
public class PointIndex {

	public boolean run() {
		int sum=0;
		boolean gotException;
		point p=[1,2];

		gotException=false;
		try {
			sum+=p[-1];
		} catch (ArrayIndexOutOfBoundsException e) {
			gotException=true;
		}
		System.out.println("1: sum="+sum+" gotException="+gotException);
		if (!(sum==0 && gotException)) return false;

		gotException=false;
		try {
			sum+=p[3];
		} catch (ArrayIndexOutOfBoundsException e) {
			gotException=true;
		}
		System.out.println("2: sum="+sum+" gotException="+gotException);
		return sum==0 && gotException;

	}
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new PointIndex()).run();
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
