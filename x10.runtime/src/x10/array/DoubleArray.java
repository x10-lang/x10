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
	public void pointwise(Array arg, Operator.Pointwise op) {
		assert arg instanceof DoubleArray;
		
		DoubleArray arg_t = (DoubleArray) arg;
		for (Iterator it = dist.iterator(); it.hasNext(); ) {
			int[] p = (int[]) it.next();
			double arg1 = get(p);
			double arg2 = arg_t.get(p);
			set(op.apply(arg1, arg2), p);
		}
	}
	
	public void pointwise(Operator.Pointwise op) {
		for (Iterator it = dist.iterator(); it.hasNext(); ) {
			int[] p = (int[]) it.next();
			double arg1 = get(p);
			set(op.apply(arg1), p);
		}
	}
	
	public void reduction(Operator.Reduction op) {
		for (Iterator it = dist.iterator(); it.hasNext(); ) {
			int[] p = (int[]) it.next();
			double arg1 = get(p);
			op.apply(arg1);
		}
	}
	
	public void scan(Operator.Scan op) {
		for (Iterator it = dist.iterator(); it.hasNext(); ) {
			int[] p = (int[]) it.next();
			double arg1 = get(p);
			set(op.apply(arg1), p);
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