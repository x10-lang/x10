/*
 * Created on Oct 28, 2004
 */
package x10.array;

/**
 * @author Christoph von Praun
 * 
 * Double Arrays are currently not implemented.
 */
public abstract class DoubleArray extends Array {
	public DoubleArray(Distribution d) {
		super (d);
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
