/*
 * Created on Oct 28, 2004
 */
package x10.array;

import java.util.Iterator;
import x10.lang.distribution;
import x10.lang.point;
import x10.lang.FloatReferenceArray;

/**
 * @author Christoph von Praun
 * 
 * Float Arrays are currently not implemented.
 */
public abstract class FloatArray extends FloatReferenceArray {
    public FloatArray(distribution d) {
        super(d);
    }
    
    public static class Assign extends Operator.Scan {
        private final float c_;

        public Assign(float c) {
            c_ = c;
        }

        public float apply(float i) {
            return c_;
        }
    }

    protected void assign(FloatArray rhs) {
        assert rhs instanceof FloatArray;

        FloatArray rhs_t =  rhs;
        for (Iterator it = rhs_t.distribution.region.iterator(); it.hasNext();) {
            point pos = (point) it.next();
            set(rhs_t.get(pos), pos);
        }
    }

	/*
	 * Generic implementation - an array with fixed, known number of dimensions
	 * can of course do without the Iterator.
	 */
	public void pointwise(FloatArray res, Operator.Pointwise op, FloatArray arg) {
	    assert res.distribution.equals(distribution);
        assert arg.distribution.equals(distribution);
		
		FloatArray arg_t =  arg;
		FloatArray res_t = res;
		for (Iterator it = distribution.region.iterator(); it.hasNext(); ) {
			point p = (point) it.next();
			float arg1 = get(p);
			float arg2 = arg_t.get(p);
			float val = op.apply(p, arg1, arg2);
			res_t.set(val, p);
		}
	}
	
	public void pointwise(FloatArray res, Operator.Pointwise op) {
	    assert res == null || res.distribution.equals(distribution);
        
        for (Iterator it = distribution.region.iterator(); it.hasNext(); ) {
			point p = (point) it.next();
			float arg1 = get(p);
			float val = op.apply(p, arg1);
			if (res != null)
			    res.set(val, p);
		}
	}
	
	public void reduction(Operator.Reduction op) {
		for (Iterator it = distribution.region.iterator(); it.hasNext(); ) {
			point p = (point) it.next();
			float arg1 = get(p);
			op.apply(arg1);
		}
	}
	
	public void scan(FloatArray res, Operator.Scan op) {
	    assert res.distribution.equals(distribution);
        for (Iterator it = distribution.region.iterator(); it.hasNext(); ) {
			point p = (point) it.next();
			float arg1 = get(p);
			res.set(op.apply(arg1), p);
		}
	}
	
	  public void scan( FloatArray res, pointwiseOp op ) {
        assert res == null || res instanceof FloatArray;
        assert res.distribution.equals(distribution);

        FloatArray res_t = (res == null) ? null : (FloatArray) res;
        for (Iterator it = distribution.region.iterator(); it.hasNext();) {
            point p = (point) it.next();
            float val = op.apply(p);
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
    public abstract float set(float v, point pos);

    public abstract float set(float v, int d0);

    public abstract float set(float v, int d0, int d1);

    public abstract float set(float v, int d0, int d1, int d2);

    public abstract float set(float v, int d0, int d1, int d2, int d3);

    /**
     * Generic flat access.
     */
    public abstract float get(point pos);

    public abstract float get(int d0);

    public abstract float get(int d0, int d1);

    public abstract float get(int d0, int d1, int d2);

    public abstract float get(int d0, int d1, int d2, int d3);
    public abstract float get(int[] p);
    
    public Object toJava() {        
        final int[] dims_tmp = new int[distribution.rank];       
        for (int i = 0; i < distribution.rank; ++i) {
            dims_tmp[i] = distribution.region.rank(i).high() + 1;
        }
        
        final Object ret = java.lang.reflect.Array.newInstance(Float.TYPE, dims_tmp);
        pointwise(null, new Operator.Pointwise() {
            public float apply(point p, float arg) {
                Object handle = ret;
                int i = 0;
                for (; i < dims_tmp.length - 1; ++i) {
                    handle = java.lang.reflect.Array.get(handle, p.get(i));
                }
                java.lang.reflect.Array.setFloat(handle, p.get(i), arg);
                return arg;
            }
        });
        return ret;
    }
    
    /* for debugging */
    public static void printArray(String prefix, float[][] a) {
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
