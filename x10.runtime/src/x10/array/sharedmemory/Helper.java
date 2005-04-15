/*
 * Created on Oct 20, 2004
 */
package x10.array.sharedmemory;

/**
 * @author: cmd
 * Ideally this would be in the abstract interface for region,however we don't
 * want to introduce integer indicies--points shall remain as only method of array
 * dereferencing. 
 */

import x10.lang.region;


public class Helper {
	final public static int ordinal(region r,int d0){
		assert (r.rank == 1);
		d0 -= r.rank(0).low();
		return d0;
	}
	
	final public static int ordinal(region r,int d0,int d1){
		assert (r.rank == 2);
		d0 -= r.rank(0).low();
		d1 -= r.rank(1).low();
		return d1+ d0*r.rank(1).size();
	}
	final public static int ordinal(region r,int d0,int d1, int d2){
		assert (r.rank == 3);
		d0 -= r.rank(0).low();
		d1 -= r.rank(1).low();
		d2 -= r.rank(2).low();
		int d1Size=r.rank(2).size();
		int d0Size=d1Size * r.rank(1).size();
		return d2+ d1*d1Size+ d0*d0Size;
	}
	final public static int ordinal(region r,int d0,int d1, int d2,int d3){
		assert (r.rank == 4);
		d0 -= r.rank(0).low();
		d1 -= r.rank(1).low();
		d2 -= r.rank(2).low();
		d3 -= r.rank(3).low();
		int d2Size=r.rank(3).size();
		int d1Size=d2Size * r.rank(2).size();
		int d0Size=d1Size * r.rank(1).size();
		return d3+ d2*d2Size + d1*d1Size + d0*d0Size;
		
	}
}