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

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;

import x10.runtime.impl.java.Runtime;

class DeserializationDictionary {
    private HashMap<Short,Method> idsToMethod = new HashMap<Short,Method>();
    private HashMap<Short,Class<?>> idsToClass = new HashMap<Short,Class<?>>();
    private final boolean isSharedDict;
    
    DeserializationDictionary() {
        isSharedDict = true;
    }
    
    DeserializationDictionary(X10JavaDeserializer jds) {
        isSharedDict = false;
        try {
            short numEntries = jds.readShort();
            if (Runtime.TRACE_SER) {
                Runtime.printTraceMessage("\tReceiving "+numEntries+" serialization ids");                
            }
            for (short i=0; i<numEntries; i++) {
                short id = jds.readShort();
                String name = jds.readStringValue();
                if (Runtime.TRACE_SER) {
                    Runtime.printTraceMessage("\tserialization id: "+id+" = "+name);                
                }
                addEntry(id, name);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failure while reading message dictionary", e);
        }
    }

    void addEntry(short id, String name) {
        Class<?> clazz;
        try {
            clazz = Class.forName(name);
        } catch (ClassNotFoundException e) {
            System.err.println("readMessageDictionary: failed to load class "+name);
            throw new RuntimeException(e);
        }
        idsToClass.put(Short.valueOf(id), clazz);
        if (x10.serialization.X10JavaSerializable.class.isAssignableFrom(clazz) && !clazz.isInterface()) {
            Method m;
            try {
                m = clazz.getMethod("$_deserializer", X10JavaDeserializer.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("DeserializationDictionary: class "+clazz+
                                           " implements X10JavaSerializable but does not have a $_deserializer method", e);
            }
            m.setAccessible(true);
            idsToMethod.put(Short.valueOf(id), m);
        }
    }
    
    Class<?> getClassForID(short sid) {
        if (!isSharedDict && (sid < SerializationConstants.FIRST_DYNAMIC_ID)) {
            return SharedDictionaries.getClassForID(sid);
        }
        
        Class<?> clazz = idsToClass.get(Short.valueOf(sid));
        if (clazz == null) {
            throw new RuntimeException("DeserializationDictionary: id "+sid+" is not mapped to a class!");
        }
        return clazz;
    }
    
    Method getMethod(short sid) {
        if (!isSharedDict && (sid < SerializationConstants.FIRST_DYNAMIC_ID)) {
            return SharedDictionaries.getMethod(sid);
        }
        
        return idsToMethod.get(Short.valueOf(sid));
    }

}
