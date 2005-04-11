import java.util.Iterator;
import x10.lang.*;
/**
 * Minimal test for regions.
 */
public class RegionTestIterator {

	public boolean run() {
		region r = region.factory.region(0,100); // (low,high)
		region[] r2 = new region[] {r,r};
		// reg[0]=r;
		// reg[1]=r;
		region reg = region.factory.region(r2);
		
		int sum = 0;
		for (Iterator it = reg.iterator(); it.hasNext();) {
			point p = (point) it.next();
			sum  += p.get(1) - p.get(2);
		}
		// for(point [i,j]:reg) sum += i - j;
		return sum == 0;
	}
	public static void main(String args[]) {
		boolean b= (new RegionTestIterator()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}

}
