import x10.lang.*;

/**
 * Testing atomic vs. non atomic updates.
 * The compiler should actually flag 
 * the non atomic update to the shared variable 
 * cnt_broken as an error.
 */
public class Atomic1 extends x10.lang.Object {
	int cnt;
	int cnt_broken;
        static final int N=100;
	int threadRun() {
		for (int i=0;i<N;++i) {
			atomic {
				++cnt;
			}
			++cnt_broken;
		}
		return 0;
	}
	public boolean run() {
		future<int> a=future(this){threadRun()};
		future<int> b=future(this){threadRun()};
		future<int> c=future(this){threadRun()};
		future<int> d=future(this){threadRun()};
		future<int> e=future(this){threadRun()};
		future<int> f=future(this){threadRun()};
		future<int> g=future(this){threadRun()};
		future<int> h=future{threadRun()}; // compiler should infer location as this.location
		int i=a.force();
		int j=b.force();
		int k=c.force();
		int l=d.force();
		int m=e.force();
		int n=f.force();
		int o=g.force();
		int p=h.force();
		int temp;
		atomic{temp=cnt;}
		System.out.println("Atomic1: "+ temp + " =?= " + cnt_broken);
		return temp == 8*N;
	}
	public static void main(String args[]) {
		boolean b= (new Atomic1()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
}
