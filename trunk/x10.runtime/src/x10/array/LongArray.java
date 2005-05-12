/*
 * Created on Oct 28, 2004
 */
package x10.array;

import java.util.Iterator;
import x10.lang.dist;
import x10.lang.point;
import x10.lang.LongReferenceArray;

/**
 * @author Christoph von Praun
 * 
 * Long Arrays are currently not implemented.
 */
public abstract class LongArray extends LongReferenceArray {
    public LongArray(dist d) {
        super(d);
    }
    
    public static class Assign extends Operator.Scan {
        private final long c_;

        public Assign(long c) {
            c_ = c;
        }

        public long apply(long i) {
            return c_;
        }
    }

    protected void assign(LongArray rhs) {
        assert rhs instanceof LongArray;

        LongArray rhs_t =  rhs;
        for (Iterator it = rhs_t.distribution.region.iterator(); it.hasNext();) {
            point pos = (point) it.next();
            set(rhs_t.get(pos), pos);
        }
    }

	/*
	 * Generic implementation - an array with fixed, known number of dimensions
	 * can of course do without the Iterator.
	 */
	public void pointwise(LongArray res, Operator.Pointwise op, LongArray arg) {
	    assert res.distribution.equals(distribution);
        assert arg.distribution.equals(distribution);
		
		LongArray arg_t =  arg;
		LongArray res_t = res;
		for (Iterator it = distribution.region.iterator(); it.hasNext(); ) {
			point p = (point) it.next();
			long arg1 = get(p);
			long arg2 = arg_t.get(p);
			long val = op.apply(p, arg1, arg2);
			res_t.set(val, p);
		}
	}
	
	public void pointwise(LongArray res, Operator.Pointwise op) {
	    assert res == null || res.distribution.equals(distribution);

        for (Iterator it = distribution.region.iterator(); it.hasNext(); ) {
			point p = (point) it.next();
			long arg1 = get(p);
			long val = op.apply(p, arg1);
			if (res != null)
			    res.set(val, p);
		}
	}
	
	public void reduction(Operator.Reduction op) {
		for (Iterator it = distribution.region.iterator(); it.hasNext(); ) {
			point p = (point) it.next();
			long arg1 = get(p);
			op.apply(arg1);
		}
	}
	
	public void scan(LongArray res, Operator.Scan op) {
	    assert res != null;
        assert res.distribution.equals(distribution);
        
        for (Iterator it = distribution.region.iterator(); it.hasNext(); ) {
			point p = (point) it.next();
			long arg1 = get(p);
			res.set(op.apply(arg1), p);
		}
	}
	
	  public void scan(LongArray res, pointwiseOp op ) {
        assert res == null || res instanceof LongArray;
        assert res.distribution.equals(distribution);

        LongArray res_t = (res == null) ? null : (LongArray) res;
        for (Iterator it = distribution.region.iterator(); it.hasNext();) {
            point p = (point) it.next();
            long val = op.apply(p);
            if (res_t != null)
                res_t.set(val, p);
        }
    }
    
	
	public void circshift (int[] args) {
		throw new RuntimeException("TODO");
	}
	
    /**
     * Generic flat access.
     */
    public abstract long set(long v, point pos);

    public abstract long set(long v, int d0);

    public abstract long set(long v, int d0, int d1);

    public abstract long set(long v, int d0, int d1, int d2);

    public abstract long set(long v, int d0, int d1, int d2, int d3);

    /**
     * Generic flat access.
     */
    public abstract long get(point pos);

    public abstract long get(int d0);

    public abstract long get(int d0, int d1);

    public abstract long get(int d0, int d1, int d2);

    public abstract long get(int d0, int d1, int d2, int d3);
    
    public Object toJava() {        
        final int[] dims_tmp = new int[distribution.rank];       
        for (int i = 0; i < distribution.rank; ++i) {
            dims_tmp[i] = distribution.region.rank(i).high() + 1;
        }
        
        final Object ret = java.lang.reflect.Array.newInstance(Long.TYPE, dims_tmp);
        pointwise(null, new Operator.Pointwise() {
            public long apply(point p, long arg) {
                Object handle = ret;
                int i = 0;
                for (; i < dims_tmp.length - 1; ++i) {
                    handle = java.lang.reflect.Array.get(handle, p.get(i));
                }
                java.lang.reflect.Array.setLong(handle, p.get(i), arg);
                return arg;
            }
        });
        return ret;
    }
    
    /* for debugging */
    public static void printArray(String prefix, long[][] a) {
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

}
