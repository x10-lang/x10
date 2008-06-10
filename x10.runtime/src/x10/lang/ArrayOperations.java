/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on 09.09.2006
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package x10.lang;

import java.util.Iterator;
import java.io.PrintStream;

import x10.array.BooleanArray;
import x10.array.Operator;
import x10.array.sharedmemory.BooleanArray_c;
import x10.compilergenerated.Parameter1;
import java.lang.System;
/**
 * Helper routines for array manipulation.
 * This class contains only static methods, and is not intended
 * to be instantiated or subclassed.
 * 
 * @author igor
 */
public final class ArrayOperations {

	protected ArrayOperations() { }

	/* for debugging */
	public static void printArray(String prefix, boolean[][] a) {
		printArray(prefix, a, System.out);
	}

	/* for debugging */
	public static void printArray(String prefix, boolean[][] a, PrintStream out) {
	    out.print(prefix + "{");
	    for (int i = 0; i < a.length; ++i) {
	        out.print("{");
	        for (int j = 0; j < a[i].length; ++ j) {
	            out.print(a[i][j]);
	            if (j < a[i].length - 1)
	                out.print(", ");
	        }
	        out.print("}");
	        if (i < a.length - 1)
	            out.print(", ");
	    }
	    out.println("}");
	}

	/* for debugging */
	public static void printArray(String prefix, byte[][] a) {
		printArray(prefix, a, System.out);
	}

	/* for debugging */
	public static void printArray(String prefix, byte[][] a, PrintStream out) {
	    out.print(prefix + "{");
	    for (int i = 0; i < a.length; ++i) {
	        out.print("{");
	        for (int j = 0; j < a[i].length; ++ j) {
	            out.print(a[i][j]);
	            if (j < a[i].length - 1)
	                out.print(", ");
	        }
	        out.print("}");
	        if (i < a.length - 1)
	            out.print(", ");
	    }
	    out.println("}");
	}

	public static void reduction(BooleanReferenceArray a, Operator.Reduction op) {
	    place here = x10.lang.Runtime.runtime.currentPlace();
	    try {
	        dist distribution = ((x10Array)a).distribution;
			for (Iterator it = distribution.region.iterator(); it.hasNext(); ) {
	            point p = (point) it.next();
	            place pl = distribution.get(p);
	            x10.lang.Runtime.runtime.setCurrentPlace(pl);
	            boolean arg1 = a.get(p);
	            op.apply(arg1);
	        }
	    } finally {
	        x10.lang.Runtime.runtime.setCurrentPlace(here);
	    }
	}

	public static void scan(BooleanReferenceArray res, Operator.Unary op, booleanArray a) {
		BooleanArray r = ((BooleanArray) a);
		assert ((x10Array)res).distribution.equals(r.distribution);
		place here = x10.lang.Runtime.runtime.currentPlace();
		try {
		    for (Iterator it = r.distribution.region.iterator(); it.hasNext(); ) {
		        point p = (point) it.next();
		        place pl = r.distribution.get(p);
		        x10.lang.Runtime.runtime.setCurrentPlace(pl);
		        boolean arg1 = r.get(p);
		        res.set(op.apply(arg1), p);
		    }
		} finally {
		    x10.lang.Runtime.runtime.setCurrentPlace(here);
		}
	}
	public static void scan(BooleanArray res, Operator.Pointwise op, booleanArray a) {
		dist distribution = ((x10Array)a).distribution;
        assert res == null || res instanceof BooleanArray;
        assert res.distribution.equals(distribution);
        place here = x10.lang.Runtime.runtime.currentPlace();

        BooleanArray res_t = (res == null) ? null : (BooleanArray) res;
        try {
            for (Iterator it = distribution.region.iterator(); it.hasNext();) {
                point p = (point) it.next();
				place pl = distribution.get(p);
                x10.lang.Runtime.runtime.setCurrentPlace(pl);
                boolean val = op.apply(p, false);
                if (res_t != null)
                    res_t.set(val, p);
            }
        } finally {
            x10.lang.Runtime.runtime.setCurrentPlace(here);
        }
    }

