/*
 * Created on Oct 23, 2004
 */
package x10.array.sharedmemory;

import x10.array.Range;
import x10.array.ContiguousRange;
import x10.lang.region;
import x10.lang.point;
import junit.framework.TestCase;

/**
 * @author Christian Grothoff, Christoph von Praun
 * @author vj
 */
public class TestRegion extends TestCase {
    
    public void testRegion_sub() {
        Range[] ranges = new Range[] { new ContiguousRange(10,109), new ContiguousRange(100, 1099) }; // 6x4         
        Region_c reg = new Region_c(ranges);
        
        region sub2 = reg.sub(5, 2); // => (100, 1000) x (20,1000)
        Range[] ranges2 = new Range[] { new ContiguousRange(50,69), new ContiguousRange(100, 1099) }; // 2x1
        assertTrue(new Region_c(ranges2).equals(sub2));
    }
    
    public void testRegion_ordinal() {
        Range[] ranges = new Range[] { new ContiguousRange(10,109), new ContiguousRange(100, 1099) }; // 6x4         
        Region_c reg = new Region_c(ranges);
        
        int ord = (int) reg.ordinal(point.factory.point(reg, new int[] {10, 100}));
        System.out.println("Result is " + ord + "; should be " + 0);
        assertTrue(ord == 0);
        
        ord = (int) reg.ordinal(point.factory.point(reg, new int[] {11, 100}));
        System.out.println("Result is " + ord + "; should be " + 1);
        assertTrue(ord == 1);
        
        ord = (int) reg.ordinal(point.factory.point(reg, new int[] {11, 102}));
        System.out.println("Result is " + ord + "; should be " + 201);
        assertTrue(ord == 201);
    }
}
