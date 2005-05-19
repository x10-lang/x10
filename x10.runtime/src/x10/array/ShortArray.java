/*
 * Created on Oct 28, 2004
 */
package x10.array;

import java.util.Iterator;
import x10.lang.dist;
import x10.lang.place;
import x10.lang.point;
import x10.lang.ShortReferenceArray;

/**
 * @author Christoph von Praun
 * 
 * Short Arrays are currently not implemented.
 */
public abstract class ShortArray extends ShortReferenceArray {
    public ShortArray(dist d) {
        super(d);
    }
    
    public static class Assign extends Operator.Scan {
        private final short c_;

        public Assign(short c) {
            c_ = c;
        }

        public short apply(short i) {
            return c_;
        }
    }

    protected void assign(ShortArray rhs) {
        assert rhs instanceof ShortArray;

        ShortArray rhs_t =  rhs;
        for (Iterator it = rhs_t.distribution.region.iterator(); it.hasNext();) {
            point pos = (point) it.next();
            set(rhs_t.get(pos), pos);
        }
    }

	/*
	 * Generic implementation - an array with fixed, known number of dimensions
	 * can of course do without the Iterator.
	 */
    public void pointwise(ShortArray res, Operator.Pointwise op, ShortArray arg) {
        assert res.distribution.equals(distribution);
        assert arg.distribution.equals(distribution);
        
        place here = x10.lang.Runtime.runtime.currentPlace();
        ShortArray arg_t =  arg;
        ShortArray res_t = res;
        try {
            for (Iterator it = distribution.region.iterator(); it.hasNext(); ) {      
                point p = (point) it.next();
                place pl = distribution.get(p);
                x10.lang.Runtime.runtime.setCurrentPlace(pl);
                short arg1 = get(p);
                short arg2 = arg_t.get(p);
                short val = op.apply(p, arg1, arg2);
                res_t.set(val, p);
            }
        } finally {
            x10.lang.Runtime.runtime.setCurrentPlace(here);
        }
    }
	
    public void pointwise(ShortArray res, Operator.Pointwise op) {
        assert res == null || res.distribution.equals(distribution);
        
        place here = x10.lang.Runtime.runtime.currentPlace();
        try {
            for (Iterator it = distribution.region.iterator(); it.hasNext(); ) {
                point p = (point) it.next();
                place pl = distribution.get(p);
                x10.lang.Runtime.runtime.setCurrentPlace(pl);
                short arg1 = get(p);
                short val = op.apply(p, arg1);
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
                short arg1 = get(p);
                op.apply(arg1);
            } 
        } finally {
            x10.lang.Runtime.runtime.setCurrentPlace(here);
        }
    }
	
    public void scan(ShortArray res, Operator.Scan op) {
        assert res.distribution.equals(distribution);
        
        place here = x10.lang.Runtime.runtime.currentPlace();
        try {
            for (Iterator it = distribution.region.iterator(); it.hasNext(); ) {
                point p = (point) it.next();
                place pl = distribution.get(p);
                x10.lang.Runtime.runtime.setCurrentPlace(pl);
                short arg1 = get(p);
                res.set(op.apply(arg1), p);
            }
        } finally {
            x10.lang.Runtime.runtime.setCurrentPlace(here);
        }
    }
	
    public void scan( ShortArray res, pointwiseOp op ) {
        assert res == null || res instanceof ShortArray;
        assert res.distribution.equals(distribution);
        
        place here = x10.lang.Runtime.runtime.currentPlace();
        ShortArray res_t = (res == null) ? null : (ShortArray) res;
        try {
            for (Iterator it = distribution.region.iterator(); it.hasNext();) {
                point p = (point) it.next();
                place pl = distribution.get(p);
                x10.lang.Runtime.runtime.setCurrentPlace(pl);
                short val = op.apply(p);
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
    public abstract short set(short v, point pos);

    public abstract short set(short v, int d0);

    public abstract short set(short v, int d0, int d1);

    public abstract short set(short v, int d0, int d1, int d2);

    public abstract short set(short v, int d0, int d1, int d2, int d3);

    /**
     * Generic flat access.
     */
    public abstract short get(point pos);

    public abstract short get(int d0);

    public abstract short get(int d0, int d1);

    public abstract short get(int d0, int d1, int d2);

    public abstract short get(int d0, int d1, int d2, int d3);
    public abstract short get(int[] p);
    
    public Object toJava() {        
        final int[] dims_tmp = new int[distribution.rank];       
        for (int i = 0; i < distribution.rank; ++i) {
            dims_tmp[i] = distribution.region.rank(i).high() + 1;
        }
        
        final Object ret = java.lang.reflect.Array.newInstance(Short.TYPE, dims_tmp);
        pointwise(null, new Operator.Pointwise() {
            public short apply(point p, short arg) {
                Object handle = ret;
                int i = 0;
                for (; i < dims_tmp.length - 1; ++i) {
                    handle = java.lang.reflect.Array.get(handle, p.get(i));
                }
                java.lang.reflect.Array.setShort(handle, p.get(i), arg);
                return arg;
            }
        });
        return ret;
    }
    
    /* for debugging */
    public static void printArray(String prefix, short[][] a) {
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
