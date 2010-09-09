/**
 * Array operations and points must be type checked.
 *
 * The expected result is that compilation must fail.
 *
 * As of 4/05, this is a limitation of the current release
 *
 * @author kemal 4/2005
 */
public class ArrayTypeCheck {

	public boolean run() {
		int [.] a1=new int[[0:2,0:3]->here](point p[i]){ return i; };
		System.out.println("1");
		int [.] a2=(int[[-1:-2]->here])a1;
		System.out.println("2");
		int [.] a3=(int[dist.factory.unique()])a2;
		System.out.println("3");
		int i=1;
		int j=2;
		int k=0;
		point p = [i,j,k];
		point q = [i,j];
		point r = [i];
		if (p == q) return false;
		System.out.println("4");
		if (a1[q] + a3[q] != 2) return false;

		boolean gotException;
		System.out.println("5");
		try {
			return a1[i]==a1[i,j,k];
		} catch (RankMismatchException e) {
			gotException = true;
			System.out.println("Caught "+e);
		}
		if (!gotException) return false;

		return true;
	}

	public static void main(String[] args) {
		final boxedBoolean b=new boxedBoolean();
		try {
			finish async b.val=(new ArrayTypeCheck()).run();
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

