import harness.x10Test;

/**
 * Purpose: Check primitive dependent type assignment to primitive variable works.
 * @author vcave
 **/
public class ArrayDynamicCast extends x10Test {

	public boolean run() {
		region e = region.factory.region(0, 10);
		// region(:rank==1&&zeroBased) e = region.factory.region(:rank==1&&zeroBased)(0, 10);
		// region(:rank==2) e2 = (region(:rank==2&&zeroBased)) e
		// region r = region.factory.region(new region[] {e, e});
		// dist d = dist.factory.constant(r, here);
		dist(:rank==2&&rect&&zeroBased) d1 = [0:10,0:10]->here;
		// x10.lang.Object obj = d1;
		// dist(:rank==2&&rect) d2 = (dist(:rank==2&&rect)) obj;

		
		int[.] x10array = new int[d1];

		x10.lang.Object obj = x10array;
		
		int[:rank==2&&rect] dynCast =  (int[:rank==2&&rect]) obj;
		return true;
	}

	public static void main(String[] args) {
		new ArrayDynamicCast().execute();
	}

}
 