/*
 * Created on Oct 23, 2004
 */
package x10.array;

import java.util.Iterator;

import junit.framework.TestCase;
import x10.lang.Runtime;
import x10.lang.dist;
import x10.lang.point;
import x10.lang.region;
import x10.runtime.Activity;

/**
 * @author Christian Grothoff, Christoph von Praun
 * @author vj
 */
public class TestMultiDimRegion extends TestCase {
    
    static {
        Runtime.init();
    }
    
    public void testRegion_iterator1() {    	
    	Runtime.runAsync( new Activity() { public void runX10Task() {
    		Range[] ranges = new Range[] { new ContiguousRange(1,3), new ContiguousRange(2, 4) }; // 6x4         
    		MultiDimRegion reg = new MultiDimRegion(ranges, false);
    		
    		int cnt = 0;
    		for (Iterator it = reg.iterator(); it.hasNext(); ) {
    			point p = (point) it.next();
    			System.out.println(p);
    			cnt++;
    		}
    		assertTrue(cnt == 9);
    		}
    	});
    }
    
    public void testRegion_sub() {
    	Runtime.runAsync( new Activity() { public void runX10Task() {
        Range[] ranges = new Range[] { new ContiguousRange(10,109), new ContiguousRange(100, 1099) }; // 6x4         
        MultiDimRegion reg = new MultiDimRegion(ranges, false);
        
        // region sub2 = reg.sub(5, 2); // => (100, 1000) x (20,1000)
        region[] subs = reg.partition(5, 0);
        region sub2 = subs[2];
        Range[] ranges2 = new Range[] { new ContiguousRange(50,69), new ContiguousRange(100, 1099) }; // 2x1
        assertTrue(new MultiDimRegion(ranges2, false).equals(sub2));
    	}});
    }
    
    /* the X10 arrays should provide a row-major interface */
    public void testRegion_majorness() {
    	Runtime.runAsync( new Activity() { public void runX10Task() {
    		Runtime.Factory F = Runtime.factory;
    		dist.factory DF = F.getDistributionFactory();
    		ArrayFactory IF = Runtime.factory.getArrayFactory();
    		Range[] ranges = new Range[] { new ContiguousRange(0,2), new ContiguousRange(0,2) };          
    		MultiDimRegion r = new MultiDimRegion(ranges, true);
    		dist d = DF.constant(r, Runtime.here());
    		x10.lang.IntReferenceArray arr = IF.IntReferenceArray(d, 0);
    		int num = 1;
    		for (int row = 0; row < 3; ++ row) {
    			for (int col = 0; col < 3; ++ col) {
    				arr.set(num, row, col);
    				num++;
    			}
    		}
    		StringBuffer sb = new StringBuffer();
    		for (int i = 0; i < 9; ++ i) {
    			sb.append(arr.get(r.coord(i)) + " ");
    		}
    		System.out.println("[0,0] [0,1] [0,2], ... == " + sb.toString());
    		
    		int[][] a1 = (int[][]) ((IntArray) arr).toJava();
    		IntArray.printArray("a1 = ", a1);
    		
    		assertTrue("1 2 3 4 5 6 7 8 9 ".equals(sb.toString()));
    	}});
    }
    
    public void testRegion_ordinal() {
    	Runtime.runAsync( new Activity() {
    		public void runX10Task() {
    			Range[] ranges = new Range[] { new ContiguousRange(10,109), new ContiguousRange(100, 1099) }; // 6x4         
    	        MultiDimRegion reg = new MultiDimRegion(ranges, false);
    	        
    	        int ord = (int) reg.ordinal(point.factory.point(new int[] {10, 100}));
    	        System.out.println("Result is " + ord + "; should be " + 0);
    	        assertTrue(ord == 0);
    	        
    	        ord = (int) reg.ordinal(point.factory.point(new int[] {11, 100}));
    	        System.out.println("Result is " + ord + "; should be " + 1000);
    	        assertTrue(ord == 1000);
    	        
    	        ord = (int) reg.ordinal(point.factory.point(new int[] {11, 102}));
    	        System.out.println("Result is " + ord + "; should be " + 1002);
    	        assertTrue(ord == 1002);
    		}
    	});
    }
}