	/**
	 * Return an array of B@P defined on the intersection of 
	 * the region underlying this and the parametric distribution.
	 */    
	public static /*(distribution(:rank=this.rank) D)*/ 
	BooleanReferenceArray/*(distribution.restriction(D.region)())*/
	restriction(booleanArray a, dist D)
	{
		return restriction(a, D.region);
	}
	
	public static BooleanReferenceArray restriction(booleanArray a, place P) {
		return restriction(a, ((x10Array)a).distribution.restriction(P));
	}
	
	/**
	 * Return an array of B@P defined on the intersection of the
	 * region underlying the array a and the parameter region R.
	 */
	public static /*(region(rank) R)*/
	BooleanReferenceArray/*(distribution.restriction(R)())*/
	restriction(booleanArray a, region R)
	{
		BooleanArray_c array = ((BooleanArray_c) a);
		dist dist = array.distribution.restriction(R);
		BooleanArray ret = array.newInstance(dist);
		place here = x10.lang.Runtime.runtime.currentPlace();
		try {
		    for (Iterator it = dist.iterator(); it.hasNext(); ) {
		        point p = (point) it.next();
		        place pl = dist.get(p);
		        x10.lang.Runtime.runtime.setCurrentPlace(pl);
		        ret.set(array.get(p), p);
		    }
		} finally {
		    x10.lang.Runtime.runtime.setCurrentPlace(here);
		}   
		return ret;
	}

	/**
	 * Parameterized by a distribution D of the same rank as a.distribution,
	 * and defined over a disjoint region. Take as argument an array b over
	 * D. Return an array whose distribution is the union of a.distribution
	 * and D and which takes on the value a.get(p) for p in a.region and
	 * b.get(p) for p in b.region.
	 */
	public static /*(distribution(:region.disjoint(this.region) && rank=this.rank) D)*/
	BooleanReferenceArray/*(distribution.union(other.distribution))*/
	union(booleanArray a, booleanArray b)
	{
		BooleanArray_c r = ((BooleanArray_c) a);
		dist dist = r.distribution.union(((x10Array)b).distribution);
		BooleanArray ret = r.newInstance(dist);
		place here = x10.lang.Runtime.runtime.currentPlace();
		try {
			for (Iterator it = dist.iterator(); it.hasNext(); ) {
				point p = (point) it.next();
				place pl = dist.get(p);
				x10.lang.Runtime.runtime.setCurrentPlace(pl);
				boolean val = (r.distribution.region.contains(p)) ? r.get(p) : b.get(p);
				ret.set(val, p);
			}
		} finally {
			x10.lang.Runtime.runtime.setCurrentPlace(here);
		}   
		return ret;
	}
	
	/**
	 * Update array a in place by overlaying the array b on top of it.
	 * The distribution of a array must be a subdistribution of the distribution of a.
	 * TODO: update the description of the parametric type.
	 */
	public static void update(BooleanReferenceArray a, booleanArray b) {
		assert (((x10Array)a).region.contains(((x10Array)b).region));
		place here = x10.lang.Runtime.runtime.currentPlace();
		try {
			for (Iterator it = ((x10Array)b).iterator(); it.hasNext(); ) {
				point p = (point) it.next();
				place pl = ((x10Array)a).distribution.get(p);
				x10.lang.Runtime.runtime.setCurrentPlace(pl);
				a.set(b.get(p), p);
			}
		} finally {
			x10.lang.Runtime.runtime.setCurrentPlace(here);
		}
	}
	
	/**
	 * Return the array obtained by overlaying array a on top of
	 * array b. The method is parameterized by a distribution D over the
	 * same rank. It returns an array over the distribution
	 * a.distribution.asymmetricUnion(D).
	 */
	public static /*(distribution(:rank=a.rank) D)*/ BooleanReferenceArray
	overlay(booleanArray a, booleanArray/*(D)*/ b)
	{
		BooleanArray_c r = ((BooleanArray_c) a);
		dist dist = r.distribution.overlay(((x10Array)b).distribution);
		BooleanArray ret = r.newInstance(dist);
		place here = x10.lang.Runtime.runtime.currentPlace();
		try {
			for (Iterator it = dist.iterator(); it.hasNext(); ) {
				point p = (point) it.next();
				place pl = dist.get(p);
				x10.lang.Runtime.runtime.setCurrentPlace(pl);
				boolean val = (((x10Array)b).distribution.region.contains(p)) ? b.get(p) : r.get(p);
				ret.set(val, p);
			}
		} finally {
			x10.lang.Runtime.runtime.setCurrentPlace(here);
		}   
		return ret;
	}
	
