/*
 * Created on Jun 1st, 2005
 */
package x10.array.sharedmemory;

import x10.array.Helper;
import x10.lang.Runtime;
import x10.lang.dist;
import x10.runtime.Configuration;

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
    
    public int set(int v, int d0) {return set(v,d0,true,true);}
    	  
    public int set(int v, int d0,boolean chkPl,boolean chkAOB) {
       if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
           Runtime.hereCheckPlace(distribution.get(d0));     
       int	theIndex = Helper.rawordinal(region,d0,chkAOB);
       return arr_.setInt(v,theIndex);
    }
    
    public int get(int v, int d0) {return get(d0,true,true);}
    public int get(int d0,boolean chkPl,boolean chkAOB) {
        if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0));
       	int	theIndex = Helper.rawordinal(region,d0,chkAOB);
    	return arr_.getInt(theIndex);  	
    } 
    
    public int set(int v, int d0,int d1) {return set(v,d0,d1,true,true);}
    
    public int set(int v, int d0, int d1,boolean chkPl,boolean chkAOB) {
        if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1));     
        int	theIndex = Helper.rawordinal(region,d0,d1,chkAOB);
    	return arr_.setInt(v,theIndex);
    }
    
    public int get(int v, int d0,int d1) {return get(d0,d1,true,true);}
    public int get(int d0, int d1,boolean chkPl,boolean chkAOB) {
        if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1));     
        int	theIndex = Helper.rawordinal(region,d0,d1,chkAOB);
    	return arr_.getInt(theIndex);  	
    } 
    
    public int set(int v, int d0,int d1,int d2) {return set(v,d0,d1,d2,true,true);}
    
    public int set(int v, int d0, int d1, int d2,boolean chkPl,boolean chkAOB) {
        if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2));     
        int	theIndex = Helper.rawordinal(region,d0,d1,d2,chkAOB);
        return arr_.setInt(v,theIndex);
    }
    
    public int get(int v, int d0,int d1,int d2) {return get(d0,d1,d2,true,true);}
    
    public int get(int d0, int d1, int d2,boolean chkPl,boolean chkAOB) {
        if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2));     
        int	theIndex = Helper.rawordinal(region,d0,d1,d2,chkAOB);
    	return arr_.getInt(theIndex);  	
    } 
    

    public int set(int v, int d0,int d1,int d2,int d3) {return set(v,d0,d1,d2,d3,true,true);}
      
    public int set(int v, int d0, int d1, int d2,int d3,boolean chkPl,boolean chkAOB) {
        if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2, d3));     
        int	theIndex = Helper.rawordinal(region,d0,d1,d2,d3,chkAOB);
    	return arr_.setInt(v,theIndex);
    }
    
    public int get(int v, int d0,int d1,int d2,int d3) {return get(d0,d1,d2,d3,true,true);}
    
    public int get(int d0, int d1, int d2, int d3,boolean chkPl,boolean chkAOB) {
        if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2, d3));     
        int	theIndex = Helper.rawordinal(region,d0,d1,d2,d3,chkAOB);
    	return arr_.getInt(theIndex);  	
    } 
}