/*
 * Created on Nov 3, 2004
 */
package x10.array;

import java.util.Iterator;

import junit.framework.TestCase;
import x10.lang.Activity;
import x10.lang.Runtime;
import x10.lang.point;
import x10.lang.region;
import x10.runtime.Configuration;
import x10.runtime.DefaultRuntime_c;
import x10.runtime.Place;
import x10.runtime.ThreadRegistry;

/**
 * @author Christoph von Praun
 */
public class TestRegion extends TestCase {
    
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
    
    public void testContiguous1() {
        try {
            region r1 = new ContiguousRange(30);
            region r2 = new ContiguousRange(50);
            region r3 = r1.union(r2);
            assertTrue(r3.equals(r2));
        } catch (Exception e) {
            System.err.println("Exception" + e);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    public void testContiguous2() {
        try {
            region r1 = new ContiguousRange(10, 30);
            region r2 = new ContiguousRange(5, 25);
            region r3 = new ContiguousRange(5, 30);
            region r4 = r1.union(r2);
            assertTrue(r3.equals(r4));
        } catch (Exception e) {
            System.err.println("Exception" + e);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    public void testContiguous3() {
        try {
            region r1 = new ContiguousRange(10, 30);
            region r2 = new ContiguousRange(50, 100);
            region r3 = new EmptyRegion(1);
            region r4 = r1.intersection(r2);
            assertTrue(r3.equals(r4));
        } catch (Exception e) {
            System.err.println("Exception" + e);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    public void testContiguous4() {
        try {
            region r1 = new ContiguousRange(10, 30);
            region r2 = new ContiguousRange(50, 100);
            region r3 = r1.union(r2);
            
            region r4 = new ContiguousRange(10, 100);
            region r5 = new ContiguousRange(31, 49);
            region r6 = r4.difference(r5);
            
            assertTrue(r3.equals(r6));
        } catch (Exception e) {
            System.err.println("Exception" + e);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    public void testRegion_iterator0() {
        region[] ranges = new region[] { new ContiguousRange(2, 4)};        
        ArbitraryRegion reg = new ArbitraryRegion(ranges);
        
        int cnt = 0;
        for (Iterator it = reg.iterator(); it.hasNext(); ) {
            point p = (point) it.next();
            System.out.println(p);
            cnt++;
        }
        assertTrue(cnt == 3);
    }
    
    public void testRegion_iterator1() {
        region[] ranges = new region[] { new ContiguousRange(1,3), new ContiguousRange(2, 4)};         
        ArbitraryRegion reg = new ArbitraryRegion(ranges);
        
        int cnt = 0;
        for (Iterator it = reg.iterator(); it.hasNext(); ) {
            point p = (point) it.next();
            System.out.println(p);
            cnt++;
        }
        assertTrue(cnt == 9);
    }
    
    public void testRegion_iterator2() {
        region[] ranges = new region[] { new ContiguousRange(1,3), new ContiguousRange(2, 4), new ContiguousRange(1,3)};         
        ArbitraryRegion reg = new ArbitraryRegion(ranges);
        
        int cnt = 0;
        for (Iterator it = reg.iterator(); it.hasNext(); ) {
            point p = (point) it.next();
            System.out.println(p);
            cnt++;
        }
        assertTrue(cnt == 27);
    }
}
