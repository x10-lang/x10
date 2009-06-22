
package x10.array;

import java.util.Iterator;
import x10.lang.Runtime;
import x10.lang.point;
import x10.runtime.Activity;
import x10.runtime.Configuration;
import junit.framework.TestCase;

/**
 * @author Christoph von Praun
 */
public class TestTriangularRegion extends TestCase {

    static {
        Runtime.init();
    }
    
    public void testRegion_iterator1() {
    	Runtime.runAsync( new Activity() { public void runX10Task() {
    		Range[] ranges = new Range[] { new ContiguousRange(0,3), new ContiguousRange(1, 4) }; // 6x4         
    		
    		TriangularRegion reg = new TriangularRegion(ranges, true);
    		
    		// check ordinal and iterator and contains method
    		int cnt = 0;
    		for (Iterator it = reg.iterator(); it.hasNext(); ) {
    			point p = (point) it.next();
    			System.out.println("cnt = " + cnt + " ord=" + reg.ordinal(p) + " point=" + p + " contains=" + reg.contains(p));
    			cnt++;
    		}
    		assertTrue(cnt == 10);
    		
    		// check contains method
    		point tmp_p = point.factory.point(new int[] {2,4});
    		boolean tmp_b = reg.contains(tmp_p);
    		System.out.println("reg=" + reg + " should not contain" + tmp_p + " (" + tmp_b + ")");
    		assertTrue(tmp_b);
    	}});
    }

}
