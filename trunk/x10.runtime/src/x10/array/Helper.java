/*
 * Created on Oct 20, 2004
 */
package x10.array;

/**
 * @author: cmd
 * Ideally this would be in the abstract interface for region,however we don't
 * want to introduce integer indicies--points shall remain as only method of array
 * dereferencing. 
 */

import x10.array.MultiDimRegion;
import x10.lang.dist;
import x10.lang.region;



public class Helper {
	public final static boolean performBoundsCheck=true;
	public final static boolean simpleRangeCheck=false;
	
    final static void checkBounds(region r,int d0){
	    try {
	        if (r instanceof MultiDimRegion && r.isConvex()) {
	            MultiDimRegion mdr = (MultiDimRegion)r;
	            int lowd0 = mdr.rank(0).low();		
	            int highd0 = mdr.rank(0).high();				
	            if (d0 < lowd0 || d0 > highd0) {
	            	   System.out.println("IndexError. d0=|" + d0 + "|, lowd0 = " + lowd0 + "|, highd0=|" + highd0 + r);
	                throw new ArrayIndexOutOfBoundsException();
	            }
	        } else {
	            final int index[] = {d0};
	            if(!r.contains(index)) {
	                System.out.println("IndexError=|" + d0 + "|, r = " + r);
	                throw new ArrayIndexOutOfBoundsException();
	            }
	        }
	    } catch (UnsupportedOperationException e) {
	        throw new ArrayIndexOutOfBoundsException();
	    }
	}
    
	final private static void rangeCheck(region r,int index){
		if(index < 0 || index >= r.size()) throw new ArrayIndexOutOfBoundsException();
		
	}
	
		
	 public static int ordinal(dist d,int d0){
		region r = d.region;
		int index;
		assert (r.rank == 1);
		if(performBoundsCheck) checkBounds(r,d0);
		try {
			if(!r.isConvex()){
				index = r.ordinal(x10.array.point_c.factory.point(d0));
			}
			else {
				index = d0 - r.rank(0).low();
				if (simpleRangeCheck) rangeCheck(r,index);
			}          
		} catch (UnsupportedOperationException e) {
            throw new ArrayIndexOutOfBoundsException();
        }
 
		return index;
	}
	
	 public static int rawordinal(region r,int d0){
		assert (r.rank == 1);
		if(performBoundsCheck) checkBounds(r,d0);
		if(simpleRangeCheck) rangeCheck(r,d0);
		return d0;
	}
	
	final static void checkBounds(region r,int d0,int d1){
	    try {
	        if (r instanceof MultiDimRegion && r.isConvex()) {	           
	            MultiDimRegion mdr = (MultiDimRegion) r;
	            int lowd0 = mdr.rank(0).low();
	            int lowd1 = mdr.rank(1).low();	            
	            int highd0 = mdr.rank(0).high();
	            int highd1 = mdr.rank(1).high();	            
	            if(d0 < lowd0 || d0 > highd0 ||
	                    d1 < lowd1 || d1 > highd1){
	                throw new ArrayIndexOutOfBoundsException();
	            }
	        } else {
	            final int index[] = {d0,d1};
	            
	            if(!r.contains(index)) 
	                throw new ArrayIndexOutOfBoundsException();
	        }
	    } catch (UnsupportedOperationException e) {
	        throw new ArrayIndexOutOfBoundsException();
	    }       	   
	}
    
	 public static int ordinal(dist d,int d0,int d1){
	    int index;
	    region r = d.region;
	    assert (r.rank == 2);
	    if (performBoundsCheck) checkBounds(r,d0,d1);
	    try {
	    	if(!r.isConvex()){
	    		index = r.ordinal(x10.array.point_c.factory.point(d0,d1));
	    	}
	    	else {
	    		d0 -= r.rank(0).low();
	    		d1 -= r.rank(1).low();
	    		
	    		index = d1+ d0*r.rank(1).size();
	    		if(simpleRangeCheck) rangeCheck(r,index);
	    	}
	    } catch (UnsupportedOperationException e) {
	        throw new ArrayIndexOutOfBoundsException();
	    }
	   
	    return index;
	}
	
	final public static int rawordinal(region r,int d0,int d1){
		assert (r.rank == 2);
		if (performBoundsCheck) checkBounds(r,d0,d1);
		
		int index = d1+ d0 * r.rank(1).size();
		if(simpleRangeCheck) rangeCheck(r,index);
		return index;
	}
	
