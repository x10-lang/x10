import harness.x10Test;

public class FieldDepType extends x10Test {
	double[:rank==1 && rect &&zeroBased] f = new double[[0:10]] (point [i]) { return 10-i;};
	
	void m(double[:rank==1&&rect&&zeroBased] a) {
		
	}
	public boolean run() {
		m(f);
		System.out.println("f[0] = " + f[0]);
		return true;
	}
	public static void main(String[] a) {
		new FieldDepType().execute();
	}
}