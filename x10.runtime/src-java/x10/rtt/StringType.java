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

import x10.x10rt.X10JavaDeserializer;
import x10.x10rt.X10JavaSerializable;
import x10.x10rt.X10JavaSerializer;

import java.io.IOException;


public final class StringType extends RuntimeType<java.lang.String> {
//public final class StringType extends RuntimeType<java.lang.String> implements X10JavaSerializable {

    private static final long serialVersionUID = 1L;
//    private static final short _serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, StringType.class.getName());

    // make sure deserialized RTT object is not duplicated
    private Object readResolve() throws java.io.ObjectStreamException {
        return Types.STRING;
    }

    public StringType() {
        super(String.class,
            new Type[] {
                ParameterizedType.make(Types.COMPARABLE, UnresolvedType.THIS)
            }
        );
    }
    
    @Override
    public boolean isInstance(Object obj) {
        // TODO remove boxing of String
//        // rules for String boxing currently are not straightforward,
//        // so we accept both unboxed (java.lang) and boxed (x10.core) objects.
//        return obj instanceof java.lang.String || obj instanceof x10.core.String;
        return obj instanceof java.lang.String;
    }
    
    @Override
    public String typeName() {
        return "x10.lang.String";
    }

//    public void $_serialize(X10JavaSerializer serializer) throws IOException {
//    }
//
//    public short $_get_serialization_id() {
//        return _serialization_id;
//    }
//
//    public static X10JavaSerializable $_deserializer(X10JavaDeserializer deserializer) throws IOException {
//		return $_deserialize_body(null, deserializer);
//	}
//
//    public static X10JavaSerializable $_deserialize_body(StringType s, X10JavaDeserializer deserializer) throws IOException {
//        StringType stringType = (StringType) Types.STRING;
//        deserializer.record_reference(stringType);
//        return stringType;
//    }
}
