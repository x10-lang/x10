/*
 * Created on Oct 28, 2004
 */
package x10.array;

import java.util.Iterator;

/**
 * @author Christoph von Praun
 * 
 * Double Arrays are currently not implemented.
 */
public abstract class DoubleArray extends Array {
    public DoubleArray(Distribution d) {
        super(d);
    }

    protected void assign(Array rhs) {
        assert rhs instanceof DoubleArray;

        DoubleArray rhs_t = (DoubleArray) rhs;
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
	    assert res.dist.equals(dist);
        assert arg.dist.equals(dist);
        /*
         * the following assertions are limitation that are in the current
         * implementation, not in the spec FIXME
         */
        assert arg instanceof DoubleArray;
        assert res instanceof DoubleArray;
		
		DoubleArray arg_t = (DoubleArray) arg;
		DoubleArray res_t = (DoubleArray) res;
		for (Iterator it = dist.iterator(); it.hasNext(); ) {
			int[] p = (int[]) it.next();
			double arg1 = get(p);
			double arg2 = arg_t.get(p);
			double val = op.apply(p, arg1, arg2);
			res_t.set(val, p);
		}
	}
	
	public void pointwise(Array res, Operator.Pointwise op) {
	    assert res.dist.equals(dist);
        /*
         * the following assertions are limitation that are in the current
         * implementation, not in the spec FIXME
         */
        assert res instanceof DoubleArray;
        
        DoubleArray res_t = (DoubleArray) res;
        for (Iterator it = dist.iterator(); it.hasNext(); ) {
			int[] p = (int[]) it.next();
			double arg1 = get(p);
			double val = op.apply(p, arg1);
			res_t.set(val, p);
		}
	}
	
	public void reduction(Operator.Reduction op) {
		for (Iterator it = dist.iterator(); it.hasNext(); ) {
			int[] p = (int[]) it.next();
			double arg1 = get(p);
			op.apply(arg1);
		}
	}
	
	public void scan(Array res, Operator.Scan op) {
	    assert res.dist.equals(dist);
        /*
         * the following assertions are limitation that are in the current
         * implementation, not in the spec FIXME
         */
        assert res instanceof DoubleArray;
        DoubleArray res_t = (DoubleArray) res;
        for (Iterator it = dist.iterator(); it.hasNext(); ) {
			int[] p = (int[]) it.next();
			double arg1 = get(p);
			res_t.set(op.apply(arg1), p);
		}
	}
	
	public void circshift (int[] args) {
		throw new RuntimeException("TODO");
	}
	
    /**
     * Generic flat access.
     */
    public abstract void set(double v, int[] pos);

    public abstract void set(double v, int d0);

    public abstract void set(double v, int d0, int d1);

    public abstract void set(double v, int d0, int d1, int d2);

    public abstract void set(double v, int d0, int d1, int d2, int d3);

    /**
     * Generic flat access.
     */
    public abstract double get(int[] pos);

    public abstract double get(int d0);

    public abstract double get(int d0, int d1);

    public abstract double get(int d0, int d1, int d2);

    public abstract double get(int d0, int d1, int d2, int d3);

}