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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import x10.runtime.impl.java.Runtime;

abstract class DeserializationDictionary implements SerializationConstants {
    protected final Map<Short,Method> idsToMethod;
    protected final Map<Short,Class<?>> idsToClass;

    DeserializationDictionary(Map<Short,Method> mMap, Map<Short,Class<?>> cMap) {
        this.idsToMethod = mMap;
        this.idsToClass = cMap;
    }

    void addEntry(short id, String name) {
        Class<?> clazz;
        try {
            clazz = Class.forName(name);
        } catch (ClassNotFoundException e) {
            String msg = "DeserializationDictionary.addEntry: failed to load class "+name;
            if (Runtime.TRACE_SER) Runtime.printTraceMessage(msg);
            throw new RuntimeException(msg, e);
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
        return idsToClass.get(Short.valueOf(sid));
    }

    Method getMethod(short sid) {
        return idsToMethod.get(Short.valueOf(sid));
    }

    static final class MasterDeserializationDictionary extends DeserializationDictionary {

        MasterDeserializationDictionary() {
            super(new ConcurrentHashMap<Short, Method>(), new ConcurrentHashMap<Short, Class<?>>());
        }

        @Override
        Class<?> getClassForID(short sid) {
            Class<?> clazz = super.getClassForID(sid);
            assert clazz != null || sid >= FIRST_DYNAMIC_ID : "master dictionary does not contain id for supposedly shared sid " + sid;
            return clazz;
        }

        @Override
        Method getMethod(short sid) {
            Method m = super.getMethod(sid);
            // Note: it is valid for m to be null when sid doesn't implement X10JavaSerializable. So no assert here.
            return m;
        }

        @Override
        void addEntry(short id, String name) {
            assert id >= FIRST_SHARED_ID && id < FIRST_DYNAMIC_ID : "invalid id in addEntry of master dictionary" + id;
            super.addEntry(id, name);
        }
    }

    static final class LocalDeserializationDictionary extends DeserializationDictionary {
        final private DeserializationDictionary shared;

        LocalDeserializationDictionary(DeserializationDictionary parent) {
            super(new HashMap<Short, Method>(), new HashMap<Short, Class<?>>());
            this.shared = parent;
        }

        LocalDeserializationDictionary(X10JavaDeserializer jds, DeserializationDictionary parent) {
            this(parent);

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

        Class<?> getClassForID(short sid) {
            if (sid < FIRST_DYNAMIC_ID) {
                return shared.getClassForID(sid);
            }
            Class<?> clazz = super.getClassForID(sid);
            assert clazz != null : "DeserializationDictionary: id "+sid+" is not mapped to a class!";
            return clazz;
        }

        Method getMethod(short sid) {
            if (sid < FIRST_DYNAMIC_ID) {
                return shared.getMethod(sid);
            } else {
                return super.getMethod(sid);
            }
        }
    }
}