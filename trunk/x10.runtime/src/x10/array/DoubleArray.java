/*
 * Created on Oct 28, 2004
 */
package x10.array;

import java.util.Iterator;
import x10.lang.distribution;
import x10.lang.point;
import x10.lang.DoubleReferenceArray;

/**
 * @author Christoph von Praun
 * 
 * Double Arrays are currently not implemented.
 */
public abstract class DoubleArray extends DoubleReferenceArray {
    public DoubleArray(distribution d) {
        super(d);
    }
    
    public static class Assign extends Operator.Scan {
        private final double c_;

        public Assign(double c) {
            c_ = c;
        }

        public double apply(double i) {
            return c_;
        }
    }

    protected void assign(DoubleArray rhs) {
        assert rhs instanceof DoubleArray;

        DoubleArray rhs_t =  rhs;
        for (Iterator it = rhs_t.distribution.region.iterator(); it.hasNext();) {
            point pos = (point) it.next();
            set(rhs_t.get(pos), pos);
        }
    }

	/*
	 * Generic implementation - an array with fixed, known number of dimensions
	 * can of course do without the Iterator.
	 */
	public void pointwise(DoubleArray res, Operator.Pointwise op, DoubleArray arg) {
	    assert res.distribution.equals(distribution);
        assert arg.distribution.equals(distribution);
        /*
         * the following assertions are limitation that are in the current
         * implementation, not in the spec FIXME
         */
        assert arg instanceof DoubleArray;
        assert res instanceof DoubleArray;
		
		DoubleArray arg_t =  arg;
		DoubleArray res_t = res;
		for (Iterator it = distribution.region.iterator(); it.hasNext(); ) {
			point p = (point) it.next();
			double arg1 = get(p);
			double arg2 = arg_t.get(p);
			double val = op.apply(p, arg1, arg2);
			res_t.set(val, p);
		}
	}
	
	public void pointwise(DoubleArray res, Operator.Pointwise op) {
	    assert res == null || res.distribution.equals(distribution);
        /*
         * the following assertions are limitation that are in the current
         * implementation, not in the spec FIXME
         */
        assert res == null || res instanceof DoubleArray;
        
        for (Iterator it = distribution.region.iterator(); it.hasNext(); ) {
			point p = (point) it.next();
			double arg1 = get(p);
			double val = op.apply(p, arg1);
			if (res != null)
			    res.set(val, p);
		}
	}
	
	public void reduction(Operator.Reduction op) {
		for (Iterator it = distribution.region.iterator(); it.hasNext(); ) {
			point p = (point) it.next();
			double arg1 = get(p);
			op.apply(arg1);
		}
	}
	
	public void scan(DoubleArray res, Operator.Scan op) {
	    assert res.distribution.equals(distribution);
        /*
         * the following assertions are limitation that are in the current
         * implementation, not in the spec FIXME
         */
        assert res instanceof DoubleArray;
        DoubleArray res_t = (DoubleArray) res;
        for (Iterator it = distribution.region.iterator(); it.hasNext(); ) {
			point p = (point) it.next();
			double arg1 = get(p);
			res_t.set(op.apply(arg1), p);
		}
	}
	
	  public void scan( DoubleArray res, pointwiseOp op ) {
        assert res == null || res instanceof DoubleArray;
        assert res.distribution.equals(distribution);

        DoubleArray res_t = (res == null) ? null : (DoubleArray) res;
        for (Iterator it = distribution.region.iterator(); it.hasNext();) {
            point p = (point) it.next();
            double val = op.apply(p);
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
    public abstract void set(double v, point pos);

    public abstract void set(double v, int d0);

    public abstract void set(double v, int d0, int d1);

    public abstract void set(double v, int d0, int d1, int d2);

    public abstract void set(double v, int d0, int d1, int d2, int d3);

    /**
     * Generic flat access.
     */
    public abstract double get(point pos);

    public abstract double get(int d0);

    public abstract double get(int d0, int d1);

    public abstract double get(int d0, int d1, int d2);

    public abstract double get(int d0, int d1, int d2, int d3);
    
    public Object toJava() {        
        final int[] dims_tmp = new int[distribution.rank];       
        for (int i = 0; i < distribution.rank; ++i) {
            dims_tmp[i] = distribution.region.rank(i).high() + 1;
        }
        
        final Object ret = java.lang.reflect.Array.newInstance(Double.TYPE, dims_tmp);
        pointwise(null, new Operator.Pointwise() {
            public double apply(point p, double arg) {
                Object handle = ret;
                int i = 0;
                for (; i < dims_tmp.length - 1; ++i) {
                    handle = java.lang.reflect.Array.get(handle, p.get(i));
                }
                java.lang.reflect.Array.setDouble(handle, p.get(i), arg);
                return arg;
            }
        });
        return ret;
    }
    
    /* for debugging */
    public static void printArray(String prefix, double[][] a) {
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