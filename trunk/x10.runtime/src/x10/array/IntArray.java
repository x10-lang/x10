/*
 * Created on Oct 10, 2004
 *
 */
package x10.array;

import java.util.Iterator;

import x10.lang.dist;
import x10.lang.place;
import x10.lang.point;
import x10.lang.region;
import x10.lang.x10Array;
import x10.lang.Runtime;


/**
 * Integer arrays.
 * 
 * @author Christoph von Praun
 * @author Christian Grothoff
 */
public abstract class IntArray extends x10.lang.IntReferenceArray {

    public static class Assign extends Operator.Scan {
        private final int c_;

        public Assign(int c) {
            c_ = c;
        }

        public int apply(int i) {
            return c_;
        }
    }

    public IntArray(dist d) {
        super(d);
    }

    /**
     * Constructor that implements constant promotion (Chap. 10.4.2)
     * 
     * @param d
     *            Distribution of the array
     * @param c
     *            constant used to intialize all values of the array
     */
    protected IntArray(dist d, int c) {
        super(d);
        // initialize all array alements
        scan(this, new Assign(c));
    }

    protected void assign(IntArray rhs) {
        assert rhs instanceof IntArray;
        assert rhs.distribution.equals(distribution);
        place here = x10.lang.Runtime.runtime.currentPlace();
        
        IntArray rhs_t = (IntArray) rhs;
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
    public void pointwise(IntArray res, Operator.Pointwise op, IntArray arg) {
        assert res == null || res.distribution.equals(distribution);
        assert arg != null;
        assert arg.distribution.equals(distribution);

        IntArray res_t = (IntArray) res;
        IntArray arg_t = (IntArray) arg;
        place here = x10.lang.Runtime.runtime.currentPlace();
        
        try {
            for (Iterator it = distribution.region.iterator(); it.hasNext();) {
                point p = (point) it.next();
                place pl = distribution.get(p);
                x10.lang.Runtime.runtime.setCurrentPlace(pl);
                int arg1 = get(p);
                int arg2 = arg_t.get(p);
                int val = op.apply(p, arg1, arg2);
                if (res_t != null)
                    res_t.set(val, p);
            }
        } finally {
            x10.lang.Runtime.runtime.setCurrentPlace(here);
        }
    }

    public void pointwise(IntArray res, Operator.Pointwise op) {
        assert res == null || res.distribution.equals(distribution);
        place here = x10.lang.Runtime.runtime.currentPlace();
        
        try {
            for (Iterator it = distribution.region.iterator(); it.hasNext();) {
                point p = (point) it.next();
                place pl = distribution.get(p);
                x10.lang.Runtime.runtime.setCurrentPlace(pl);
                int arg1 = get(p);
                int val = op.apply(p, arg1);
                if (res != null)
                    res.set(val, p);
            }
        } finally {
            x10.lang.Runtime.runtime.setCurrentPlace(here);
        }
    }

    /* operations can be performed in any order */
    public void reduction(Operator.Reduction op) {
        place here = x10.lang.Runtime.runtime.currentPlace();
        try {
            for (Iterator it = distribution.region.iterator(); it.hasNext();) {
                point p = (point) it.next();
                place pl = distribution.get(p);
                x10.lang.Runtime.runtime.setCurrentPlace(pl); 
                int arg1 = get(p);
                op.apply(arg1);
            }
        } finally {
            x10.lang.Runtime.runtime.setCurrentPlace(here);
        }
    }


    /* operations are performed in canonical order */
    public void scan(region targetRegion, IntArray res, Operator.Scan op ) {
    	assert res == null || res instanceof IntArray;
    	assert res.distribution.equals(distribution);
    	place here = x10.lang.Runtime.runtime.currentPlace();
    	IntArray res_t = (res == null) ? null : (IntArray) res;
    	
    	try {
    	    for (Iterator it = targetRegion.iterator(); it.hasNext();) {
    	        point p = (point) it.next();
                place pl = distribution.get(p);
                x10.lang.Runtime.runtime.setCurrentPlace(pl);
                int arg1 = get(p);
                int val = op.apply(arg1);
                if (res_t != null)
                    res_t.set(val, p);
            }
    	} finally {
    	    x10.lang.Runtime.runtime.setCurrentPlace(here);
    	}
    }
    /* operations are performed in canonical order */
    public void scan( IntArray res, Operator.Scan op ) {
    	assert res == null || res instanceof IntArray;
    	assert res.distribution.equals(distribution);
    	place here = x10.lang.Runtime.runtime.currentPlace();
    	IntArray res_t = (res == null) ? null : (IntArray) res;
    	
    	try {
    	    for (Iterator it = distribution.region.iterator(); it.hasNext();) {
    	        point p = (point) it.next();
                place pl = distribution.get(p);
                x10.lang.Runtime.runtime.setCurrentPlace(pl);
                int arg1 = get(p);
                int val = op.apply(arg1);
                if (res_t != null)
                    res_t.set(val, p);
            }
    	} finally {
    	    x10.lang.Runtime.runtime.setCurrentPlace(here);
    	}
    }
    
    public void scan( IntArray res, IntArray.pointwiseOp op ) {
        assert res == null || res instanceof IntArray;
        assert res.distribution.equals(distribution);
        place here = x10.lang.Runtime.runtime.currentPlace();
        IntArray res_t = (res == null) ? null : (IntArray) res;
        try {
            for (Iterator it = distribution.region.iterator(); it.hasNext();) {
                point p = (point) it.next();
                place pl = distribution.get(p);
                x10.lang.Runtime.runtime.setCurrentPlace(pl); 
                int val = op.apply(p);
                if (res_t != null)
                    res_t.set(val, p);
            }
        } finally {
            x10.lang.Runtime.runtime.setCurrentPlace(here);
        }
    }
    
  
    public void circshift(int[] args) {
        throw new RuntimeException("TODO");
    }
    

    /**
     * copy any non-intersecting points between dest and src to
     * dest for all the povided region, which is on a particular place
     * This code must be executed at this place.
     * We assume that destArray and baseArray have the same
     * distribution
     * @param dest
     * @param src
     * @param localRegion
     */
    public void copyDisjoint(x10Array dest,x10Array src,region localRegion){
    	IntArray destArray = (IntArray)dest;
    	IntArray srcArray = (IntArray)src;
    	
    	dist destDist=destArray.distribution;
		for(Iterator it = localRegion.iterator();it.hasNext();){
			point p = (point) it.next();
			assert(Runtime.here() == destArray.distribution.get(p));
			if(!destDist.contains(p)){
				destArray.set(destArray.get(p),p);
			}
		}
    }
    
  /**
   * Copy src to dest over region localRegion, which is assumed to be on here for
   * both src and dest
   * @param dest
   * @param src
   * @param localRegion
   */
    public void copyLocalSection(x10Array dest,x10Array src,region localRegion){
    	IntArray destArray = (IntArray)dest;
    	IntArray srcArray = (IntArray)src;
		for(Iterator it = localRegion.iterator();it.hasNext();)	{
			point p = (point)it.next();
			assert srcArray.region.contains(p);
			assert destArray.region.contains(p);
			destArray.set(srcArray.get(p),p);
		}
    }
		
    

    /**
     * Generic flat access.
     */
    public abstract int set(int v, point pos);

    public abstract int set(int v, int d0); 
    
    public abstract int setOrdinal(int v, int d0);

    public abstract int set(int v, int d0, int d1);

    public abstract int set(int v, int d0, int d1, int d2);

    public abstract int set(int v, int d0, int d1, int d2, int d3);

    /**
     * Generic flat access. the leas significant dimension is pos[0].
     */
    public abstract int get(point pos);

    public abstract int get(int d0);
   
    public abstract int getOrdinal(int d0);

    public abstract int get(int d0, int d1);

    public abstract int get(int d0, int d1, int d2);

    public abstract int get(int d0, int d1, int d2, int d3);

    public Object toJava() {        

        final int[] dims_tmp = new int[distribution.rank];       
        for (int i = 0; i < distribution.rank; ++i) 
            dims_tmp[i] = distribution.region.rank(i).high() + 1;
        
        final Object ret = java.lang.reflect.Array.newInstance(Integer.TYPE, dims_tmp);
        pointwise(null, new Operator.Pointwise() {
            public int apply(point p, int arg) {
                Object handle = ret;
                int i = 0;
                for (; i < dims_tmp.length - 1; ++i) {
                    handle = java.lang.reflect.Array.get(handle, p.get(i));
                }
                java.lang.reflect.Array.setInt(handle, p.get(i), arg);
                return arg;
            }
        });
		return ret;
	}
    
    /* for debugging */
    public static void printArray(String prefix, int[][] a) {
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
} // end of IntArray
