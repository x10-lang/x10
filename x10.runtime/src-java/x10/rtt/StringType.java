/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.rtt;

import x10.serialization.SerializationConstants;



public final class StringType extends RuntimeType<String> {

    @Override
    public short $_get_serialization_id() {
        return SerializationConstants.RTT_STRING_ID;
    }

    public StringType() {
        super(String.class,
            new Type[] {
                ParameterizedType.make(Types.COMPARABLE, UnresolvedType.THIS),
                Types.CHAR_SEQUENCE
            }
        );
    }
    
    @Override
    public boolean isInstance(Object obj) {
        return obj instanceof String;
    }
    
    @Override
    public String typeName() {
        return "x10.lang.String";
    }
}
