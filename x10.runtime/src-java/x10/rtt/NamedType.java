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

package x10.rtt;

import x10.x10rt.DeserializationDispatcher;
import x10.x10rt.X10JavaDeserializer;
import x10.x10rt.X10JavaSerializable;
import x10.x10rt.X10JavaSerializer;

import java.io.IOException;

public class NamedType<T> extends RuntimeType<T> implements X10JavaSerializable {

	private static final long serialVersionUID = 1L;
    static {
            x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, NamedType.class);
    }
    private static short _serialization_id;
    
    public String typeName;

    // Just for allocation
    public NamedType() {
        super();
    }
    
    public NamedType(String typeName, Class<?> c) {
        super(c);
        this.typeName = typeName;
    }

    public NamedType(String typeName, Class<?> c, Variance[] variances) {
        super(c, variances);
        this.typeName = typeName;
    }

    public NamedType(String typeName, Class<?> c, Type<?>[] parents) {
        super(c, parents);
        this.typeName = typeName;
    }
    
    public NamedType(String typeName, Class<?> c, Variance[] variances, Type<?>[] parents) {
        super(c, variances, parents);
        this.typeName = typeName;
    }

    @Override
    public String typeName() {
        return typeName;
    }

    @Override
    public void $_serialize(X10JavaSerializer serializer) throws IOException {
        super.$_serialize(serializer);
        serializer.writeClassID(typeName);
    }

    @Override
    public short $_get_serialization_id() {
        return _serialization_id;
    }

    public static void $_set_serialization_id(short id) {
         _serialization_id = id;
    }

    public static X10JavaSerializable $_deserializer(X10JavaDeserializer deserializer) throws IOException {
        NamedType namedType = new NamedType();
        deserializer.record_reference(namedType);
		return $_deserialize_body(namedType, deserializer);
	}

    public static X10JavaSerializable $_deserialize_body(NamedType nt, X10JavaDeserializer deserializer) throws IOException {
        RuntimeType.$_deserialize_body(nt, deserializer);
        short classId = deserializer.readShort();
        nt.typeName = DeserializationDispatcher.getClassNameForID(classId, deserializer);
        return nt;
    }
}
