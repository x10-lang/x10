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

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import x10.serialization.X10JavaDeserializer;
import x10.serialization.X10JavaSerializable;
import x10.serialization.X10JavaSerializer;

public class NamedStructType<T> extends RuntimeType<T> implements X10JavaSerializable {
    
    public String typeName;

    // Just for allocation
    public NamedStructType() {
        super();
    }

    protected NamedStructType(String typeName, Class<?> javaClass, int numParams, Type<?>[] parents) {
        super(javaClass, numParams, parents);
        this.typeName = typeName;
    }

    private static final boolean useCache = true;
    private static final ConcurrentHashMap<Class<?>, NamedStructType<?>> typeCache = new ConcurrentHashMap<Class<?>, NamedStructType<?>>();
    public static <T> NamedStructType/*<T>*/ make(String typeName, Class<?> javaClass) {
        if (useCache) {
            NamedStructType<?> type = typeCache.get(javaClass);
            if (type == null) {
                NamedStructType<?> type0 = new NamedStructType<T>(typeName, javaClass, 0, null);
                type = typeCache.putIfAbsent(javaClass, type0);
                if (type == null) type = type0;
            }
            return (NamedStructType<T>) type;
        } else {
            return new NamedStructType<T>(typeName, javaClass, 0, null);
        }
    }

    public static <T> NamedStructType/*<T>*/ make(String typeName, Class<?> javaClass, int numParams) {
        if (useCache) {
            NamedStructType<?> type = typeCache.get(javaClass);
            if (type == null) {
                NamedStructType<?> type0 = new NamedStructType<T>(typeName, javaClass, numParams, null);
                type = typeCache.putIfAbsent(javaClass, type0);
                if (type == null) type = type0;
            }
            return (NamedStructType<T>) type;
        } else {
            return new NamedStructType<T>(typeName, javaClass, numParams, null);
        }
    }

    public static <T> NamedStructType/*<T>*/ make(String typeName, Class<?> javaClass, Type<?>[] parents) {
        if (useCache) {
            NamedStructType<?> type = typeCache.get(javaClass);
            if (type == null) {
                NamedStructType<?> type0 = new NamedStructType<T>(typeName, javaClass, 0, parents);
                type = typeCache.putIfAbsent(javaClass, type0);
                if (type == null) type = type0;
            }
            return (NamedStructType<T>) type;
        } else {
            return new NamedStructType<T>(typeName, javaClass, 0, parents);
        }
    }
    
    public static <T> NamedStructType/*<T>*/ make(String typeName, Class<?> javaClass, int numParams, Type<?>[] parents) {
        if (useCache) {
            NamedStructType<?> type = typeCache.get(javaClass);
            if (type == null) {
                NamedStructType<?> type0 = new NamedStructType<T>(typeName, javaClass, numParams, parents);
                type = typeCache.putIfAbsent(javaClass, type0);
                if (type == null) type = type0;
            }
            return (NamedStructType<T>) type;
        } else {
            return new NamedStructType<T>(typeName, javaClass, numParams, parents);
        }
    }

    @Override
    public String typeName() {
        return typeName;
    }

    @Override
    public boolean isref() {
        return false;
    }

    @Override
    public void $_serialize(X10JavaSerializer serializer) throws IOException {
        super.$_serialize(serializer);
        serializer.write(typeName);
    }

    public static X10JavaSerializable $_deserializer(X10JavaDeserializer deserializer) throws IOException {
        NamedStructType namedType = new NamedStructType();
        deserializer.record_reference(namedType);
        return $_deserialize_body(namedType, deserializer);
    }

    public static X10JavaSerializable $_deserialize_body(NamedStructType nt, X10JavaDeserializer deserializer) throws IOException {
        RuntimeType.$_deserialize_body(nt, deserializer);
        nt.typeName = deserializer.readString().intern();
        return nt;
    }
}
