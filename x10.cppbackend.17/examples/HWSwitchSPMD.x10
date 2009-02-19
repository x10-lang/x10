public class HWSwitchSPMD {
	private final static dist(:unique) UNIQUE = dist.UNIQUE;
	public static void main(String[] args) {
		switch (args.length) {
			case 2:
				System.out.println("Two arguments");
				break;
			case 1:
				System.out.println("One argument");
				finish ateach (point p : UNIQUE) {
				}
				break;
			default:
				System.out.println("Stop flooding, man!");
				break;
		}
		System.out.println((String)"Hello World");
	}
}