	/**
	 * Assume given BooleanArrays a and b over the given distribution.
	 * Assume given a function f: boolean -> boolean -> boolean.
	 * Return a BooleanArray with distribution dist 
	 * containing fun(a.atValue(p),b.atValue(p)) for each p in
	 * dist.region.
	 */
	public static BooleanReferenceArray lift(booleanArray left, Operator.Binary op, booleanArray right) {
		BooleanArray_c l = ((BooleanArray_c) left);
		assert ((x10Array)right).distribution.equals(l.distribution); 
		BooleanArray r = (BooleanArray)right;
		BooleanArray result = l.newInstance(l.distribution);
		place here = x10.lang.Runtime.runtime.currentPlace();
		try {
		    for (Iterator it = l.distribution.region.iterator(); it.hasNext();) {
		        point p = (point) it.next();
		        place pl = l.distribution.get(p);
		        x10.lang.Runtime.runtime.setCurrentPlace(pl);
		        result.set(op.apply(l.get(p), r.get(p)),p);
		    }
		} finally {
		    x10.lang.Runtime.runtime.setCurrentPlace(here);
		}  
		return result;
	}

	/**
	 * Assume given BooleanArray a over the given distribution.
	 * Assume given a function f: boolean -> boolean.
	 * Return a BooleanArray with distribution dist 
	 * containing fun(a.atValue(p)) for each p in
	 * dist.region.
	 */
	public static BooleanReferenceArray lift(Operator.Unary op, booleanArray arg) {
		BooleanArray_c a = ((BooleanArray_c) arg);
		BooleanArray result = a.newInstance(a.distribution);
		place here = x10.lang.Runtime.runtime.currentPlace();
		try {
		    for (Iterator it = a.distribution.region.iterator(); it.hasNext();) {
		        point p = (point) it.next();
		        place pl = a.distribution.get(p);
		        x10.lang.Runtime.runtime.setCurrentPlace(pl);
		        result.set(op.apply(a.get(p)),p);
		    }
		} finally {
		    x10.lang.Runtime.runtime.setCurrentPlace(here);
		}   
		return result;
	}

	/**
	 * Return the value obtained by reducing the given array a with the
	 * function op, which is assumed to be associative and commutative.
	 * unit should satisfy op(unit,x)=x=op(x,unit).
	 */
	public static boolean reduce(booleanArray a, Operator.Binary op, boolean unit) {
		boolean result = unit;
		place here = x10.lang.Runtime.runtime.currentPlace();
		try {
			for (Iterator it = ((x10Array)a).distribution.region.iterator(); it.hasNext();) {
				point p = (point) it.next();
				place pl = ((x10Array)a).distribution.get(p);
				x10.lang.Runtime.runtime.setCurrentPlace(pl);
				result = op.apply(result, a.get(p));
			}
		} finally {
			x10.lang.Runtime.runtime.setCurrentPlace(here);
		}   
		return result;
	}
	
	/**
	 * Return a BooleanArray with the same distribution as arg, by
	 * scanning this with the function op, and unit unit.
	 */
	public static BooleanReferenceArray/*(a.distribution)*/
	scan(booleanArray arg, Operator.Binary op, boolean unit)
	{
		BooleanArray_c a = ((BooleanArray_c) arg);
		boolean temp = unit;
		BooleanArray result = a.newInstance(a.distribution);
		place here = x10.lang.Runtime.runtime.currentPlace();
		try {
			for (Iterator it = a.distribution.region.iterator(); it.hasNext();) {
				point p = (point) it.next();
				place pl = a.distribution.get(p);
				x10.lang.Runtime.runtime.setCurrentPlace(pl);
				temp = op.apply(a.get(p), temp);
				result.set(temp, p);
			}
		} finally {
			x10.lang.Runtime.runtime.setCurrentPlace(here);
		}   
		return result;
	}

