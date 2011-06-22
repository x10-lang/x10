/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2011.
 */

package x10.rtt;

import x10.core.fun.Fun_0_1;


public class StringType extends RuntimeType<String> {

    private static final long serialVersionUID = 1L;

    // make sure deserialized RTT object is not duplicated
    private Object readResolve() throws java.io.ObjectStreamException {
        return Types.STRING;
    }

    public StringType() {
        super(String.class,
            new Type[] {
                new ParameterizedType(Fun_0_1.$RTT, Types.INT, Types.CHAR),
                new ParameterizedType(Types.COMPARABLE, UnresolvedType.THIS)
            }
        );
    }
    
    @Override
    public boolean instanceOf(Object obj) {
        // rules for String boxing currently are not straightforward,
        // so we accept both unboxed (java.lang) and boxed (x10.core) objects.
        return obj instanceof java.lang.String || obj instanceof x10.core.String;
    }
    
    @Override
    public String typeName() {
        return "x10.lang.String";
    }
    
}
