/*
 * Created on Oct 23, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package x10.array;

import junit.framework.TestCase;

/**
 * @author praun
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TestArray extends TestCase {
    
	public void testRegion_subOrdinal() {
        Range[] ranges = new Range[] { new Range_c(7,12), new Range_c(42, 45) }; // 6x4         
        Region_c reg = new Region_c(ranges);
        
        Region sub2 = reg.subOrdinal(6, 7); // => (7,8) x (43,43)
        Range[] ranges2 = new Range[] { new Range_c(7,8), new Range_c(43, 43) }; // 2x1
        assertTrue(new Region_c(ranges2).equals(sub2));
        
        Region sub3 = reg.subOrdinal(6, 17); // => (7,12) x (43,44)
        Range[] ranges3 = new Range[] { new Range_c(7,12), new Range_c(43, 44) }; // 6x2
        assertTrue(new Region_c(ranges3).equals(sub3));        
    }
}
