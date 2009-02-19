public class HWArrays {
	private static final String[] data = { "Hello,", "world!" };
	private static final HWArrays[.] a = new HWArrays[[0:1]] (point p[i]) { return i==0 ? new HWArrays(0) : new HWArrays(1); };
	public int value;
	public final String[] z = { "Hi,", "mom!" };
	int v;
	{ v = 5; }
	public final String[] y = new String[] { this.z[0], this.z[1] };
	public HWArrays(int value) { this.value = value; }
	public static void main(String[] s) {
		HWArrays[:self.rank==1 && self.rect && self.zeroBased] b =
			(HWArrays[:self.rank==1 && self.rect && self.zeroBased])
			new HWArrays[[0:1]] (point p[i]) { return i==1 ? new HWArrays(1) : new HWArrays(0); };
		System.out.println(data[a[0].value]+" "+data[a[1].value]);
		final String[] x = { a[0].y[0], a[1].y[1] };
		final String[] w = new String[] { x[0], x[1] };
		System.out.println(w[0]+" "+w[1]);
		data[1] = "dad?";
		System.out.println(data[0]+" "+data[1]);
	}
}
