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
	 * @param d Distribution of the array
	 * @param c constant used to intialize all values of the array
	 */
	public IntArray(Distribution d, int c) {
		super(d);
		// initialize all array alements
		scan(new Assign(c));
	}
	
    protected void assign(Array rhs) {
    	assert rhs instanceof IntArray;
    	
    	IntArray rhs_t = (IntArray) rhs;
    	for (Iterator it = rhs_t.dist.iterator(); it.hasNext(); ) {
    		int[] pos = (int[]) it.next();
    		set(rhs_t.get(pos), pos);
    	}
    }
    
	/*
	 * Generic implementation - an array with fixed, known number of dimensions
	 * can of course do without the Iterator.
	 */
	public void pointwise(Array arg, Operator.Pointwise op) {
		assert arg instanceof IntArray;
		
		IntArray arg_t = (IntArray) arg;
		for (Iterator it = dist.iterator(); it.hasNext(); ) {
			int[] p = (int[]) it.next();
			int arg1 = get(p);
			int arg2 = arg_t.get(p);
			set(op.apply(arg1, arg2), p);
		}
	}
	
	public void pointwise(Operator.Pointwise op) {
		for (Iterator it = dist.iterator(); it.hasNext(); ) {
			int[] p = (int[]) it.next();
			int arg1 = get(p);
			set(op.apply(arg1), p);
		}
	}
	
	public void reduction(Operator.Reduction op) {
		for (Iterator it = dist.iterator(); it.hasNext(); ) {
			int[] p = (int[]) it.next();
			int arg1 = get(p);
			op.apply(arg1);
		}
	}
	
	public void scan(Operator.Scan op) {
		for (Iterator it = dist.iterator(); it.hasNext(); ) {
			int[] p = (int[]) it.next();
			int arg1 = get(p);
			set(op.apply(arg1), p);
		}
	}
	
	public void circshift (int[] args) {
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
     * Generic flat access.
     */
    public abstract int get(int[] pos);
    public abstract int get(int d0);
    public abstract int get(int d0, int d1);
    public abstract int get(int d0, int d1, int d2);
    public abstract int get(int d0, int d1, int d2, int d3);
    
} // end of IntArray