/*
 * Created on Oct 23, 2004
 */
package x10.array.sharedmemory;

import x10.array.Range;
import x10.array.ContiguousRange;
import x10.array.Region;
import junit.framework.TestCase;

/**
 * @author Christian Grothoff, Christoph von Praun
 */
public class TestRegion extends TestCase {
    
    public void testRegion_subOrdinal() {
        Range[] ranges = new Range[] { new ContiguousRange(7, 12), new ContiguousRange(42, 45) }; // 6x4
        Region_c reg = new Region_c(ranges);

        Region sub2 = reg.subOrdinal(6, 7); // => (7,8) x (43,43)
        Range[] ranges2 = new Range[] { new ContiguousRange(7, 8), new ContiguousRange(43, 43) }; // 2x1
        assertTrue(new Region_c(ranges2).equals(sub2));

        Region sub3 = reg.subOrdinal(6, 17); // => (7,12) x (43,44)
        Range[] ranges3 = new Range[] { new ContiguousRange(7, 12), new ContiguousRange(43, 44) }; // 6x2
        assertTrue(new Region_c(ranges3).equals(sub3));
    }
    
    public void testRegion_sub() {
        Range[] ranges = new Range[] { new ContiguousRange(10,109), new ContiguousRange(100, 1099) }; // 6x4         
        Region_c reg = new Region_c(ranges);
        
        Region sub2 = reg.sub(5, 2); // => (100, 1000) x (20,1000)
        Range[] ranges2 = new Range[] { new ContiguousRange(50,69), new ContiguousRange(100, 1099) }; // 2x1
        assertTrue(new Region_c(ranges2).equals(sub2));
    }
}