	/*
	 * Generic implementation - an array with fixed, known number of dimensions
	 * can of course do without the Iterator.
	 */
	public static void pointwise(booleanArray a, BooleanReferenceArray res, Operator.Pointwise op, booleanArray b) {
		assert ((x10Array)res).distribution.equals(((x10Array)a).distribution);
		assert ((x10Array)b).distribution.equals(((x10Array)a).distribution);
		
		place here = x10.lang.Runtime.runtime.currentPlace();
		try {
			for (Iterator it = ((x10Array)a).distribution.region.iterator(); it.hasNext(); ) {		        
				point p = (point) it.next();
				place pl = ((x10Array)a).distribution.get(p);
				x10.lang.Runtime.runtime.setCurrentPlace(pl);
				boolean arg1 = a.get(p);
				boolean arg2 = b.get(p);
				res.set(op.apply(p, arg1, arg2), p);
			} 
		} finally {
			x10.lang.Runtime.runtime.setCurrentPlace(here);
		}
	}
	
	public static void pointwise(booleanArray a, BooleanReferenceArray res, Operator.Pointwise op) {
		dist distribution = ((x10Array)a).distribution;
		assert res == null || ((x10Array)res).distribution.equals(distribution);
		place here = x10.lang.Runtime.runtime.currentPlace();
		
		try {
			for (Iterator it = distribution.region.iterator(); it.hasNext(); ) {
				point p = (point) it.next();
				place pl = distribution.get(p);
				x10.lang.Runtime.runtime.setCurrentPlace(pl);
				boolean arg1 = a.get(p);
				boolean val = op.apply(p, arg1);
				if (res != null)
					res.set(val, p);
			}
		} finally {
			x10.lang.Runtime.runtime.setCurrentPlace(here);
		}
	}

	public static boolean[] arrayInit(boolean[] arr, Operator.Pointwise init) {
		for (int i = 0; i < arr.length; i++)
			arr[i] = init.apply(point.factory.point(i), false);
		return arr;
	}

	public static byte[] arrayInit(byte[] arr, Operator.Pointwise init) {
		for (int i = 0; i < arr.length; i++)
			arr[i] = init.apply(point.factory.point(i), (byte)0);
		return arr;
	}

	public static char[] arrayInit(char[] arr, Operator.Pointwise init) {
		for (int i = 0; i < arr.length; i++)
			arr[i] = init.apply(point.factory.point(i), (char)0);
		return arr;
	}

	public static short[] arrayInit(short[] arr, Operator.Pointwise init) {
		for (int i = 0; i < arr.length; i++)
			arr[i] = init.apply(point.factory.point(i), (short)0);
		return arr;
	}

	public static int[] arrayInit(int[] arr, Operator.Pointwise init) {
		for (int i = 0; i < arr.length; i++)
			arr[i] = init.apply(point.factory.point(i), 0);
		return arr;
	}

	public static long[] arrayInit(long[] arr, Operator.Pointwise init) {
		for (int i = 0; i < arr.length; i++)
			arr[i] = init.apply(point.factory.point(i), (long)0);
		return arr;
	}

	public static float[] arrayInit(float[] arr, Operator.Pointwise init) {
		for (int i = 0; i < arr.length; i++)
			arr[i] = init.apply(point.factory.point(i), (float)0.);
		return arr;
	}

	public static double[] arrayInit(double[] arr, Operator.Pointwise init) {
		for (int i = 0; i < arr.length; i++)
			arr[i] = init.apply(point.factory.point(i), 0.);
		return arr;
	}

	public static java.lang.Object[] arrayInit(java.lang.Object[] arr, Operator.Pointwise init) {
		for (int i = 0; i < arr.length; i++)
			arr[i] = init.apply(point.factory.point(i), (Parameter1)null);
		return arr;
	}
}
