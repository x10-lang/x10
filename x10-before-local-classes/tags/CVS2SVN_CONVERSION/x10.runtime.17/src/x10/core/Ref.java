/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.core;

import x10.runtime.X10Thread;

// Base class of all X10 ref objects -- should be generated, but we need this class to get Box to compile.
public class Ref {
    public final int placeId;
    
    public Ref() {
        Thread t = Thread.currentThread();
        if (t instanceof X10Thread) {
        	placeId = ((X10Thread) t).getPlaceId();
        } else {
        	placeId = 0;
        }
    }
    
    public final int placeId() {
        return placeId;
     }
     
    public final Integer placeId$() {
        return placeId();
     }
     
    public String toString$() {
    	return toString();
    }
    
    public Boolean equals$(Object o) {
    	return equals(o);
    }
    
    public Integer hashCode$() {
    	return hashCode();
    }
}
