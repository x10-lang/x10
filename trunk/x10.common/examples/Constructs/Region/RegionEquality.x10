import harness.x10Test;

public class RegionEquality extends x10Test {

	public boolean run() {
		final int size=10;
		final region R=[0:size-1,0:size-1];
		return R==[0:size-1,0:size-1];
	}
	public static void main(String[] args) {
		 new RegionEquality().execute();
	}
}
