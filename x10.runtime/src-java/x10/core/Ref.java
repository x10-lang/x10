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
    
    public Ref() {
    }
     
    public boolean equals(Object o) {
	return this == o;
    }
    
    public Ref box$() {
        return this;
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
    public RuntimeType<?> getRTT() {return _RTT;}
    public Type<?> getParam(int i) {return null;}
}
