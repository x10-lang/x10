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

import x10.x10rt.X10JavaDeserializer;
import x10.x10rt.X10JavaSerializable;
import x10.x10rt.X10JavaSerializer;

import java.io.IOException;

public class NamedType<T> extends RuntimeType<T> implements X10JavaSerializable {

	private static final long serialVersionUID = 1L;
    private static final int _serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(NamedType.class.getName());
    
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

    public void _serialize(X10JavaSerializer serializer) throws IOException {
        serializer.write(typeName);
        serializer.write(super.getImpl().getName());
    }

    public int _get_serialization_id() {
        return _serialization_id;
    }

    public static X10JavaSerializable _deserializer(X10JavaDeserializer deserializer) throws IOException {
        NamedType namedType = new NamedType();
        deserializer.record_reference(namedType);
		return _deserialize_body(namedType, deserializer);
	}

    public static X10JavaSerializable _deserialize_body(NamedType nt, X10JavaDeserializer deserializer) throws IOException {
        String name = deserializer.readString();
        try {
             Class<?> aClass = Class.forName(deserializer.readString());
            nt.typeName = name;
            nt.impl = aClass;
            return nt;
        } catch (ClassNotFoundException e) {
            // This should not happen though
            throw new RuntimeException(e);
        }
    }

}
