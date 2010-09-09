import harness.x10Test;

/**
 * Purpose: Check primitive dependent type assignment to primitive variable works.
 * @author vcave
 **/
public class ArrayDynamicCastFromObject extends x10Test {

	public boolean run() {
		try {
			// array creation
			region e = region.factory.region(0, 10);
			dist(:rank==2&&rect&&zeroBased) d1 = [0:10,0:10]->here;
			int[.] x10array = new int[d1];
			// upcast
			x10.lang.Object obj = x10array;
			//invalid downcast
			int[:rank==3&&rect] dynCast =  (int[:rect&&rank==3]) obj;		
		} catch (ClassCastException e) {
			return true;
		}

		return false;
	}

	public static void main(String[] args) {
		new ArrayDynamicCastFromObject().execute();
	}

}
 