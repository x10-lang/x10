/*
 * Created on Nov 2, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package x10.array.sharedmemory;

import x10.array.ContiguousRange;
import x10.array.DoubleArray;
import x10.array.IntArray;
import x10.array.Operator;
import x10.array.Range;
import x10.array.MultiDimRegion;
import x10.lang.Activity;
import x10.lang.Runtime;
import x10.lang.distribution;
import x10.lang.region;
import x10.lang.point;
import x10.runtime.DefaultRuntime_c;
import x10.runtime.Place;
import x10.runtime.ThreadRegistry;


import junit.framework.TestCase;


/**
 * @author praun
 *
 
 */
public class TestXXArray_c extends TestCase {
    
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
    
    public void testIntArray_reduce() {
    	Runtime.Factory F = Runtime.factory;
        region.factory rf = F.getRegionFactory();
        region r = rf.region(new region[] {new ContiguousRange(3), new ContiguousRange(3)});
        distribution d = Runtime.factory.getDistributionFactory().constant(r, x10.lang.place.FIRST_PLACE);
        IntArray ia = (IntArray) Runtime.factory.getIntArrayFactory().IntReferenceArray(d, 12);
        
        Operator.Reduction red = new Operator.Reduction() {
           private int acc_;
           public void apply(int i) {
               acc_ += i;
           }
           public int getIntResult() {
               return acc_;
           }
        };
        ia.reduction(red);
        int result = red.getIntResult();
        System.out.println("Result is " + result + "; should be " + 192);
        assertTrue(result == 192);
    }
    
    public void testDoubleArray_reduce() {
        final int N = 100; 

        MultiDimRegion r = new MultiDimRegion(new Range[] {
                new ContiguousRange(0, N - 1),
                new ContiguousRange(0, N - 1) });
        distribution d = Runtime.factory.getDistributionFactory().block(r);
        DoubleArray A = (DoubleArray) Runtime.factory.getDoubleArrayFactory().DoubleReferenceArray(d, 0);

        // initialize array A (this should happen in parallel, distributed
        // over
        // all places in a real implementation).
        A.pointwise(A, new Operator.Pointwise() {
            public double apply(point point, double arg) {
                return N * point.get(0) + point.get(1);
            }
        });
        
        Operator.Reduction checksum = new Operator.Reduction() {
            private double acc_;
            public void apply(double d) {
                acc_ += d;
            }
            public double getDoubleResult() {
                return acc_;
            }
            public void reset() {
                acc_ = 0;
            }
        };
        A.reduction(checksum);
        double result = checksum.getDoubleResult();
        double should_result = 4.9995E7; 
        System.out.println("Result is " + result + "; should be " + should_result);
        assertTrue(result == should_result);
    }
}
