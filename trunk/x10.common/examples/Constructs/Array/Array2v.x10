import harness.x10Test;

/**
 * Testing 3D arrays.
 */
public class Array2v extends x10Test {

	public boolean run() {
		region e = [0:9];
		region r = [e,e,e];
		dist d = r->here;
		chk(d.equals([0:9,0:9,0:9]->here));
		int[d] ia = new int[d];

		for (point [i,j,k]: d) {
			chk(ia[i,j,k] == 0);
			ia[i,j,k] = 100*i + 10*j + k;
		}

		for (point [i,j,k]: d) {
			chk(ia[i,j,k] == 100*i + 10*j + k);
		}

		return true;
	}

	public static void main(String[] args) {
		new Array2v().execute();
	}
}

