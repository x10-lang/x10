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


// Base class of all X10 ref objects -- should be generated, but we need this class to get Box to compile.
public class Ref implements Any {
    
    public Ref() {}
     
    // XTENLANG-1858: every Java class that could be an (non-static) inner class must have constructors with the outer instance parameter
    public Ref(Object out$) {}

    public static RuntimeType<Ref> _RTT = new RuntimeType<Ref>(Ref.class) {
        @Override
        public String typeName() {
            return "x10.lang.Object";
        }
    };
    public RuntimeType<?> getRTT() {return _RTT;}
    public Type<?> getParam(int i) {return null;}
}
