/*
 * Created on Nov 2, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package x10.array.sharedmemory;

import x10.array.ContiguousRange;
import x10.array.DoubleArray;
import x10.array.Operator;
import x10.array.Range;
import x10.array.ArrayRuntime;
import x10.base.Place;


import junit.framework.TestCase;


/**
 * @author praun
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TestIntArray extends TestCase {
    
    public void testIntArray_reduce() {
        Place[] places = ArrayRuntime.places();
        int[] dims = {3,3};
        Region_c r = new Region_c(dims);
        Distribution_c d = Distribution_c.makeConstant(r, places[0]);
        IntArray_c ia = new IntArray_c(d, new Operator.Pointwise() {
            public int apply(int[] p, int i) {
                return 12;
            }
        }, true);
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
        System.out.println("Result is " + result + "; should be " + 108);
        assertTrue(result == 108);
    }
    
    public void testDoubleArray_reduce() {
        final int N = 100; 

        Region_c r = new Region_c(new Range[] {
                new ContiguousRange(0, N - 1),
                new ContiguousRange(0, N - 1) });
        Place[] places = ArrayRuntime.places();
        Distribution_c d = Distribution_c.makeBlock(r, places);
        DoubleArray A = new DoubleArray_c(d, true);

        // initialize array A (this should happen in parallel, distributed
        // over
        // all places in a real implementation).
        A.pointwise(A, new Operator.Pointwise() {
            public double apply(int[] point, double arg) {
                return N * point[0] + point[1];
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
