public class Atomic1 {
	int cnt;
	int threadRun() {
		for (int i=0;i<100000;++i) {
			atomic {
				++cnt;
			}
		}
		return 0;
	}
	void mainRun() {
		future int a=future{threadRun()};
		future int b=future{threadRun()};
		future int c=future{threadRun()};
		future int d=future{threadRun()};
		future int e=future{threadRun()};
		future int f=future{threadRun()};
		future int g=future{threadRun()};
		future int h=future{threadRun()};
		int i=force a;
		int j=force b;
		int k=force c;
		int l=force d;
		int m=force e;
		int n=force f;
		int o=force g;
		int p=force h;
		System.out.println(cnt);
	}
	public static void main(String[] v) {
		new Atomic1().mainRun();
	}
}

