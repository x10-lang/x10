/*
 * Created on Oct 10, 2004
 *
 */
package x10.array;

import java.util.Iterator;
import x10.lang.distribution;
import x10.lang.region;
import x10.lang.point;


/**
 * Integer arrays.
 * 
 * @author Christoph von Praun
 * @author Christian Grothoff
 */
public abstract class IntArray extends x10.lang.IntReferenceArray {

    public static class Assign extends Operator.Scan {
        private final int c_;

        public Assign(int c) {
            c_ = c;
        }

        public int apply(int i) {
            return c_;
        }
    }

    public IntArray(distribution d) {
        super(d);
    }

    /**
     * Constructor that implements constant promotion (Chap. 10.4.2)
     * 
     * @param d
     *            Distribution of the array
     * @param c
     *            constant used to intialize all values of the array
     */
    protected IntArray(distribution d, int c) {
        super(d);
        // initialize all array alements
        scan(this, new Assign(c));
    }

    protected void assign(IntArray rhs) {
        assert rhs instanceof IntArray;
        assert rhs.distribution.equals(distribution);

        IntArray rhs_t = (IntArray) rhs;
        for (Iterator it = rhs_t.distribution.region.iterator(); it.hasNext();) {
            point pos = (point) it.next();
            set(rhs_t.get(pos), pos);
        }
    }

    /*
     * Generic implementation - an array with fixed, known number of dimensions
     * can of course do without the Iterator.
     */
    public void pointwise(IntArray res, Operator.Pointwise op, IntArray arg) {
        assert res == null || res.distribution.equals(distribution);
        assert arg != null;
        assert arg.distribution.equals(distribution);

        IntArray res_t = (IntArray) res;
        IntArray arg_t = (IntArray) arg;
        for (Iterator it = distribution.region.iterator(); it.hasNext();) {
            point p = (point) it.next();
            int arg1 = get(p);
            int arg2 = arg_t.get(p);
            int val = op.apply(p, arg1, arg2);
            if (res_t != null)
                res_t.set(val, p);
        }
    }

    public void pointwise(IntArray res, Operator.Pointwise op) {
        assert res == null || res.distribution.equals(distribution);
        
        for (Iterator it = distribution.region.iterator(); it.hasNext();) {
            point p = (point) it.next();
            int arg1 = get(p);
            int val = op.apply(p, arg1);
            if (res != null)
                res.set(val, p);
        }
    }

    /* operations can be performed in any order */
    public void reduction(Operator.Reduction op) {
        for (Iterator it = distribution.region.iterator(); it.hasNext();) {
            point p = (point) it.next();
            int arg1 = get(p);
            op.apply(arg1);
        }
    }

    /* operations are performed in canonical order */
    public void scan( IntArray res, Operator.Scan op ) {
        assert res == null || res instanceof IntArray;
        assert res.distribution.equals(distribution);

        IntArray res_t = (res == null) ? null : (IntArray) res;
        for (Iterator it = distribution.region.iterator(); it.hasNext();) {
            point p = (point) it.next();
            int arg1 = get(p);
            int val = op.apply(arg1);
            if (res_t != null)
                res_t.set(val, p);
        }
    }
    
    public void scan( IntArray res, IntArray.pointwiseOp op ) {
        assert res == null || res instanceof IntArray;
        assert res.distribution.equals(distribution);

        IntArray res_t = (res == null) ? null : (IntArray) res;
        for (Iterator it = distribution.region.iterator(); it.hasNext();) {
            point p = (point) it.next();
            int val = op.apply(p);
            if (res_t != null)
                res_t.set(val, p);
        }
    }
    
    

    public void circshift(int[] args) {
        throw new RuntimeException("TODO");
    }

    /**
     * Generic flat access.
     */
    public abstract int set(int v, point pos);

    public abstract int set(int v, int d0);

    public abstract int set(int v, int d0, int d1);

    public abstract int set(int v, int d0, int d1, int d2);

    public abstract int set(int v, int d0, int d1, int d2, int d3);

    /**
     * Generic flat access. the leas significant dimension is pos[0].
     */
    public abstract int get(point pos);

    public abstract int get(int d0);

    public abstract int get(int d0, int d1);

    public abstract int get(int d0, int d1, int d2);

    public abstract int get(int d0, int d1, int d2, int d3);

    public Object toJava() {        

        final int[] dims_tmp = new int[distribution.rank];       
        for (int i = 0; i < distribution.rank; ++i) 
            dims_tmp[i] = distribution.region.rank(i).high() + 1;
        
        final Object ret = java.lang.reflect.Array.newInstance(Integer.TYPE, dims_tmp);
        pointwise(null, new Operator.Pointwise() {
            public int apply(point p, int arg) {
                Object handle = ret;
                int i = 0;
                for (; i < dims_tmp.length - 1; ++i) {
                    handle = java.lang.reflect.Array.get(handle, p.get(i));
                }
                java.lang.reflect.Array.setInt(handle, p.get(i), arg);
                return arg;
            }
        });
		return ret;
	}
    
    /* for debugging */
    public static void printArray(String prefix, int[][] a) {
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
} // end of IntArray
