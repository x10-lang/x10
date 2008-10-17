/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.core;

import x10.runtime.Place;

// Base class of all X10 ref objects -- should be generated, but we need this class to get Box to compile.
public class Ref {
    public final Place location;
    
    public Ref() {
        location = x10.runtime.Runtime.runtime.here();
    }
    
    /** Property accessor */
    public final Place location() {
        return location;
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
