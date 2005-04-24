/**
 * @author kemal 4/2005
 * Only final free variables can be passed to async body.
 */
public class AsyncTest4_MustFailCompile  {

	const int  N=20;

	public boolean run() {
		int s=0;
		for(int i=0;i<N;i++) {
			//==> compiler error expected here
			finish async(here) System.out.println("s="+s+" i="+i);
			s+=i;
		}
		// no compiler error here
		s=0;
		for(int i=0;i<N;i++) {
			{
				final int i1=i; 
				final int s1=s;
				finish async(here) System.out.println("s1="+s1+" i1="+i1);
			}
			s+=i;
		}
		final int y;
		//==> Compiler error expected here
		finish async(here) {async(here) y=3;}
		System.out.println("y="+y);
	  	return true;
	}

	public static void main(String args[]) {
		boolean b= (new AsyncTest4_MustFailCompile()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
}
