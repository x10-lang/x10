package x10.lang;

import java.util.Iterator;
import static java.lang.System.*;
public class X10System {
	
	/**
	 * Copy the i'th element in the enumeration order of the source array into the
	 * i'th element in the enumeration order of the target array. The two arrays must
	 * be of the same size.
	 * @param src
	 * @param dest
	 */
	public static void arraycopy(doubleArray src, doubleArray dest) {
		double[] s = src.getBackingArray(), d  = dest.getBackingArray();
		System.arraycopy(s,0,d,0,s.length);
		
	}
	public static void arraycopy(doubleArray src, int srcPos, doubleArray dest, int destPos, int length) {
		double[] s = src.getBackingArray(), d  = dest.getBackingArray();
		System.arraycopy(s,srcPos,d,destPos,length);
		
	}
	/**
	 * Copy into each element
	 * @param src
	 * @param srcR
	 * @param dest
	 * @param destR
	 */
	public static void arraycopy(doubleArray src, region srcR, DoubleReferenceArray dest, region destR) {
		int count=0;
		point srcOrd=srcR.startPoint();
		for (Iterator it = destR.iterator(); it.hasNext(); ) {
			point p = (point) it.next();
			dest.set(src.get(srcR.coord(count)), p);
			count++;
		}
	}

}
