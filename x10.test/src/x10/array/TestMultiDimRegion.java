/*
 * Created on Oct 23, 2004
 */
package x10.array;

import x10.lang.Runtime;
import x10.lang.distribution;
import x10.lang.region;
import x10.lang.point;
import x10.runtime.Activity;
import x10.runtime.Configuration;
import x10.runtime.DefaultRuntime_c;
import x10.runtime.Place;
import x10.runtime.ThreadRegistry;
import junit.framework.TestCase;
import java.util.Iterator;

/**
 * @author Christian Grothoff, Christoph von Praun
 * @author vj
 */
public class TestMultiDimRegion extends TestCase {
    
    static {
        Configuration.parseCommandLine(null);
        Runtime.init();
    }
    
    private final Activity a
    = new Activity() { public void run() {} }; // dummy
    
    /**
     * Junit may use additional threads to run the testcases
     * (other than the main one used to initialize the
     * Runtime class).  Hence we need a litte hack to register
     * the thread used to run the testcase as a 'local' thread
     * with the Runtime.
     */
    public void setUp() {
        DefaultRuntime_c r = (DefaultRuntime_c) Runtime.runtime;
        Place[] pls = Place.places();
        if (r instanceof ThreadRegistry) {
            Thread t = Thread.currentThread();
            ThreadRegistry tr = (ThreadRegistry) r;
            tr.registerThread(t, pls[0]);
            tr.registerActivityStart(t, a, null);
        }
    }
    
    /**
     * Clean-up effects from setUp().
     */
    public void tearDown() {
        DefaultRuntime_c r = (DefaultRuntime_c) Runtime.runtime;
        if (r instanceof ThreadRegistry) {
            Thread t = Thread.currentThread();
            ThreadRegistry tr = (ThreadRegistry) r;
            tr.registerActivityStop(t, a);
        }
    }
    
    public void testRegion_iterator1() {
        Range[] ranges = new Range[] { new ContiguousRange(1,3), new ContiguousRange(2, 4) }; // 6x4         
        MultiDimRegion reg = new MultiDimRegion(ranges);
        
        int cnt = 0;
        for (Iterator it = reg.iterator(); it.hasNext(); ) {
            point p = (point) it.next();
            System.out.println(p);
            cnt++;
        }
        assertTrue(cnt == 9);
    }
    
    public void testRegion_sub() {
        Range[] ranges = new Range[] { new ContiguousRange(10,109), new ContiguousRange(100, 1099) }; // 6x4         
        MultiDimRegion reg = new MultiDimRegion(ranges);
        
        // region sub2 = reg.sub(5, 2); // => (100, 1000) x (20,1000)
        region[] subs = reg.partition(5);
        region sub2 = subs[2];
        Range[] ranges2 = new Range[] { new ContiguousRange(50,69), new ContiguousRange(100, 1099) }; // 2x1
        assertTrue(new MultiDimRegion(ranges2).equals(sub2));
    }
    
    /* the X10 arrays should provide a row-major interface */
    public void testRegion_majorness() {
        Runtime.Factory F = Runtime.factory;
        distribution.factory DF = F.getDistributionFactory();
        IntArray.factory IF = Runtime.factory.getIntArrayFactory();
        Range[] ranges = new Range[] { new ContiguousRange(0,2), new ContiguousRange(0,2) };          
        MultiDimRegion r = new MultiDimRegion(ranges);
        distribution d = DF.constant(r, Runtime.here());
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
    }
    
    public void testRegion_ordinal() {
        Range[] ranges = new Range[] { new ContiguousRange(10,109), new ContiguousRange(100, 1099) }; // 6x4         
        MultiDimRegion reg = new MultiDimRegion(ranges);
        
        int ord = (int) reg.ordinal(point.factory.point(reg, new int[] {10, 100}));
        System.out.println("Result is " + ord + "; should be " + 0);
        assertTrue(ord == 0);
        
        ord = (int) reg.ordinal(point.factory.point(reg, new int[] {11, 100}));
        System.out.println("Result is " + ord + "; should be " + 1000);
        assertTrue(ord == 1000);
        
        ord = (int) reg.ordinal(point.factory.point(reg, new int[] {11, 102}));
        System.out.println("Result is " + ord + "; should be " + 1002);
        assertTrue(ord == 1002);
    }
}
