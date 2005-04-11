import x10.lang.*;

public class AtomicReturn  {
	int a = 0;
	static final int N=100;
	
	int update1() {
		atomic {
			a++;
			return a;
		}
	}
	int update3() {
		atomic {
			return a++;
		}
	}
	public boolean run() {
		update1();
		update3();
		System.out.println(a);
		return a == 2;
	}
	public static void main(String args[]) {
		boolean b= (new AtomicReturn()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
}
