/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.core;

import x10.rtt.RuntimeType;
import x10.rtt.Type;
import x10.runtime.impl.java.Thread;


// Base class of all X10 ref objects -- should be generated, but we need this class to get Box to compile.
public class Ref implements Any {
    public final int home;
    
    public Ref() {
       	home = Thread.currentThread().home();
    }
     
    /** Note: since this is final, it's important that the method name not conflict with any methods introduced by subclasses of Ref in X10 code. */
    public final int home() {
        return home;
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

    public boolean at(int p) { return home == p;}
   
    public boolean at(Ref r) { return home == r.home();}

    public static boolean at(Object obj, int p) {
        if (obj instanceof Ref) {
            return ((Ref)obj).at(p);
        } else {
            return p == Thread.currentThread().home();
        }
    }

    // Called in those cases in which the X10 class of 
    // obj2 is either a struct (e.g. Place) or nativeRep'ed.
    public static boolean at(Object obj1, Object obj2) {
        if (obj1 instanceof Ref) {
            if (obj2 instanceof Ref) {
                return ((Ref)obj1).home == ((Ref)obj2).home;
            } else {
                return ((Ref)obj1).home == Thread.currentThread().home();
            }
        } else {
            if (obj2 instanceof Ref) {
                return ((Ref)obj2).home == Thread.currentThread().home();
            } else {
                return true;
            }
        }
    }

    public static int home(Object obj) {
        if (obj instanceof Ref) {
            return ((Ref)obj).home();
        } else {
            return Thread.currentThread().home();
        }
    }
    public static String typeName(Object obj) {
        String s = obj.getClass().toString();
    	return s.equals("class java.lang.Object") ? "class x10.lang.Object" : s;
    }
}
