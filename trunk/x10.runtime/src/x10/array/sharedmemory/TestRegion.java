/*
 * Created on Oct 23, 2004
 */
package x10.array.sharedmemory;

import x10.array.Range;
import x10.array.Region;
import junit.framework.TestCase;

/**
 * @author Christian Grothoff, Christoph von Praun
 */
public class TestRegion extends TestCase {
    
	public void testRegion_subOrdinal() {
        Range[] ranges = new Range[] { new Range(7,12), new Range(42, 45) }; // 6x4         
        Region_c reg = new Region_c(ranges);
        
        Region sub2 = reg.subOrdinal(6, 7); // => (7,8) x (43,43)
        Range[] ranges2 = new Range[] { new Range(7,8), new Range(43, 43) }; // 2x1
        assertTrue(new Region_c(ranges2).equals(sub2));
        
        Region sub3 = reg.subOrdinal(6, 17); // => (7,12) x (43,44)
        Range[] ranges3 = new Range[] { new Range(7,12), new Range(43, 44) }; // 6x2
        assertTrue(new Region_c(ranges3).equals(sub3));        
    }
}
