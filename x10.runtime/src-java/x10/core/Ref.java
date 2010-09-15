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


// Base class of all X10 ref objects -- should be generated, but we need this class to get Box to compile.
public class Ref implements Any {
    
    public Ref() {
    }
     
    public boolean equals(Object o) {
        return this == o;
    }
    
    public static String typeName(Object obj) {
        String s;
        if (obj instanceof Any) {
            s = ((Any) obj).getRTT().typeName(obj);
        } else if (Types.getNativeRepRTT(obj) != null) {
            s = Types.getNativeRepRTT(obj).typeName();
        } else {
            // Note: for java classes that don't have RTTs
            s = obj.getClass().toString().substring("class ".length());
            if ("java.lang.Object".equals(s)) {
                // x10.lang.Object is @NativeRep'ed to java.lang.Object and does not have RTT
                s = "x10.lang.Object";
            }
        }
        return s;
    }
    
    public static RuntimeType<Ref> _RTT = new RuntimeType<Ref>(Ref.class);
    public RuntimeType<?> getRTT() {return _RTT;}
    public Type<?> getParam(int i) {return null;}
}
