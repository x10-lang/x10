/*
 * Created on Oct 10, 2004
 *
 */
package x10.array;

import java.util.Iterator;


/**
 * Integer arrays.
 * 
 * @author Christoph von Praun
 * @author Christian Grothoff
 */
public abstract class IntArray extends Array {

    public static class Assign extends Operator.Scan {
        private final int c_;

        public Assign(int c) {
            c_ = c;
        }

        public int apply(int i) {
            return c_;
        }
    }

    public IntArray(Distribution d) {
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
    protected IntArray(Distribution d, int c) {
        super(d);
        // initialize all array alements
        scan(this, new Assign(c));
    }

    protected void assign(Array rhs) {
        assert rhs instanceof IntArray;
        assert rhs.dist.equals(dist);

        IntArray rhs_t = (IntArray) rhs;
        for (Iterator it = rhs_t.dist.iterator(); it.hasNext();) {
            int[] pos = (int[]) it.next();
            set(rhs_t.get(pos), pos);
        }
    }

    /*
     * Generic implementation - an array with fixed, known number of dimensions
     * can of course do without the Iterator.
     */
    public void pointwise(Array res, Operator.Pointwise op, Array arg) {
        assert res == null || res.dist.equals(dist);
        assert arg.dist.equals(dist);
        /*
         * the following assertions are limitation that are in the current
         * implementation, not in the spec FIXME
         */
        assert arg instanceof IntArray;
        assert res == null || res instanceof IntArray;

        IntArray res_t = (IntArray) res;
        IntArray arg_t = (IntArray) arg;
        for (Iterator it = dist.iterator(); it.hasNext();) {
            int[] p = (int[]) it.next();
            int arg1 = get(p);
            int arg2 = arg_t.get(p);
            int val = op.apply(p, arg1, arg2);
            if (res_t != null)
                res_t.set(val, p);
        }
    }

    public void pointwise(Array res, Operator.Pointwise op) {
        assert res == null || res.dist.equals(dist);
        /*
         * the following assertions are limitation that are in the current
         * implementation, not in the spec FIXME
         */
        assert res == null || res instanceof IntArray;
        
        IntArray res_t = (IntArray) res;
        for (Iterator it = dist.iterator(); it.hasNext();) {
            int[] p = (int[]) it.next();
            int arg1 = get(p);
            int val = op.apply(p, arg1);
            if (res_t != null)
                res_t.set(val, p);
        }
    }

    /* operations can be performed in any order */
    public void reduction(Operator.Reduction op) {
        for (Iterator it = dist.iterator(); it.hasNext();) {
            int[] p = (int[]) it.next();
            int arg1 = get(p);
            op.apply(arg1);
        }
    }

    /* operations are performed in canonical order */
    public void scan(Array res, Operator.Scan op) {
        assert res == null || res instanceof IntArray;
        assert res.dist.equals(dist);

        IntArray res_t = (res == null) ? null : (IntArray) res;
        for (Iterator it = dist.iterator(); it.hasNext();) {
            int[] p = (int[]) it.next();
            int arg1 = get(p);
            int val = op.apply(arg1);
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
    public abstract void set(int v, int[] pos);

    public abstract void set(int v, int d0);

    public abstract void set(int v, int d0, int d1);

    public abstract void set(int v, int d0, int d1, int d2);

    public abstract void set(int v, int d0, int d1, int d2, int d3);

    /**
     * Generic flat access. the leas significant dimension is pos[0].
     */
    public abstract int get(int[] pos);

    public abstract int get(int d0);

    public abstract int get(int d0, int d1);

    public abstract int get(int d0, int d1, int d2);

    public abstract int get(int d0, int d1, int d2, int d3);

    public Object toJava() {
        Range[] dims = dist.dim();
        final int[] dims_tmp = new int[dims.length];       
        for (int i = 0; i < dims_tmp.length; ++i) 
            dims_tmp[i] = dims[i].count;
        
        final Object ret = java.lang.reflect.Array.newInstance(Integer.TYPE, dims_tmp);
        pointwise(null, new Operator.Pointwise() {
            public int apply(int[] p, int arg) {
                Object handle = ret;
                int i = 0;
                for (; i < dims_tmp.length - 1; ++i) {
                    handle = java.lang.reflect.Array.get(handle, p[i]);
                }
                java.lang.reflect.Array.setInt(handle, p[i], arg);
                return arg;
            }
        });
		return ret;
	}
} // end of IntArray
