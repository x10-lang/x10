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

public class ObjectType extends RuntimeType<x10.core.RefI> {

    private static final long serialVersionUID = 1L;
//    private static short _serialization_id;x10.x10rt.DeserializationDispatcher.addDispatcher(DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, ObjectType.class.getName());

    // make sure deserialized RTT object is not duplicated
    private Object readResolve() throws java.io.ObjectStreamException {
        return Types.OBJECT;
    }

    public ObjectType() {
        super(x10.core.RefI.class,
            new Type[] { Types.ANY }
        );
    }
    
    @Override
    public String typeName() {
        return "x10.lang.Object";
    }
    
    // for shortcut
    @Override
    public boolean isSubtype(Type<?> o) {
        return o == Types.OBJECT || o == Types.ANY;
    };

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
//    public static X10JavaSerializable $_deserialize_body(ObjectType o, X10JavaDeserializer deserializer) throws IOException {
//        ObjectType objectType = (ObjectType) Types.OBJECT;
//        deserializer.record_reference(objectType);
//        return objectType;
//    }
}