	final static void checkBounds(region r,int d0,int d1, int d2){
	    try {
	        if (r instanceof MultiDimRegion && r.isConvex()) {
	        	
	            MultiDimRegion mdr = (MultiDimRegion)r;
	            int lowd0 = mdr.rank(0).low();
	            int lowd1 = mdr.rank(1).low();
	            int lowd2 = mdr.rank(2).low();
	            int highd0 = mdr.rank(0).high();
	            int highd1 = mdr.rank(1).high();
	            int highd2 = mdr.rank(2).high();
	            
	            if(d0 < lowd0 || d0 > highd0 ||
	                    d1 < lowd1 || d1 > highd1 ||
	                    d2 < lowd2 || d2 > highd2){
	                throw new ArrayIndexOutOfBoundsException();
	                
	            }
	        } else {
	            final int index[] = {d0,d1,d2};
	            if (!r.contains(index)) 
	                throw new ArrayIndexOutOfBoundsException();
	        }
	    } catch (UnsupportedOperationException e) {
	        throw new ArrayIndexOutOfBoundsException();
	    }		
	}
    
	 public static int ordinal(dist d,int d0,int d1, int d2){
	    int index;
	    region r = d.region;
	    assert (r.rank == 3);
	    if (performBoundsCheck) checkBounds(r,d0,d1,d2);
	    
	    try {
	    	if(!r.isConvex()){
	    		index = r.ordinal(x10.array.point_c.factory.point(d0,d1,d2));
	    	}
	    	else {
	    		
	    		d0 -= r.rank(0).low();
	    		d1 -= r.rank(1).low();
	    		d2 -= r.rank(2).low();
	    		
	    		int d1Size=r.rank(2).size();
	    		int d0Size=d1Size * r.rank(1).size();
	    		
	    		index = d2+ d1*d1Size+ d0*d0Size;
	    		if(simpleRangeCheck) rangeCheck(r,index);
	    	}
	    } catch (UnsupportedOperationException e) {
	        throw new ArrayIndexOutOfBoundsException();
	    }
	   
	    return index;
	}
    
	final public static int rawordinal(region r,int d0,int d1, int d2){
		assert (r.rank == 3);
		//if(performBoundsCheck)checkBounds(r,d0,d1,d2);
		
		int d1Size=r.rank(2).size();
		int d0Size=d1Size * r.rank(1).size();
		
		int index = d2+ d1*d1Size+ d0*d0Size;
		if(simpleRangeCheck) rangeCheck(r,index);
		return index;
	}
	
	final static void checkBounds(region r,int d0,int d1, int d2,int d3){
	    try {
	        if (r instanceof MultiDimRegion && r.isConvex()) {
	            MultiDimRegion mdr = (MultiDimRegion)r;
	            int lowd0 = mdr.rank(0).low();
	            int lowd1 = mdr.rank(1).low();
	            int lowd2 = mdr.rank(2).low();
	            int lowd3 = mdr.rank(3).low();
	            int highd0 = mdr.rank(0).high();
	            int highd1 = mdr.rank(1).high();
	            int highd2 = mdr.rank(2).high();
	            int highd3 = mdr.rank(3).high();
	            if (d0 < lowd0 || d0 > highd0 ||
	                    d1 < lowd1 || d1 > highd1 ||
	                    d2 < lowd2 || d2 > highd2 ||
	                    d3 < lowd3 || d3 > highd3){
	                throw new ArrayIndexOutOfBoundsException();
	            }
	        }
	        else {
	            final int index[] = {d0,d1,d2,d3};
	            if(!r.contains(index)) 
	                throw new ArrayIndexOutOfBoundsException();
	        } 
	    } catch (UnsupportedOperationException e) {
	        throw new ArrayIndexOutOfBoundsException();
	    }
	}
    
	 public static int ordinal(dist d,int d0,int d1, int d2,int d3){
	    int index;
	    region r = d.region;
	    assert (r.rank == 4);	    
	    if (performBoundsCheck) checkBounds(r,d0,d1,d2,d3);
	    
        try {
        	if(!r.isConvex()){
        		index = r.ordinal(x10.array.point_c.factory.point(d0,d1,d2,d3));
        	}
        	else {
        		d0 -= r.rank(0).low();
        		d1 -= r.rank(1).low();
        		d2 -= r.rank(2).low();
        		d3 -= r.rank(3).low();
        		int d2Size=r.rank(3).size();
        		int d1Size=d2Size * r.rank(2).size();
        		int d0Size=d1Size * r.rank(1).size();
        		
        		index = d3+ d2*d2Size + d1*d1Size + d0*d0Size;
        		if(simpleRangeCheck) rangeCheck(r,index);
        	}
	    } catch (UnsupportedOperationException e) {
	        throw new ArrayIndexOutOfBoundsException();
	    }
	  
	    return index;
	}
	
	final public static int rawordinal(region r,int d0,int d1, int d2,int d3){
		assert (r.rank == 4);
		
		if(performBoundsCheck) checkBounds(r,d0,d1,d2,d3);
		
		int d2Size=r.rank(3).size();
		int d1Size=d2Size * r.rank(2).size();
		int d0Size=d1Size * r.rank(1).size();
		int index = d3+ d2*d2Size + d1*d1Size + d0*d0Size;
		if(simpleRangeCheck) rangeCheck(r,index);
		return index;
	}
}