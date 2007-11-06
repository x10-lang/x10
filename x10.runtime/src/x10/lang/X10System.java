package x10.lang;

import java.util.Iterator;

public class X10System {
	
	/**
	 * Copy the i'th element in the enumeration order of the source array into the
	 * i'th element in the enumeration order of the target array. The two arrays must
	 * be of the same size.
	 * @param src
	 * @param dest
	 */
	public static void arrayCopy(doubleArray src, doubleArray dest) {
		double[] s = src.getBackingArray(), d  = dest.getBackingArray();
		for (int i=0; i < s.length; i++) d[i]=s[i];
	}
	/**
	 * Copy into each element
	 * @param src
	 * @param srcR
	 * @param dest
	 * @param destR
	 */
	public static void arrayCopy(doubleArray src, region srcR, DoubleReferenceArray dest, region destR) {
		int count=0;
		point srcOrd=srcR.startPoint();
		for (Iterator it = destR.iterator(); it.hasNext(); ) {
			point p = (point) it.next();
			dest.set(src.get(srcR.coord(count)), p);
			count++;
		}
	}

}
