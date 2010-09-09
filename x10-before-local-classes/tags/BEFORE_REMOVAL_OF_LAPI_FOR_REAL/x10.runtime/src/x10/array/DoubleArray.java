/*
 * Created on Oct 28, 2004
 */
package x10.array;

import java.util.Iterator;
import x10.lang.dist;
import x10.lang.place;
import x10.lang.point;
import x10.lang.region;
import x10.lang.DoubleReferenceArray;

/**
 * @author Christoph von Praun
 * 
 * Double Arrays are currently not implemented.
 */
public abstract class DoubleArray extends DoubleReferenceArray {
    public DoubleArray(dist d) {
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
        
        place here = x10.lang.Runtime.runtime.currentPlace();
        DoubleArray rhs_t =  rhs;
        try {
            for (Iterator it = rhs_t.distribution.region.iterator(); it.hasNext();) {
                
                point pos = (point) it.next();
                place pl = distribution.get(pos);
                x10.lang.Runtime.runtime.setCurrentPlace(pl);
                set(rhs_t.get(pos), pos);
            }
        } finally {
            x10.lang.Runtime.runtime.setCurrentPlace(here);
        } 
    }

	/*
	 * Generic implementation - an array with fixed, known number of dimensions
	 * can of course do without the Iterator.
	 */
	public void pointwise(DoubleArray res, Operator.Pointwise op, DoubleArray arg) {
	    assert res.distribution.equals(distribution);
        assert arg.distribution.equals(distribution);
        
        place here = x10.lang.Runtime.runtime.currentPlace();
        DoubleArray arg_t =  arg;
        DoubleArray res_t = res;
        try {
            for (Iterator it = distribution.region.iterator(); it.hasNext(); ) {
                point p = (point) it.next();
                place pl = distribution.get(p);
                x10.lang.Runtime.runtime.setCurrentPlace(pl);
                double arg1 = get(p);
                double arg2 = arg_t.get(p);
                double val = op.apply(p, arg1, arg2);
                res_t.set(val, p);
            }
        } finally {
            x10.lang.Runtime.runtime.setCurrentPlace(here);
        } 
	}
	
	public void pointwise(DoubleArray res, Operator.Pointwise op) {
	    assert res == null || res.distribution.equals(distribution);
        
        place here = x10.lang.Runtime.runtime.currentPlace();
        try {
            for (Iterator it = distribution.region.iterator(); it.hasNext(); ) {
                point p = (point) it.next();
                place pl = distribution.get(p);
                x10.lang.Runtime.runtime.setCurrentPlace(pl);
                double arg1 = get(p);
                double val = op.apply(p, arg1);
                if (res != null)
                    res.set(val, p);
            }
		} finally {
            x10.lang.Runtime.runtime.setCurrentPlace(here);
        } 
	}
	
	public void reduction(Operator.Reduction op) {
	    place here = x10.lang.Runtime.runtime.currentPlace();
	    try {
	        for (Iterator it = distribution.region.iterator(); it.hasNext(); ) {
	            point p = (point) it.next();
	            place pl = distribution.get(p);
	            x10.lang.Runtime.runtime.setCurrentPlace(pl);
	            double arg1 = get(p);
	            op.apply(arg1);
	        }
	    } finally {
	        x10.lang.Runtime.runtime.setCurrentPlace(here);
	    } 
	}
	
	public void scan(DoubleArray res, Operator.Scan op) {
		assert res.distribution.equals(distribution);
		place here = x10.lang.Runtime.runtime.currentPlace();
        
        try {
            for (Iterator it = distribution.region.iterator(); it.hasNext(); ) {
                point p = (point) it.next();
                place pl = distribution.get(p);
                x10.lang.Runtime.runtime.setCurrentPlace(pl);
                double arg1 = get(p);
                res.set(op.apply(arg1), p);
            }
        } finally {
            x10.lang.Runtime.runtime.setCurrentPlace(here);
        }
	}
	
	
	public void scan( DoubleArray res, pointwiseOp op ) {
		assert res == null || res instanceof DoubleArray;
		assert res.distribution.equals(distribution);
		
        place here = x10.lang.Runtime.runtime.currentPlace();
		DoubleArray res_t = (res == null) ? null : (DoubleArray) res;
		try {
		    for (Iterator it = distribution.region.iterator(); it.hasNext();) {
		        point p = (point) it.next();
		        place pl = distribution.get(p);
                x10.lang.Runtime.runtime.setCurrentPlace(pl);
                double val = op.apply(p);
		        if (res_t != null)
		            res_t.set(val, p);
		    }
		} finally {
		    x10.lang.Runtime.runtime.setCurrentPlace(here);
		} 
		
	}
    
	
	public void circshift (int[] args) {
		throw new RuntimeException("TODO");
	}
	
    /**
     * Generic flat access.
     */
	public abstract double setOrdinal(double v, int i);
	
    public abstract double set(double v, point pos);

    public abstract double set(double v, int d0);

    public abstract double set(double v, int d0, int d1);

    public abstract double set(double v, int d0, int d1, int d2);

    public abstract double set(double v, int d0, int d1, int d2, int d3);

    public abstract double set(double v, point pos,boolean chkPl,boolean chkAOB);

    public abstract double set(double v, int d0,boolean chkPl,boolean chkAOB);

    public abstract double set(double v, int d0, int d1,boolean chkPl,boolean chkAOB);

    public abstract double set(double v, int d0, int d1, int d2,boolean chkPl,boolean chkAOB);

    public abstract double set(double v, int d0, int d1, int d2, int d3,boolean chkPl,boolean chkAOB);

    /**
     * Generic flat access.
     */
    public abstract double get(point pos);

	public abstract double getOrdinal( int i);
	
    public abstract double get(int d0);

    public abstract double get(int d0, int d1);

    public abstract double get(int d0, int d1, int d2);

    public abstract double get(int d0, int d1, int d2, int d3);
    public abstract double get(int[] p);
    
    public abstract double get(point pos,boolean chkPl,boolean chkAOB);

	public abstract double get(int d0,boolean chkPl,boolean chkAOB);

    public abstract double get(int d0, int d1,boolean chkPl,boolean chkAOB);

    public abstract double get(int d0, int d1, int d2,boolean chkPl,boolean chkAOB);

    public abstract double get(int d0, int d1, int d2, int d3,boolean chkPl,boolean chkAOB);
    public abstract double get(int[] p,boolean chkPl,boolean chkAOB);
    
    public DoubleReferenceArray restrictShallow (region r) {
    	dist dist = distribution.restriction(r);
    	return new DoubleProxyArray(dist, this);    	
    }
    
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