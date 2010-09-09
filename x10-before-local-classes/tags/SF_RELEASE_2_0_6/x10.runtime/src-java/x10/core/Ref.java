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
import x10.rtt.Types;
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
        String s;
        if (obj instanceof Any) {
            // unsigned numbers come here
            s = ((Any) obj).getRTT().typeName(obj);
        } else if (obj instanceof Number) {
            // @NativeRep'ed numeric primitive type
            s = Types.getNativeRepRTT(obj).typeName();
        } else if (obj instanceof String) {
            // @NativeRep'ed String type
            s = Types.STR.typeName();
        } else {
            s = obj.getClass().toString().substring("class ".length());
            // TODO: create mapping table of @NativeRep'ed type to X10 type and use it.
            // TODO: unsigned types
            if (s.startsWith("java.")) {
                if (s.startsWith("java.io.")) {
                    if (s.equals("java.io.FileInputStream")) {
                        s = "x10.io.FileReader";
                    } else if (s.equals("java.io.FileOutputStream")) {
                        s = "x10.io.FileWriter";
                    } else if (s.equals("java.io.InputStream")) {
                        s = "x10.io.InputStreamReader";
                    } else if (s.equals("java.io.OutputStream")) {
                        s = "x10.io.OutputStreamWriter";
                    } else {
                        s = "x10." + s.substring("java.".length());
                    }
                } else if (s.startsWith("java.lang.Integer")) {
                    s = "x10.lang.Int";
                } else {
                    s = "x10." + s.substring("java.".length());
                }
            }
        }
        return s;
    }
    
    public static RuntimeType<Ref> _RTT = new RuntimeType<Ref>(Ref.class);
    public RuntimeType getRTT() {return _RTT;}
    public Type<?> getParam(int i) {return null;}
}
