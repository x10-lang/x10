public class Async0 {
	public void threadRun() {
		System.out.println("Hello world!");
	}
	public void mainRun() {
		async { threadRun(); }
	}
	public static void main(String[] v) {
		new Async0().mainRun();
	}
}

