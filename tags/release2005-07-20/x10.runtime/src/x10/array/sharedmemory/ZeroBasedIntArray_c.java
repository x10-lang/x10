/*
 * Created on Jun 1st, 2005
 */
package x10.array.sharedmemory;

import x10.array.sharedmemory.Helper;
import x10.lang.dist;

/**
 * @author CMD
 */
public class ZeroBasedIntArray_c extends IntArray_c { 

   public ZeroBasedIntArray_c( dist d, int c, boolean safe, boolean mutable) {
   	super(d,c,safe,mutable);	  
    }
   
    public ZeroBasedIntArray_c( dist d, int c) {
	super(d,c);
    }
   
     public ZeroBasedIntArray_c( dist d, int c, boolean mutable ) {
    	super( d, c, true, mutable);
    }
   
     protected ZeroBasedIntArray_c(Distribution_c d, boolean safe) {
     super(d,safe);
   }
   
   public ZeroBasedIntArray_c( dist d, int[] a, boolean safe, 
                                    boolean mutable ) {
   super(d,a,safe,mutable);
   }
    
     public int set(int v, int d0) {
    	int	theIndex = Helper.rawordinal(region,d0);
    	return arr_.setInt(v,theIndex);
    }
    
    public int get(int d0) {
       	int	theIndex = Helper.rawordinal(region,d0);
    	return arr_.getInt(theIndex);  	
    } 
    public int set(int v, int d0, int d1) {
    	int	theIndex = Helper.rawordinal(region,d0,d1);
    	return arr_.setInt(v,theIndex);
    }
    
    public int get(int d0, int d1) {
       	int	theIndex = Helper.rawordinal(region,d0,d1);
    	return arr_.getInt(theIndex);  	
    } 
    
 public int set(int v, int d0, int d1, int d2) {
    	int	theIndex = Helper.rawordinal(region,d0,d1,d2);
    	return arr_.setInt(v,theIndex);
    }
    
    public int get(int d0, int d1, int d2) {
       	int	theIndex = Helper.rawordinal(region,d0,d1,d2);
    	return arr_.getInt(theIndex);  	
    } 
    
    public int set(int v, int d0, int d1, int d2,int d3) {
    	int	theIndex = Helper.rawordinal(region,d0,d1,d2,d3);
    	return arr_.setInt(v,theIndex);
    }
    
    public int get(int d0, int d1, int d2, int d3) {
       	int	theIndex = Helper.rawordinal(region,d0,d1,d2,d3);
    	return arr_.getInt(theIndex);  	
    } 
}