/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2012.
 */

package x10.serialization;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import x10.rtt.RuntimeType;

/**
 * Used during serialization to maintain a mapping from Class to id.
 */
class SerializationDictionary implements SerializationConstants {
    
    protected HashMap<Class<?>,Short> dict = new HashMap<Class<?>,Short>();
    
    protected short nextId;
    private final boolean isShared;
    
    public SerializationDictionary(short firstId) {
        nextId = firstId;
        isShared = firstId == FIRST_SHARED_ID;
    }

    short getSerializationId(Class<?> clazz, Object obj) {
        if (!isShared) {
            short sid = SharedDictionaries.getSerializationId(clazz, obj);
            if (sid != NO_PREASSIGNED_ID) return sid;
        }
        return getSerializationId(clazz, obj, true);
    }

    short getSerializationId(Class<?> clazz, Object obj, boolean allocateIfAbsent) {
        if (obj instanceof RuntimeType<?>) {
            short sid = ((RuntimeType<?>)obj).$_get_serialization_id();
            if (sid <= MAX_HARDCODED_ID) {
                return sid;
            }
        }
        Short id = dict.get(clazz);
        if (null == id) {
            if (allocateIfAbsent) {
                id = Short.valueOf(nextId++);
                dict.put(clazz, id);
            } else {
                return SerializationConstants.NO_PREASSIGNED_ID;
            }
        }
        return id.shortValue();
    }
    
    byte[] encode() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeShort(dict.size());
        for (Entry<Class<?>, Short> es  : dict.entrySet()) {
            dos.writeShort(es.getValue());
            String name = es.getKey().getName();
            dos.writeInt(name.length());
            dos.write(name.getBytes());
        }
        dos.close();
        
        return baos.toByteArray();
    }
    
    
    @Override
    public String toString() {
        return dict.toString();
    }
}
