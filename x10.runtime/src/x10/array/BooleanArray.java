/*
 * Created on Oct 28, 2004
 */
package x10.array;

import java.util.Iterator;

import x10.lang.ArrayOperations;
import x10.lang.BooleanReferenceArray;
import x10.lang.dist;
import x10.lang.place;
import x10.lang.point;
import x10.lang.x10Array;

/**
 * Boolean Arrays are currently not implemented.
 * @author Christoph von Praun
 */
public abstract class BooleanArray extends x10Array implements BooleanReferenceArray {
    public BooleanArray(dist d) {
        super(d);
    }

    protected static class Constant extends Operator.Unary {
        private final boolean c_;
        public Constant(boolean c) { c_ = c; }
        public boolean apply(boolean i) { return c_; }
    }

    protected void assign(BooleanArray rhs) {
        assert rhs instanceof BooleanArray;
        place here = x10.lang.Runtime.runtime.currentPlace();
        BooleanArray rhs_t =  rhs;
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

	public void reduction(Operator.Reduction op) {
		ArrayOperations.reduction(this, op);
	}
    	
	public Object toJava() {        
        final int[] dims = new int[distribution.rank];       
        for (int i = 0; i < distribution.rank; ++i) {
            dims[i] = distribution.region.rank(i).high() + 1;
        }
        
        final Object ret = java.lang.reflect.Array.newInstance(Boolean.TYPE, dims);
        ArrayOperations.pointwise(this, null, new Operator.Pointwise() {
            public boolean apply(point p, boolean arg) {
                Object handle = ret;
                int i = 0;
                for (; i < dims.length - 1; ++i) {
                    handle = java.lang.reflect.Array.get(handle, p.get(i));
                }
                java.lang.reflect.Array.setBoolean(handle, p.get(i), arg);
                return arg;
            }
        });
        return ret;
    }

	public boolean bitAndSet(boolean v, point/*(region)*/ p) {
		return BooleanArrayOperations.bitAndSet(this, v, p);
	}
	public boolean bitAndSet(boolean v, int p) {
		return BooleanArrayOperations.bitAndSet(this, v, p);
	}
	public boolean bitAndSet(boolean v, int p, int q) {
		return BooleanArrayOperations.bitAndSet(this, v, p, q);
	}
	public boolean bitAndSet(boolean v, int p, int q, int r) {
		return BooleanArrayOperations.bitAndSet(this, v, p, q, r);
	}
	public boolean bitAndSet(boolean v, int p, int q, int r, int s) {
		return BooleanArrayOperations.bitAndSet(this, v, p, q, r, s);
	}
	public boolean bitOrSet(boolean v, point/*(region)*/ p) {
		return BooleanArrayOperations.bitOrSet(this, v, p);
	}
	public boolean bitOrSet(boolean v, int p) {
		return BooleanArrayOperations.bitOrSet(this, v, p);
	}
	public boolean bitOrSet(boolean v, int p, int q) {
		return BooleanArrayOperations.bitOrSet(this, v, p, q);
	}
	public boolean bitOrSet(boolean v, int p, int q, int r) {
		return BooleanArrayOperations.bitOrSet(this, v, p, q, r);
	}
	public boolean bitOrSet(boolean v, int p, int q, int r, int s) {
		return BooleanArrayOperations.bitOrSet(this, v, p, q, r, s);
	}
	public boolean bitXorSet(boolean v, point/*(region)*/ p) {
		return BooleanArrayOperations.bitXorSet(this, v, p);
	}
	public boolean bitXorSet(boolean v, int p) {
		return BooleanArrayOperations.bitXorSet(this, v, p);
	}
	public boolean bitXorSet(boolean v, int p, int q) {
		return BooleanArrayOperations.bitXorSet(this, v, p, q);
	}
	public boolean bitXorSet(boolean v, int p, int q, int r) {
		return BooleanArrayOperations.bitXorSet(this, v, p, q, r);
	}
	public boolean bitXorSet(boolean v, int p, int q, int r, int s) {
		return BooleanArrayOperations.bitXorSet(this, v, p, q, r, s);
	}
}
