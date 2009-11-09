/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.core;

import x10.runtime.impl.java.Thread;
import x10.types.RuntimeType;
import x10.types.Type;


// Base class of all X10 ref objects -- should be generated, but we need this class to get Box to compile.
public class Ref implements Any {
    public final int location;
    
    public Ref() {
       	location = Thread.currentThread().location();
    }
     
    /** Note: since this is final, it's important that the method name not conflict with any methods introduced by subclasses of Ref in X10 code. */
    public final int location() {
        return location;
    }

    public boolean equals(Object o) {
	return this == o;
    }

    
    public static class RTT extends RuntimeType<Ref> {
    	public static final RTT it = new RTT();

    	public RTT() {
            super(Ref.class);
        }

        @Override
        public boolean instanceof$(Object o) {
            return o instanceof Ref;
        }
    }

    public Ref box$() {
        return this;
    }

    public boolean at(int p) { return location == p;}
    public boolean at(Ref r) { return location == r.location();}

    public static boolean at(Object obj, int p) {
        if (obj instanceof Ref) {
            return ((Ref)obj).at(p);
        } else {
            return p == Thread.currentThread().location();
        }
    }

    // Called in those cases in which the X10 class of 
    // obj2 is either a struct (e.g. Place) or nativeRep'ed.
    public static boolean at(Object obj1, Object obj2) {
        if (obj1 instanceof Ref) {
            if (obj2 instanceof Ref) {
                return ((Ref)obj1).location == ((Ref)obj2).location;
            } else {
                return ((Ref)obj1).location == Thread.currentThread().location();
            }
        } else {
            if (obj2 instanceof Ref) {
                return ((Ref)obj2).location == Thread.currentThread().location();
            } else {
                return true;
            }
        }
    }

    public static int location(Object obj) {
        if (obj instanceof Ref) {
            return ((Ref)obj).location();
        } else {
            return Thread.currentThread().location();
        }
    }
    public static String typeName(Object obj) {
    	return obj.getClass().toString();
    }
}
