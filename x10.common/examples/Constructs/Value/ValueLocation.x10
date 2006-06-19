import harness.x10Test;

/**
 * Value class location test:
 *
 * Verifying that the location of a value class instance (complex) is
 * always 'here', regardless of where the query is made.
 *
 * For example, for the same value class instance x,
 *
 * at place P0, x.getLocation() == P0
 *
 * at place P1, x.getLocation() == P1
 *
 * but P0 != P1
 *
 * @author kemal, 5/2005
 */
public class ValueLocation extends x10Test {

	public boolean run() {
		final dist P = dist.factory.unique();
		chk(P.region.equals([0:place.MAX_PLACES-1]));
		chk(P[0] == here);
		final complex one = new complex(1,1);
		ateach (point [i]: P) {
			//System.out.println("#1 "+i+" "+one.getLocation()+" "+P[i]+" "+here);
			chk(one.getLocation() == here);
			chk(here == future(one) { here }.force());
		}
		foreach (point[i]: P) {
			//System.out.println("#2 "+i+" "+future(P[i]) { one.getLocation() }.force());
			foreach (point[j]: P) {
				//System.out.println("#3 "+i+" "+j+" "+P[i]+" "+P[j]);
				chk(implies(P[i] == P[j], i == j));
			}
			chk(P[i] == future(P[i]) { one.getLocation() }.force());
		}

		return true;
	}

	static boolean implies(boolean x, boolean y) { return (!x) | y; }

	public static void main(String[] args) {
		new ValueLocation().execute();
	}

	static final value complex {
		int re;
		int im;
		complex(int re, int im) {
			this.re = re;
			this.im = im;
		}
	}
}

