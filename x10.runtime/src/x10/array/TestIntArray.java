/*
 * Created on Nov 3, 2004
 */
package x10.array;

import junit.framework.TestCase;
import x10.lang.Runtime;
import x10.lang.region;
import x10.lang.distribution;
import x10.lang.place;

/**
 * @author Christoph von Praun
 */
public class TestIntArray extends TestCase {

    public void testToJava() {
        // ArrayFactory.init(ArrayRuntime.getRuntime());
        try {
            final int SIZE = 3;
            Runtime.Factory F = Runtime.factory;
            region.factory rf = F.getRegionFactory();
            region[] ranges = { rf.region(0, SIZE - 1), rf.region(0, SIZE-1)};
            region r = rf.region(ranges);
            distribution.factory DF = F.getDistributionFactory();
            distribution d = DF.constant(r, Runtime.here());
            IntArray.factory IF = Runtime.factory.getIntArrayFactory();
            x10.lang.IntArray value = IF.IntArray(d, 12);
    
            int[][] a1 = (int[][]) ((IntArray) value).toJava();
            int[][] a2 = new int[][] { { 12, 12, 12 }, { 12, 12, 12 },
                    { 12, 12, 12 } };
            printArray_("a1 = ", a1);
            printArray_("a2 = ", a2);
            assertTrue(equalArrays_(a1, a2));
        } catch (Exception e) {
            System.err.println("Exception" + e);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    private static void printArray_(String prefix, int[][] a) {
        System.out.print(prefix + "{");
        for (int i = 0; i < a.length; ++i) {
            System.out.print("{");
            for (int j = 0; j < a[i].length; ++ j) {
                System.out.print(a[i][j]);
                if (j < a[i].length - 1)
                    System.out.print(", ");
            }
            System.out.print("}");
            if (i < a.length - 1)
                System.out.print(", ");
        }
        System.out.println("}");
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
