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

import x10.serialization.SerializationConstants;



public final class StringType extends RuntimeType<java.lang.String> {

    private static final long serialVersionUID = 1L;

    // make sure deserialized RTT object is not duplicated
    private Object readResolve() throws java.io.ObjectStreamException {
        return Types.STRING;
    }
    @Override
    public short $_get_serialization_id() {
        return SerializationConstants.RTT_STRING_ID;
    }

    public StringType() {
        super(java.lang.String.class,
            new Type[] {
                ParameterizedType.make(Types.COMPARABLE, UnresolvedType.THIS)
            }
        );
    }
    
    @Override
    public boolean isInstance(Object obj) {
        return obj instanceof java.lang.String;
    }
    
    @Override
    public java.lang.String typeName() {
        return "x10.lang.String";
    }
}
