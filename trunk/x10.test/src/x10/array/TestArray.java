/*
 * Created on Nov 3, 2004
 */
package x10.array;

import junit.framework.TestCase;
import x10.lang.Runtime;
import x10.lang.region;
import x10.lang.dist;
import x10.runtime.Activity;
import x10.runtime.Configuration;
import x10.runtime.DefaultRuntime_c;
import x10.runtime.Place;


/**
 * @author Christoph von Praun
 */
public class TestArray extends TestCase {
    
    static {
        Configuration.parseCommandLine(null);
        Runtime.init();
    }
    
    private final Activity a
    = new Activity() { public void run() {} }; // dummy
    
    public void testToJava() {
    	Runtime.runAsync(new Activity() {
    		public void run() {
    			
    			try {
    				final int SIZE = 3;
    				Runtime.Factory F = Runtime.factory;
    				region.factory rf = F.getRegionFactory();
    				region[] ranges = { rf.region(0, SIZE - 1), rf.region(0, SIZE-1)};
    				region r = new ArbitraryRegion(ranges);// rf.region(ranges);
    				dist.factory DF = F.getDistributionFactory();
    				dist d = DF.constant(r, Runtime.here());
    				IntArray.factory IF = Runtime.factory.getIntArrayFactory();
    				x10.lang.intArray value = IF.IntReferenceArray(d, 12);
    				
    				int[][] a1 = (int[][]) ((IntArray) value).toJava();
    				int[][] a2 = new int[][] { { 12, 12, 12 }, { 12, 12, 12 },
    						{ 12, 12, 12 } };
    				IntArray.printArray("a1 = ", a1);
    				IntArray.printArray("a2 = ", a2);
    				assertTrue(equalArrays_(a1, a2));
    			} catch (Exception e) {
    				System.err.println("Exception" + e);
    				e.printStackTrace();
    				throw new RuntimeException(e);
    			}
    		}
    	});
    }
       
    private static boolean equalArrays_(int[][] a1, int[][] a2) {
        boolean ret = true;
        ret &= a1.length == a2.length;
            
        for (int i = 0; ret && i < a1.length; ++i) {
            ret &= a1[i].length == a2[i].length;
            for (int j = 0; ret && j < a1[i].length; ++ j)
                ret &= a1[i][j] == a2[i][j];
        }
        return ret;
    }
}
